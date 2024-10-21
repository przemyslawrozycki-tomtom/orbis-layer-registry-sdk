package com.tomtom.orbis.odp.layer.registry.spring;

import com.tomtom.orbis.odp.configuration.api.configuration.LayerRegistryConfiguration;
import com.tomtom.orbis.odp.configuration.api.credentials.PrivateKeyCredentials;
import com.tomtom.orbis.odp.layer.registry.api.LayerRegistry;
import com.tomtom.orbis.odp.layer.registry.api.GitHubRegistryConfiguration;
import com.tomtom.orbis.odp.layers.impl.FileBasedLayerRegistry;
import com.tomtom.orbis.odp.layers.impl.GitHubClientProvider;
import com.tomtom.orbis.odp.layers.impl.GithubLayerRegistry;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import java.time.Duration;

@Configuration
@ComponentScan(basePackages = "com.tomtom.orbis.odp.layer.registry.spring")
public class LayersAutoConfiguration {

    @SneakyThrows
    @Bean
    @ConditionalOnProperty("layer.registry.file")
    LayerRegistry fileBasedLayerRegistry(@Value("${layer.registry.file}") final Resource registryFile) {
        return new FileBasedLayerRegistry(registryFile.getInputStream());
    }

    @Bean
    @ConditionalOnProperty(value = "layer.registry.file", matchIfMissing = true, havingValue = "false")
    LayerRegistry githubLayerRegistry(final GitHubClientProvider githubClientProvider) {
        return new GithubLayerRegistry(githubClientProvider);
    }

    @Bean
    @ConditionalOnProperty(value = "layer.registry.file", matchIfMissing = true, havingValue = "false")
    GitHubClientProvider gitHubClientProvider(GitHubRegistryConfiguration authentication) {
        return new GitHubClientProvider(authentication);
    }

    @Bean
    @ConditionalOnProperty(value = "layer.registry.file", matchIfMissing = true, havingValue = "false")
    public GitHubRegistryConfiguration authentication(
        @Value(value = "${layer.registry.appId}") final String appId,
        @Value(value = "${layer.registry.appInstallationId:-1}") final long appInstallationId,
        @Value(value = "${layer.registry.privateKey}") final String privateKey,
        @Value(value = "${layer.registry.ttl:10m}") final Duration ttl,
        @Value("${layer.registry.branch:main}") final String registryBranch) {

        return GitHubRegistryConfiguration
            .builder().layerRegistryConfiguration(
                LayerRegistryConfiguration.builder()
                                          .appId(appId).appInstallationId(appInstallationId)
                                          .credentials(PrivateKeyCredentials.builder()
                                                                            .privateKey(privateKey)
                                                                            .build()).build()).ttl(ttl)
            .branch(registryBranch)
            .build();

    }

}
