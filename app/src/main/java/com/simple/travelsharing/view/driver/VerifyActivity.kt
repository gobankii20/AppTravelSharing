package com.simple.travelsharing.view.driver

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.simple.travelsharing.R
import com.simple.travelsharing.data.SharedPreference
import com.simple.travelsharing.model.ModelCreateUser
import com.simple.travelsharing.utils.CustomDialogLoading
import com.simple.travelsharing.utils.Utils
import com.simple.travelsharing.utils.updateUser
import kotlinx.android.synthetic.main.activity_verify.*
import java.util.*
import java.util.concurrent.TimeUnit


class VerifyActivity : AppCompatActivity() {

    var phoneNumber = ""

    var TAG = VerifyActivity::class.java.toString()

    private lateinit var auth: FirebaseAuth

    var verificationCode: String = ""

    var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null

    private fun Activity.hideKeyboard() = hideKeyboard(currentFocus ?: View(this))

    private lateinit var mSharedPreference: SharedPreference

    var cdt: CountDownTimer? = null

    private val FORMAT = "%02d:%02d"

    private fun Context.hideKeyboard(view: View) =
        (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            view.windowToken,
            0
        )

    val mRootRef = FirebaseDatabase.getInstance().reference.child("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        initView()
        initOnClick()
        startFirebaseLogin()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        mSharedPreference = SharedPreference(this)
        CustomDialogLoading.newProgressDialog(this)
        phoneNumber = SharedPreference(this).getPhone() ?: ""
        if (phoneNumber.isNotEmpty()){
            phoneNumber = phoneNumber.replaceRange(0, 1, "+66")
        }

        tvMessageConfirm.text = "กรอกรหัส OTP ที่ส่งไปยัง\nเบอร์โทรศัพท์ $phoneNumber เพื่อยืนยันตัวตน"
    }


    private fun sendOtp() {
        if (phoneNumber.isNotEmpty()) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,                     // Phone number to verify
                60,                           // Timeout duration
                TimeUnit.SECONDS,                // Unit of timeout
                this,        // Activity (for callback binding)
                mCallback!!
            )
        } else {
            Utils.dialogMessage(this, "ไม่พบเบอร์โทรศัพท์")
        }

    }

    private fun startTimeIntervalOTP(){
        val TIME_INTERVAL = 1000L * 300L

        cdt = object : CountDownTimer(TIME_INTERVAL, 1000L) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                tvTimeInterval.text = ""+String.format(FORMAT,
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
            }

            override fun onFinish() {
                tvTimeInterval.text = ""
                verificationCode = ""
                tvTryAgine.visibility = View.VISIBLE
            }
        }.start()
    }

    private fun startFirebaseLogin() {
        auth = FirebaseAuth.getInstance()
        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Toast.makeText(this@VerifyActivity, "verification completed", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Utils.dialogMessage(this@VerifyActivity, "verification failed $e")
                Log.d("FirebaseException", e.toString())
            }

            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                verificationCode = s
                startTimeIntervalOTP()
                Log.d("verificationCode", verificationCode)
            }
        }
        sendOtp()
    }


    private fun checkOTPInterval():Boolean{
        return verificationCode.isNotEmpty()
    }

    private fun initOnClick() {
        btVerify.setOnClickListener {
            hideKeyboard()
            if (firstPinView.text.toString().isNotEmpty() && checkOTPInterval()) {
                val credential =
                    PhoneAuthProvider.getCredential(verificationCode, firstPinView.text.toString())
                SigninWithPhone(credential)
            } else {
                if (!checkOTPInterval())
                    Utils.dialogMessage(this, "ไม่สามารถยืนยันตัวตนได้ เนื่องจาก OTP หมดอายุ")
                else
                    Utils.dialogMessage(this, "กรุณากรอกหมายเลข OTP")
            }
        }

        tvTryAgine.setOnClickListener {
            hideKeyboard()
            firstPinView.clearFocus()
            firstPinView.setText("")
            sendOtp()
            cdt?.cancel()
            startTimeIntervalOTP()
            tvTryAgine.visibility = View.INVISIBLE
        }
    }

    private fun SigninWithPhone(credential: PhoneAuthCredential) {
        CustomDialogLoading.showProgressDialog(resources.getString(R.string.loading))

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startAppVerifySuccess()
                } else {
                    CustomDialogLoading.dissProgressDialog()
                    Utils.dialogMessage(this, "หมายเลข OTP ไม่ถูกต้อง")
                }
            }
    }

    private fun startAppVerifySuccess() {
        mRootRef.orderByChild("user_code")
            .equalTo(SharedPreference(this).getUserCode())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val list =
                            dataSnapshot.children.map { it.getValue(ModelCreateUser::class.java)!! }
                        doUpdateStatusUser(list, dataSnapshot)
                    } else {
                        Toast.makeText(this@VerifyActivity, "ไม่พบผู้ใช้งาน", Toast.LENGTH_SHORT)
                            .show()
                    }
                    CustomDialogLoading.dissProgressDialog()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    CustomDialogLoading.dissProgressDialog()
                    Utils.dialogMessage(
                        this@VerifyActivity,
                        "error : ${databaseError.message}"
                    )
                }
            })
    }

    private fun doUpdateStatusUser(data: List<ModelCreateUser>, dataSnapshot: DataSnapshot) {
        mSharedPreference.saveUserRule(data[0].user_rule ?: "")
        mSharedPreference.saveUserName(data[0].user_name ?: "")
        mSharedPreference.saveUserCode(data[0].user_code ?: "")
        mSharedPreference.saveFullName(data[0].user_fullname ?: "")
        mSharedPreference.saveImage(data[0].user_image ?: "")
        mSharedPreference.saveEmail(data[0].user_email ?: "")
        mSharedPreference.savePhone(data[0].user_phone ?: "")

        val mModelCreateUser = ModelCreateUser(
            data[0].user_code,
            data[0].user_fullname,
            data[0].user_phone,
            data[0].user_email,
            data[0].user_address,
            data[0].user_image, data[0].user_name,
            data[0].user_password,
            data[0].user_rule, data[0].date_request, data[0].status, data[0].image_card,
            true
        ).updateUser().toMap()

        for (postSnapshot in dataSnapshot.children) {
            val postValues = mModelCreateUser
            val childUpdates = HashMap<String, Any>()
            childUpdates["/${postSnapshot.key}"] = postValues
            mRootRef.updateChildren(childUpdates)
        }

        Handler().postDelayed({
            val intentMain = Intent(
                this,
                MainActivity::class.java
            )
            startActivity(intentMain)
        }, 1500)
    }
}