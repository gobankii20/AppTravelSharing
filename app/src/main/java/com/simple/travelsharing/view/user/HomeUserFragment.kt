package com.simple.travelsharing.view.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.simple.travelsharing.R
import com.simple.travelsharing.adapter.AdapterUserHome
import com.simple.travelsharing.data.SharedPreference
import com.simple.travelsharing.model.ModelCreateJob
import com.simple.travelsharing.model.ModelCreateJobDetail
import com.simple.travelsharing.model.ModelParticipants
import com.simple.travelsharing.utils.CustomDialogLoading
import com.simple.travelsharing.utils.Utils
import com.simple.travelsharing.utils.createJobRequest
import com.simple.travelsharing.view.driver.jobDetail.JobDetailActivity
import kotlinx.android.synthetic.main.custom_toolbar_main.view.*
import kotlinx.android.synthetic.main.fragment_favorites_driver.view.rvListFlavour
import kotlinx.android.synthetic.main.fragment_profile_driver.view.icToolbar

class HomeUserFragment : Fragment() {

    private val mListFlavour = ArrayList<ModelCreateJob>()
    private lateinit var adapterUserHome: AdapterUserHome

    private val mRootRef = FirebaseDatabase.getInstance().reference.child("jobs")

    private val mRootRefJobs = FirebaseDatabase.getInstance().reference.child("jobs")

    private val mRootRefParticipants =
        FirebaseDatabase.getInstance().reference.child("participants")

    private var mListParticipants = ArrayList<ModelParticipants>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_user, container, false)
        // Inflate the layout for this fragment
        initView(view)
        return view.rootView
    }

    private fun initView(view: View?) {
        CustomDialogLoading.newProgressDialog(requireContext())

        view!!.icToolbar.tvTitle.text = resources.getString(R.string.title_home)
        view.icToolbar.layBackInvisible.visibility = View.GONE

        setListFlavour(view)
    }


    private fun createJobDetail(it: ModelCreateJob): ModelCreateJobDetail {
        return ModelCreateJobDetail(
            it.job_code,
            it.job_create, it.job_name,
            it.job_detail, it.job_count,
            it.job_date, it.job_place,
            it.job_latitude, it.job_longitude,
            it.job_status, it.driver_name,
            "", it.driver_code,
            it.request_count
        )
    }

    private fun setListFlavour(view: View) {
        adapterUserHome = AdapterUserHome(requireContext(), mListFlavour) { action, dataObj ->
            if (action == "detail") {
                val intent = Intent(requireContext(), JobDetailActivity::class.java)
                intent.putExtra("action", "userDetailJob")
                intent.putExtra("dataObj", createJobDetail(dataObj))
                requireActivity().startActivity(intent)
            } else {
                actionRequestActivity(dataObj.job_code)
            }
        }

        view.rvListFlavour.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterUserHome
        }


        val query = mRootRefParticipants.orderByChild("user_code")
            .equalTo(SharedPreference(requireContext()).getUserCode())
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mListParticipants.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val exercise: ModelParticipants =
                        postSnapshot.getValue(ModelParticipants::class.java)!!
                    mListParticipants.add(exercise)
                }
                onConnectListUser()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                CustomDialogLoading.dissProgressDialog()
                Utils.dialogMessage(
                    requireActivity(),
                    "error : ${databaseError.message}"
                )
            }
        })
    }


    private fun actionRequestActivity(jobCode: String?) {
        CustomDialogLoading.showProgressDialog(requireContext().resources.getString(R.string.loading))

        val query = mRootRef.orderByChild("job_code").equalTo(jobCode)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val list =
                        dataSnapshot.children.map { it.getValue(ModelCreateJob::class.java)!! }

                    if (list[0].job_status == "1") {
                        if (list[0].job_count?.toInt() ?: 0 <= list[0].request_count ?: 0)
                            requestJob(jobCode, list, dataSnapshot)
                        else
                            Utils.dialogMessage(requireActivity(), "ไม่สามารถเข้าร่วมกิจกรรมนี้ได้\nเนื่องจำนวนที่เปิดรับเต็มแล้ว")
                    } else {
                        Utils.dialogMessage(
                            requireActivity(),
                            "ไม่สามารถเข้าร่วมกิจกรรมนี้ได้\nเนื่องจากกิจกรรมนี้ปิดไปแล้ว"
                        )
                    }
                } else {
                    Utils.dialogMessage(
                        requireActivity(),
                        "ไม่สามารถเข้าร่วมกิจกรรมนี้ได้\nเนื่องจากคุณได้ขอเข้าร่วมไปแล้ว"
                    )
                }
                CustomDialogLoading.dissProgressDialog()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                CustomDialogLoading.dissProgressDialog()
                Utils.dialogMessage(
                    requireActivity(),
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
            jobCode, SharedPreference(requireContext()).getUserCode(),
            SharedPreference(requireContext()).getFullName(),
            SharedPreference(requireContext()).getImage(),
            SharedPreference(requireContext()).getEmail(), "1",
            Utils.getDateTimeCurrent()
        ).createJobRequest()

        for (postSnapshot in dataSnapshot.children) {
            val postValues = requestJobDetail(listData[0]).toMap()
            val childUpdates = HashMap<String, Any>()
            childUpdates["/${postSnapshot.key}"] = postValues
            mRootRefJobs.updateChildren(childUpdates)
        }

        Utils.dialogMessage(
            requireContext(),
            "ขอเข้าร่วมกิจกรรมสำเร็จ\nกรุณารอการดำเนินการตรวจสอบจาก driver เพื่อเข้าร่วมกิจกรรม"
        ) {
        }

    }

    private fun requestJobDetail(it: ModelCreateJob): ModelCreateJob {
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

    private fun onConnectListUser() {
        context?.resources?.getString(R.string.loading)?.let {
            CustomDialogLoading.showProgressDialog(it)
        }
        val query = mRootRef.orderByChild("job_create")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mListFlavour.clear()
                if (dataSnapshot.exists()) {
                    for (postSnapshot in dataSnapshot.children) {
                        val exercise: ModelCreateJob =
                            postSnapshot.getValue(ModelCreateJob::class.java)!!

                        val checkAddJob =
                            mListParticipants.filter { it.job_code == exercise.job_code }
                        if (checkAddJob.isEmpty())
                            mListFlavour.add(exercise)
                    }
                } else {
                    Utils.dialogMessage(requireActivity(), "ไม่พบกิจกรรม")
                }
                adapterUserHome.notifyDataSetChanged()

                CustomDialogLoading.dissProgressDialog()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                CustomDialogLoading.dissProgressDialog()
                Utils.dialogMessage(
                    requireActivity(),
                    "error : ${databaseError.message}"
                )
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeUserFragment()
    }
}