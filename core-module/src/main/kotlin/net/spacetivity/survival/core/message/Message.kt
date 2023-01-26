package net.spacetivity.survival.core.message

data class Message(val key: String, val type: ReceiveType, val content: String)