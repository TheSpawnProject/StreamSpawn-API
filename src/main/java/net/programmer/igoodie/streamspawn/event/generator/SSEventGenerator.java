package net.programmer.igoodie.streamspawn.event.generator;

import net.programmer.igoodie.streamspawn.plugin.SSPlugin;

public abstract class SSEventGenerator {

    protected SSPlugin plugin;

    public SSEventGenerator(SSPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void start();

    public abstract void stop();

}
