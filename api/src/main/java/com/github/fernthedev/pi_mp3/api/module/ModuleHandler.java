package com.github.fernthedev.pi_mp3.api.module;

import com.github.fernthedev.fernutils.thread.ThreadUtils;
import com.github.fernthedev.fernutils.thread.multiple.TaskInfoList;
import com.github.fernthedev.lightchat.core.ColorCode;
import com.github.fernthedev.lightchat.core.StaticHandler;
import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.api.exceptions.module.*;
import com.google.gson.Gson;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class ModuleHandler {

    private final Map<String, ClassLoader> loaders = new HashMap<>();

    private Map<Class<?>, Module> moduleMap = new HashMap<>();
    private Map<String, Module> moduleNameMap = new HashMap<>();
    private List<Pattern> jarPatterns = List.of(
            Pattern.compile(".*\\.jar")
    );
//    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();


    public Set<Module> getModuleList() {
        return new HashSet<>(moduleNameMap.values());
    }

    public TaskInfoList initializeModules() {
        Map<String, Module> loadedModules = new HashMap<>();


        TaskInfoList task = ThreadUtils.runForLoopAsync(moduleNameMap.keySet(), s -> {
            StaticHandler.getCore().getLogger().info(ColorCode.YELLOW + "Loading module {}", s);

            Module module = moduleNameMap.get(s);
            ModuleInfo moduleInfo = module.getClass().getAnnotation(ModuleInfo.class);

            List<String> missingDependencies = new ArrayList<>();

            for (String dep : moduleInfo.depend()) {
                if (!moduleNameMap.containsKey(dep)) {
                    missingDependencies.add(dep);
                }
            }

            if (!missingDependencies.isEmpty()) {
                throw new MissingDependenciesException("Missing dependencies for module " + module.getName() + " are: " + missingDependencies);
            }

            List<String> awaitDependencies = new ArrayList<>();

            awaitDependencies.addAll(Arrays.asList(moduleInfo.depend()));
            awaitDependencies.addAll(Arrays.asList(moduleInfo.softDepend()));

            StaticHandler.getCore().getLogger().debug("{} Waiting for dependencies: {}", module.getName(), awaitDependencies);


            for (String awaitDep : awaitDependencies) {
                if (awaitDep.isEmpty() || awaitDep.trim().isEmpty()) continue;

                StaticHandler.getCore().getLogger().debug("Checking for module {} await dependency {}", module.getName(), awaitDep);
                while (!loadedModules.containsKey(awaitDep)) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                }
            }

            StaticHandler.getCore().getLogger().info(ColorCode.GREEN + "Loaded module {} successfully", module.getName());
            StaticHandler.getCore().getLogger().info(ColorCode.UNDERLINE + "Starting module {}", module.getName());
            module.onEnable();
            loadedModules.put(module.getName(), module);
            StaticHandler.getCore().getLogger().info(ColorCode.GREEN + "Started module {}", module.getName());
        });



        ThreadUtils.runAsync(() -> {
            try {
                task.runThreads(MP3Pi.getInstance().getExecutorService());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
            StaticHandler.getCore().getLogger().info(ColorCode.GREEN + "Finished loading modules");
        }, Executors.newSingleThreadExecutor());

        while (task.getFuture() == null) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return task;
    }

    public ModuleInfoJSON getModuleDescription(File file) throws ModuleInvalidDescriptionException {
        Validate.notNull(file, "File cannot be null");

        JarFile jar = null;
        InputStream stream = null;

        try {
            jar = new JarFile(file);
            JarEntry entry = jar.getJarEntry(ModuleInfoJSON.FILE_NAME);

            if (entry == null) {
                throw new ModuleInvalidDescriptionException(new FileNotFoundException("Jar does not contain " + ModuleInfoJSON.FILE_NAME));
            }

            stream = jar.getInputStream(entry);

            return new Gson().fromJson(new InputStreamReader(stream), ModuleInfoJSON.class);

        } catch (IOException ex) {
            throw new ModuleInvalidDescriptionException(ex);
        } /* catch (YAMLException ex) {
            throw new InvalidDescriptionException(ex);
        }*/ finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException ignored) {}
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) {}
            }
        }
    }

    public ModuleDescription parseDescription(ModuleInfo moduleInfo, Module module) {
        String version = moduleInfo.version();

        if (version.isEmpty()) version = module.getClass().getPackage().getImplementationVersion();

        if (version == null) version = "null";

        return new ModuleDescription(moduleInfo.authors(),
                version,
                moduleInfo.name(),
                moduleInfo.depend(),
                moduleInfo.softDepend()
        );
    }

    public void registerModule(Module module) {
        if (moduleMap.containsKey(module.getClass())) {
            throw new ModuleAlreadyRegisteredException("Module " + module.getClass().getName() + " is already registered");
        }

        if (module.getClass().isAnnotationPresent(ModuleInfo.class)) {
            ModuleInfo moduleInfo = module.getClass().getAnnotation(ModuleInfo.class);
            module.setDescription(parseDescription(moduleInfo, module));

            if (moduleNameMap.containsKey(module.getName())) {
                throw new ModuleAlreadyRegisteredException("Module " + module.getClass().getName() + "'s  name " + module.getName() + " already taken by " + module.getClass().getName());
            }

            List<String> awaitDependencies = new ArrayList<>();

            awaitDependencies.addAll(Arrays.asList(moduleInfo.depend()));
            awaitDependencies.addAll(Arrays.asList(moduleInfo.softDepend()));

            if (awaitDependencies.parallelStream().anyMatch(s -> s.equals(module.getName()))) throw new ModuleException("You cannot depend on your own module " + module.getName());

        } else {
            throw new ModuleRequirementException("Class " + module.getClass().getName() + " must have a @ModuleInfo annotation.");
        }


        MP3Pi.getInjector().injectMembers(module);

        moduleMap.put(module.getClass(), module);
        moduleNameMap.put(module.getName(), module);
    }

    public void unregisterModule(@NonNull Module module) {
        module.onDisable();

        moduleMap.remove(module.getClass());
        moduleNameMap.remove(module.getName());

        ClassLoader classLoader = loaders.get(module.getName());

        if (classLoader != null) {
            if (classLoader instanceof ModuleClassLoader) {
                ModuleClassLoader loader = (ModuleClassLoader) classLoader;
                loaders.values().removeAll(Collections.singleton(loader)); // Remove only removes one element, so use removeAll

//                Set<String> names = loader.getClasses();
//
//                for (String name : names) {
//                    removeClass(name);
//                }
            }
        }
    }

