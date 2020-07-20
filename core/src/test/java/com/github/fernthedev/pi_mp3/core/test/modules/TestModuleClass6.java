package com.github.fernthedev.pi_mp3.core.test.modules;

import com.github.fernthedev.lightchat.core.StaticHandler;
import com.github.fernthedev.modules.Module;
import com.github.fernthedev.modules.ModuleInfo;

@ModuleInfo(authors = "Fernthedev", name = "TestModule6", depend = {"TestModule1", "TestModule3", "TestModule5"})
public class TestModuleClass6 extends Module {

    @Override
    public void onEnable() {
        StaticHandler.getCore().getLogger().info("On enable module 6");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StaticHandler.getCore().getLogger().info("On enable module 4 finish");
    }
}
