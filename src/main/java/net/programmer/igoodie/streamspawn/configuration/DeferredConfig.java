package net.programmer.igoodie.streamspawn.configuration;

import java.util.function.Supplier;

public class DeferredConfig<C extends StreamSpawnConfig> {

    C config;
    Supplier<C> generator;

    public DeferredConfig(Supplier<C> generator) {
        this.generator = generator;
        this.config = generator.get();
    }

    public C get() {
        return config;
    }

    public void reconstruct() {
        config = generator.get();
    }

}
