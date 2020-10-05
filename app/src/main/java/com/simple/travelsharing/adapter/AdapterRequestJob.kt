package com.simple.travelsharing.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simple.travelsharing.R
import com.simple.travelsharing.model.ModelParticipants
import com.simple.travelsharing.utils.Utils
import kotlinx.android.synthetic.main.item_driver_favorites.view.*
import kotlinx.android.synthetic.main.item_driver_favorites.view.tvAdapterDriverName
import kotlinx.android.synthetic.main.item_request_job.view.*
import java.lang.Exception
import java.util.*


class AdapterRequestJob(
    private var mContext: Context,
    private var mListProduct: ArrayList<ModelParticipants>,
    private var mOnClick: (String, ModelParticipants) -> Unit
) : RecyclerView.Adapter<AdapterRequestJob.ViewHolder>() {

    override fun getItemCount(): Int {
        return mListProduct.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_request_job, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvAdapterDriverName.text = mListProduct[position].user_fullname

        try {
            Utils.setImageView(mContext, mListProduct[position].user_image ?: "", holder.ivProfile)
        }catch (ex:Exception){}
        holder.tvDateRequest.text =  "วันที่ขอเข้าร่วม ${mListProduct[position].request_date}"


        when (mListProduct[position].status_request) {
            "3" -> {
                holder.tvStatus.text = "สถานะ : อนุมัติ"
                holder.tvStatus.setTextColor(mContext.resources.getColor(R.color.colorStatusSuccess))
                holder.ivAccept.visibility = View.GONE
            }
            "2" -> {
                holder.tvStatus.text = "สถานะ : ไม่อนุมัติ"
                holder.tvStatus.setTextColor(mContext.resources.getColor(R.color.colorStatusCancel))
                holder.ivAccept.visibility = View.GONE
                holder.ivCancel.visibility = View.GONE
            }
            else -> {
                holder.tvStatus.text = "สถานะ : รอดำเนินการ"
                holder.tvStatus.setTextColor(mContext.resources.getColor(R.color.colorStatusPending))
                holder.ivAccept.visibility = View.VISIBLE
                holder.ivCancel.visibility = View.VISIBLE
            }
        }

        holder.itemView.ivCancel.setOnClickListener {
            mOnClick.invoke("cancel", mListProduct[position])
        }

        holder.itemView.ivAccept.setOnClickListener {
            mOnClick.invoke("approve", mListProduct[position])
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAdapterDriverName = itemView.tvAdapterDriverName!!
        val ivCancel = itemView.ivCancel!!
        val ivAccept = itemView.ivAccept!!
        val tvStatus = itemView.tvStatus!!

        val ivProfile = itemView.ivProfileRequest!!
        val tvDateRequest = itemView.tvDateRequest!!

    }

}
