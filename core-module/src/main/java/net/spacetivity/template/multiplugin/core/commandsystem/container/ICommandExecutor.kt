package net.spacetivity.template.singleplugin.commandsystem.container

interface ICommandExecutor {

    fun execute(sender: ICommandSender, args: List<String>)
    fun sendUsage(sender: ICommandSender)
    fun onTabComplete(sender: ICommandSender, args: List<String>): MutableList<String>

}