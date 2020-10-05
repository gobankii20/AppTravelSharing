package com.simple.travelsharing.view.driver.groupChat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.simple.travelsharing.R
import com.simple.travelsharing.adapter.CustomAdapterChat
import com.simple.travelsharing.data.SharedPreference
import com.simple.travelsharing.model.ModelChat
import com.simple.travelsharing.model.ModelCreateJob
import com.simple.travelsharing.model.ModelGroupChat
import com.simple.travelsharing.utils.CustomDialogLoading
import com.simple.travelsharing.utils.Utils
import com.simple.travelsharing.utils.createGroupChat
import kotlinx.android.synthetic.main.activity_group_chat.*
import java.util.HashMap

class GroupChatActivity : AppCompatActivity() {

    private var mListDatChat = ArrayList<ModelGroupChat>()
    private lateinit var mCustomAdapterChat: CustomAdapterChat
    private var jobCode = ""
    private var jobName = ""

    private val mRootRef = FirebaseDatabase.getInstance().reference.child("groupChat")

    private val mRootRefJobs = FirebaseDatabase.getInstance().reference.child("jobs")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        initSetViewChat()
        initOnClick()
    }

    private fun initOnClick() {
        intentGetExtra()
        viewBack.setOnClickListener {
            finish()
        }

        ivSendMessage.setOnClickListener {
            ModelGroupChat(
                jobCode, SharedPreference(this).getUserCode(),
                SharedPreference(this).getFullName(), SharedPreference(this).getImage(),
                SharedPreference(this).getUserRule(),
                etAddMessage.text.toString(), Utils.getDateCurrent(), Utils.getDateTime()
            ).createGroupChat()

            etAddMessage.setText("")
        }
    }

    private fun intentGetExtra() {
        jobCode = intent?.getStringExtra("job_code").toString()
        jobName = intent?.getStringExtra("job_name").toString()

        initSetTitle()
        onnectCheckCreateGroup()
    }

    private fun initSetTitle() {
        tvTitle.text = jobName
        SharedPreference(this).getImage()?.let { Utils.setImageView(this, it,imProfileBottom) }
    }

    private fun onnectCheckCreateGroup() {
        CustomDialogLoading.showProgressDialog(resources.getString(R.string.loading))
        val query = mRootRef.orderByChild("job_code").equalTo(jobCode)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mListDatChat.clear()
                if (dataSnapshot.exists()) {
                    val list =
                        dataSnapshot.children.map { it.getValue(ModelGroupChat::class.java)!! }
                    mListDatChat.addAll(list)

                    for (i in mListDatChat.indices) {
                        mListDatChat[i].chatType =
                            if (mListDatChat[i].user_code == SharedPreference(this@GroupChatActivity).getUserCode()) 1 else 0
                    }
                }

                mCustomAdapterChat.notifyItemChanged(mListDatChat.size - 1)
                rvChat.scrollToPosition(mListDatChat.size - 1)
                mCustomAdapterChat.notifyDataSetChanged()

                CustomDialogLoading.dissProgressDialog()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                CustomDialogLoading.dissProgressDialog()
                Utils.dialogMessage(this@GroupChatActivity, "error : ${databaseError.message}")
            }

        })
    }

    private fun initSetViewChat() {
        CustomDialogLoading.newProgressDialog(this)

        mCustomAdapterChat = CustomAdapterChat(this, mListDatChat)

        rvChat.apply {
            layoutManager = LinearLayoutManager(this@GroupChatActivity)
            adapter = mCustomAdapterChat
        }
    }
}