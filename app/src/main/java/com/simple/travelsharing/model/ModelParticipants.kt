package com.simple.travelsharing.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class ModelParticipants(
    val job_code: String? = "",
    val user_code: String? = "",
    val user_fullname: String? = "",
    val user_image: String? = "",
    val user_email: String? = "",
    val status_request: String? = "",
    val request_date: String? = ""
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "job_code" to job_code,
            "user_code" to user_code,
            "user_fullname" to user_fullname,
            "user_image" to user_image,
            "user_email" to user_email,
            "status_request" to status_request,
            "request_date" to request_date
        )
    }
}