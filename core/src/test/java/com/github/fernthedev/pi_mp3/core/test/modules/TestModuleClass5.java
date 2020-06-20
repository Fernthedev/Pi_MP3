package com.github.fernthedev.pi_mp3.core.test.modules;

import com.github.fernthedev.lightchat.core.StaticHandler;
import com.github.fernthedev.pi_mp3.api.module.Module;
import com.github.fernthedev.pi_mp3.api.module.ModuleInfo;

@ModuleInfo(authors = "Fernthedev", name = "TestModule5", depend = {"TestModule1", "TestModule3"})
public class TestModuleClass5 extends Module {

    @Override
    public void onEnable() {
        StaticHandler.getCore().getLogger().info("On enable module 5");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StaticHandler.getCore().getLogger().info("On enable module 5");

    }
}
