package com.tomtom.orbis.odp.layers.impl;

import com.google.common.reflect.Reflection;
import com.tomtom.orbis.odp.layer.registry.api.Layer;
import com.tomtom.orbis.odp.layer.registry.api.LayerRegistry;
import com.tomtom.orbis.odp.layer.registry.api.LayerRegistryContent;
import com.tomtom.orbis.platform.layercatalogue.api.v1.CoreLayer;
import com.tomtom.orbis.platform.layercatalogue.api.v1.CoreLayerServiceGrpc.CoreLayerServiceBlockingStub;
import com.tomtom.orbis.platform.layercatalogue.api.v1.GetAllLayersRequest;
import com.tomtom.orbis.platform.layercatalogue.api.v1.GetAllLayersResponse;
import com.tomtom.orbis.platform.layercatalogue.api.v1.GetCoreLayersRequest;
import com.tomtom.orbis.platform.layercatalogue.api.v1.GetCoreLayersResponse;
import com.tomtom.orbis.platform.layercatalogue.api.v1.LicenseType;
import com.tomtom.orbis.platform.layercatalogue.api.v1.ProvenanceType;
import com.tomtom.orbis.platform.layercatalogue.api.v1.ThinLayer;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import lombok.SneakyThrows;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LayerCatalogueLayerRegistry implements LayerRegistry {

    private final CoreLayerServiceBlockingStub coreLayers;

    public LayerCatalogueLayerRegistry(
        final CoreLayerServiceBlockingStub coreLayerServiceBlockingStub) {
        this.coreLayers = coreLayerServiceBlockingStub;
    }

    @Override
    public LayerRegistryContent getContent() {
        return LayerRegistryContent.builder()
                                   .layers(lazyLayers())
                                   .build();
    }

    private List<Layer> lazyLayers() {
        final GetAllLayersResponse allLayers = coreLayers.getAllLayers(GetAllLayersRequest.newBuilder().build());
        return allLayers.getLayersList()
                 .stream()
                 .map(l -> new LazyLayer(coreLayers, l).toLayer())
                 .toList();
    }

    static class LazyLayer {

        private final CoreLayerServiceBlockingStub coreLayersStub;
        private final ThinLayer ref;
        private Optional<Layer> fullContent = Optional.empty();

        private final Map<Method, BiFunction<Layer, Object[], Object>> handlers;

        @SneakyThrows
        public LazyLayer(final CoreLayerServiceBlockingStub coreLayersStub, ThinLayer ref) {
            this.coreLayersStub = coreLayersStub;
            this.ref = ref;

            handlers = Map.of(
                Layer.class.getMethod("getLayerId"), (layer, args) -> fullContentOr(Layer::getLayerId, ref::getLayerId),
                Layer.class.getMethod("getLayerName"), (layer, args) -> fullContentOr(Layer::getLayerName, ref::getName),
                Layer.class.getMethod("getDescription"), (layer, args) ->
                    fullContentOr(Layer::getDescription, ref::getDescription),
                Layer.class.getMethod("getFeatures"), (layer, args) -> fullContentOr(Layer::getFeatures, ref::getFeaturesList),
                Layer.class.getMethod("getLicence"), (layer, args) -> fullContentOr(Layer::getLicence, () -> mapLicense(ref.getLicense(0))),
                Layer.class.getMethod("getProvenance"), (layer, args) -> fullContentOr(Layer::getProvenance, () -> mapProvenance(ref.getProvenance(0))),
                Layer.class.getMethod("getSubscriptions"), (layer, args) -> initialize().getSubscriptions(),
                Layer.class.getMethod("getParents"), (layer, args) -> initialize().getParents(),
                Layer.class.getMethod("getContact"), (layer, args) -> Set.of()
            );
        }

        public int getLayerId() {
            return fullContentOr(Layer::getLayerId, ref::getLayerId);
        }

        public List<Integer> getSubscriptions() {
            return initialize().getSubscriptions();
        }

        private Layer initialize() {
            fullContent = fullContent.or(() -> {
                final GetCoreLayersResponse coreLayersResponse = coreLayersStub.getCoreLayers(
                    GetCoreLayersRequest.newBuilder()
                                        .addLayerIds(ref.getLayerId())
                                        .getDefaultInstanceForType());
                return Optional.of(fromCoreLayer(coreLayersResponse.getCoreLayers(0)));
            });
            return fullContent.get();
        }

        private Layer fromCoreLayer(final CoreLayer coreLayers) {
            return Layer.builder()
                .layerId(coreLayers.getLayerId())
                .layerName(coreLayers.getName())
                .description(coreLayers.getDescription())
                .provenance(mapProvenance(ref.getProvenance(0)))
                .licence(mapLicense(ref.getLicense(0)))
                .features(Set.copyOf(ref.getFeaturesList().stream().map(f -> f.getString()).collect(Collectors.toSet())))
                .parents(coreLayers.getParentLayerIdsList())
                .subscriptions(coreLayers.getSubscriptionLayerIdsList())
                        .build();
        }

        private String mapLicense(final LicenseType license) {
            return null;
        }

        private String mapProvenance(final ProvenanceType provenance) {
            return null;
        }

        private <T> T fullContentOr(Function <Layer, T> fromLayer, Supplier<T> orElseSupplier) {
            return fullContent.map(fromLayer).orElseGet(orElseSupplier);
        }

        @SneakyThrows
        public Layer toLayer() {
            final ProxyFactory proxyFactory = new ProxyFactory();
            proxyFactory.setSuperclass(Layer.class);
            final Class<?> proxiedLayerClass = proxyFactory.createClass();
            final Constructor<?> constructor = proxiedLayerClass.getDeclaredConstructor();
            constructor.setAccessible(true);

            final Layer proxy = (Layer) constructor.newInstance();
            ((ProxyObject) proxy).setHandler((self, thisMethod, proceed, args) ->
                                                   handlers.get(thisMethod).apply((Layer) self, args));

            return proxy;
        }
    }
}