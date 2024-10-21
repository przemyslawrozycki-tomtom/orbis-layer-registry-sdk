package com.tomtom.orbis.odp.layer.registry.api;

import static com.tomtom.orbis.odp.layer.registry.api.fixtures.ObjectMothers.aGitHubRegistryConfiguration;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class GitHubRegistryConfigurationTest {

    @Test
    void shouldConstruct() {
        final GitHubRegistryConfiguration gitHubRegistryConfiguration = aGitHubRegistryConfiguration();
        assertNotNull(gitHubRegistryConfiguration);
        assertNotNull(gitHubRegistryConfiguration.getLayerRegistryConfiguration());
        assertNotNull(gitHubRegistryConfiguration.getBranch());
        assertNotNull(gitHubRegistryConfiguration.getTtl());
    }

}