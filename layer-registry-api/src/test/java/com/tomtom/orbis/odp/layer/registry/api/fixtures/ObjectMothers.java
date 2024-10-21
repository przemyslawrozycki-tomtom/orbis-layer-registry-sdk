package com.tomtom.orbis.odp.layer.registry.api.fixtures;

import com.tomtom.orbis.odp.configuration.api.configuration.LayerRegistryConfiguration;
import com.tomtom.orbis.odp.configuration.api.credentials.PrivateKeyCredentials;
import com.tomtom.orbis.odp.layer.registry.api.GitHubRegistryConfiguration;
import com.tomtom.orbis.odp.layer.registry.api.Layer;
import java.util.List;
import java.util.Set;

public class ObjectMothers {

    public static final String ANY_VALUE = "anyValue";
    public static final int LAYER_ID = 666;

    public static Layer aLayer() {
        return Layer
            .builder()
            .layerId(LAYER_ID)
            .layerName("test")
            .description("description")
            .licence("license")
            .provenance("provenance")
            .parents(List.of(1))
            .subscriptions(List.of(2))
            .features(Set.of("feature"))
            .contact(Set.of("contact"))
            .build();
    }

    public static GitHubRegistryConfiguration aGitHubRegistryConfiguration() {
        return GitHubRegistryConfiguration.builder().layerRegistryConfiguration(
                                              LayerRegistryConfiguration.builder().appId(ANY_VALUE).appInstallationId(0).credentials(
                                                  PrivateKeyCredentials.builder().privateKey(ANY_VALUE).build()).build())
                                          .build();
    }

}