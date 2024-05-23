package com.appanhnt.applocker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.appanhnt.applocker.R
import com.appanhnt.applocker.activity.icon.IconCamouflageActivity
import com.appanhnt.applocker.interfaces.OnItemIconListener
import com.bumptech.glide.Glide


class IconAdapter(
    appName: String,
    context: Context,
    mIconApps: ArrayList<Int>,
    onItemIconListener: OnItemIconListener
) :
    RecyclerView.Adapter<IconAdapter.MyViewHolder?>() {
    private var appName: String
    private var context: Context
    private var mIconApps: ArrayList<Int>
    private var onItemIconListener: OnItemIconListener
    fun getContext(): Context {
        return context
    }

    fun setContext(context: Context) {
        this.context = context
    }

    fun getAppName(): String {
        return appName
    }

    fun setAppName(str: String) {
        appName = str
    }

    fun getMIconApps(): ArrayList<Int> {
        return mIconApps
    }

    fun setMIconApps(arrayList: ArrayList<Int>) {
        mIconApps = arrayList
    }

    fun getOnItemIconListener(): OnItemIconListener {
        return onItemIconListener
    }

    fun setOnItemIconListener(onItemIconListener: OnItemIconListener) {
        this.onItemIconListener = onItemIconListener
    }

    init {
        this.context = context
        this.appName = appName
        this.mIconApps = mIconApps
        this.onItemIconListener = onItemIconListener
    }
    inner class MyViewHolder(iconAdapter: IconAdapter, view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        /* synthetic */ val `this$0`: IconAdapter

        init {
            `this$0` = iconAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val myView: View =
            LayoutInflater.from(context).inflate(R.layout.layout_item_icon_app, parent, false)
        return MyViewHolder(this, myView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
        val view = holder.itemView
        val num = mIconApps[i]
        if (view.context is IconCamouflageActivity) {
            val context = view.context
            if (num == (context as IconCamouflageActivity).mIconSelected) {
                view.findViewById<View>(R.id.layoutSelected).visibility = View.VISIBLE
                (view.findViewById<View>(R.id.ivSelected) as AppCompatImageView).visibility =
                    View.VISIBLE
            } else {
                view.findViewById<View>(R.id.layoutSelected).visibility = View.INVISIBLE
                (view.findViewById<View>(R.id.ivSelected) as AppCompatImageView).visibility =
                    View.INVISIBLE
            }
        }
        Glide.with(view.context).load(Integer.valueOf(num)).placeholder(R.drawable.loading as Int)
            .error(R.drawable.loading)
            .into(view.findViewById<View>(R.id.ivIconApp) as AppCompatImageView)
        view.setOnClickListener { view2 ->
            // from class: com.lutech.applock.adapter.IconAdapter$$ExternalSyntheticLambda0
            // android.view.View.OnClickListener
            `onBindViewHolder$lambda$1`(this@IconAdapter, num, i, view2)
        }
    }

    override fun getItemCount(): Int {
        return mIconApps.size
    }

    companion object {
        /* JADX INFO: Access modifiers changed from: private */
        fun `onBindViewHolder$lambda$1`(`this$0`: IconAdapter, i: Int, i2: Int, view: View?) {
            val onItemIconListener = `this$0`.onItemIconListener
            val sb = StringBuilder()
            val lowerCase = `this$0`.appName.lowercase()
            sb.append(lowerCase)
            sb.append('_')
            sb.append(i2)
            onItemIconListener.onItemIconClick(i, sb.toString())
        }
    }
}