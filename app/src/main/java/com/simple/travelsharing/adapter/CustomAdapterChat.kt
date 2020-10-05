package com.simple.travelsharing.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simple.travelsharing.R
import com.simple.travelsharing.model.ModelChat
import com.simple.travelsharing.model.ModelGroupChat
import com.simple.travelsharing.utils.Utils
import kotlinx.android.synthetic.main.item_list_chat.view.*
import java.lang.Exception
import java.util.*

class CustomAdapterChat(
    private var mContext: Context,
    private var mListProduct: ArrayList<ModelGroupChat>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_CHAT_DRIVER = 0
        private const val TYPE_CHAT_USER = 1
    }

    override fun getItemCount(): Int {
        return mListProduct.size
    }

    override fun getItemViewType(position: Int): Int {
        return mListProduct[position].chatType ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CHAT_DRIVER -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_chat, parent, false)
                return ViewHolder(v)
            }
            TYPE_CHAT_USER -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_user, parent, false)
                return ViewHolderUser(v)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.tvTitleDate.text = mListProduct[position].chat_date
            holder.tvMessage.text = mListProduct[position].chat_message
            holder.tvTime.text = mListProduct[position].chat_date+" "+mListProduct[position].chat_time

            try {
                Utils.setImageView(
                    mContext,
                    mListProduct[position].user_image ?: "",
                    holder.ivProfile
                )
            }catch (ex: Exception){}

        } else if (holder is ViewHolderUser) {
            holder.tvTitleDate.text = mListProduct[position].chat_date
            holder.tvMessage.text = mListProduct[position].chat_message
            holder.tvTime.text = mListProduct[position].chat_date+" "+mListProduct[position].chat_time

            try {
                Utils.setImageView(
                    mContext,
                    mListProduct[position].user_image ?: "",
                    holder.ivProfile
                )
            }catch (ex: Exception){}

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitleDate = itemView.tvTitleDate!!
        val tvMessage = itemView.tvMessage!!
        val tvTime = itemView.tvTime!!
        val ivProfile = itemView.ivProfile!!
    }

    class ViewHolderUser(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitleDate = itemView.tvTitleDate!!
        val tvMessage = itemView.tvMessage!!
        val tvTime = itemView.tvTime!!
        val ivProfile = itemView.ivProfile!!
    }

}