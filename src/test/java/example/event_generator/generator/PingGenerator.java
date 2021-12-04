package example.event_generator.generator;

import example.event_generator.PingPlugin;
import net.programmer.igoodie.goodies.runtime.GoodieObject;
import net.programmer.igoodie.streamspawn.StreamSpawnApi;
import net.programmer.igoodie.streamspawn.event.generator.SSEventGenerator;
import net.programmer.igoodie.streamspawn.plugin.SSPlugin;

import java.util.Timer;
import java.util.TimerTask;

public class PingGenerator extends SSEventGenerator {

    private Timer timer;

    public PingGenerator(SSPlugin plugin) {
        super(plugin);
    }

    @Override
    public void start() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                StreamSpawnApi.pushEvent("Ping", new GoodieObject());
            }
        }, 0, ((PingPlugin) this.plugin).config.getPeriod() * 1000);
    }

    @Override
    public void stop() {
        timer.purge();
        timer.cancel();
        timer = null;
    }

}
