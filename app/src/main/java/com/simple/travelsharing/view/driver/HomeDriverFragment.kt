package com.simple.travelsharing.view.driver

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.simple.travelsharing.R
import com.simple.travelsharing.adapter.AdapterDriverFavorites
import com.simple.travelsharing.adapter.AdapterDriverHome
import com.simple.travelsharing.model.ModelCreateJob
import com.simple.travelsharing.model.ModelCreateJobDetail
import com.simple.travelsharing.model.ModelCreateUser
import com.simple.travelsharing.model.ModelParticipants
import com.simple.travelsharing.utils.CustomDialogLoading
import com.simple.travelsharing.utils.Utils
import com.simple.travelsharing.utils.createJobRequest
import com.simple.travelsharing.view.driver.createJob.CreateJobActivity
import com.simple.travelsharing.view.driver.jobDetail.JobDetailActivity
import kotlinx.android.synthetic.main.custom_toolbar_main.view.*
import kotlinx.android.synthetic.main.fragment_favorites_driver.view.*
import kotlinx.android.synthetic.main.fragment_favorites_driver.view.rvListFlavour
import kotlinx.android.synthetic.main.fragment_home_driver.view.*
import kotlinx.android.synthetic.main.fragment_profile_driver.view.icToolbar

class HomeDriverFragment : Fragment() {

    private val mListFlavour = ArrayList<ModelCreateJob>()
    private lateinit var adapterDriverHome : AdapterDriverHome

    private val mRootRef = FirebaseDatabase.getInstance().reference.child("jobs")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home_driver, container, false)
        // Inflate the layout for this fragment
        initView(view)
        return view.rootView
    }

    private fun initView(view: View) {
        CustomDialogLoading.newProgressDialog(requireActivity())

        view.icToolbar.tvTitle.text = resources.getString(R.string.title_home)
        view.icToolbar.layBackInvisible.visibility = View.GONE

        setListFlavour(view)
        initOnClick(view)

//        ModelParticipants("134",
//        "839","User002",
//            "iVBORw0KGgoAAAANSUhEUgAAAPAAAADwCAMAAAAJixmgAAAA3lBMVEVHcEw5GwE6HAE8HQA3GQEzFQE3GgFkKQA7HQH",
//            "gobankii20@gmail.com",
//            "1",Utils.getDateCurrent()).createJobRequest()
    }

    private fun initOnClick(view: View) {
        view.floatingButtonCreate.setOnClickListener {
            val intentCreateJob = Intent(requireActivity(), CreateJobActivity::class.java)
            requireActivity().startActivity(intentCreateJob)
        }
    }

    private fun setListFlavour(view: View) {
        adapterDriverHome = AdapterDriverHome(requireContext(), mListFlavour){
            val intent = Intent(requireContext(), JobDetailActivity::class.java)
            intent.putExtra("action","hideButton")
            intent.putExtra("dataObj", createJobDetail(it))
            requireActivity().startActivity(intent)
        }

        view.rvListFlavour.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterDriverHome
        }


        onConnectListUser()
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

    private fun onConnectListUser() {
        CustomDialogLoading.showProgressDialog(resources.getString(R.string.loading))
        val query = mRootRef.orderByChild("job_create")
        query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    mListFlavour.clear()
                    if (dataSnapshot.exists()) {
                        val list =
                            dataSnapshot.children.map { it.getValue(ModelCreateJob::class.java)!! }
                        mListFlavour.addAll(list)
                    } else {
                        Utils.dialogMessage(requireActivity(), "ไม่พบกิจกรรม")
                    }
                    adapterDriverHome.notifyDataSetChanged()

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
        fun newInstance() = HomeDriverFragment()
    }
}