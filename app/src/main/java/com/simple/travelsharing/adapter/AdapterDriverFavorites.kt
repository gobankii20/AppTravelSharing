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
import java.lang.Exception
import java.util.*


class AdapterDriverFavorites(
    private var mContext: Context,
    private var mListProduct: ArrayList<ModelCreateJob>,
    private var mOnClick: (ModelCreateJob) -> Unit
) : RecyclerView.Adapter<AdapterDriverFavorites.ViewHolder>() {

    override fun getItemCount(): Int {
        return mListProduct.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_driver_favorites, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvAdapterDriverName.text = mListProduct[position].driver_name

        holder.tvPostCreate.text = "วันที่โพสต์ " + mListProduct[position].job_create + " น."
        holder.tvPostDate.text = "วันที่จัดงาน ${mListProduct[position].job_date}"
        holder.tvMessage.text = "ชื่อกิจกรรม ${mListProduct[position].job_name}"

        holder.tvCountOpen.text = "จำนวนคนเปิดรับ ${mListProduct[position].job_count} คน"
        holder.tvCount.text = "จำนวนคนขอเข้าร่วม ${mListProduct[position].request_count} คน"

        try {
            Utils.setImageView(
                mContext,
                mListProduct[position].driver_image ?: "",
                holder.ivProfile
            )
        } catch (ex: Exception) {
        }

        holder.itemView.rootView.setOnClickListener {
            mOnClick.invoke(mListProduct[position])
        }

        viewStatusJob(holder, position)
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
        val ivProfile = itemView.ivProfile!!
        val tvPostCreate = itemView.tvPostCreate!!
        val tvPostDate = itemView.tvPostDate!!
        val tvMessage = itemView.tvMessage!!
        val tvCountOpen = itemView.tvCountOpen!!
        val tvCount = itemView.tvCount!!
        val tvStatus = itemView.tvStatusFavorites!!

    }

}
