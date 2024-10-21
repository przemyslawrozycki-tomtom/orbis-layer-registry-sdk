package com.tomtom.orbis.odp.layers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomtom.orbis.odp.layer.registry.api.LayerRegistry;
import com.tomtom.orbis.odp.layer.registry.api.LayerRegistryContent;
import java.io.IOException;
import java.io.InputStream;

public class FileBasedLayerRegistry implements LayerRegistry {

    private final LayerRegistryContent content;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FileBasedLayerRegistry(final InputStream file) {
        this.content = readFromInputStream(file);
    }

    @Override
    public LayerRegistryContent getContent() {
        return content;
    }
    
    private LayerRegistryContent readFromInputStream(InputStream file) {
        try {
            return objectMapper.readValue(file, LayerRegistryContent.class);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
