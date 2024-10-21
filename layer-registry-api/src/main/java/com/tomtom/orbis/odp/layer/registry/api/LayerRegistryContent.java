package com.tomtom.orbis.odp.layer.registry.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Jacksonized
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class LayerRegistryContent {

    @Singular
    List<Layer> layers;

}
