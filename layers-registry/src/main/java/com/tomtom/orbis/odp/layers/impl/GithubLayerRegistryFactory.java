package com.tomtom.orbis.odp.layers.impl;

import com.tomtom.orbis.odp.layer.registry.api.GitHubRegistryConfiguration;

public class GithubLayerRegistryFactory {

    public GithubLayerRegistry getLayerRegistry(
        GitHubRegistryConfiguration layerRegistryAuthenticationConfiguration) {
        GitHubClientProvider gitHubClientProvider = new GitHubClientProvider(layerRegistryAuthenticationConfiguration);
        return new GithubLayerRegistry(gitHubClientProvider);
    }

}
