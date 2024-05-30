package com.haihd.applock.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.haihd.applock.databinding.ItemLanguageBinding
import com.haihd.applock.item.ItemLanguage

class LanguageAdapter(val context: Context, val list: MutableList<ItemLanguage>) :
    RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    private var currentItem: ItemLanguage = list[0]
    var onClickListener: ((Int) -> Unit)? = null

    class ViewHolder(val binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLanguageBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        val data = list[position]

        binding.tvLanguage.text = data.name
        binding.imgFlag.setImageResource(data.flag)

        if (data == currentItem) {
            binding.container.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4D7EFF"));
            binding.tvLanguage.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            binding.container.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#383856"));
            binding.tvLanguage.setTextColor(Color.parseColor("#FFFFFF"))
        }

        binding.root.setOnClickListener {
            currentItem = data
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getCurrentItem(): ItemLanguage {
        return currentItem
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCurrentItem(code: String){
        for (item in list) {
            if (item.code == code) {
                currentItem = item
                notifyDataSetChanged()
                return
            }
        }
    }
}