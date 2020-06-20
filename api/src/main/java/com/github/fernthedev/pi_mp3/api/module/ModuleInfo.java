package com.github.fernthedev.pi_mp3.api.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {

    String[] authors();

    /**
     * By default, use the jar's manifest version.
     */
    String version() default "";
    String name();

    String[] depend() default {};
    String[] softDepend() default {};

    boolean includeInModuleInfoFile() default true;

}
