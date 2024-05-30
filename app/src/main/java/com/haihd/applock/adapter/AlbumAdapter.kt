package com.haihd.applock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haihd.applock.databinding.LayoutDetailPhotoBinding
import com.haihd.applock.item.ItemDetailPhoto

class AlbumAdapter(val context: Context, var list: MutableList<ItemDetailPhoto>) :
    RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
    var listenerOnClick: ((Int) -> Unit)? = null
    var listenerOnLongClick: ((Int) -> Unit)? = null

    class ViewHolder(var binding: LayoutDetailPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutDetailPhotoBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        Glide.with(context)
            .load(data.file.absolutePath)
            .into(holder.binding.imgDetailPhoto)

//        if (data.isSelected) {
//            if (!holder.binding.layoutCheck.isShown) {
//                holder.binding.layoutCheck.visibility = View.VISIBLE
//            }
//            holder.binding.txtNumber.text = data.number.toString()
//        } else {
//            holder.binding.layoutCheck.visibility = View.GONE
//        }
        holder.binding.ivCheckOrExpand.isSelected = data.isSelected
        holder.itemView.setOnLongClickListener {
            listenerOnLongClick?.invoke(holder.adapterPosition)
            true
        }
        holder.itemView.setOnClickListener {
            listenerOnClick?.invoke(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}