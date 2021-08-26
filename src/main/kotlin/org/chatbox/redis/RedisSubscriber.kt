package org.chatbox.redis

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.chatbox.ChatBoxConstants
import org.chatbox.client.ClientData
import redis.clients.jedis.JedisPubSub
import java.util.*

class RedisSubscriber : JedisPubSub() {

    override fun onMessage(channel: String?, message: String?) {
        val jsonObject = JsonParser.parseString(message) as JsonObject

        val clientMessage = jsonObject.get("message").asString
        val clientDataJson = jsonObject.get("client").asString
        val messageSentDate = jsonObject.get("date").asLong

        val clientData = ChatBoxConstants.GSON.fromJson(
            clientDataJson,
            ClientData::class.java
        )

        println("${clientData.username} (${ChatBoxConstants.FORMAT.format(Date(messageSentDate))}): $clientMessage")
    }
}
