package com.tomtom.orbis.odp.layer.registry.api;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public interface LayerRegistry {

    LayerRegistryContent getContent();

    default Layer getLayer(Integer layerId) {
        return getContent().getLayers().stream()
                           .collect(toMap(Layer::getLayerId, identity()))
                           .get(layerId);
    }
}
