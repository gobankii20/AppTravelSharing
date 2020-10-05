package com.simple.travelsharing.utils

import com.google.firebase.database.FirebaseDatabase
import com.simple.travelsharing.model.ModelCreateJob
import com.simple.travelsharing.model.ModelCreateUser
import com.simple.travelsharing.model.ModelGroupChat
import com.simple.travelsharing.model.ModelParticipants


fun ModelGroupChat.createGroupChat() {
    val mRootRef = FirebaseDatabase.getInstance().reference
    val mUsersRef = mRootRef.child("groupChat")

    val modelGroupChat =
        ModelGroupChat(
            this.job_code, this.user_code,
            this.user_fullname, this.user_image,
            this.user_rule, this.chat_message,
            this.chat_date,this.chat_time
        )
    mUsersRef.push().setValue(modelGroupChat)
}

fun ModelParticipants.createJobRequest() {
    val mRootRef = FirebaseDatabase.getInstance().reference
    val mUsersRef = mRootRef.child("participants")

    val modelParticipants =
        ModelParticipants(
            this.job_code, this.user_code,
            this.user_fullname, this.user_image,
            this.user_email, this.status_request,
            this.request_date
        )
    mUsersRef.push().setValue(modelParticipants)
}

fun ModelCreateJob.createJob() {
    val mRootRef = FirebaseDatabase.getInstance().reference
    val mUsersRef = mRootRef.child("jobs")

    val modelCreateUser =
        ModelCreateJob(
            this.job_code, this.job_create,
            this.job_name, this.job_detail,
            this.job_count, this.job_date,
            this.job_place, this.job_latitude,
            this.job_longitude, this.job_status,
            this.driver_name, this.driver_image,
            this.driver_code, this.request_count, this.user_approve
        )
    mUsersRef.push().setValue(modelCreateUser)
}

fun ModelCreateUser.createUser() {
    val mRootRef = FirebaseDatabase.getInstance().reference
    val mUsersRef = mRootRef.child("users")

    val modelCreateUser =
        ModelCreateUser(
            this.user_code,
            this.user_fullname,
            this.user_phone,
            this.user_email,
            this.user_address,
            this.user_image,
            this.user_name,
            this.user_password,
            this.user_rule, this.date_request, this.status, this.image_card
        )
    mUsersRef.push().setValue(modelCreateUser)
}

fun ModelCreateUser.updateUser(): ModelCreateUser {
    val modelCreateUser = ModelCreateUser(
        this.user_code,
        this.user_fullname,
        this.user_phone,
        this.user_email,
        this.user_address,
        this.user_image,
        this.user_name,
        this.user_password,
        this.user_rule, this.date_request, this.status, this.image_card, this.status_verify
    )
    return modelCreateUser
}