package example.event_generator;

import net.programmer.igoodie.goodies.runtime.GoodieObject;
import net.programmer.igoodie.streamspawn.StreamSpawnApi;
import net.programmer.igoodie.streamspawn.configuration.StreamSpawnConfig;
import net.programmer.igoodie.streamspawn.event.generator.SSEventGenerator;
import net.programmer.igoodie.util.Couple;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class PingTester {

    @Test
    public void testPlugin() throws InterruptedException {
        PingPlugin plugin = new PingPlugin();
        String rootPath = "./build/test-configs";

        for (StreamSpawnConfig config : StreamSpawnApi.getConfigs()) {
            File configFile = new File(rootPath + File.separator + config.getConfigName());
            config.readConfig(configFile);
        }

        for (SSEventGenerator eventGenerator : StreamSpawnApi.getEventGenerators()) {
            eventGenerator.start();
        }

        plugin.onLoaded();

        long t0 = System.currentTimeMillis();
        long dt = 0;

        while ((dt = System.currentTimeMillis() - t0) <= 5 * 1000) {
            Couple<String, GoodieObject> polledEvent = StreamSpawnApi.pollUnhandledEvent();
            if (polledEvent != null) {
                System.out.println("dt = " + dt);
                System.out.println(polledEvent.getFirst() + " -> " + polledEvent.getSecond());
            }
        }

        for (SSEventGenerator eventGenerator : StreamSpawnApi.getEventGenerators()) {
            eventGenerator.stop();
        }
    }

}
