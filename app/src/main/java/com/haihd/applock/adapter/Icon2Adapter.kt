package com.haihd.applock.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.haihd.applock.R
import com.haihd.applock.databinding.LayoutItemIconAppBinding
import com.haihd.applock.item.ItemIconCamouflage
import com.bumptech.glide.Glide

class Icon2Adapter(val context: Context, val list: MutableList<ItemIconCamouflage>, val callback: Callback) :
    RecyclerView.Adapter<Icon2Adapter.ViewHolder>() {

    private var currentItem: ItemIconCamouflage? = null
    var onClickListener: ((Int) -> Unit)? = null

    class ViewHolder(val binding: LayoutItemIconAppBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemIconAppBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        val data = list[position]

        if (data == currentItem) {
            binding.ivSelected.visibility = View.VISIBLE
            binding.layoutSelected.visibility = View.VISIBLE
        } else {
            binding.ivSelected.visibility = View.GONE
            binding.layoutSelected.visibility = View.GONE
        }
        Glide.with(context).load(data.icon).placeholder(R.drawable.loading)
            .error(R.drawable.loading)
            .into(binding.ivIconApp)

        binding.root.setOnClickListener {
            currentItem = data
            callback.onSelectIcon(data)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getCurrentItem(): ItemIconCamouflage? {
        return currentItem
    }

    interface Callback{
        fun onSelectIcon(icon: ItemIconCamouflage)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCurrentItem(icon: ItemIconCamouflage?){
        currentItem = icon
        notifyDataSetChanged()
    }
}