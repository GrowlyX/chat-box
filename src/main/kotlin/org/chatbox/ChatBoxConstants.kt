package org.chatbox

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.util.*

object ChatBoxConstants {

    @JvmStatic
    val REDIS_HOST: String = "localhost"

    @JvmStatic
    val REDIS_PORT: Int = 6379

    @JvmStatic
    val REDIS_PASSWORD: Optional<String> = Optional.ofNullable(null)

    @JvmStatic
    val REDIS_CACHE_ONLINE: String = "chat_box_online"

    @JvmStatic
    val GSON: Gson = GsonBuilder().create()

    @JvmStatic
    val FORMAT: SimpleDateFormat = SimpleDateFormat("HH:mma")

    @JvmStatic
    val COMMANDS: List<String> = listOf(
        "help - shows this help page",
        "changename - changes your client display name",
        "leave - disconnect from the chat box"
    )

}
