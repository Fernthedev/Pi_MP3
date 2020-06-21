package com.github.fernthedev.pi_mp3.core.test;

import com.github.fernthedev.pi_mp3.core.MP3Server;
import com.github.fernthedev.pi_mp3.core.test.modules.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class ModuleLoadingTest {


    @DisplayName("Asynchronous module registration")
    @Test
    public void testAsyncModuleRegistration() {
        Assertions.assertTimeout(Duration.ofSeconds(180), () -> MP3Server.testModules(new String[0],
                new TestModuleClass1(),
                new TestModuleClass2(),
                new TestModuleClass3(),
                new TestModuleClass4(),
                new TestModuleClass5(),
                new TestModuleClass6()
        ));


//        Assertions.assertDoesNotThrow(() -> {
//        ModuleHandler.registerModule(new TestModuleClass1());
//        ModuleHandler.registerModule(new TestModuleClass2());
//        ModuleHandler.registerModule(new TestModuleClass3());
//        ModuleHandler.registerModule(new TestModuleClass4());
//        ModuleHandler.registerModule(new TestModuleClass5());
//        ModuleHandler.registerModule(new TestModuleClass6());
//        ModuleHandler.initializeModules();
//        });
    }

}
