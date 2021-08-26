package org.chatbox.redis

import com.google.gson.JsonObject
import lombok.SneakyThrows
import org.chatbox.ChatBoxApplication
import org.chatbox.ChatBoxConstants
import org.chatbox.client.ClientData
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import java.util.*
import java.util.concurrent.CompletableFuture

object RedisHandler {

    private val publishJedisPool: JedisPool = JedisPool(
        ChatBoxConstants.REDIS_HOST,
        ChatBoxConstants.REDIS_PORT
    )

    fun subscribe(clientData: ClientData) {
        val subscribeJedisPool = JedisPool(
            ChatBoxConstants.REDIS_HOST,
            ChatBoxConstants.REDIS_PORT
        )

        runCommand(subscribeJedisPool) {
            CompletableFuture.runAsync {
                it.subscribe(RedisSubscriber(), "messages")
                it.connect()
            }

            updateDisplayName(clientData, clientData.username)
        }
    }

    fun updateDisplayName(clientData: ClientData, newDisplayName: String) {
        runCommand(publishJedisPool) {
            clientData.username = newDisplayName
            clientData.json = ChatBoxConstants.GSON.toJson(clientData)

            it.hset(
                ChatBoxConstants.REDIS_CACHE_ONLINE,
                clientData.username,
                "${System.currentTimeMillis()}"
            )
        }
    }

    fun unsubscribe(clientData: ClientData) {
        runCommand(publishJedisPool) {
            it.hdel(
                ChatBoxConstants.REDIS_CACHE_ONLINE,
                clientData.username
            )
        }

        publishJedisPool.close()
    }

    fun publishMessage(message: String, clientData: ClientData) {
        runCommand(publishJedisPool) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("client", clientData.json)
            jsonObject.addProperty("message", message)
            jsonObject.addProperty("date", System.currentTimeMillis())

            it.publish("messages", jsonObject.toString())
        }
    }

    @SneakyThrows
    fun runCommand(jedisPool: JedisPool, check: (t: Jedis) -> Any) {
        jedisPool.resource.use {
            ChatBoxConstants.REDIS_PASSWORD.ifPresent { password ->
                it.auth(password)
            }

            check.invoke(it)
        }
    }
}
