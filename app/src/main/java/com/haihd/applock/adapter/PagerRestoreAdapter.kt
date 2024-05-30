package com.haihd.applock.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.haihd.applock.R
import com.haihd.applock.fragment.FragmentAlbumRestoreAudios
import com.haihd.applock.fragment.FragmentAlbumRestoreImage
import com.haihd.applock.fragment.FragmentAlbumRestoreVideos
import com.haihd.applock.fragment.FragmentImage

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