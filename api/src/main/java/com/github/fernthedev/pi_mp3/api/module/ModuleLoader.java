package com.github.fernthedev.pi_mp3.api.module;

import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.api.exceptions.ModuleRequirementException;
import org.reflections.Reflections;

import java.util.Set;

public class ModuleLoader {



    public static void scanPackage(Package pack) {
        Set<Class<? extends Module>> classes = new Reflections(pack).getSubTypesOf(Module.class);
        for (Class<? extends Module> modClass : classes) {
            scanClass(modClass);
        }
    }

    public static void scanClass(Class<?> clazz) {
        if (clazz.isAnnotationPresent(ModuleInfo.class) && Module.class.isAssignableFrom(clazz)) {
            //                Module module = clazz.asSubclass(Module.class).getConstructor().newInstance();
            Module module = MP3Pi.getInjector().getInstance(clazz.asSubclass(Module.class));


            ModuleHandler.registerModule(module);
        } else {
            throw new ModuleRequirementException("Class " + clazz.getName() + " must have a @ModuleInfo annotation.");
        }
    }

}
