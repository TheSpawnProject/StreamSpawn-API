package example.event_generator;

import example.event_generator.configuration.PingConfig;
import example.event_generator.generator.PingGenerator;
import net.programmer.igoodie.streamspawn.api.StreamSpawnApi;
import net.programmer.igoodie.streamspawn.api.configuration.DeferredConfig;
import net.programmer.igoodie.streamspawn.api.plugin.SSPlugin;

public class PingPlugin extends SSPlugin {

    public final DeferredConfig<PingConfig> config;
    public final PingGenerator generator;

    public PingPlugin() {
        this.config = StreamSpawnApi.hookConfig(PingConfig::new);
        this.generator = StreamSpawnApi.hookEventGenerator(new PingGenerator(this));
    }

    @Override
    public void onLoaded() {}

}
