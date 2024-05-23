package com.appanhnt.applocker.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appanhnt.applocker.databinding.LayoutSetSameImageBinding
import com.appanhnt.applocker.item.ItemSameImage
import com.appanhnt.applocker.item.ItemSetSameImage
import com.appanhnt.applocker.utils.RecycleViewUtils
import com.anhnt.baseproject.adapter.BaseRecyclerAdapter

class SetSameImageAdapter(var context: Context, list: MutableList<ItemSetSameImage>) :
    BaseRecyclerAdapter<ItemSetSameImage, SetSameImageAdapter.ViewModel>(context, list) {
    inner class ViewModel(var binding: LayoutSetSameImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: ItemSetSameImage) {
            if (data.listImage.isNotEmpty()) {
                val listSame = mutableListOf<ItemSameImage>()
                for (it in data.listImage) {
                    listSame.add(it)
                }
                val adapterSameImage = SameImageAdapter(context, listSame)
                adapterSameImage.listenerOnClick = {
                    listSame[it].isSelected = !listSame[it].isSelected
                    adapterSameImage.notifyItemChanged(it)
                }
                binding.rclSameImage.adapter = adapterSameImage
                RecycleViewUtils.clearAnimation(binding.rclSameImage)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        holder.bindData(list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
        return ViewModel(LayoutSetSameImageBinding.inflate(layoutInflater, parent, false))
    }
}