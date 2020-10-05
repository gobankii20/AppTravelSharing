package com.simple.travelsharing.model

data class ModelChat(val driverProfile:String, val messageDriver:String,
                        var messageDate:String,var messageTime:String,
                        var messageUser:String,var messageTimeUser:String,
                        var userProfile:String,val chatType:Int)