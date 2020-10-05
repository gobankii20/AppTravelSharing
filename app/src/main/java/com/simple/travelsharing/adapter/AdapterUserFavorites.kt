package com.simple.travelsharing.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simple.travelsharing.R
import com.simple.travelsharing.model.ModelCreateJob
import kotlinx.android.synthetic.main.item_user_favorites.view.*
import java.util.*


class AdapterUserFavorites(
    private var mContext: Context,
    private var mListProduct: ArrayList<ModelCreateJob>,
    private var mOnClick: (String, ModelCreateJob) -> Unit
) : RecyclerView.Adapter<AdapterUserFavorites.ViewHolder>() {

    override fun getItemCount(): Int {
        return mListProduct.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_favorites, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvAdapterDriverName.text = mListProduct[position].driver_name
        holder.tvJobCreate.text = "วันที่จัดงาน ${mListProduct[position].job_create}"
        holder.tvMessage.text = "ชื่อกิจกรรม ${mListProduct[position].job_name}"

        holder.itemView.rootView.setOnClickListener {
            mOnClick.invoke("detail", mListProduct[position])
        }

        holder.viewApprove.setOnClickListener {
            mOnClick.invoke("chat", mListProduct[position])
        }


        viewStatusJob(holder, position)
        holder.viewApprove.visibility = View.GONE
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
        val viewApprove = itemView.viewApprove!!
        val tvStatus = itemView.tvTitleStatus!!
        val tvJobCreate = itemView.tvJobCreate!!
        val tvMessage = itemView.tvMessage!!

    }

}
