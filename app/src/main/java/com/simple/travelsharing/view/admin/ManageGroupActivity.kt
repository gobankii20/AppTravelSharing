package com.simple.travelsharing.view.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.simple.travelsharing.R
import com.simple.travelsharing.adapter.AdapterAdminManageGroup
import com.simple.travelsharing.data.Constants
import com.simple.travelsharing.model.ModelCreateUser
import com.simple.travelsharing.utils.CustomDialogLoading
import com.simple.travelsharing.utils.Utils
import com.simple.travelsharing.utils.updateUser
import kotlinx.android.synthetic.main.activity_manage_group.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.map
import kotlin.collections.set
import kotlin.collections.toMap


class ManageGroupActivity : AppCompatActivity() {

    private val mListDataGroup = ArrayList<ModelCreateUser>()
    private lateinit var mAdapterAdminManageGroup: AdapterAdminManageGroup
    val mRootRef = FirebaseDatabase.getInstance().reference.child("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_group)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        CustomDialogLoading.newProgressDialog(this)
        initSetView()
        initSetListManageUser()
        initEventTabView()
    }

    private fun initEventTabView() {
        tabLayoutView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.text == resources.getString(R.string.text_label_2)) {
                    onConnectListUser(Constants.USER_TYPE_DRIVER)
                } else {
                    onConnectListUser(Constants.USER_TYPE_USER)
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

    private fun initSetListManageUser() {
        mAdapterAdminManageGroup =
            AdapterAdminManageGroup(this, mListDataGroup) { action, data ->
                CustomDialogLoading.showProgressDialog(resources.getString(R.string.loading))
                val mModelCreateUser = ModelCreateUser(
                    data.user_code,
                    data.user_fullname,
                    data.user_phone,
                    data.user_email,
                    data.user_address,
                    data.user_image, data.user_name,
                    data.user_password,
                    data.user_rule, data.date_request, if (action == "cancel") 3 else 2
                ).updateUser().toMap()

                val query = mRootRef.orderByChild("user_code").equalTo(data.user_code)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) { // dataSnapshot is the "issue" node with all children with id 0
                            for (dataSnapshot1 in dataSnapshot.children){
                                val postValues = mModelCreateUser.toMap()
                                val childUpdates = HashMap<String, Any>()
                                childUpdates["/${dataSnapshot1.key}"] = postValues
                                mRootRef.updateChildren(childUpdates)
                            }
                        }
                        CustomDialogLoading.dissProgressDialog()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        CustomDialogLoading.dissProgressDialog()
                        Utils.dialogMessage(this@ManageGroupActivity, "error : ${databaseError.message}")
                    }

                })
            }

        rvListUser.apply {
            layoutManager = LinearLayoutManager(this@ManageGroupActivity)
            adapter = mAdapterAdminManageGroup
        }


        onConnectListUser(Constants.USER_TYPE_USER)
    }

    private fun onConnectListUser(userRule: String) {
        CustomDialogLoading.showProgressDialog(resources.getString(R.string.loading))
        mRootRef.orderByChild("user_rule")
            .equalTo(userRule).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    mListDataGroup.clear()
                    if (dataSnapshot.exists()) {
                        val list =
                            dataSnapshot.children.map { it.getValue(ModelCreateUser::class.java)!! }
                        mListDataGroup.addAll(list)
                    } else {
                        Toast.makeText(this@ManageGroupActivity, "ไม่พบผู้ใช้งาน", Toast.LENGTH_SHORT).show()
                    }
                    mAdapterAdminManageGroup.notifyDataSetChanged()

                    CustomDialogLoading.dissProgressDialog()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    CustomDialogLoading.dissProgressDialog()
                    Utils.dialogMessage(
                        this@ManageGroupActivity,
                        "error : ${databaseError.message}"
                    )
                }
            })
    }

    private fun initSetView() {
        this.title = "จัดการผู้ใช้งาน"
    }
}