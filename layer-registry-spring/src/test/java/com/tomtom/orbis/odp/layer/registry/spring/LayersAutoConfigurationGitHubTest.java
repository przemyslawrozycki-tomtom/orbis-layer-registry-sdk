package com.tomtom.orbis.odp.layer.registry.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.tomtom.orbis.odp.layer.registry.api.LayerRegistry;
import com.tomtom.orbis.odp.layers.impl.GithubLayerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = LayersAutoConfiguration.class)
@SpringBootTest
class LayersAutoConfigurationGitHubTest {

    @Autowired
    LayerRegistry registry;

    @Test
    void makeSureBeanCanBeSetupWithoutConfig() {
        assertThat(registry).isNotNull().isInstanceOf(GithubLayerRegistry.class);
    }
}
