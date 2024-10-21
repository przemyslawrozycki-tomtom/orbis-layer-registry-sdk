package com.tomtom.orbis.odp.layers.impl;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;

class FileBaseLayerRegistryTest implements WithAssertions {

    @ParameterizedTest
    @ValueSource(strings = {"registry.json", "registry-without-name.json", "registry-without-owner-and-features.json", "registry-with-unknown-fields.json"})
    void shouldDeserializeLayersProperly(String registryFile) throws IOException {
        //given
        var layerRegistryFile = new ClassPathResource(registryFile).getInputStream();
        var layerRegistry = new FileBasedLayerRegistry(layerRegistryFile);

        //when
        var content = layerRegistry.getContent();

        //then
        assertThat(content).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"registry.json", "registry-without-name.json", "registry-without-owner-and-features.json", "registry-with-unknown-fields.json"})
    void returnsSameContentEveryTime(String registryFile) throws IOException {
        //given
        var layerRegistryFile = new ClassPathResource(registryFile).getInputStream();
        var layerRegistry = new FileBasedLayerRegistry(layerRegistryFile);
        var firstCallContent = layerRegistry.getContent();

        //when
        var secondCallContent = layerRegistry.getContent();

        //then
        assertThat(firstCallContent).isNotNull();
        assertThat(secondCallContent).isNotNull();
        assertThat(secondCallContent).isEqualTo(firstCallContent);
    }
}
