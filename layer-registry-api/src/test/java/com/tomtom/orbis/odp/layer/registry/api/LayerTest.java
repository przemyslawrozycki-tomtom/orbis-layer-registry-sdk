package com.tomtom.orbis.odp.layer.registry.api;

import static com.tomtom.orbis.odp.layer.registry.api.fixtures.ObjectMothers.LAYER_ID;
import static com.tomtom.orbis.odp.layer.registry.api.fixtures.ObjectMothers.aLayer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import java.util.List;

class LayerTest {

    @Test
    void shouldConstruct() {
        final Layer layer = aLayer();
        assertNotNull(layer);
        assertEquals(LAYER_ID, layer.getLayerId());
        assertNotNull(layer.getLayerName());
        assertNotNull(layer.getProvenance());
        assertNotNull(layer.getLicence());
        assertNotNull(layer.getDescription());
        assertNotNull(layer.getParents());
        assertNotNull(layer.getSubscriptions());
        assertNotNull(layer.getFeatures());
        assertNotNull(layer.getContact());
    }
    @Test
    void shouldCopyLayer() {
        final Layer layer = aLayer();
        final Layer updatedLayer = layer.toBuilder().subscriptions(List.of(3,4,5,6,7)).build();
        assertEquals(layer.getLayerId(), updatedLayer.getLayerId());
        assertEquals(layer.getLayerName(), updatedLayer.getLayerName());
        assertEquals(layer.getProvenance(), updatedLayer.getProvenance());
        assertEquals(layer.getLicence(), updatedLayer.getLicence());
        assertEquals(layer.getDescription(), updatedLayer.getDescription());
        assertEquals(layer.getParents(), updatedLayer.getParents());
        assertEquals(layer.getFeatures(), updatedLayer.getFeatures());
        assertEquals(layer.getContact(), updatedLayer.getContact());
        assertNotEquals(layer.getSubscriptions(), updatedLayer.getSubscriptions());
    }

}