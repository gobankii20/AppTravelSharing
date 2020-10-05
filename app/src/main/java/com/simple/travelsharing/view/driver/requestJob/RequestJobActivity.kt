package com.simple.travelsharing.view.driver.requestJob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.simple.travelsharing.R
import com.simple.travelsharing.adapter.AdapterRequestJob
import com.simple.travelsharing.data.SharedPreference
import com.simple.travelsharing.model.ModelCreateJob
import com.simple.travelsharing.model.ModelCreateJobDetail
import com.simple.travelsharing.model.ModelCreateUser
import com.simple.travelsharing.model.ModelParticipants
import com.simple.travelsharing.utils.CustomDialogLoading
import com.simple.travelsharing.utils.Utils
import com.simple.travelsharing.utils.updateUser
import kotlinx.android.synthetic.main.activity_group_chat.*
import kotlinx.android.synthetic.main.activity_group_chat.rvChat
import kotlinx.android.synthetic.main.activity_group_chat.viewBack
import kotlinx.android.synthetic.main.activity_request_job.*
import java.util.HashMap

class RequestJobActivity : AppCompatActivity() {

    private val mListRequestJob = ArrayList<ModelParticipants>()
    private lateinit var mAdapterRequestJob: AdapterRequestJob
    private var jobCode = ""

    private val mRootRef = FirebaseDatabase.getInstance().reference.child("participants")

    private val mRootRefJobs = FirebaseDatabase.getInstance().reference.child("jobs")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_job)

        initOnClick()
        initSetViewListRequestJob()
    }

    private fun initSetViewListRequestJob() {
        CustomDialogLoading.newProgressDialog(this)
        mAdapterRequestJob = AdapterRequestJob(this, mListRequestJob) { action, dataObj ->

            val mModelCreateUser = ModelParticipants(
                dataObj.job_code, dataObj.user_code, dataObj.user_fullname,
                dataObj.user_image, dataObj.user_email, if (action == "cancel") "2" else "3",
                dataObj.request_date
            ).toMap()


            CustomDialogLoading.showProgressDialog(resources.getString(R.string.loading))
            val query = mRootRef.orderByChild("user_code").equalTo(dataObj.user_code)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) { // dataSnapshot is the "issue" node with all children with id 0

                        for (dataSnapshot1 in dataSnapshot.children) {
                            val postValues = mModelCreateUser.toMap()
                            val childUpdates = HashMap<String, Any>()
                            childUpdates["/${dataSnapshot1.key}"] = postValues
                            mRootRef.updateChildren(childUpdates)
                        }

                        if (action != "cancel") {
                            updateStatusJob(dataObj)
                        }else{
                            CustomDialogLoading.dissProgressDialog()
                        }
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    CustomDialogLoading.dissProgressDialog()
                    Utils.dialogMessage(this@RequestJobActivity, "error : ${databaseError.message}")
                }
            })

        }

        rvChat.apply {
            layoutManager = LinearLayoutManager(this@RequestJobActivity)
            adapter = mAdapterRequestJob
        }

        intentGetExtra()
    }

    private fun createJobDetail(it: ModelCreateJob): ModelCreateJob {
        val userApprove = it.user_approve!! + 1
        return ModelCreateJob(
            it.job_code,
            it.job_create, it.job_name,
            it.job_detail, it.job_count,
            it.job_date, it.job_place,
            it.job_latitude, it.job_longitude,
            it.job_status, it.driver_name,
            "", it.driver_code,
            it.request_count, userApprove
        )
    }

    private fun updateStatusJob(dataObj: ModelParticipants) {
        val queryJob = mRootRefJobs.orderByChild("job_code").equalTo(jobCode)
        queryJob.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) { // dataSnapshot is the "issue" node with all children with id 0
                    val list =
                        dataSnapshot.children.map { it.getValue(ModelCreateJob::class.java)!! }

                    for (dataSnapshot1 in dataSnapshot.children) {
                        val postValues = createJobDetail(list[0]).toMap()
                        val childUpdates = HashMap<String, Any>()
                        childUpdates["/${dataSnapshot1.key}"] = postValues
                        mRootRefJobs.updateChildren(childUpdates)
                    }
                }
                CustomDialogLoading.dissProgressDialog()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                CustomDialogLoading.dissProgressDialog()
                Utils.dialogMessage(
                    this@RequestJobActivity,
                    "error : ${databaseError.message}"
                )
            }

        })
    }

    private fun intentGetExtra() {
        jobCode = intent?.getStringExtra("job_code").toString()

        CustomDialogLoading.showProgressDialog(resources.getString(R.string.loading))
        val query = mRootRef.orderByChild("job_code").equalTo(jobCode)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mListRequestJob.clear()
                if (dataSnapshot.exists()) {
                    val list =
                        dataSnapshot.children.map { it.getValue(ModelParticipants::class.java)!! }
                    mListRequestJob.addAll(list)
                } else {
                    Utils.dialogMessage(
                        this@RequestJobActivity,
                        "ไม่พบรายชื่อผู้ขอ เข้าร่วมกิจกรรม"
                    )
                }
                mAdapterRequestJob.notifyDataSetChanged()
                CustomDialogLoading.dissProgressDialog()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                CustomDialogLoading.dissProgressDialog()
                Utils.dialogMessage(
                    this@RequestJobActivity,
                    "error : ${databaseError.message}"
                )
            }
        })
    }

    private fun initOnClick() {
        viewBack.setOnClickListener {
            finish()
        }
    }
}