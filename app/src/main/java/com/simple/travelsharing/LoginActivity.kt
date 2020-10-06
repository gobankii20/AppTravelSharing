package com.simple.travelsharing

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.simple.travelsharing.data.SharedPreference
import com.simple.travelsharing.model.ModeLogin
import com.simple.travelsharing.utils.CustomDialogLoading
import com.simple.travelsharing.utils.Utils
import com.simple.travelsharing.view.RegisterActivity
import com.simple.travelsharing.view.admin.AdminMainActivity
import com.simple.travelsharing.view.driver.MainActivity
import com.simple.travelsharing.view.driver.VerifyActivity
import com.simple.travelsharing.view.user.UserMainActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.etPassword
import kotlinx.android.synthetic.main.activity_login.etUserName


class LoginActivity : AppCompatActivity() {

    val mRootRef = FirebaseDatabase.getInstance().reference.child("users")

    private lateinit var mSharedPreference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mSharedPreference = SharedPreference(this)

        CustomDialogLoading.newProgressDialog(this)

        if (mSharedPreference.getUserName().isNullOrEmpty()) {
            initOnClick()
            initRequestPermission()
        } else {
            when (mSharedPreference.getUserRule()) {
                "driver" -> {
                    val intentMain = Intent(this, MainActivity::class.java)
                    startActivity(intentMain)
                }
                "user" -> {
                    val intentMain = Intent(this, UserMainActivity::class.java)
                    startActivity(intentMain)
                }
                "admin" -> {
                    val intentMain = Intent(this, AdminMainActivity::class.java)
                    startActivity(intentMain)
                }
            }
        }
    }

    private fun initRequestPermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : PermissionListener, MultiplePermissionsListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                }

                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                }
            }).check()
    }

    private fun initOnClick() {
        btRegister.setOnClickListener {
            val intentRegister = Intent(this, RegisterActivity::class.java)
            startActivity(intentRegister)
        }

        btLogin.setOnClickListener {
            if (!isCheckEntry()) {
                Utils.dialogMessage(this@LoginActivity, "กรุณากรอกข้อมูล")
            } else {
                CustomDialogLoading.showProgressDialog(resources.getString(R.string.loading))
                loginFirebase(etUserName.text.toString(), etPassword.text.toString())
            }
        }
    }

    private fun isCheckEntry(): Boolean {
        return etUserName.text.toString().isNotEmpty() && etPassword.text.toString().isNotEmpty()
    }

    private fun loginFirebase(userName: String, password: String) {
        mRootRef.orderByChild("user_name")
            .equalTo(userName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val list =
                            dataSnapshot.children.map { it.getValue(ModeLogin::class.java)!! }
                        if (isCheckPassword(list[0].user_password ?: "", password)) {
                            if (isCheckStatusActive(list[0].status ?: 1)) {
                                fetchDataLogin(list[0])
                            } else {
                                Utils.dialogMessage(
                                    this@LoginActivity,
                                    "ไม่สามารถเข้าสู่ระบบได้\nเนื่องจาก Account ของคุณอยู่ระหว่างขั้นตอนการพิจารณา "
                                )
                            }
                        } else {
                            Utils.dialogMessage(this@LoginActivity, "รหัสผ่านไม่ถูกต้อง")
                        }
                    } else {
                        Utils.dialogMessage(this@LoginActivity, "ไม่พบผู้ใช้งาน")
                    }
                    CustomDialogLoading.dissProgressDialog()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    CustomDialogLoading.dissProgressDialog()
                    Toast.makeText(this@LoginActivity, "error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun isCheckStatusActive(status: Int): Boolean {
        val statusApprove = 2
        return status == statusApprove
    }

    private fun isCheckPassword(password: String, passwordCurrent: String): Boolean {
        return password == passwordCurrent
    }


    private fun fetchDataLogin(modeLogin: ModeLogin) {
        mSharedPreference.savePhone(modeLogin.user_phone ?: "")
        when (modeLogin.user_rule) {
            "driver" -> {
                if (modeLogin.status_verify == true) {
                    val intentMain = Intent(this, MainActivity::class.java)
                    startActivity(intentMain)
                }else{
                    val intentMain = Intent(this, VerifyActivity::class.java)
                    intentMain.putExtra("user_code",modeLogin.user_code)
                    startActivity(intentMain)
                }
            }
            "user" -> {
                saveData(modeLogin)
                val intentMain = Intent(this, UserMainActivity::class.java)
                startActivity(intentMain)
            }
            "admin" -> {
                saveData(modeLogin)
                val intentMain = Intent(this, AdminMainActivity::class.java)
                startActivity(intentMain)
            }
        }
    }

    private fun saveData(modeLogin: ModeLogin) {
        mSharedPreference.saveUserRule(modeLogin.user_rule ?: "")
        mSharedPreference.saveUserName(modeLogin.user_name ?: "")
        mSharedPreference.saveUserCode(modeLogin.user_code ?: "")
        mSharedPreference.saveFullName(modeLogin.user_fullname ?: "")
        mSharedPreference.saveImage(modeLogin.user_image ?: "")
        mSharedPreference.saveEmail(modeLogin.user_email ?: "")
        mSharedPreference.savePhone(modeLogin.user_phone ?: "")
    }
}