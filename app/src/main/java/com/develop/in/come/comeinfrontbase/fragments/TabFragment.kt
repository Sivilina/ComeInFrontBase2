package com.develop.`in`.come.comeinfrontbase.fragments
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.fragments.group_chat.FirstFragment
import com.develop.`in`.come.comeinfrontbase.fragments.private_chat.FriendsFragment

class TabFragment : androidx.fragment.app.Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val x = inflater.inflate(R.layout.tab_layout,null)
        tabLayout = x.findViewById(R.id.tabs) as TabLayout
        viewPager = x.findViewById(R.id.viewpager) as androidx.viewpager.widget.ViewPager

        viewPager.adapter = MyAdapter(childFragmentManager)
        tabLayout.post { tabLayout.setupWithViewPager(viewPager) }
        return x
    }
    internal inner class MyAdapter(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm){
        override fun getItem(position: Int): androidx.fragment.app.Fragment? {
            when(position){
                0 -> return FirstFragment()
                1 -> return FriendsFragment()
            }
            return null
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when(position){
                0 -> return "Groups"
                1 -> return "My Chats"
            }
            return null
        }
    }
    companion object{
        lateinit var tabLayout: TabLayout
        lateinit var viewPager: androidx.viewpager.widget.ViewPager
        var int_items = 2

    }
}