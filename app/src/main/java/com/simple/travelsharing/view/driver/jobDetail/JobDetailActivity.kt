package com.simple.travelsharing.view.driver.jobDetail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.simple.travelsharing.R
import com.simple.travelsharing.data.SharedPreference
import com.simple.travelsharing.model.ModelCreateJob
import com.simple.travelsharing.model.ModelCreateJobDetail
import com.simple.travelsharing.model.ModelParticipants
import com.simple.travelsharing.utils.CustomDialogLoading
import com.simple.travelsharing.utils.Utils
import com.simple.travelsharing.utils.createJobRequest
import com.simple.travelsharing.view.driver.groupChat.GroupChatActivity
import com.simple.travelsharing.view.driver.requestJob.RequestJobActivity
import kotlinx.android.synthetic.main.activity_job_detail.*


class JobDetailActivity : AppCompatActivity() {

    private var mAction: String? = ""
    private var mStatus: String? = ""

    private var dataGetIntent: ModelCreateJobDetail? = null
    private val mRootRef = FirebaseDatabase.getInstance().reference.child("jobs")
    private val mRootRefParticipants = FirebaseDatabase.getInstance().reference.child("participants")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_detail)


        initBundle()
        initOnClick()
    }

    @SuppressLint("SetTextI18n")
    private fun initBundle() {
        mAction = intent?.getStringExtra("action")
        dataGetIntent = intent.getParcelableExtra("dataObj")

        dataGetIntent?.let {
            // Utils.setImageView(this, Utils.convertBase64ToImage(MainActivity.jobDriverImage), ivProfile)
            tvAdapterDriverName.text = it.driver_name
            tvTitleCreate.text = "วันที่โพสต์ ${it.job_create}"
            jobName.text = it.job_name
            jobDetail.text = it.job_detail
            tvJobCount.text = it.job_count
            tvSelectDate.text = it.job_date
            tvSelectPlace.text = it.job_place
            mStatus = it.job_status

            when (mStatus) {
                "1" -> {
                    tvTitleStatus.text = "สถานะ : เปิดลงทะเบียน"
                    tvTitleStatus.setTextColor(resources.getColor(R.color.colorStatusPending))
                }
                "3" -> {
                    tvTitleStatus.text = "สถานะ : ยกเลิกกิจกรรม"
                    tvTitleStatus.setTextColor(resources.getColor(R.color.colorStatusCancel))
                    cvActionOther.visibility = View.GONE
                }
                else -> {
                    tvTitleStatus.text = "สถานะ : ปิดรับลงทะเบียน"
                    tvTitleStatus.setTextColor(resources.getColor(R.color.colorStatusSuccess))
                    cvActionOther.visibility = View.GONE
                }
            }
        }

        when (mAction) {
            "hideButton" -> {
                cvRequestJob.visibility = View.GONE
                cvGroundChat.visibility = View.GONE
                cvRequestGroup.visibility = View.GONE
                viewUserDetail.visibility = View.VISIBLE
                lineUserDetail.visibility = View.VISIBLE
                cvActionOther.visibility = View.GONE
            }
            "showButton" -> {
                cvRequestJob.visibility = View.VISIBLE
                cvGroundChat.visibility = View.VISIBLE
                cvRequestGroup.visibility = View.GONE
                viewUserDetail.visibility = View.VISIBLE
                lineUserDetail.visibility = View.VISIBLE
                cvActionOther.visibility = View.GONE
            }
            "userDetailJob" -> {
                cvRequestJob.visibility = View.GONE
                cvGroundChat.visibility = View.GONE
                cvRequestGroup.visibility = View.VISIBLE
                viewUserDetail.visibility = View.GONE
                lineUserDetail.visibility = View.GONE
                cvActionOther.visibility = View.GONE
            }
            "driverFavorites" -> {
                cvRequestJob.visibility = View.VISIBLE
                cvRequestGroup.visibility = View.GONE
                viewUserDetail.visibility = View.VISIBLE
                lineUserDetail.visibility = View.VISIBLE
                cvActionOther.visibility = View.VISIBLE
                cvGroundChat.visibility = View.VISIBLE
            }
            "userFavorites" -> {
                cvRequestJob.visibility = View.GONE
                cvRequestGroup.visibility = View.GONE
                viewUserDetail.visibility = View.VISIBLE
                lineUserDetail.visibility = View.VISIBLE
                cvActionOther.visibility = View.GONE
                tvStatusRequest.visibility = View.VISIBLE
                onnectCheckStatusRequestJob()
            }
            else -> {
                cvRequestJob.visibility = View.VISIBLE
                cvGroundChat.visibility = View.VISIBLE
                cvRequestGroup.visibility = View.GONE
                viewUserDetail.visibility = View.VISIBLE
                lineUserDetail.visibility = View.VISIBLE
                cvActionOther.visibility = View.GONE
            }
        }
    }

    private fun onnectCheckStatusRequestJob() {
        CustomDialogLoading.showProgressDialog(resources.getString(R.string.loading))
        val query =
            mRootRefParticipants.orderByChild("job_code").equalTo(dataGetIntent!!.job_code)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (postSnapshot in dataSnapshot.children) {
                        val exercise: ModelParticipants =
                            postSnapshot.getValue(ModelParticipants::class.java)!!

                        if (exercise.user_code == SharedPreference(this@JobDetailActivity).getUserCode()) {
                            when (exercise.status_request) {
                                "1" -> {
                                    cvGroundChat.visibility = View.GONE
                                    tvStatusRequest.text = "สถานะขอเข้าร่วม : รอดำเนินการ"
                                    tvStatusRequest.setTextColor(ContextCompat.getColor(this@JobDetailActivity,R.color.colorStatusPending))
                                }
                                "2" -> {
                                    cvGroundChat.visibility = View.GONE
                                    tvStatusRequest.text = "สถานะขอเข้าร่วม : ไม่อนุมัติ"
                                    tvStatusRequest.setTextColor(ContextCompat.getColor(this@JobDetailActivity,R.color.colorStatusCancel))
                                }
                                else -> {
                                    tvStatusRequest.text = "สถานะขอเข้าร่วม : อนุมัติ"
                                    tvStatusRequest.setTextColor(ContextCompat.getColor(this@JobDetailActivity,R.color.colorStatusSuccess))
                                }
                            }
                            actionButton("chatAction", exercise.status_request.toString())
                        }
                    }
                } else {
                    Utils.dialogMessage(this@JobDetailActivity, "ไม่พบกิจกรรม")
                }
                CustomDialogLoading.dissProgressDialog()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                CustomDialogLoading.dissProgressDialog()
                Utils.dialogMessage(
                    this@JobDetailActivity, "error : ${databaseError.message}"
                )
            }
        })
    }

    private fun actionButton(action:String, status:String){
        when (action) {
            "chatAction" -> {
                cvGroundChat.visibility = if (status == "3") View.VISIBLE else View.GONE
            }
        }

    }

    private fun initOnClick() {
        viewBack.setOnClickListener {
            finish()
        }

        btnRequestJob.setOnClickListener {
            intentJobRequest()
        }

        btnGroundChat.setOnClickListener {
            intentChat()
        }

        cvRequestJob.setOnClickListener {
            intentJobRequest()
        }

        cvGroundChat.setOnClickListener {
            intentChat()
        }

        viewNavigator.setOnClickListener {
            dataGetIntent?.let {
                Utils.openGoogleMapNavigation(
                    this,
                    it.job_latitude ?: "0.0",
                    it.job_longitude ?: "0.0"
                )
            } ?: kotlin.run {
                Utils.dialogMessage(this, "ไม่พบตำแหน่ง")
            }

        }

        cvRequestGroup.setOnClickListener {
            actionRequestActivity(dataGetIntent?.job_code)
        }

        btnRequestGroup.setOnClickListener {
            actionRequestActivity(dataGetIntent?.job_code)
        }

        cvActionOther.setOnClickListener {
            actionClickOther()
        }

        btnActionOther.setOnClickListener {
            actionClickOther()
        }
    }

    private fun actionClickOther() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("เลือกคำสั่ง")

        // add a list
        val animals = arrayOf("ปิดรับสมัครกิจกรรม", "ยกเลิกกิจกรรม")
        builder.setItems(animals) { dialog, which ->
            when (which) {
                0 -> {
                    Utils.dialogConfirm(this, "ปิดรับสมัครกิจกรรม") {
                        updateStatusActivity("2")
                    }
                }
                1 -> {
                    Utils.dialogConfirm(this, "ยกเลิกกิจกรรม") {
                        updateStatusActivity("3")
                    }
                }
            }
        }

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }


    private fun requestJobDetail(it: ModelCreateJob, statusAction: String): ModelCreateJob {
        return ModelCreateJob(
            it.job_code,
            it.job_create, it.job_name,
            it.job_detail, it.job_count,
            it.job_date, it.job_place,
            it.job_latitude, it.job_longitude,
            statusAction, it.driver_name,
            it.driver_image, it.driver_code,
            it.request_count, it.user_approve
        )
    }

    private fun updateStatusActivity(status: String) {
        CustomDialogLoading.showProgressDialog(resources.getString(R.string.loading))

        val query = mRootRef.orderByChild("job_code").equalTo(dataGetIntent?.job_code)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (postSnapshot in dataSnapshot.children) {
                        val modelJob = postSnapshot.getValue(ModelCreateJob::class.java)!!
                        val postValues = requestJobDetail(modelJob, status).toMap()
                        val childUpdates = HashMap<String, Any>()
                        childUpdates["/${postSnapshot.key}"] = postValues
                        mRootRef.updateChildren(childUpdates)
                    }

                    Handler().postDelayed({
                        Utils.dialogMessage(this@JobDetailActivity, "เปลี่ยนสถานะงานเรียบร้อย")
                    }, 1000)
                }
                CustomDialogLoading.dissProgressDialog()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                CustomDialogLoading.dissProgressDialog()
                Utils.dialogMessage(
                    this@JobDetailActivity, "error : ${databaseError.message}"
                )
            }
        })
    }

    private fun actionRequestActivity(jobCode: String?) {
        CustomDialogLoading.showProgressDialog(resources.getString(R.string.loading))

        val query = mRootRef.orderByChild("job_code").equalTo(jobCode)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val list =
                        dataSnapshot.children.map { it.getValue(ModelCreateJob::class.java)!! }

                    if (list[0].job_status == "1") {
                        if (list[0].job_count?.toInt() ?: 0 <= list[0].request_count ?: 0)
                            Utils.dialogMessage(this@JobDetailActivity, "ไม่สามารถเข้าร่วมกิจกรรมนี้ได้\nเนื่องจำนวนที่เปิดรับเต็มแล้ว")
                        else
                            requestJob(jobCode, list, dataSnapshot)
                    } else {
                        Utils.dialogMessage(
                            this@JobDetailActivity,
                            "ไม่สามารถเข้าร่วมกิจกรรมนี้ได้\nเนื่องจากกิจกรรมนี้ปิดไปแล้ว"
                        )
                    }
                } else {
                    Utils.dialogMessage(
                        this@JobDetailActivity,
                        "ไม่สามารถเข้าร่วมกิจกรรมนี้ได้\nเนื่องจากคุณได้ขอเข้าร่วมไปแล้ว"
                    )
                }
                CustomDialogLoading.dissProgressDialog()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                CustomDialogLoading.dissProgressDialog()
                Utils.dialogMessage(
                    this@JobDetailActivity,
                    "error : ${databaseError.message}"
                )
            }
        })
    }

    private fun requestJob(
        jobCode: String?,
        listData: List<ModelCreateJob>,
        dataSnapshot: DataSnapshot
    ) {
        ModelParticipants(
            jobCode, SharedPreference(this).getUserCode(),
            SharedPreference(this).getFullName(),
            SharedPreference(this).getImage(),
            SharedPreference(this).getEmail(), "1",
            Utils.getDateTimeCurrent()
        ).createJobRequest()

        for (postSnapshot in dataSnapshot.children) {
            val postValues = requestJobForUser(listData[0]).toMap()
            val childUpdates = HashMap<String, Any>()
            childUpdates["/${postSnapshot.key}"] = postValues
            mRootRef.updateChildren(childUpdates)
        }

        Utils.dialogMessage(
            this,
            "ขอเข้าร่วมกิจกรรมสำเร็จ\nกรุณารอการดำเนินการตรวจสอบจาก driver เพื่อเข้าร่วมกิจกรรม"
        ) {
        }

    }

    private fun requestJobForUser(it: ModelCreateJob): ModelCreateJob {
        var userRequest = it.request_count ?: 0
        userRequest++
        return ModelCreateJob(
            it.job_code,
            it.job_create, it.job_name,
            it.job_detail, it.job_count,
            it.job_date, it.job_place,
            it.job_latitude, it.job_longitude,
            it.job_status, it.driver_name,
            it.driver_image, it.driver_code,
            userRequest, it.user_approve
        )
    }

    private fun intentChat() {
        val intentChat = Intent(this, GroupChatActivity::class.java)
        intentChat.putExtra("job_code", dataGetIntent?.job_code)
        intentChat.putExtra("job_name", dataGetIntent?.job_name)
        startActivity(intentChat)
    }

    private fun intentJobRequest() {
        val intentJob = Intent(this, RequestJobActivity::class.java)
        intentJob.putExtra("job_code", dataGetIntent?.job_code)
        startActivity(intentJob)
    }
}