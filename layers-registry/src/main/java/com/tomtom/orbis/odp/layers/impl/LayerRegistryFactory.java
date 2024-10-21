package com.tomtom.orbis.odp.layers.impl;

import com.tomtom.orbis.odp.layer.registry.api.LayerRegistry;
import com.tomtom.orbis.odp.layer.registry.api.GitHubRegistryConfiguration;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LayerRegistryFactory {

    public static LayerRegistry fromGithub(
        final GitHubRegistryConfiguration layerRegistryAuthenticationConfiguration) {
        return new GithubLayerRegistryFactory().getLayerRegistry(layerRegistryAuthenticationConfiguration);
    }

    public static LayerRegistry fromLayerCatalogue(final String layerCatalogueUrl, final String layerCatalogueToken) {
        return new LayerCatalogueLayerRegistryFactory().getLayerRegistry(layerCatalogueUrl, layerCatalogueToken);
    }
}
