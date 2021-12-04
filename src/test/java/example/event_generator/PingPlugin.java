package example.event_generator;

import example.event_generator.configuration.PingConfig;
import example.event_generator.generator.PingGenerator;
import net.programmer.igoodie.streamspawn.StreamSpawnApi;
import net.programmer.igoodie.streamspawn.plugin.SSPlugin;

public class PingPlugin extends SSPlugin {

    public final PingConfig config;
    public final PingGenerator generator;

    public PingPlugin() {
        this.config = StreamSpawnApi.hookConfig(new PingConfig());
        this.generator = StreamSpawnApi.hookEventGenerator(new PingGenerator(this));
    }

    @Override
    public void onLoaded() {}

}
