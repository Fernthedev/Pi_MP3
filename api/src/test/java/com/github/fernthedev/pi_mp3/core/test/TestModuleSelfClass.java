package com.github.fernthedev.pi_mp3.core.test;

import com.github.fernthedev.pi_mp3.api.module.Module;
import com.github.fernthedev.pi_mp3.api.module.ModuleInfo;

@ModuleInfo(name = "TestModuleSelf", depend = "TestModuleSelf")
public class TestModuleSelfClass extends Module {


}
