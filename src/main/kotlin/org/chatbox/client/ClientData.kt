package org.chatbox.client

import org.chatbox.ChatBoxConstants

class ClientData(
    var username: String,
) {
    var json: String = ChatBoxConstants.GSON.toJson(this)
}
