package com.example.kotlincashloan.adapter.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

class ProfilePagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm){

    private val fragmentList: MutableList<Fragment> = ArrayList()
    private val titleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment: Fragment, title:String){
        fragmentList.add(fragment)
        titleList.add(title)
    }


    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }
}