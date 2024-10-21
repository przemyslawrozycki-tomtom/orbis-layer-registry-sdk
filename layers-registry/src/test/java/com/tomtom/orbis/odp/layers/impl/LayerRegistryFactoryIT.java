package com.tomtom.orbis.odp.layers.impl;

import com.tomtom.orbis.odp.configuration.api.configuration.LayerRegistryConfiguration;
import com.tomtom.orbis.odp.configuration.api.credentials.PrivateKeyCredentials;
import com.tomtom.orbis.odp.layer.registry.api.LayerRegistry;
import com.tomtom.orbis.odp.layer.registry.api.GitHubRegistryConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.Duration;

class LayerRegistryFactoryIT {

    @Test
    void shouldConnectGithub() {
        LayerRegistryConfiguration layerRegistryConfiguration =
            LayerRegistryConfiguration.builder().appId("205171").appInstallationId(26029868L).credentials(
                                          PrivateKeyCredentials.builder().privateKey(System.getenv(
                                              "LAYER_REGISTRY_PRIVATE_KEY")).build())
                                      .build();
        GitHubRegistryConfiguration layerRegistryAuthenticationConfiguration =
            GitHubRegistryConfiguration.builder().layerRegistryConfiguration(layerRegistryConfiguration)
                                       .ttl(Duration.ofSeconds(5))
                                       .build();

        LayerRegistry registry = LayerRegistryFactory.fromGithub(layerRegistryAuthenticationConfiguration);
        Assertions.assertNotNull(registry);
        Assertions.assertNotNull(registry.getContent());
    }

}