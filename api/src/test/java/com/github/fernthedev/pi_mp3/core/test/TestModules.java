package com.github.fernthedev.pi_mp3.core.test;

import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.api.exceptions.ModuleAlreadyRegisteredException;
import com.github.fernthedev.pi_mp3.api.module.ModuleHandler;
import com.google.inject.Guice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestModules {

    private static void registerInjector() {
        try {
            MP3Pi.setInjector(Guice.createInjector());
        } catch (IllegalStateException ignored) {}
    }

    @DisplayName("Same class module registration exception")
    @Test
    public void testDependencySameClassFail() {
        registerInjector();
        Assertions.assertThrows(ModuleAlreadyRegisteredException.class, () -> {
            ModuleHandler.registerModule(new TestModuleClass());
            ModuleHandler.registerModule(new TestModuleClass());
        });

    }

    @DisplayName("Same name module registration exception")
    @Test
    public void testDependencySameNameFail() {
        registerInjector();
        Assertions.assertThrows(ModuleAlreadyRegisteredException.class, () -> {
            ModuleHandler.registerModule(new TestModuleClass());
            ModuleHandler.registerModule(new TestModuleSameNameClass());
        });
    }
}
