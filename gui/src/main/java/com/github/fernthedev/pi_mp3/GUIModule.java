package com.github.fernthedev.pi_mp3;

import com.github.fernthedev.fernutils.thread.ThreadUtils;
import com.github.fernthedev.lightchat.core.StaticHandler;
import com.github.fernthedev.pi_mp3.api.module.Module;
import com.github.fernthedev.pi_mp3.api.module.ModuleInfo;

import java.util.concurrent.Executors;

@ModuleInfo(name = "GUIModule")
public class GUIModule extends Module {




    @Override
    public void onEnable() {
        StaticHandler.getCore().getLogger().info("GUI Module initialized ");
        ThreadUtils.runAsync(HelloFX::launchWindow, Executors.newSingleThreadExecutor());

    }
}
