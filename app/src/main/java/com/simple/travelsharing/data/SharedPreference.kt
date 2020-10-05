package com.simple.travelsharing.data

import android.content.Context
import android.content.SharedPreferences
import java.util.*


class SharedPreference(val context: Context) {
    private val PREFS_NAME = "kotlincodes"
    private val KEY_USER = "user_name"
    private val KEY_FULL_NAME = "user_fullname"
    private val KEY_IMAGE = "user_image"
    private val KEY_PHONE = "user_phone"
    private val KEY_EMAIL = "user_email"
    private val KEY_CODE = "user_code"
    private val KEY_RULE = "user_rule"

    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun savePhone(value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_PHONE, value)
        editor.apply()
    }

    fun getPhone(): String? {
        return sharedPref.getString(KEY_PHONE, null)
    }


    fun saveEmail(value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_EMAIL, value)
        editor.apply()
    }

    fun getEmail(): String? {
        return sharedPref.getString(KEY_EMAIL, null)
    }

    fun saveFullName(value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_FULL_NAME, value)
        editor.apply()
    }

    fun getFullName(): String? {
        return sharedPref.getString(KEY_FULL_NAME, null)
    }

    fun saveImage(value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_IMAGE, value)
        editor.apply()
    }

    fun getImage(): String? {
        return sharedPref.getString(KEY_IMAGE, null)
    }

    fun saveUserName(value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_USER, value)
        editor.apply()
    }

    fun getUserName(): String? {
        return sharedPref.getString(KEY_USER, null)
    }

    fun saveUserCode(value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_CODE, value)
        editor.apply()
    }

    fun getUserCode(): String? {
        return sharedPref.getString(KEY_CODE, null)
    }

    fun saveUserRule(value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_RULE, value)
        editor.apply()
    }

    fun getUserRule(): String? {
        return sharedPref.getString(KEY_RULE, null)
    }



    fun clear(){
        saveUserName("")
        saveUserCode("")
        saveUserRule("")
        saveEmail("")
        saveFullName("")
        saveImage("")
    }

}