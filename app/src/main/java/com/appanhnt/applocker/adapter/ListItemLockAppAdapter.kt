package com.appanhnt.applocker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appanhnt.applocker.databinding.LayoutListItemLockAppBinding
import com.appanhnt.applocker.item.ItemListAppLock

class ListItemLockAppAdapter(val context: Context, var list: MutableList<ItemListAppLock>, val callback: ListItemLockAppAdapter.Callback) :
    RecyclerView.Adapter<ListItemLockAppAdapter.ViewHolder>() {
    private var isShowTitle: Boolean = true

    class ViewHolder(var binding: LayoutListItemLockAppBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutListItemLockAppBinding.inflate(
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
        binding.tvTitle.text = data.title
        if (isShowTitle) {
            binding.tvTitle.visibility = View.VISIBLE
        } else {
            binding.tvTitle.visibility = View.GONE
        }
        val adapter = ItemLockAppAdapter(context, data.list, object : ItemLockAppAdapter.Callback {
            override fun onSelected(pos: Int) {
                if (isShowTitle) {
                    for (item in list) {
                        for (app in item.list) {
                            if (app.packageName == data.list[pos].packageName) {
                                notifyItemChanged(list.indexOf(item))
                            }
                        }
                    }
                }
                callback.onSelectItem()
            }
        })
        binding.rcv.adapter = adapter
        if (data.list.isEmpty() || !isShowTitle) {
            binding.icSelectAll.visibility = View.GONE
        } else {
            binding.icSelectAll.visibility = View.VISIBLE
        }
        binding.icSelectAll.setOnClickListener {
            var isSelectAll = true
            for (item in data.list) {
                if (!item.isLocked) {
                    isSelectAll = false
                    break
                }
            }
            for (item in data.list) {
                item.isLocked = !isSelectAll
            }
            callback.onSelectItem()
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setShowTittle(isShowTitle: Boolean) {
        this.isShowTitle = isShowTitle
    }

    interface Callback{
        fun onSelectItem()
    }
}