package com.anhnt.baseproject.share

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.anhnt.baseproject.R
import com.anhnt.baseproject.databinding.ShareViewBinding

class ShareView : ConstraintLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private val binding: ShareViewBinding = ShareViewBinding.bind(
        LayoutInflater.from(context).inflate(
            R.layout.share_view, this, true
        )
    )

    var shareProvider:((ShareFileDelegate) -> Unit)? = null

    init {
        initViews()
        initListener()
    }

    private fun initViews() {

    }

    private fun initListener() {
        binding.ivFacebook.setOnClickListener {
            shareProvider?.invoke(FacebookShare())
        }

        binding.ivMessenger.setOnClickListener {
            shareProvider?.invoke(MessengerShare())
        }

        binding.ivInstagram.setOnClickListener {
            shareProvider?.invoke(InstagramShare())
        }

        binding.ivTwitter.setOnClickListener {
            shareProvider?.invoke(TwitterShare())
        }

        binding.ivTelegram.setOnClickListener {
            shareProvider?.invoke(TelegramShare())
        }

        binding.ivMore.setOnClickListener {
            shareProvider?.invoke(NormalShare())
        }
    }
}