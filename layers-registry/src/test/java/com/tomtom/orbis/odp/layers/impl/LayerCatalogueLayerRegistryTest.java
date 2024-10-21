package com.tomtom.orbis.odp.layers.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.tomtom.orbis.odp.layer.registry.api.LayerRegistryContent;
import com.tomtom.orbis.platform.layercatalogue.api.v1.CoreLayerServiceGrpc;
import com.tomtom.orbis.platform.layercatalogue.api.v1.CoreLayerServiceGrpc.CoreLayerServiceBlockingStub;
import com.tomtom.orbis.platform.layercatalogue.api.v1.GetAllLayersResponse;
import com.tomtom.orbis.platform.layercatalogue.api.v1.ThinLayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LayerCatalogueLayerRegistryTest {

    @Mock
    private CoreLayerServiceBlockingStub coreLayerServiceBlockingStub;

    private LayerCatalogueLayerRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new LayerCatalogueLayerRegistry(coreLayerServiceBlockingStub);
    }

    @Test
    void shouldRespondWithEmptyLayers() {
        // given
        when(coreLayerServiceBlockingStub.getAllLayers(any())).thenReturn(GetAllLayersResponse.newBuilder().build());

        // when
        final LayerRegistryContent content = registry.getContent();

        // then
        assertTrue(content.getLayers().isEmpty());
    }

    @Test
    void shouldRespondWith2Layers() {
        // given
        when(coreLayerServiceBlockingStub.getAllLayers(any())).thenReturn(GetAllLayersResponse.newBuilder()
            .addLayers(ThinLayer.newBuilder().setLayerId(1).build())
            .addLayers(ThinLayer.newBuilder().setLayerId(2).build())
            .build());

        // when
        final LayerRegistryContent content = registry.getContent();

        // then
        assertEquals(2, content.getLayers().size());
    }

}