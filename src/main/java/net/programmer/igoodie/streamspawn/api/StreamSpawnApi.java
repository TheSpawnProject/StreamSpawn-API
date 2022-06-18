package net.programmer.igoodie.streamspawn.api;

import net.programmer.igoodie.goodies.runtime.GoodieObject;
import net.programmer.igoodie.goodies.util.Couple;
import net.programmer.igoodie.streamspawn.api.configuration.DeferredConfig;
import net.programmer.igoodie.streamspawn.api.configuration.StreamSpawnConfig;
import net.programmer.igoodie.streamspawn.api.generator.SSEventGenerator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StreamSpawnApi {

    private final static List<DeferredConfig<?>> CONFIGS = new LinkedList<>();

    public static <T extends StreamSpawnConfig> DeferredConfig<T> hookConfig(Supplier<T> configGenerator) {
        DeferredConfig<T> config = new DeferredConfig<>(configGenerator);
        CONFIGS.add(config);
        return config;
    }

    public static List<StreamSpawnConfig> getConfigs() {
        return Collections.unmodifiableList(CONFIGS.stream().map(DeferredConfig::get).collect(Collectors.toList()));
    }

    /* --------------------------------------- */

    private final static List<SSEventGenerator<?>> EVENT_GENERATORS = new LinkedList<>();

    public static <T extends SSEventGenerator<?>> T hookEventGenerator(T eventGenerator) {
        EVENT_GENERATORS.add(eventGenerator);
        return eventGenerator;
    }

    public static List<SSEventGenerator<?>> getEventGenerators() {
        return Collections.unmodifiableList(EVENT_GENERATORS);
    }

    public static void stopAllEventGenerators() {
        EVENT_GENERATORS.forEach(SSEventGenerator::stop);
    }

    /* --------------------------------------- */

    private static final Queue<Couple<String, GoodieObject>> EVENT_BUCKET = new LinkedList<>();

    public static synchronized void pushEvent(String eventName, GoodieObject eventArguments) {
        synchronized (EVENT_BUCKET) {
            EVENT_BUCKET.offer(new Couple<>(eventName, eventArguments));
        }
    }

    public static synchronized int unhandledEventCount() {
        synchronized (EVENT_BUCKET) {
            return EVENT_BUCKET.size();
        }
    }

    public static synchronized Couple<String, GoodieObject> pollUnhandledEvent() {
        synchronized (EVENT_BUCKET) {
            return EVENT_BUCKET.poll();
        }
    }

    /* --------------------------------------- */


}
