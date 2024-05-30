package com.haihd.applock.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haihd.applock.databinding.LayoutItemProtectedBinding
import com.haihd.applock.item.ItemAppLock
import com.anhnt.baseproject.adapter.BaseRecyclerAdapter

class ProtectedAdapter(var context: Context, list: MutableList<ItemAppLock>) :
    BaseRecyclerAdapter<ItemAppLock, ProtectedAdapter.ViewHolder>(context, list) {

    inner class ViewHolder(var binding: LayoutItemProtectedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: ItemAppLock) {
            data.resId?.let {
                Glide.with(context).load(it.toBitmap()).into(binding.imgIc)
            }

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutItemProtectedBinding.inflate(layoutInflater, parent, false))
    }
}