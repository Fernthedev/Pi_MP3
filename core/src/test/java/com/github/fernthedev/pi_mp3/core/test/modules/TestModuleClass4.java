package com.github.fernthedev.pi_mp3.core.test.modules;

import com.github.fernthedev.lightchat.core.StaticHandler;
import com.github.fernthedev.pi_mp3.api.module.Module;
import com.github.fernthedev.pi_mp3.api.module.ModuleInfo;

@ModuleInfo(authors = "Fernthedev", name = "TestModule4")
public class TestModuleClass4 extends Module {

    @Override
    public void onEnable() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StaticHandler.getCore().getLogger().info("On enable module 4");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("On enable module 4 finish");
    }
}
