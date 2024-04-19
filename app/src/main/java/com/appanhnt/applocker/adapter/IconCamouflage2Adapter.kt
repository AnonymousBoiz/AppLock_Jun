package com.appanhnt.applocker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appanhnt.applocker.databinding.LayoutItemIconCamouflageBinding
import com.appanhnt.applocker.item.ItemGroupIconCamouflage
import com.appanhnt.applocker.item.ItemIconCamouflage
import com.appanhnt.applocker.adapter.IconAdapter

class IconCamouflage2Adapter(
    val context: Context,
    val list: MutableList<ItemGroupIconCamouflage>,
    val callback: Callback
) :
    RecyclerView.Adapter<IconCamouflage2Adapter.ViewHolder>() {

    private var currentItem: ItemGroupIconCamouflage? = null
    private var currentIcon: ItemIconCamouflage? = null
    var onClickListener: ((Int) -> Unit)? = null

    class ViewHolder(val binding: LayoutItemIconCamouflageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemIconCamouflageBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        val data = list[position]
        val adapter: Icon2Adapter
        if (binding.rvIconApp.adapter == null) {
            binding.tvAppName.text = data.name
            binding.rvIconApp.layoutManager =
                GridLayoutManager(context, 4)
            adapter = Icon2Adapter(
                context,
                data.iconList.toMutableList(),
                object : Icon2Adapter.Callback {
                    override fun onSelectIcon(icon: ItemIconCamouflage) {
                        currentItem = data
                        currentIcon = icon
                        callback.onSelectedIcon(icon)
                        notifyDataSetChanged()
                    }
                })
            binding.rvIconApp.adapter = adapter
        } else {
            adapter = binding.rvIconApp.adapter as Icon2Adapter
        }
        if (currentItem == data) {
            adapter.setCurrentItem(currentIcon)
        } else {
            adapter.setCurrentItem(null)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

//    fun getCurrentItem(): ItemGroupIconCamouflage {
//        return currentItem
//    }

    interface Callback {
        fun onSelectedIcon(icon: ItemIconCamouflage)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCurrentIcon(icon: ItemIconCamouflage) {
        currentIcon = icon
        for (group in list) {
            for (item in group.iconList) {
                if (item == currentIcon) {
                    currentItem = group
                    notifyDataSetChanged()
                    return
                }
            }
        }
    }
}