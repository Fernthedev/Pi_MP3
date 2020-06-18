package com.github.fernthedev.pi_mp3.api.module;

import com.github.fernthedev.fernutils.thread.ThreadUtils;
import com.github.fernthedev.fernutils.thread.multiple.TaskInfoList;
import com.github.fernthedev.lightchat.core.ColorCode;
import com.github.fernthedev.lightchat.core.StaticHandler;
import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.api.exceptions.MissingDependenciesException;
import com.github.fernthedev.pi_mp3.api.exceptions.ModuleAlreadyRegisteredException;
import com.github.fernthedev.pi_mp3.api.exceptions.ModuleRequirementException;
import lombok.NonNull;

import java.util.*;

public class ModuleHandler {

    private static Map<Class<?>, Module> moduleMap = new HashMap<>();
    private static Map<String, Module> moduleNameMap = new HashMap<>();


    public static void registerModule(Module module) {
        if (moduleMap.containsKey(module.getClass())) {
            throw new ModuleAlreadyRegisteredException("Module " + module.getClass().getName() + " is already registered");
        }



        if (module.getClass().isAnnotationPresent(ModuleInfo.class)) {
            ModuleInfo moduleInfo = module.getClass().getAnnotation(ModuleInfo.class);
            module.setName(moduleInfo.name());

            if (moduleNameMap.containsKey(module.getName())) {
                throw new ModuleAlreadyRegisteredException("Module " + module.getClass().getName() + "'s  name " + module.getName() + " already taken by " + module.getClass().getName());
            }
        } else {
            throw new ModuleRequirementException("Class " + module.getClass().getName() + " must have a @ModuleInfo annotation.");
        }


        MP3Pi.getInjector().injectMembers(module);

        moduleMap.put(module.getClass(), module);
        moduleNameMap.put(module.getName(), module);
    }

    public static void unregisterModule(@NonNull Module module) {
        moduleMap.remove(module.getClass());
        moduleNameMap.remove(module.getName());
    }

    public static void initializeModules() {
        Map<String, Module> loadedModules = new HashMap<>();



        TaskInfoList task;

        task = ThreadUtils.runForLoopAsync(moduleNameMap.keySet(), s -> {
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


            StaticHandler.getCore().getLogger().info(ColorCode.GREEN + "Loaded module {} sucessfully", module.getName());
        });

        try {
            task.runThreads(ThreadUtils.ThreadExecutors.CACHED_THREADS.getExecutorService());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

}
