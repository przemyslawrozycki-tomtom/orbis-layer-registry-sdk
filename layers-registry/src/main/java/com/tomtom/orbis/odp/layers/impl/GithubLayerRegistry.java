package com.tomtom.orbis.odp.layers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomtom.orbis.odp.layer.registry.api.LayerRegistry;
import com.tomtom.orbis.odp.layer.registry.api.LayerRegistryContent;
import lombok.SneakyThrows;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.HttpException;

import java.io.IOException;
import java.util.List;

public class GithubLayerRegistry implements LayerRegistry {

    private final GitHubClientProvider gitHubClientProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private LayerRegistryContent content;

    public GithubLayerRegistry(final GitHubClientProvider gitHubClientProvider) {
        this.gitHubClientProvider = gitHubClientProvider;
    }

    @SneakyThrows
    public LayerRegistryContent getContent() {
        if (content == null) {
            content = RetryHelper.doWithRetry(this::readContent, List.of(HttpException.class));
        }
        return content;
    }

    private LayerRegistryContent readContent() throws IOException {
        final GitHub githubApp = gitHubClientProvider.getGithubApp();
        final GHRepository repository =
            githubApp.getOrganization("tomtom-internal").getRepository("orbis-layer-registry");
        final GHContent registryContent =
                repository.getFileContent("registry/registry.json", gitHubClientProvider.getBranch());

        return objectMapper.readValue(registryContent.read(), LayerRegistryContent.class);
    }
}

