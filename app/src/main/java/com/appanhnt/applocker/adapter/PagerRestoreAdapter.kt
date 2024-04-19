package com.appanhnt.applocker.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.appanhnt.applocker.R
import com.appanhnt.applocker.fragment.FragmentAlbumRestoreAudios
import com.appanhnt.applocker.fragment.FragmentAlbumRestoreImage
import com.appanhnt.applocker.fragment.FragmentAlbumRestoreVideos
import com.appanhnt.applocker.fragment.FragmentImage

class PagerRestoreAdapter(var fragmentManager: FragmentManager, val context: Context) :
    FragmentPagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        var frag: Fragment? = null
        when (position) {
            0 -> {
                frag = FragmentAlbumRestoreImage()
            }
            1 -> {
                frag = FragmentAlbumRestoreVideos()
            }
            2 -> {
                frag = FragmentAlbumRestoreAudios()
            }
        }
        return frag ?: FragmentImage()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title = ""
        when (position) {
            0 -> title = context.getString(R.string.IMAGES)
            1 -> title = context.getString(R.string.VIDEOS)
            2 -> title = context.getString(R.string.Audios)
        }
        return title
    }
}