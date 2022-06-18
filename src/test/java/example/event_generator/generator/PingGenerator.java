package example.event_generator.generator;

import example.event_generator.PingPlugin;
import net.programmer.igoodie.goodies.runtime.GoodieObject;
import net.programmer.igoodie.streamspawn.StreamSpawnApi;
import net.programmer.igoodie.streamspawn.event.generator.SSEventGenerator;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PingGenerator extends SSEventGenerator<PingPlugin> {

    private Timer timer;

    public PingGenerator(PingPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "Ping Generator";
    }

    @Override
    public boolean isRunning() {
        return timer != null;
    }

    @Override
    public void start() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                StreamSpawnApi.pushEvent("Ping", new GoodieObject());
                if (new Random().nextBoolean()) {
                    System.out.println("Decided to randomly stop.");
                    StreamSpawnApi.stopAllEventGenerators();
                }
            }
        }, 0, this.plugin.config.get().getPeriod() * 1000);
    }

    @Override
    public void stop() {
        if (timer == null) return;
        timer.purge();
        timer.cancel();
        timer = null;
    }

}
