package com.tomtom.orbis.odp.layer.registry.api;

import static com.tomtom.orbis.odp.layer.registry.api.fixtures.ObjectMothers.LAYER_ID;
import static com.tomtom.orbis.odp.layer.registry.api.fixtures.ObjectMothers.aLayer;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import java.util.List;

class LayerRegistryTest {

    @Test
    void shouldHasLayer() {
        final LayerRegistry layerRegistry =
            () -> LayerRegistryContent.builder().layers(List.of(aLayer())).build();
        final Layer layer = layerRegistry.getLayer(LAYER_ID);
        assertNotNull(layer);
    }

}