package net.programmer.igoodie.streamspawn.api.plugin;

import com.vdurmont.semver4j.SemverException;
import net.programmer.igoodie.goodies.util.ReflectionUtilities;
import net.programmer.igoodie.tsl.TheSpawnLanguage;
import net.programmer.igoodie.tsl.exception.TSLPluginLoadingException;
import net.programmer.igoodie.tsl.plugin.TSLPluginLoader;
import net.programmer.igoodie.tsl.plugin.TSLPluginManifest;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class SSPluginLoader {

    private static final Map<String, Class<?>> ALL_LOADED_CLASSES = new HashMap<>();

    private final URI uri;
    private SSPlugin loadedPlugin;

    private final Set<Class<?>> loadedClasses = new HashSet<>();
    private TSLPluginLoader.State state = TSLPluginLoader.State.PRISTINE;
    private Throwable failCause;
    private TSLPluginManifest pluginManifest;

    public SSPluginLoader(File file) {
        this(file.toURI());
    }

    public SSPluginLoader(URI uri) {
        this.uri = uri;
    }

    public TSLPluginLoader.State getState() {
        return state;
    }

    public Throwable getFailCause() {
        return failCause;
    }

    public Set<Class<?>> getLoadedClasses() {
        return loadedClasses;
    }

    public SSPlugin getLoadedPlugin() {
        return loadedPlugin;
    }

    public void load() {
        try {
            state = TSLPluginLoader.State.LOADING;

            loadJAR();
            this.loadedPlugin = loadPlugin();

            state = TSLPluginLoader.State.SUCCESS;

        } catch (TSLPluginLoadingException e) {
            e.printStackTrace();
            failCause = e;
            state = TSLPluginLoader.State.FAIL;
        }
    }

    private void loadJAR() throws TSLPluginLoadingException {
        JarFile jarFile = getJarFile();

        checkForConflicts(jarFile);
        checkManifestIntegrity(jarFile);

        pluginManifest = new TSLPluginManifest(getJarManifestAttrs(jarFile));

        Enumeration<JarEntry> entries = jarFile.entries();

        URLClassLoader classLoader = getURLClassLoader();

        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();

            if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
                continue;
            }

            try {
                String className = jarEntry.getName()
                        .substring(0, jarEntry.getName().length() - 6)
                        .replace('/', '.');
                Class<?> loadedClass = classLoader.loadClass(className);
                ALL_LOADED_CLASSES.put(className, loadedClass);
                loadedClasses.add(loadedClass);

            } catch (ClassNotFoundException e) {
                throw new InternalError();
            }
        }
    }

    private SSPlugin loadPlugin() throws TSLPluginLoadingException {
        List<Class<? extends SSPlugin>> pluginClasses = getPluginClasses();

        if (pluginClasses.size() == 0) {
            throw new TSLPluginLoadingException("JAR does not contain a plugin.", uri.getPath());
        }

        if (pluginClasses.size() > 1) {
            throw new TSLPluginLoadingException("Plugin JAR MUST not contain multiple plugin classes", uri.getPath());
        }

        checkTargetVersionIntegrity(pluginManifest);

        try {
            Class<? extends SSPlugin> pluginClass = pluginClasses.get(0);
            SSPlugin plugin = ReflectionUtilities.createNullaryInstance(pluginClass);
            Field manifestField = SSPlugin.class.getDeclaredField("manifest");
            ReflectionUtilities.setValue(plugin, manifestField, pluginManifest);
            plugin.onLoaded();
            return plugin;

        } catch (InstantiationException e) {
            throw new TSLPluginLoadingException("Failed to instantiate the Plugin", e, uri.getPath());

        } catch (IllegalAccessException e) {
            throw new TSLPluginLoadingException("Plugin's nullary constructor MUST be accessible", uri.getPath());

        } catch (NoSuchFieldException e) {
            throw new InternalError();
        }
    }

    /* ------------------------ */

    private JarFile getJarFile() {
        try {
            return new JarFile(new File(uri));

        } catch (IOException e) {
            throw new TSLPluginLoadingException("IOException raised", e, uri.getPath());
        }
    }

    private URLClassLoader getURLClassLoader() {
        try {
            URL[] urls = new URL[]{new URL("jar:file:" + uri.toString() + "!/")};
            ClassLoader currentClassLoader = this.getClass().getClassLoader();
            return URLClassLoader.newInstance(urls, currentClassLoader);

        } catch (MalformedURLException e) {
            throw new TSLPluginLoadingException("Malformed URL", e, uri.getPath());
        }
    }

    private Attributes getJarManifestAttrs(JarFile jarFile) {
        try {
            Manifest manifest = jarFile.getManifest();
            return manifest.getMainAttributes();

        } catch (IOException e) {
            throw new TSLPluginLoadingException("", e, uri.getPath());
        }
    }

    private List<Class<? extends SSPlugin>> getPluginClasses() {
        List<Class<? extends SSPlugin>> classes = new LinkedList<>();
        for (Class<?> loadedClass : loadedClasses) {
            if (SSPlugin.class.isAssignableFrom(loadedClass)) {
                @SuppressWarnings("unchecked")
                Class<? extends SSPlugin> loadedPluginClass = (Class<? extends SSPlugin>) loadedClass;
                classes.add(loadedPluginClass);
            }
        }
        return classes;
    }

    private void checkForConflicts(JarFile jarFile) {
        traverseClassNames(jarFile, className -> {
            if (ALL_LOADED_CLASSES.containsKey(className)) {
                throw new TSLPluginLoadingException("Confliction detected! Class already loaded in -> " + className, jarFile.getName());
            }
        });
    }

    private void checkManifestIntegrity(JarFile jarFile) {
        Attributes manifestAttrs = getJarManifestAttrs(jarFile);

        if (manifestAttrs.getValue(TSLPluginManifest.ATTR_PLUGIN_ID) == null) {
            throw new TSLPluginLoadingException("Plugin manifest MUST have " + TSLPluginManifest.ATTR_PLUGIN_ID, uri.getPath());
        }
        if (manifestAttrs.getValue(TSLPluginManifest.ATTR_PLUGIN_NAME) == null) {
            throw new TSLPluginLoadingException("Plugin manifest MUST have " + TSLPluginManifest.ATTR_PLUGIN_NAME, uri.getPath());
        }
        if (manifestAttrs.getValue(TSLPluginManifest.ATTR_PLUGIN_VERSION) == null) {
            throw new TSLPluginLoadingException("Plugin manifest MUST have " + TSLPluginManifest.ATTR_PLUGIN_VERSION, uri.getPath());
        }
        if (manifestAttrs.getValue(TSLPluginManifest.ATTR_VERSION_TARGET) == null) {
            throw new TSLPluginLoadingException("Plugin manifest MUST have " + TSLPluginManifest.ATTR_VERSION_TARGET, uri.getPath());
        }
    }

    private void checkTargetVersionIntegrity(TSLPluginManifest manifest) {
        try {
            if (!TheSpawnLanguage.TSL_SEMVER.satisfies(manifest.getTargetVersion())) {
                String message = String.format("Plugin does not fit this version on TSL. (TSL Version: %s, Plugin Target: %s)",
                        TheSpawnLanguage.TSL_VERSION, manifest.getTargetVersion());
                throw new TSLPluginLoadingException(message, uri.getPath());
            }

        } catch (SemverException e) {
            throw new TSLPluginLoadingException("Malformed TSL version target -> " + manifest.getTargetVersion(), e, uri.getPath());
        }
    }

    private void traverseClassNames(JarFile jarFile, Consumer<String> consumer) {
        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();

            if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
                continue;
            }

            String className = jarEntry.getName()
                    .substring(0, jarEntry.getName().length() - 6)
                    .replace('/', '.');
            consumer.accept(className);
        }
    }

}
