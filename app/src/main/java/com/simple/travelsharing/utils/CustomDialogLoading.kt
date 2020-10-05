package com.simple.travelsharing.utils

import android.app.ProgressDialog
import android.content.Context


object CustomDialogLoading {
    var progressDialog: ProgressDialog? = null

    fun newProgressDialog(context: Context){
        progressDialog = ProgressDialog(context)
    }

    fun showProgressDialog(message:String){
        progressDialog?.setMessage(message)
        progressDialog?.show()
    }

    fun dissProgressDialog(){
        progressDialog?.dismiss()
    }
}