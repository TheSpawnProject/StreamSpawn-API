package net.programmer.igoodie.streamspawn.api.plugin;

import net.programmer.igoodie.tsl.plugin.TSLPluginManifest;

public abstract class SSPlugin {

    private TSLPluginManifest manifest;

    public SSPlugin() {}

    public SSPlugin(TSLPluginManifest manifest) {
        this.manifest = manifest;
    }

    public TSLPluginManifest getManifest() {
        return manifest;
    }

    public void onLoaded() {};

    public void onInitialized() {};

}
