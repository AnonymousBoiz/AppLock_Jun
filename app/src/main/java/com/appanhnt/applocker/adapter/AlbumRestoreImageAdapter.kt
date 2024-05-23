package com.appanhnt.applocker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.appanhnt.applocker.R
import com.appanhnt.applocker.databinding.LayoutItemAlbumRestoreImageBinding
import com.appanhnt.applocker.item.ItemAlbumRestoreImages
import com.anhnt.baseproject.adapter.BaseRecyclerAdapter
import java.io.File


class AlbumRestoreImageAdapter(var context: Context, list: MutableList<ItemAlbumRestoreImages>) :
    BaseRecyclerAdapter<ItemAlbumRestoreImages, AlbumRestoreImageAdapter.ViewHolder>(
        context,
        list
    ) {
    var listenerOnClick: ((Int) -> Unit)? = null

    inner class ViewHolder(var binding: LayoutItemAlbumRestoreImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindData(data: ItemAlbumRestoreImages) {
            Glide.with(context).load(data.listPhoto[0].path).into(binding.albumImage1)
            if (data.listPhoto.size > 1) {
                binding.card2.visibility = View.VISIBLE
                Glide.with(context).load(data.listPhoto[1].path).into(binding.albumImage2)
            } else {
                val set = ConstraintSet()
                set.clone(binding.layoutCard)
                set.connect(R.id.card1, ConstraintSet.END, R.id.layout_card, ConstraintSet.END, 0)
                set.applyTo(binding.layoutCard)
                binding.card2.visibility = View.GONE
            }
            binding.txtFolder.text = File(data.path).name
            binding.txtNumber.text = "${data.listPhoto.size} images"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutItemAlbumRestoreImageBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
        holder.itemView.setOnClickListener {
            listenerOnClick?.invoke(holder.adapterPosition)
        }
    }
}