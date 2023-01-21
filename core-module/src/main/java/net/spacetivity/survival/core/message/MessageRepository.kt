package net.spacetivity.survival.core.message

import net.spacetivity.survival.core.SpaceSurvivalPlugin

class MessageRepository(plugin: SpaceSurvivalPlugin) {

    private val cachedMessages: MutableMap<String, Message> = mutableMapOf()

    fun cacheMessage(message: Message) = cachedMessages.putIfAbsent(message.key, message)
    fun removeCachedMessage(key: String) = cachedMessages.remove(key)
    fun getCachedMessage(key: String): Message = cachedMessages[key]!!
    fun getCachedMessages(): MutableCollection<Message> = cachedMessages.values

}