package net.programmer.igoodie.streamspawn;

import net.programmer.igoodie.goodies.runtime.GoodieObject;
import net.programmer.igoodie.streamspawn.configuration.StreamSpawnConfig;
import net.programmer.igoodie.streamspawn.event.generator.SSEventGenerator;
import net.programmer.igoodie.util.Couple;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class StreamSpawnApi {

    private final static List<StreamSpawnConfig> CONFIGS = new LinkedList<>();

    public static <T extends StreamSpawnConfig> T hookConfig(T config) {
        CONFIGS.add(config);
        return config;
    }

    public static List<StreamSpawnConfig> getConfigs() {
        return Collections.unmodifiableList(CONFIGS);
    }

    /* --------------------------------------- */

    private final static List<SSEventGenerator> EVENT_GENERATORS = new LinkedList<>();

    public static <T extends SSEventGenerator> T hookEventGenerator(T eventGenerator) {
        EVENT_GENERATORS.add(eventGenerator);
        return eventGenerator;
    }

    public static List<SSEventGenerator> getEventGenerators() {
        return Collections.unmodifiableList(EVENT_GENERATORS);
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

}
