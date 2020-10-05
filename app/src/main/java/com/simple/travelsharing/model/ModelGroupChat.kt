package com.simple.travelsharing.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class ModelGroupChat(
    val job_code: String? = "",
    val user_code: String? = "",
    val user_fullname: String? = "",
    val user_image: String? = "",
    val user_rule: String? = "",
    val chat_message: String? = "",
    val chat_date: String? = "",
    val chat_time: String? = "",
    var chatType: Int? = 0
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "job_code" to job_code,
            "user_code" to user_code,
            "user_fullname" to user_fullname,
            "user_image" to user_image,
            "user_rule" to user_rule,
            "chat_message" to chat_message,
            "chat_date" to chat_date,
            "chat_time" to chat_time
        )
    }
}