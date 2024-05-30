package com.haihd.applock.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haihd.applock.databinding.LayoutImageHideBinding
import com.haihd.applock.item.ItemImageHide
import com.anhnt.baseproject.adapter.BaseRecyclerAdapter

class ImageHideAdapter(var context: Context, list: MutableList<ItemImageHide>) :
    BaseRecyclerAdapter<ItemImageHide, ImageHideAdapter.ViewHolder>(context, list) {
    var listenerOnClick: ((Int) -> Unit)? = null

    inner class ViewHolder(var binding: LayoutImageHideBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: ItemImageHide) {
            binding.ivCheckOrExpand.isSelected = false
            data.bitmap?.let {
                Glide.with(context).load(it).into(binding.imgDetailPhoto)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
        holder.itemView.setOnClickListener {
            listenerOnClick?.invoke(holder.adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutImageHideBinding.inflate(layoutInflater, parent, false))
    }

}