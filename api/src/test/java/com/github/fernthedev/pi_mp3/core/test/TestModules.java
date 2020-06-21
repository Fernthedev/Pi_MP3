package com.github.fernthedev.pi_mp3.core.test;

import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.api.exceptions.module.ModuleAlreadyRegisteredException;
import com.github.fernthedev.pi_mp3.api.exceptions.module.ModuleException;
import com.github.fernthedev.pi_mp3.api.module.ModuleHandler;
import com.google.inject.Guice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestModules {

    private static ModuleHandler moduleHandler;

    private static void registerInjector() {
        try {
            if (moduleHandler == null) moduleHandler = new ModuleHandler();

            MP3Pi.setInjector(Guice.createInjector());
        } catch (IllegalStateException ignored) {}
    }

    @DisplayName("Same class module registration exception")
    @Test
    public void testDependencySameClassFail() {
        registerInjector();
        Assertions.assertThrows(ModuleAlreadyRegisteredException.class, () -> {
            moduleHandler.registerModule(new TestModuleClass());
            moduleHandler.registerModule(new TestModuleClass());
        });

    }

    @DisplayName("Same name module registration exception")
    @Test
    public void testDependencySameNameFail() {
        registerInjector();
        Assertions.assertThrows(ModuleAlreadyRegisteredException.class, () -> {
            moduleHandler.registerModule(new TestModuleClass());
            moduleHandler.registerModule(new TestModuleSameNameClass());
        });
    }

    @DisplayName("Same self dependency module registration exception")
    @Test
    public void testDependencySelfDependFail() {
        registerInjector();
        Assertions.assertThrows(ModuleException.class, () -> {
            moduleHandler.registerModule(new TestModuleSelfClass());
            moduleHandler.initializeModules();
        });
    }


}
