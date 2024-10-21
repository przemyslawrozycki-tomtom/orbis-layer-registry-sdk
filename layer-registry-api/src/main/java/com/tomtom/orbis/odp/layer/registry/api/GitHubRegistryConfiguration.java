package com.tomtom.orbis.odp.layer.registry.api;

import com.tomtom.orbis.odp.configuration.api.configuration.LayerRegistryConfiguration;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.Duration;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GitHubRegistryConfiguration {

    public static final String DEFAULT_BRANCH_NAME = "main";

    private LayerRegistryConfiguration layerRegistryConfiguration;
    @Builder.Default
    private String branch = DEFAULT_BRANCH_NAME;
    @Builder.Default
    private Duration ttl = Duration.ofMinutes(10);

}
