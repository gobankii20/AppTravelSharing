package com.simple.travelsharing.view.driver.createJob

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.simple.travelsharing.R
import com.simple.travelsharing.data.SharedPreference
import com.simple.travelsharing.model.ModelCreateJob
import com.simple.travelsharing.utils.Utils
import com.simple.travelsharing.utils.createJob
import com.simple.travelsharing.view.driver.selectPlace.MapsActivity
import kotlinx.android.synthetic.main.activity_create_job.*
import kotlinx.android.synthetic.main.activity_create_job.btnConfirm
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


class CreateJobActivity : AppCompatActivity() {

    private var latitude = "0.0"
    private var longitude = "0.0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_job)


        initOnClick()
        initView()
        initRequestPermission()
    }

    private fun initRequestPermission() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
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
                    p1: PermissionToken?) {}
            }).check()
    }

    private fun initView() {
        tvTitleSelectDate.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        tvTitleSelectPlace.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }

    private fun initOnClick() {
        tvTitleSelectDate.setOnClickListener {
            dialogDate()
        }

        tvSelectDate.setOnClickListener {
            dialogDate()
        }

        tvTitleSelectPlace.setOnClickListener {
            intentMap()
        }

        tvSelectPlace.setOnClickListener {
            intentMap()
        }


        viewBack.setOnClickListener {
            finish()
        }

        btnConfirm.setOnClickListener {
            if (checkEntry()) {
                ModelCreateJob(
                    Utils.getRandomId(),
                    Utils.getDateTimeCurrent(), etJobName.text.toString(),
                    etJobDetail.text.toString(),
                    etJobCount.text.toString(),
                    tvSelectDate.text.toString(), tvSelectPlace.text.toString(),
                    latitude, longitude,
                    "1",
                    SharedPreference(this).getUserName(),
                    SharedPreference(this).getImage(),
                    SharedPreference(this).getUserCode(),0,0
                ).createJob()

                Utils.dialogMessage(this, "สร้างกิจกรรมสำเร็จ") {
                    finish()
                }
            } else {
                Utils.dialogMessage(this, "กรอกข้อมูลให้ครบถ้วน")
            }
        }
    }

    private fun intentMap() {
        val i = Intent(applicationContext, MapsActivity::class.java)
        startActivityForResult(i, 1)
    }

    private fun checkEntry(): Boolean {
        return etJobName.text.toString().isNotEmpty() && etJobDetail.text.toString().isNotEmpty() &&
                etJobCount.text.toString().isNotEmpty() && tvSelectDate.text.toString()
            .isNotEmpty() &&
                tvSelectPlace.text.toString().isNotEmpty()
    }

    @SuppressLint("SetTextI18n")
    private fun dialogDate() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                tvSelectDate.text = "$dayOfMonth-$month-$year"

            },
            year,
            month,
            day
        )

        dpd.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data?.getStringExtra("result")
                val lat = data?.getStringExtra("latitude")
                val long = data?.getStringExtra("longitude")

                latitude = lat ?: "0.0"
                longitude = long ?: "0.0"
                tvSelectPlace.text = result
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}