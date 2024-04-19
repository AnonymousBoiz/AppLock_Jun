package com.appanhnt.applocker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appanhnt.applocker.R
import com.appanhnt.applocker.interfaces.OnItemIconListener


class IconCamouflageAdapter(
    context: Context,
    mAppNames: List<String>,
    mIconApps: ArrayList<ArrayList<Int>>,
    onItemIconListener: OnItemIconListener
) :
    RecyclerView.Adapter<IconCamouflageAdapter.MyViewHolder>() {
    private var context: Context
    private var mAppNames: List<String>
    private var mIconApps: ArrayList<ArrayList<Int>>
    private var onItemIconListener: OnItemIconListener
    fun getContext(): Context {
        return context
    }

    fun setContext(context: Context) {
        this.context = context
    }

    fun getMAppNames(): List<String> {
        return mAppNames
    }

    fun setMAppNames(list: List<String>) {
        mAppNames = list
    }

    fun getMIconApps(): ArrayList<ArrayList<Int>> {
        return mIconApps
    }

    fun setMIconApps(arrayList: ArrayList<ArrayList<Int>>) {
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
        this.mAppNames = mAppNames
        this.mIconApps = mIconApps
        this.onItemIconListener = onItemIconListener
    }

    inner class MyViewHolder(iconCamouflageAdapter: IconCamouflageAdapter, view: View?) :
        RecyclerView.ViewHolder(view!!) {
        /* synthetic */ val `this$0`: IconCamouflageAdapter

        init {
            `this$0` = iconCamouflageAdapter
        }
    }

    // androidx.recyclerview.widget.RecyclerView.Adapter
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): MyViewHolder {
        val myView: View = LayoutInflater.from(context)
            .inflate(R.layout.layout_item_icon_camouflage, parent, false)
        return MyViewHolder(this, myView)
    }

    // androidx.recyclerview.widget.RecyclerView.Adapter
    override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
        val view = holder.itemView
        val str = mAppNames[i]
        (view.findViewById<View>(R.id.tvAppName) as TextView).text = str
        (view.findViewById<View>(R.id.rvIconApp) as RecyclerView).layoutManager =
            GridLayoutManager(view.context, 4)
        val context = view.context
        val arrayList = mIconApps[i]
        (view.findViewById<View>(R.id.rvIconApp) as RecyclerView).adapter =
            IconAdapter(str, context, arrayList, onItemIconListener)
    }

    // androidx.recyclerview.widget.RecyclerView.Adapter
    override fun getItemCount(): Int {
        return mAppNames.size
    }
}