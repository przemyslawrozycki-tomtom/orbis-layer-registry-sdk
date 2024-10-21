package com.tomtom.orbis.odp.layers.impl;

import static com.tomtom.orbis.odp.layers.impl.PrivateKeyReader.loadPrivateKey;
import static java.time.LocalDateTime.ofInstant;
import static java.util.Objects.isNull;

import com.tomtom.orbis.odp.layer.registry.api.GitHubRegistryConfiguration;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.kohsuke.github.GHAppInstallation;
import org.kohsuke.github.GHAppInstallationToken;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

public class GitHubClientProvider {

    private static final Logger log = LoggerFactory.getLogger(GitHubClientProvider.class);
    private final GitHubRegistryConfiguration layerRegistryAuthenticationConfiguration;
    private GitHub githubApp;

    private final Clock clock = Clock.systemUTC();
    private ApplicationToken applicationToken;

    public GitHubClientProvider(final GitHubRegistryConfiguration layerRegistryAuthenticationConfiguration) {
        this.layerRegistryAuthenticationConfiguration = layerRegistryAuthenticationConfiguration;
    }

    public GitHub getGithubApp() throws IOException {
        if (isNull(githubApp) || isTokenAlmostExpired()) {
            log.debug("Generating new Github App Client!");
            final ApplicationToken token = getApplicationToken();
            githubApp = new GitHubBuilder().withAppInstallationToken(token.getValue()).build();
        }
        return githubApp;
    }

    public ApplicationToken getApplicationToken() throws IOException {
        if (isTokenAlmostExpired()) {
            applicationToken = acquireToken();
            log.info("Get fresh token which expires at {}", applicationToken.getExpiresAt());
        }
        return applicationToken;
    }

    public String getBranch() {
        return layerRegistryAuthenticationConfiguration.getBranch();
    }

    protected boolean isTokenAlmostExpired() {
        return isNull(applicationToken) || isNull(applicationToken.getExpiresAt()) || now().isAfter(expiresAt());
    }

    private LocalDateTime now() {
        return ofInstant(clock.instant(), clock.getZone());
    }

    private LocalDateTime expiresAt() {
        return ofInstant(applicationToken.getExpiresAt(), clock.getZone()).minus(Duration.ofMinutes(1L));
    }

    protected ApplicationToken acquireToken() throws IOException {
        final String jwtToken = createJwtToken();
        final GitHub gitHubClient = new GitHubBuilder().withJwtToken(jwtToken).build();
        final GHAppInstallation appInstallation = gitHubClient.getApp()
                                                              .getInstallationById(
                                                                  layerRegistryAuthenticationConfiguration.getLayerRegistryConfiguration()
                                                                                                          .getAppInstallationId());
        final GHAppInstallationToken installationToken = appInstallation.createToken().create();
        final String appInstallationToken = installationToken.getToken();
        return new ApplicationToken(appInstallationToken, installationToken.getExpiresAt().toInstant());
    }

    private String createJwtToken() {
        try {
            final PrivateKey privateKey = loadPrivateKey(
                layerRegistryAuthenticationConfiguration.getLayerRegistryConfiguration().getCredentials());
            final Duration ttl = layerRegistryAuthenticationConfiguration.getTtl();
            // maximum TTL is 10 minutes
            final String jwtToken = JwtTokenCreator.createJwtToken(
                layerRegistryAuthenticationConfiguration.getLayerRegistryConfiguration().getAppId(),
                privateKey,
                ttl.toMillis(),
                clock);
            log.info("Created JwtToken with TTL: {}", ttl);
            return jwtToken;
        } catch (final GeneralSecurityException e) {
            throw new IllegalStateException("Unable to generate the JWT token", e);
        }
    }
}

class ApplicationToken {

    String value;

    Instant expiresAt;

    public ApplicationToken(final String value, final Instant expiresAt) {
        this.value = value;
        this.expiresAt = expiresAt;
    }

    public String getValue() {
        return value;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}

class JwtTokenCreator {

    public static String createJwtToken(final String githubAppId,
                                        final PrivateKey privateKey,
                                        final long ttlMillis,
                                        final Clock clock) {
        // The JWT signature algorithm we will be using to sign the token
        final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;

        final Instant instant = clock.instant();
        final Date now = Date.from(instant);

        // Let's set the JWT Claims
        final JwtBuilder builder = Jwts.builder()
                                       .setIssuedAt(now)
                                       .setIssuer(githubAppId)
                                       .signWith(signatureAlgorithm, privateKey);

        // If it has been specified, let's add the expiration
        if (ttlMillis > 0) {
            final Date exp = Date.from(instant.plusMillis(ttlMillis));
            builder.setExpiration(exp);
        }

        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

}