package com.simple.travelsharing.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simple.travelsharing.R
import com.simple.travelsharing.model.ModelCreateJob
import com.simple.travelsharing.utils.Utils
import kotlinx.android.synthetic.main.item_driver_favorites.view.*
import kotlinx.android.synthetic.main.item_driver_favorites.view.tvAdapterDriverName
import kotlinx.android.synthetic.main.item_user_home.view.*
import java.lang.Exception
import java.util.*


class AdapterUserHome(
    private var mContext: Context,
    private var mListProduct: ArrayList<ModelCreateJob>,
    private var mOnClick:(String, ModelCreateJob) -> Unit
) : RecyclerView.Adapter<AdapterUserHome.ViewHolder>() {

    override fun getItemCount(): Int {
        return mListProduct.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_home, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvAdapterDriverName.text = mListProduct[position].driver_name

        holder.tvDateCreate.text = "วันที่โพสต์ "+mListProduct[position].job_create + " น."
        holder.tvPostDate.text = "วันที่จัดงาน ${mListProduct[position].job_date}"
        holder.tvJobName.text = "ชื่อกิจกรรม ${mListProduct[position].job_name}"

        holder.tvJobCount.text = "จำนวนคนเปิดรับ ${mListProduct[position].job_count} คน"
        holder.tvJobRequest.text = "จำนวนคนขอเข้าร่วม ${mListProduct[position].request_count} คน"

        try {
            Utils.setImageView(
                mContext,
                mListProduct[position].driver_image ?: "",
                holder.ivProfile
            )
        }catch (ex: Exception){}

        viewStatusJob(holder, position)

        holder.itemView.rootView.setOnClickListener {
            mOnClick.invoke("detail",mListProduct[position])
        }

        holder.viewRequestGroup.setOnClickListener {
            mOnClick.invoke("request",mListProduct[position])
        }
    }

    private fun viewStatusJob(holder: ViewHolder, position: Int) {
        when (mListProduct[position].job_status) {
            "1" -> {
                holder.tvStatus.text = "สถานะ : เปิดลงทะเบียน"
                holder.tvStatus.setTextColor(mContext.resources.getColor(R.color.colorStatusPending))
            }
            "2" -> {
                holder.tvStatus.text = "สถานะ : ปิดรับลงทะเบียน"
                holder.tvStatus.setTextColor(mContext.resources.getColor(R.color.colorStatusSuccess))
            }
            "3" -> {
                holder.tvStatus.text = "สถานะ : ยกเลิกกิจกรรม"
                holder.tvStatus.setTextColor(mContext.resources.getColor(R.color.colorStatusCancel))
            }
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAdapterDriverName = itemView.tvAdapterDriverName!!

        val ivProfile = itemView.ivProfileUser!!
        val tvDateCreate = itemView.tvDateCreate!!
        val tvPostDate = itemView.tvPostDateUser!!
        val tvJobName = itemView.tvJobName!!
        val tvJobCount = itemView.tvJobCount!!
        val tvJobRequest = itemView.tvJobRequest!!
        val tvStatus = itemView.tvStatus!!
        val viewRequestGroup = itemView.viewRequestGroup!!

    }

}
