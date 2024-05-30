package com.haihd.applock.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.haihd.applock.R
import com.haihd.applock.fragment.FragmentAppLock
import com.haihd.applock.fragment.FragmentImage

class AppLockViewPagerAdapter(var fragmentManager: FragmentManager, val context: Context) :
    FragmentPagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return 7
    }

    override fun getItem(position: Int): Fragment {
        var frag: Fragment? = null
        when (position) {
            0 -> {
                frag = FragmentAppLock(0)
            }

            1 -> {
                frag = FragmentAppLock(1)
            }

            2 -> {
                frag = FragmentAppLock(2)
            }

            3 -> {
                frag = FragmentAppLock(3)
            }

            4 -> {
                frag = FragmentAppLock(4)
            }

            5 -> {
                frag = FragmentAppLock(5)
            }

            6 -> {
                frag = FragmentAppLock(6)
            }
        }
        return frag ?: FragmentImage()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title = ""
        when (position) {
            0 -> title = context.getString(R.string.all)
            1 -> title = context.getString(R.string.hot)
            2 -> title = context.getString(R.string.system)
            3 -> title = context.getString(R.string.social)
            4 -> title = context.getString(R.string.payment)
            5 -> title = context.getString(R.string.player)
            6 -> title = context.getString(R.string.game)
        }
        return title
    }
}