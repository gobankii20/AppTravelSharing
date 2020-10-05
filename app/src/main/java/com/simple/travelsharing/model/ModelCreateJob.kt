package com.simple.travelsharing.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ModelCreateJobDetail(
    val job_code: String? = "",
    val job_create: String? = "",
    val job_name: String? = "",
    val job_detail: String? = "",
    val job_count: String? = "",
    val job_date: String? = "",
    val job_place: String? = "",
    val job_latitude: String? = "",
    val job_longitude: String? = "",
    val job_status: String? = "",
    val driver_name: String? = "",
    val driver_image: String? = "",
    val driver_code: String? = "",
    val request_count: Int? = 0
): Parcelable

@IgnoreExtraProperties
data class ModelCreateJob(
    val job_code: String? = "",
    val job_create: String? = "",
    val job_name: String? = "",
    val job_detail: String? = "",
    val job_count: String? = "",
    val job_date: String? = "",
    val job_place: String? = "",
    val job_latitude: String? = "",
    val job_longitude: String? = "",
    val job_status: String? = "",
    val driver_name: String? = "",
    val driver_image: String? = "",
    val driver_code: String? = "",
    val request_count: Int? = 0,
    val user_approve:Int? = 0
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "job_code" to job_code,
            "job_create" to job_create,
            "job_name" to job_name,
            "job_detail" to job_detail,
            "job_count" to job_count,
            "job_date" to job_date,
            "job_place" to job_place,
            "job_latitude" to job_latitude,
            "job_longitude" to job_longitude,
            "job_status" to job_status,
            "driver_name" to driver_name,
            "driver_image" to driver_image,
            "driver_code" to driver_code,
            "request_count" to request_count,
            "user_approve" to user_approve
        )
    }
}