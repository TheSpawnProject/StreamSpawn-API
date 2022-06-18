package net.programmer.igoodie.streamspawn.api.generator;

import net.programmer.igoodie.streamspawn.api.plugin.SSPlugin;

public abstract class SSEventGenerator<P extends SSPlugin> {

    protected P plugin;

    public SSEventGenerator(P plugin) {
        this.plugin = plugin;
    }

    public abstract String getName();

    public abstract boolean isRunning();

    public abstract void start();

    public abstract void stop();

}
