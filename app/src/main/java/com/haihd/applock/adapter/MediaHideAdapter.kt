package com.haihd.applock.adapter

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haihd.applock.databinding.LayoutItemVideoBinding
import com.haihd.applock.item.ItemImageHide
import com.haihd.applock.item.ItemVideoHide
import com.anhnt.baseproject.adapter.BaseRecyclerAdapter
import java.io.File
import java.text.SimpleDateFormat

class MediaHideAdapter(var context: Context, list: MutableList<Any>) :
    BaseRecyclerAdapter<Any, MediaHideAdapter.ViewHolder>(context, list) {
    var listenerOnClick: ((Int) -> Unit)? = null

    inner class ViewHolder(var binding: LayoutItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: Any) {
            binding.ivCheckOrExpand.isSelected = false
            if (data is ItemVideoHide) {
                var duration: Long = 0
                Glide.with(context)
                    .load(data.decryptPath)
                    .into(binding.imgDetailPhoto)
                //
                data.decryptPath?.let {

                    val file = File(it)
                    val retriever = MediaMetadataRetriever()
                    try {
                        retriever.setDataSource(context, Uri.fromFile(file))
                        val time: String? =
                            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        time?.let { t ->
                            duration = t.toLong()
                        }
                    } catch (e: Exception) {
                    }
                }
                binding.tvDuration.text = if (duration >= 3600000)
                    SimpleDateFormat("hh:mm:ss").format(duration)
                else
                    SimpleDateFormat("mm:ss").format(duration)
                binding.ivPlay.visibility = View.VISIBLE
                binding.tvDuration.visibility = View.VISIBLE
            } else if (data is ItemImageHide) {
                data.bitmap?.let {
                    Glide.with(context).load(it).into(binding.imgDetailPhoto)
                }
                binding.ivPlay.visibility = View.GONE
                binding.tvDuration.visibility = View.GONE
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
        return ViewHolder(LayoutItemVideoBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

}