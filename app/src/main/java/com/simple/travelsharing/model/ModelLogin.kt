package com.simple.travelsharing.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ModeLogin(
    val user_code: String? = "",
    val user_name: String? = "",
    val user_fullname: String? = "",
    val user_image: String? = "",
    val user_password: String? = "",
    val user_rule: String? = "",
    val user_email: String? = "",
    val status: Int? = 1,
    val user_phone: String? = "",
    val status_verify: Boolean? = false
)