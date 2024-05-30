package com.haihd.applock.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import com.haihd.applock.R
import com.haihd.applock.databinding.LayoutDialogQuestionBinding
import com.haihd.applock.key.KeyQuestion
import com.anhnt.baseproject.utils.PreferencesUtils

class DialogQuestion(context: Context, style: Int) : Dialog(context, style) {
    lateinit var binding: LayoutDialogQuestionBinding
    var listenerClickOkay: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
        initListener()
    }

    private fun initData() {

    }

    override fun show() {
        if (!isShowing){
            super.show()
        }
    }

    override fun dismiss() {
        if (isShowing){
            super.dismiss()
        }
    }

    private fun initListener() {
        binding.btnOk.setOnClickListener {
            if (binding.edtAnswer.text.toString()!="") {
                listenerClickOkay?.invoke(binding.edtAnswer.text.toString())
            }
        }
    }

    private fun initView() {
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        binding = LayoutDialogQuestionBinding.inflate(LayoutInflater.from(context))
        binding.txtQuestion.text = PreferencesUtils.getString(KeyQuestion.KEY_QUESTION,context.getString(
            R.string.recovery_code))
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        binding.edtAnswer.requestFocus()
        setContentView(binding.root)
    }
}