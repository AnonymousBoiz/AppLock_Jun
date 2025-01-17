package com.appanhnt.applocker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.appanhnt.applocker.databinding.LayoutItemVideoBinding
import com.appanhnt.applocker.item.ItemVideo
import com.appanhnt.applocker.utils.AppUtil
import com.anhnt.baseproject.adapter.BaseRecyclerAdapter

class AdapterVideo(var context: Context, list: MutableList<ItemVideo>) :
    BaseRecyclerAdapter<ItemVideo, AdapterVideo.ViewHolder>(context, list) {
    var listenerOnClick: ((Int) -> Unit)? = null

    inner class ViewHolder(var binding: LayoutItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bindData(data: ItemVideo) {
            Glide.with(context)
                .load(data.file.absolutePath)
                .into(binding.imgDetailPhoto)
            binding.tvDuration.text = AppUtil.convertDuration(data.duration)
            //
            binding.ivCheckOrExpand.isSelected = data.isSelected
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
        holder.itemView.setOnClickListener {
            listenerOnClick?.invoke(holder.adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutItemVideoBinding.inflate(layoutInflater, parent, false))
    }
}