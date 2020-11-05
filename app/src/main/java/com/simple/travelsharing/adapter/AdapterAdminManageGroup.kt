package com.simple.travelsharing.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.simple.travelsharing.R
import com.simple.travelsharing.model.ModelCreateUser
import com.simple.travelsharing.utils.Utils
import kotlinx.android.synthetic.main.item_admin_manage_group.view.*
import kotlinx.android.synthetic.main.item_driver_favorites.view.tvAdapterDriverName
import kotlinx.android.synthetic.main.item_request_job.view.ivAccept
import kotlinx.android.synthetic.main.item_request_job.view.ivCancel
import kotlinx.android.synthetic.main.item_request_job.view.tvStatus
import java.lang.Exception
import java.util.*


class AdapterAdminManageGroup(
    private var mContext: Context,
    private var mListProduct: ArrayList<ModelCreateUser>,
    private var mOnClick: (String, ModelCreateUser) -> Unit
) : RecyclerView.Adapter<AdapterAdminManageGroup.ViewHolder>() {

    override fun getItemCount(): Int {
        return mListProduct.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_manage_group, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvAdapterDriverName.text = mListProduct[position].user_fullname

        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.default_pic)
        requestOptions.error(R.mipmap.ic_launcher_round)
        requestOptions.diskCacheStrategy

       try {
            Glide.with(mContext)
                .load(Utils.convertBase64ToImage(mListProduct[position].user_image?:""))
                .apply(requestOptions)
                .into(holder.ivProfile)
        }catch (ex:Exception){
           ex.printStackTrace()
       }

        when (mListProduct[position].status) {
            1 -> {
                holder.tvStatus.text = "สถานะ : รอดำเนินการ"
                holder.tvStatus.setTextColor(mContext.resources.getColor(R.color.colorStatusPending))
                holder.ivAccept.visibility = View.VISIBLE
                holder.ivCancel.visibility = View.VISIBLE
            }
            2 -> {
                holder.tvStatus.text = "สถานะ : อนุมัติ"
                holder.tvStatus.setTextColor(mContext.resources.getColor(R.color.colorStatusSuccess))
                holder.ivAccept.visibility = View.GONE
            }
            3 -> {
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

        if (mListProduct[position].image_card!!.isNotEmpty()){
            holder.tvFullScreen.visibility = View.VISIBLE
        }else {
            holder.tvFullScreen.visibility = View.GONE
        }

        holder.tvFullScreen.setOnClickListener {
            if (mListProduct[position].image_card?.isNotEmpty()!!){
                Utils.showAlertDialogFullScreen(mContext, Utils.convertBase64ToImage(mListProduct[position].image_card!!))
            }
        }
        holder.itemView.ivCancel.setOnClickListener {
            mOnClick.invoke("cancel", mListProduct[position])
        }

        holder.itemView.ivAccept.setOnClickListener {
            mOnClick.invoke("accept", mListProduct[position])
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAdapterDriverName = itemView.tvAdapterDriverName!!
        val ivCancel = itemView.ivCancel!!
        val ivAccept = itemView.ivAccept!!
        val tvStatus = itemView.tvStatus!!
        val ivProfile = itemView.ivProfile!!
        val tvFullScreen = itemView.tvFullScreen!!

    }

}