//    private void removeClass(@NotNull String name) {
//        Class<?> clazz = classes.remove(name);
//    }

    public List<Module> loadModule(final File file, ClassLoader classLoader) throws ModuleException {
        Validate.notNull(file, "File cannot be null");

        if (!file.exists()) {
            throw new ModuleException(new FileNotFoundException(file.getPath() + " does not exist"));
        }

        final ModuleInfoJSON description;
        try {
            description = getModuleDescription(file);
        } catch (ModuleInvalidDescriptionException ex) {
            throw new ModuleException(ex);
        }


        final ModuleClassLoader loader;
        try {
            loader = new ModuleClassLoader(file, description, classLoader);
        } catch (ModuleException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new ModuleException(ex);
        }

        loader.getModuleList().forEach(module -> {
            loaders.put(module.getName(), loader);
            registerModule(module);
        });

        return loader.getModuleList();
    }



    public void scanDirectory(File folder, ClassLoader classLoader) {
        if (!folder.isDirectory()) throw new IllegalArgumentException("File " + folder.toPath().toString() + " is not a folder");

        for (File file : Objects.requireNonNull(
                folder.listFiles(
                        (dir, name) -> jarPatterns.parallelStream().anyMatch(pattern -> pattern.matcher(name).matches())
                )
        )) {
            loadModule(file, classLoader);
        }
    }


}
