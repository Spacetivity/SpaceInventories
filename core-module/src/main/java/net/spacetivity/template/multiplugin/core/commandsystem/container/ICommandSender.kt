package net.spacetivity.template.singleplugin.commandsystem.container

interface ICommandSender {

    val isPlayer: Boolean
    fun <P> castTo(clazz: Class<P>): P
    fun sendMessage(message: String)
    fun sendMessages(message: Array<String>)
    fun hasPermissions(permission: String): Boolean

}