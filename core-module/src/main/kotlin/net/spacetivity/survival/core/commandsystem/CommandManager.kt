package net.spacetivity.survival.core.commandsystem

import net.spacetivity.survival.core.commandsystem.container.CommandProperties
import net.spacetivity.survival.core.commandsystem.container.ICommandExecutor

class CommandManager {

    private val commandExecutors: HashMap<String, ICommandExecutor?> = HashMap()
    private val commands: MutableList<CommandProperties> = ArrayList()

    fun getCommands(): List<CommandProperties> {
        return commands
    }

    fun registerCommand(commandExecutor: ICommandExecutor?): CommandProperties? {
        val command: CommandProperties =
            commandExecutor!!.javaClass.getAnnotation(CommandProperties::class.java) ?: return null

        if (!(command.permission.isEmpty() || command.permission.isBlank()))
            commandExecutors[command.name] = commandExecutor

        commands.add(command)
        return command
    }

    fun getCommandExecutor(name: String?): ICommandExecutor? {
        return commandExecutors.values.stream().filter { handler: ICommandExecutor? ->
            val properties = handler?.javaClass?.getAnnotation(CommandProperties::class.java)
            handler!!.javaClass.getAnnotation(CommandProperties::class.java) != null && name!!.isNotEmpty() && (properties?.name == name || properties?.aliases?.contains(
                name
            ) ?: false)
        }.findFirst().orElse(null)
    }
}