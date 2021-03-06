package example.event_generator.configuration;

import net.programmer.igoodie.goodies.configuration.validation.annotation.GoodieLong;
import net.programmer.igoodie.goodies.serialization.annotation.Goodie;
import net.programmer.igoodie.streamspawn.api.configuration.StreamSpawnConfig;

public class PingConfig extends StreamSpawnConfig {

    @Goodie
    @GoodieLong(min = 1)
    long period = 1;

    @Override
    public String getConfigName() {
        return "ping.json";
    }

    public long getPeriod() {
        return period;
    }

}
