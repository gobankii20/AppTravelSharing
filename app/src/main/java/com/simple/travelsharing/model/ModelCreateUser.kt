package com.simple.travelsharing.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ModelCreateUser(
    val user_code: String? = "",
    val user_fullname: String? = "",
    val user_phone: String? = "",
    val user_email: String? = "",
    val user_address: String? = "",
    val user_image: String? = "",
    val user_name: String? = "",
    val user_password: String? = "",
    val user_rule: String? = "",
    val date_request: String? = "",
    val status: Int? =0,
    val image_card: String? = "",
    val status_verify: Boolean? = false
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "user_code" to user_code,
            "user_fullname" to user_fullname,
            "user_phone" to user_phone,
            "user_email" to user_email,
            "user_address" to user_address,
            "user_image" to user_image,
            "user_name" to user_name,
            "user_password" to user_password,
            "user_rule" to user_rule,
            "date_request" to date_request,
            "status" to status,
            "image_card" to image_card,
            "status_verify" to status_verify
        )
    }
}