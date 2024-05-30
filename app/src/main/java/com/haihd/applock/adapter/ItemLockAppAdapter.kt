package com.haihd.applock.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.haihd.applock.databinding.LayoutItemLockAppBinding
import com.haihd.applock.item.ItemAppLock
import com.bumptech.glide.Glide

class ItemLockAppAdapter(val context: Context, var list: MutableList<ItemAppLock>, val callback: Callback) :
    RecyclerView.Adapter<ItemLockAppAdapter.ViewHolder>() {
    var listenerOnClick: ((Int) -> Unit)? = null
    class ViewHolder(var binding: LayoutItemLockAppBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemLockAppBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        val binding = holder.binding
        Glide.with(context).load(data.resId).into(binding.ivIcon)
        binding.tvAppName.text = data.name
        binding.ivSelect.isSelected = data.isLocked
        binding.root.setOnClickListener {
            data.isLocked = !data.isLocked
            notifyItemChanged(position)
            callback.onSelected(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface Callback{
        fun onSelected(pos: Int)
    }
}