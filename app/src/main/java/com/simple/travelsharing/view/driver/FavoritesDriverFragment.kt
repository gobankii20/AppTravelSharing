package com.simple.travelsharing.view.driver

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
import com.simple.travelsharing.adapter.AdapterDriverFavorites
import com.simple.travelsharing.data.SharedPreference
import com.simple.travelsharing.model.ModelCreateJob
import com.simple.travelsharing.model.ModelCreateJobDetail
import com.simple.travelsharing.utils.CustomDialogLoading
import com.simple.travelsharing.utils.Utils
import com.simple.travelsharing.view.driver.MainActivity.Companion.jobDriverImage
import com.simple.travelsharing.view.driver.jobDetail.JobDetailActivity
import kotlinx.android.synthetic.main.custom_toolbar_main.view.*
import kotlinx.android.synthetic.main.fragment_favorites_driver.view.*
import kotlinx.android.synthetic.main.fragment_profile_driver.view.icToolbar


class FavoritesDriverFragment : Fragment() {

    private val mListFlavour = ArrayList<ModelCreateJob>()
    private lateinit var adapterDriverFlavour: AdapterDriverFavorites

    private val mRootRef = FirebaseDatabase.getInstance().reference.child("jobs")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites_driver, container, false)
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

    private fun setListFlavour(view: View) {
        adapterDriverFlavour = AdapterDriverFavorites(requireContext(), mListFlavour) {
            jobDriverImage = it.driver_image ?: ""
            val intent = Intent(requireContext(), JobDetailActivity::class.java)
            intent.putExtra("action", "driverFavorites")
            intent.putExtra("dataObj", createJobDetail(it))
            requireContext().startActivity(intent)
        }

        view.rvListFlavour.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterDriverFlavour
        }

        onConnectListMyJob()
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

    private fun onConnectListMyJob() {
        CustomDialogLoading.showProgressDialog(resources.getString(R.string.loading))
        val query = mRootRef.orderByChild("driver_code")
            .equalTo(SharedPreference(requireActivity()).getUserCode())
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
                adapterDriverFlavour.notifyDataSetChanged()
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
        fun newInstance() = FavoritesDriverFragment()
    }
}