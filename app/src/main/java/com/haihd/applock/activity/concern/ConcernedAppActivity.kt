package com.haihd.applock.activity.concern

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.haihd.applock.R
import com.haihd.applock.adapter.ListItemLockAppAdapter
import com.haihd.applock.databinding.ActivityConcernedAppBinding
import com.haihd.applock.utils.RecycleViewUtils
import com.haihd.applock.viewmodel.AppLockViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.extensions.launchActivity
import com.haihd.applock.activity.home.HomeActivity
import com.haihd.applock.item.ItemListAppLock
import com.haihd.applock.key.KeyApp
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.inject


class ConcernedAppActivity : BaseActivity<ActivityConcernedAppBinding>() {
    private val viewModel by inject<AppLockViewModel>()
    private var listApp: MutableList<ItemListAppLock> = mutableListOf()
    private var listAppSearch: MutableList<ItemListAppLock> = mutableListOf()
    private var adapterConcernedApp: ListItemLockAppAdapter? = null
    private var adapterConcernedSearchApp: ListItemLockAppAdapter? = null
    override fun initView() {
        setStatusBarHomeTransparent(this)
        binding.title.setPadding(0, getHeightStatusBar(), 0, 0)
        //rcl
        adapterConcernedApp =
            ListItemLockAppAdapter(this, listApp, object : ListItemLockAppAdapter.Callback {
                override fun onSelectItem() {
                    checkSelectItem()
                }
            })
        binding.rcl.adapter = adapterConcernedApp

        adapterConcernedSearchApp =
            ListItemLockAppAdapter(this, listAppSearch, object : ListItemLockAppAdapter.Callback {
                override fun onSelectItem() {
                    checkSelectItem()
                }
            })
        adapterConcernedSearchApp!!.setShowTittle(false)
        binding.rclSearch.adapter = adapterConcernedSearchApp
        //clear anim
        RecycleViewUtils.clearAnimation(binding.rcl)
        RecycleViewUtils.clearAnimation(binding.rclSearch)
    }

    private fun checkSelectItem() {
        if (viewModel.listAppAll.isEmpty()){
            binding.frLock.visibility = View.GONE
        }else{
            val count = viewModel.listAppAll.count { it.isLocked }
            if (count > 0) {
                binding.frLock.visibility = View.VISIBLE
                binding.btnProtected.text = getString(R.string.lock) + " ($count)"
            } else {
                binding.frLock.visibility = View.GONE
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initData() {
        viewModel.listAppLiveData.observe(this) {
            listApp.clear()
            listAppSearch.clear()
            listAppSearch.addAll(viewModel.listListItemAppLockAll)
            for (item in viewModel.listListItemAppLock) {
                listApp.add(item)
            }
            adapterConcernedApp?.notifyDataSetChanged()
            adapterConcernedSearchApp?.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initListener() {
        binding.ivClose.setOnClickListener {
            binding.edt.clearFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.edt.windowToken, 0)
        }
        binding.edt.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.ivClose.visibility = View.VISIBLE
                binding.rcl.visibility = View.GONE
                binding.rclSearch.visibility = View.VISIBLE
                adapterConcernedSearchApp?.notifyDataSetChanged()
            } else {
                binding.ivClose.visibility = View.GONE
                binding.rcl.visibility = View.VISIBLE
                binding.rclSearch.visibility = View.GONE
                binding.edt.setText("")
                binding.ctEmpty.visibility = View.GONE
                adapterConcernedApp?.notifyDataSetChanged()
            }
        }
        binding.edt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (listAppSearch.isEmpty()) return
                val list = listAppSearch[0].list
                list.clear()
                for (item in viewModel.listAppLiveData.value!!) {
                    if (item.name.contains(binding.edt.text, true)) {
                        list.add(item)
                    }
                }
                if (list.isEmpty()) {
                    binding.ctEmpty.visibility = View.VISIBLE
                } else {
                    binding.ctEmpty.visibility = View.GONE
                }
                adapterConcernedSearchApp!!.notifyDataSetChanged()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        binding.btnProtected.setOnClickListener {
            launchActivity<HomeActivity> { }
            val listLock = Hawk.get(KeyApp.KEY_APP_LOCK, mutableListOf<String>())
            for (item in viewModel.listAppAll) {
                if (item.isLocked) {
                    item.packageName?.let { it1 -> listLock.add(it1) }
                }
            }
            Hawk.put(KeyApp.KEY_APP_LOCK, listLock)
        }
    }

    override fun viewBinding(): ActivityConcernedAppBinding {
        return ActivityConcernedAppBinding.inflate(LayoutInflater.from(this))
    }

}