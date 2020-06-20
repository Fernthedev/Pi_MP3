package com.github.fernthedev.pi_mp3.core.test.modules;

import com.github.fernthedev.lightchat.core.StaticHandler;
import com.github.fernthedev.pi_mp3.api.module.Module;
import com.github.fernthedev.pi_mp3.api.module.ModuleInfo;

@ModuleInfo(name = "TestModule3")
public class TestModuleClass3 extends Module {

    @Override
    public void onEnable() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StaticHandler.getCore().getLogger().info("On enable module 3");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StaticHandler.getCore().getLogger().info("On enable module 3 finish");
    }
}
