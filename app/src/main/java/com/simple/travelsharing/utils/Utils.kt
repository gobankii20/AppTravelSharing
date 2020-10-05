package com.simple.travelsharing.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.simple.travelsharing.R
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


object Utils {

    @SuppressLint("CheckResult")
    fun setImageView(context: Context, url:String, view: ImageView){
        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.default_pic)
        requestOptions.error(R.mipmap.ic_launcher_round)
        requestOptions.diskCacheStrategy

        Glide.with(context)
            .load(convertBase64ToImage(url))
            .apply(requestOptions)
            .into(view)
    }

    fun convertBase64ToImage(base64String: String): Bitmap {
        val decodedString =
            Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }


    fun encodeImageToBase64(context: Context, imageUri: Uri): String {
        val input = context.contentResolver.openInputStream(imageUri)
        val image = BitmapFactory.decodeStream(input , null, null)

        // Encode image to base64 string
        val baos = ByteArrayOutputStream()
        image!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }


    fun getDateCurrent(): String {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate: String = df.format(c)
        return formattedDate
    }

    fun getDateTimeCurrent(): String {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val formattedDate: String = df.format(c)
        return formattedDate
    }

    fun getDateTime(): String {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formattedDate: String = df.format(c)
        return formattedDate
    }

    fun getRandomId(): String {
        val min = 0
        val max = 999
        val random = Random().nextInt(max - min + 1) + min
        return random.toString()
    }

    fun dialogConfirm(context: Context,message: String, onClick: (Boolean) -> Unit?) {
        val dialog = android.app.AlertDialog.Builder(context) // set message, title, and icon
            .setTitle("ยืนยันการทำรายการ")
            .setMessage("คุณต้องการ ${message} หรือไม่?")
            .setIcon(R.mipmap.ic_launcher_round)
            .setPositiveButton(
                "ตกลง"
            ) { dialog, whichButton -> //your deleting code
                dialog.dismiss()
                onClick.invoke(true)
            }
            .setNegativeButton(
                "ยกเลิก"
            ) { dialog, which -> dialog.dismiss() }
            .create()
        dialog.show()
    }

    fun dialogConfirmExit(context: Context, onClick: (Boolean) -> Unit?) {
        val dialog = android.app.AlertDialog.Builder(context) // set message, title, and icon
            .setTitle("ออกจากระบบ")
            .setMessage("คุณต้องการออกจากระบบหรือไม่?")
            .setIcon(R.mipmap.ic_launcher_round)
            .setPositiveButton(
                "ตกลง"
            ) { dialog, whichButton -> //your deleting code
                dialog.dismiss()
                onClick.invoke(true)
            }
            .setNegativeButton(
                "ยกเลิก"
            ) { dialog, which -> dialog.dismiss() }
            .create()
        dialog.show()
    }


    fun dialogMessage(context: Context, message: String) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_message)

        val text = dialog.findViewById<View>(R.id.tvMessage) as TextView
        text.text = message

        val dialogButton: Button = dialog.findViewById<View>(R.id.btn_ok) as Button
        dialogButton.setOnClickListener { dialog.dismiss() }

        dialog.show()

    }

    fun dialogMessage(context: Context, message: String, onClick: (Boolean) -> Unit?) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_message)

        val text = dialog.findViewById<View>(R.id.tvMessage) as TextView
        text.text = message

        val dialogButton: Button = dialog.findViewById<View>(R.id.btn_ok) as Button
        dialogButton.setOnClickListener {
            dialog.dismiss()
            onClick.invoke(true)
        }

        dialog.show()

    }


    @SuppressLint("WrongViewCast", "CheckResult")
    fun showAlertDialogFullScreen(context: Context, mImageUrl: Bitmap) {
        val dialog= Dialog(context)
        dialog.setCanceledOnTouchOutside(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setContentView(R.layout.custom_dialog_image)

        val ivClose = dialog.findViewById<ImageButton>(R.id.ivClose) as ImageView
        ivClose.setOnClickListener {
            dialog.cancel()
        }

        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.default_pic)
        requestOptions.error(R.mipmap.ic_launcher_round)
        requestOptions.diskCacheStrategy

        Glide.with(context)
            .load(mImageUrl)
            .apply(requestOptions)
            .into(dialog.findViewById<ImageButton>(R.id.ivShow) as ImageView)

        dialog.setCancelable(true)
        dialog.show()

    }

    @SuppressLint("CheckResult")
    fun setImageView(context: Context, mImageUrl:Bitmap, view: ImageView){
        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.default_pic)
        requestOptions.error(R.mipmap.ic_launcher_round)
        requestOptions.diskCacheStrategy

        Glide.with(context)
            .load(mImageUrl)
            .apply(requestOptions)
            .into(view)
    }

    fun openGoogleMapNavigation(
        context: Context,
        latitude: String,
        longitude: String
    ) {
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=$latitude,$longitude")
            )
            intent.setPackage("com.google.android.apps.maps")
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                val intentUrlGoogle = Intent(Intent.ACTION_VIEW)
                intentUrlGoogle.data =
                    Uri.parse("market://details?id=" + "com.google.android.apps.maps")
                context.startActivity(intentUrlGoogle)
            }
        }catch (ex:Exception){
            dialogMessage(context,ex.message?:"No app google map")
        }
    }
}