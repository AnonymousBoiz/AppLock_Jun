package com.appanhnt.applocker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.appanhnt.applocker.R
import com.appanhnt.applocker.databinding.LayoutItemAppLockBinding
import com.appanhnt.applocker.databinding.LayoutTxtBinding
import com.appanhnt.applocker.item.ItemAppLock

class AppLockAdapter(val context: Context, var list: MutableList<ItemAppLock>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listenerClickItem: ((Int) -> Unit)? = null
    var textSearch = ""

    class ViewHolderType1(val bindingType1: LayoutItemAppLockBinding) :
        RecyclerView.ViewHolder(bindingType1.root)

    class ViewHolderType2(val bindingType2: LayoutTxtBinding) :
        RecyclerView.ViewHolder(bindingType2.root)

    //
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                ViewHolderType2(
                    LayoutTxtBinding.inflate(LayoutInflater.from(context), parent, false)
                )
            }
            else -> {
                ViewHolderType1(
                    LayoutItemAppLockBinding.inflate(LayoutInflater.from(context), parent, false)
                )
            }
        }
    }

    //
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = list[position]
        holder.apply {
            when (holder) {
                is ViewHolderType1 -> {
                    data.resId?.let {
                        Glide.with(context).load(it.toBitmap()).into(holder.bindingType1.icApp)
                    }
                    holder.bindingType1.txtNameApp.text = data.name
                    holder.bindingType1.icLocked.setImageResource(if (!data.isLocked) R.drawable.ic_unlock else R.drawable.ic_lock)
                    holder.itemView.setOnClickListener {
                        listenerClickItem?.invoke(position)
                    }
                    if (data.name.contains(textSearch, true)) {
                        holder.bindingType1.container.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                    } else {
                        holder.bindingType1.container.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 0)
                    }
                }
                is ViewHolderType2 -> {
                    holder.bindingType2.txt.text = data.name
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].packageName) {
            null -> {
                1
            }
            else -> {
                0
            }
        }
    }

    //
    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun search(text: String){
        textSearch = text
        notifyDataSetChanged()
    }
}