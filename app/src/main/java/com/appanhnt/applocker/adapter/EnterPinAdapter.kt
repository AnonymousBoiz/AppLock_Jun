package com.appanhnt.applocker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appanhnt.applocker.R
import com.appanhnt.applocker.databinding.LayoutItemEnterPinBinding
import com.appanhnt.applocker.item.ItemEnterPin
import com.appanhnt.applocker.utils.ThemeUtils

class EnterPinAdapter(val context: Context, var list: MutableList<ItemEnterPin>) :
    RecyclerView.Adapter<EnterPinAdapter.ViewHolder>() {

    class ViewHolder(var binding: LayoutItemEnterPinBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemEnterPinBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        val pinNormal =
            ThemeUtils.getImageTheme(context, ThemeUtils.getFolderNameTheme(), "pin_normal")
        val pinHighLight =
            ThemeUtils.getImageTheme(context, ThemeUtils.getFolderNameTheme(), "pin_high_light")
        holder.binding.icCircle.apply {
            setImageResource(
                if (data.isEnterPin)
                    R.drawable.ic_enter_pin_right
                else
                    R.drawable.ic_enter_pin_normal
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}