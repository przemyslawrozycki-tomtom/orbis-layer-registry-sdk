package com.tomtom.orbis.odp.layers.impl;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.tomtom.orbis.ct.sdk.grpc.api.v1.AuthorizationApplyingCallCredentials;
import com.tomtom.orbis.ct.sdk.grpc.api.v1.ChannelBuilder;
import com.tomtom.orbis.ct.sdk.grpc.api.v1.ChannelConfig;
import com.tomtom.orbis.odp.layer.registry.api.LayerRegistry;
import com.tomtom.orbis.platform.layercatalogue.api.v1.CoreLayerServiceGrpc;
import io.grpc.Channel;
import lombok.SneakyThrows;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LayerCatalogueLayerRegistryFactory {

    private static final int KEEP_ALIVE_SEC = 15;
    private static final String USER_AGENT = "layerregistry-tooling-prod";

    public LayerRegistry getLayerRegistry(final String layerCatalogueUrl, final String token) {
        final ChannelConfig cfg = new ChannelConfig(layerCatalogueUrl, KEEP_ALIVE_SEC, USER_AGENT);
        final Channel channel = ChannelBuilder.buildTlsChannelFor(cfg);
        final CoreLayerServiceGrpc.CoreLayerServiceBlockingStub lcClient = CoreLayerServiceGrpc.newBlockingStub(channel);
        return new LayerCatalogueLayerRegistry(lcClient.withCallCredentials(new AuthorizationApplyingCallCredentials(()->token)));
    }

}
