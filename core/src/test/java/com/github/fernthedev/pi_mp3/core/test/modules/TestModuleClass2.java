package com.github.fernthedev.pi_mp3.core.test.modules;

import com.github.fernthedev.lightchat.core.StaticHandler;
import com.github.fernthedev.pi_mp3.api.module.Module;
import com.github.fernthedev.pi_mp3.api.module.ModuleInfo;

@ModuleInfo(authors = "Fernthedev", name = "TestModule2")
public class TestModuleClass2 extends Module {

    @Override
    public void onEnable() {
        StaticHandler.getCore().getLogger().info("On enable module 2");
    }
}
