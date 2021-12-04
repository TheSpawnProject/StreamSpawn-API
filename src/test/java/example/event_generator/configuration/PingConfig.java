package example.event_generator.configuration;

import net.programmer.igoodie.streamspawn.configuration.StreamSpawnConfig;
import net.programmer.igoodie.configuration.validation.annotation.GoodieLong;
import net.programmer.igoodie.serialization.annotation.Goodie;

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
