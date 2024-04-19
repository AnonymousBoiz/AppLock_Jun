package com.appanhnt.applocker.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.appanhnt.applocker.R
import com.appanhnt.applocker.fragment.FragmentImage
import com.appanhnt.applocker.fragment.FragmentVideo

class PagerVaultAdapter(var fragmentManager: FragmentManager, val context: Context) :
    FragmentPagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        var frag: Fragment? = null
        when (position) {
            0 -> {
                frag = FragmentImage()
            }
            1 -> {
                frag = FragmentVideo()
            }
        }
        return frag ?:FragmentImage()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title = ""
        when (position) {
            0 -> title = context.getString(R.string.IMAGES)
            1 -> title = context.getString(R.string.VIDEOS)
        }
        return title
    }
}