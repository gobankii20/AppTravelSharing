package com.simple.travelsharing.view.user

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.simple.travelsharing.R
import com.simple.travelsharing.adapter.AdapterDriverFavorites
import com.simple.travelsharing.adapter.AdapterUserFavorites
import com.simple.travelsharing.data.SharedPreference
import com.simple.travelsharing.model.ModelCreateJob
import com.simple.travelsharing.model.ModelCreateJobDetail
import com.simple.travelsharing.model.ModelParticipants
import com.simple.travelsharing.utils.CustomDialogLoading
import com.simple.travelsharing.utils.Utils
import com.simple.travelsharing.view.driver.groupChat.GroupChatActivity
import com.simple.travelsharing.view.driver.jobDetail.JobDetailActivity
import kotlinx.android.synthetic.main.custom_toolbar_main.view.*
import kotlinx.android.synthetic.main.fragment_favorites_driver.view.*
import kotlinx.android.synthetic.main.fragment_profile_driver.view.icToolbar

class FavoritesUserFragment : Fragment() {

    private val mListFlavour = ArrayList<ModelCreateJob>()
    private lateinit var adapterUserFavorites: AdapterUserFavorites

    private val mRootRef = FirebaseDatabase.getInstance().reference.child("jobs")

    private val mRootRefParticipants = FirebaseDatabase.getInstance().reference.child("participants")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites_user, container, false)
        // Inflate the layout for this fragment
        initView(view)
        return view.rootView
    }

    private fun initView(view: View?) {
        CustomDialogLoading.newProgressDialog(requireActivity())

        view!!.icToolbar.tvTitle.text = resources.getString(R.string.title_dashboard)
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
        adapterUserFavorites =
            AdapterUserFavorites(requireContext(), mListFlavour) { action, dataObj ->
                if (action == "chat"){
                    val intent = Intent(requireContext(), GroupChatActivity::class.java)
                    requireActivity().startActivity(intent)
                }else{
                    val intent = Intent(requireContext(), JobDetailActivity::class.java)
                    intent.putExtra("action", "userFavorites")
                    intent.putExtra("dataObj",createJobDetail(dataObj))
                    requireActivity().startActivity(intent)
                }
            }

        view.rvListFlavour.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterUserFavorites
        }

        onConnectListMyJob()
    }

    private fun onConnectListMyJob() {
        CustomDialogLoading.showProgressDialog(resources.getString(R.string.loading))

        val query = mRootRefParticipants.orderByChild("user_code").equalTo(SharedPreference(requireContext()).getUserCode())
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mListFlavour.clear()
                if (dataSnapshot.exists()) {
                    for (postSnapshot in dataSnapshot.children) {
                        val exercise = postSnapshot.getValue(ModelParticipants::class.java)!!

                        joinJob(exercise)
                    }
                } else {
                    Utils.dialogMessage(requireActivity(), "ไม่พบกิจกรรมของคุณ")
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

    private fun joinJob(exercise: ModelParticipants) {
        val query = mRootRef.orderByChild("job_code").equalTo(exercise.job_code)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val list =
                        dataSnapshot.children.map { it.getValue(ModelCreateJob::class.java)!! }
                    mListFlavour.addAll(list)
                } else {
                    Utils.dialogMessage(requireActivity(), "ไม่พบกิจกรรม")
                }
                adapterUserFavorites.notifyDataSetChanged()
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
        fun newInstance() = FavoritesUserFragment()
    }
}