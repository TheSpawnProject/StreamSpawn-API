package example.event_generator;

import net.programmer.igoodie.goodies.runtime.GoodieObject;
import net.programmer.igoodie.goodies.util.Couple;
import net.programmer.igoodie.streamspawn.StreamSpawnApi;
import net.programmer.igoodie.streamspawn.configuration.StreamSpawnConfig;
import net.programmer.igoodie.streamspawn.event.generator.SSEventGenerator;
import net.programmer.igoodie.streamspawn.plugin.SSPluginLoader;
import org.junit.jupiter.api.Test;
import util.TestUtils;

import java.io.File;
import java.net.URISyntaxException;

public class PingTester {

    @Test
    public void testPluginFromJar() throws URISyntaxException {
        SSPluginLoader pluginLoader = new SSPluginLoader(TestUtils.pluginPath("ping.jar").toURI());
        pluginLoader.load();

        System.out.println(pluginLoader.getState());

        String rootPath = "./build/test-configs";

        for (StreamSpawnConfig config : StreamSpawnApi.getConfigs()) {
            File configFile = new File(rootPath + File.separator + config.getConfigName());
            config.readConfig(configFile);
        }

        for (SSEventGenerator eventGenerator : StreamSpawnApi.getEventGenerators()) {
            eventGenerator.start();
        }

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

    @Test
    public void testPluginDuringRuntime() {
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
