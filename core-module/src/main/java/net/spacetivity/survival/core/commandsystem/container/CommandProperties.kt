package net.spacetivity.survival.core.commandsystem.container

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class CommandProperties(val name: String, val permission: String = "", val aliases: Array<String> = [])