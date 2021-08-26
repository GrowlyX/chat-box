package org.chatbox

import org.chatbox.client.ClientData
import org.chatbox.redis.RedisHandler
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.system.exitProcess

object ChatBoxApplication {

    private lateinit var clientData: ClientData
    private lateinit var redisHandler: RedisHandler

    @JvmStatic
    fun main(args: Array<String>) {
        val reader = BufferedReader(
            InputStreamReader(System.`in`)
        )

        val name = fetchUsername(reader)
        println("You've set your username to: $name")

        clientData = ClientData(name)

        redisHandler = RedisHandler
        redisHandler.subscribe(clientData)

        val printingHook = Thread {
            println("Disconnecting from the chat, goodbye!")

            redisHandler.unsubscribe(clientData)
        }

        Runtime.getRuntime().addShutdownHook(printingHook)

        checkForMessages(reader)
    }

    @JvmStatic
    private fun fetchUsername(reader: BufferedReader): String {
        println("What username do you want to use?")

        return reader.readLine()
    }

    @JvmStatic
    private fun checkForMessages(reader: BufferedReader) {
        val newMessage = reader.readLine()

        if (newMessage.startsWith("/")) {
            handleCommand(newMessage.removePrefix("/"))
        } else {
            redisHandler.publishMessage(newMessage, clientData)
        }

        checkForMessages(reader)
    }

    @JvmStatic
    private fun handleCommand(message: String) {
        val args = message.split(" ")

        when (args[0]) {
            "help" -> {
                println("Showing all commands:")

                ChatBoxConstants.COMMANDS.forEach {
                    println(it)
                }
            }
            "changename" -> {
                if (args.size != 2) {
                    println("Usage: /changename <username>")
                    return
                }

                val newName = args[1]

                if (newName.isEmpty() || newName.isBlank()) {
                    println("That's not a valid username, sorry.")
                    return
                }

                redisHandler.updateDisplayName(clientData, newName)

                println("You've changed your display name to \"$newName\"")
            }
            "leave" -> {
                exitProcess(1)
            }
            else -> {
                println("Unknown command. Type \"/help\" for help.")
            }
        }
    }
}
