package net.spacetivity.survival.core.translation.serialization

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.translation.TextSendFunction
import net.spacetivity.survival.core.translation.TranslatableText
import org.bukkit.Sound
import kotlin.properties.Delegates

class TranslatableTextTypeAdapter(val plugin: SpaceSurvivalPlugin) : TypeAdapter<TranslatableText>() {

    override fun write(writer: JsonWriter?, translatableText: TranslatableText?) {
        writer!!.beginObject()

        writer.name("key")
        writer.value(translatableText!!.key)

        writer.name("sendFunction")
        writer.value(translatableText.sendFunction.name.uppercase())

        writer.name("playSoundOnTrigger")
        writer.value(translatableText.playSoundOnTrigger)

        writer.name("sound")
        writer.value(translatableText.sound?.name?.uppercase())

        writer.name("text")
        writer.value(translatableText.text)

        writer.endObject()
    }

    override fun read(reader: JsonReader?): TranslatableText {
        lateinit var key: String
        lateinit var sendFunction: TextSendFunction
        var playSoundOnTrigger by Delegates.notNull<Boolean>()
        var sound: Sound? = null
        lateinit var text: String

        reader?.beginObject()

        var fieldName: String? = null

        while (reader!!.hasNext()) {
            val token: JsonToken = reader.peek()

            if (token == JsonToken.NAME) fieldName = reader.nextName()

            if (fieldName == "key") {
                reader.peek()
                key = reader.nextString()
            }

            if (fieldName == "sendFunction") {
                reader.peek()
                sendFunction = TextSendFunction.valueOf(reader.nextString().uppercase())
            }

            if (fieldName == "playSoundOnTrigger") {
                reader.peek()
                playSoundOnTrigger = reader.nextBoolean()
            }

            if (fieldName == "sound") {
                reader.peek()
                sound = Sound.valueOf(reader.nextString())
            }

            if (fieldName == "text") {
                reader.peek()
                text = reader.nextString()
            }
        }

        reader.endObject()
        return TranslatableText(plugin, key, sendFunction, playSoundOnTrigger, sound, text)
    }

}