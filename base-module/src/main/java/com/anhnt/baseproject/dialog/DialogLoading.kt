package com.anhnt.baseproject.dialog

import android.content.Context
import android.view.LayoutInflater
import com.anhnt.baseproject.databinding.DialogLoadingBinding

class DialogLoading(context: Context, builder: ExtendBuilder) :

    BaseDialog<DialogLoadingBinding, DialogLoading.ExtendBuilder>(builder, context) {

    class ExtendBuilder(context: Context) : BuilderDialog(context) {

        override fun build(): BaseDialog<*, *> {
            return DialogLoading(context, this)
        }

    }


    override fun initListener() {

    }

    override val viewBinding: DialogLoadingBinding
        get() = DialogLoadingBinding.inflate(LayoutInflater.from(context))
}