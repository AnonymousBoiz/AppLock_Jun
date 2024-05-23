package com.appanhnt.applocker.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appanhnt.applocker.databinding.LayoutItemRestoreAudioBinding
import com.appanhnt.applocker.item.ItemAudioRestore
import com.appanhnt.applocker.utils.AppUtil
import com.anhnt.baseproject.adapter.BaseRecyclerAdapter

class DetailAlbumRestoreAudiosAdapter(var context: Context, list: MutableList<ItemAudioRestore>) :
    BaseRecyclerAdapter<ItemAudioRestore, DetailAlbumRestoreAudiosAdapter.ViewHolder>(
        context,
        list
    ) {
    var listenerOnClick: ((Int) -> Unit)? = null

    inner class ViewHolder(var binding: LayoutItemRestoreAudioBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: ItemAudioRestore) {
            binding.txtDuration.text = AppUtil.convertDuration(data.duration)
            if (data.isSelected) {
                if (!binding.layoutCheck.isShown) {
                    binding.layoutCheck.visibility = View.VISIBLE
                }
                binding.txtNumber.text = data.number.toString()
            } else {
                binding.layoutCheck.visibility = View.GONE
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
        return ViewHolder(LayoutItemRestoreAudioBinding.inflate(layoutInflater, parent, false))
    }
}