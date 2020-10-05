package com.simple.travelsharing.view

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.simple.travelsharing.R
import com.simple.travelsharing.model.ModelCreateUser
import com.simple.travelsharing.utils.Utils
import com.simple.travelsharing.utils.createUser
import kotlinx.android.synthetic.main.activity_register.*
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern


class RegisterActivity : AppCompatActivity() {
    private var actionPhotos = "takePhotos"
    private var actionPhotosCard = "takePhotosCard"

    private var uriImageView: Uri? = null
    private var mActionPageCurrent = 0

    private val TAKE_PHOTO_REQUEST = 101
    private var mCurrentPhotoPath: String = ""

    private val TAKE_PHOTO_CARD_REQUEST = 102
    private var mCurrentPhotoPathCard: String = ""
    private var uriImageViewCard: Uri? = null

    var pattern =
        "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$"
    var m: Matcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initOnClick()
        initEventTabView()
    }

    private fun initEventTabView() {
        tabLayoutView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.text == resources.getString(R.string.text_label_2)) {
                    manageViewVisibility(View.VISIBLE)
                    mActionPageCurrent = 1
                } else {
                    manageViewVisibility(View.GONE)
                    mActionPageCurrent = 0
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })
    }

    private fun manageViewVisibility(viewVisibility: Int) {
        viewDriver.visibility = viewVisibility
        viewLineDriver.visibility = viewVisibility
    }


    private fun initOnClick() {
        ivBack.setOnClickListener {
            finish()
        }

        layoutImageProfile.setOnClickListener {
            validatePermissions()
        }

        ivCard.setOnClickListener {
            launchCamera(actionPhotosCard)
        }

        btnConfirm.setOnClickListener {
            if (checkTextEntry()) {
                if (etPhone.text.toString().length == 10) {
                    val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()
                    val swissNumberProto: Phonenumber.PhoneNumber =
                        phoneUtil.parse(etPhone.text.toString(), "TH")
                    val isValid = phoneUtil.isValidNumber(swissNumberProto)
                    if (isValid) {
                        ModelCreateUser(
                            Utils.getRandomId(),
                            etFullName.text.toString(),
                            etPhone.text.toString(),
                            etEmail.text.toString(),
                            etAddress.text.toString(),
                            uriImageView?.let { it1 -> Utils.encodeImageToBase64(this, it1) },
                            etUserName.text.toString(),
                            etPassword.text.toString(),
                            if (mActionPageCurrent == 0) "user" else "driver",
                            Utils.getDateCurrent(),
                            1, uriImageViewCard?.let { it1 -> Utils.encodeImageToBase64(this, it1) },
                            false
                        ).createUser()
                        Utils.dialogMessage(this, "ลงทะเบียนสำเร็จ") {
                            finish()
                        }
                    } else {
                        Utils.dialogMessage(this, "รูปแบบเบอร์โทรศัพท์ไม่ถูกต้อง")
                    }
                } else {
                    Utils.dialogMessage(this, "รูปแบบเบอร์โทรศัพท์ไม่ถูกต้อง")
                }
            } else {
                Utils.dialogMessage(this, "กรุณากรอกข้อมูลให้ครบถ้วบ")
            }
        }
    }

    private fun validatePermissions() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    launchCamera(actionPhotos)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    AlertDialog.Builder(applicationContext)
                        .setTitle(R.string.storage_permission_rationale_title)
                        .setMessage(R.string.storage_permition_rationale_message)
                        .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                            token!!.cancelPermissionRequest()
                        }
                        .setPositiveButton(android.R.string.ok) { dialog, _ ->
                            dialog.dismiss()
                            token!!.continuePermissionRequest()
                        }
                        .setOnDismissListener { token?.cancelPermissionRequest() }
                        .show()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Snackbar.make(
                        viewDriver,
                        R.string.storage_permission_denied_message,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }).check()
    }

    private fun launchCamera(actionType: String) {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        val fileUri = contentResolver
            .insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            if (actionType == actionPhotos)
                mCurrentPhotoPath = fileUri.toString()
            else
                mCurrentPhotoPathCard = fileUri.toString()

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            startActivityForResult(
                intent,
                if (actionType == actionPhotos) TAKE_PHOTO_REQUEST else TAKE_PHOTO_CARD_REQUEST
            )
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_REQUEST) {
            processCapturedPhoto(actionPhotos)
        } else if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_CARD_REQUEST) {
            processCapturedPhoto(actionPhotosCard)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun processCapturedPhoto(actionPhotos: String) {
        val cursor = contentResolver.query(
            Uri.parse(if (actionPhotos == this.actionPhotos) mCurrentPhotoPath else mCurrentPhotoPathCard),
            Array(1) { android.provider.MediaStore.Images.ImageColumns.DATA },
            null, null, null
        )
        cursor?.moveToFirst()
        val photoPath = cursor?.getString(0)
        cursor?.close()
        val file = File(photoPath)
        if (actionPhotos == this.actionPhotos) {
            uriImageView = Uri.fromFile(file)
            ivProfile.setImageURI(uriImageView)
        } else {
            uriImageViewCard = Uri.fromFile(file)
            ivCard.setImageURI(uriImageViewCard)
        }

    }

    private fun checkTextEntry(): Boolean {
        return etFullName.text.toString().isNotEmpty() && etPhone.text.toString().isNotEmpty()
                && etUserName.text.toString().isNotEmpty() && etPassword.text.toString()
            .isNotEmpty() && etEmail.text.toString().isNotEmpty()
                && etConfirmPassword.text.toString().isNotEmpty()
    }
}