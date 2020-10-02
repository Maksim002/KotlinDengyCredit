package com.example.kotlincashloan.ui.profile


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.profile.ProfilePagerAdapter
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {
    private var indicatorWidth = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.show()
        initPager()
    }

    private fun initPager() {
        val adapter = ProfilePagerAdapter(childFragmentManager)
        adapter.addFragment(MyOperationFragment(), "Мои операции")
        adapter.addFragment(MyApplicationFragment(), "Мои заявки")
        profile_pager.setAdapter(adapter)
        profile_tab.setupWithViewPager(profile_pager)
        adapter.notifyDataSetChanged()

        profile_tab.post(Runnable {
            indicatorWidth = profile_tab.getWidth() / profile_tab.getTabCount()
            //Assign new width
            val indicatorParams = profile_indicator.getLayoutParams() as FrameLayout.LayoutParams
            indicatorParams.width = indicatorWidth
            profile_indicator.setLayoutParams(indicatorParams)
        })
        profile_pager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(i: Int, positionOffset: Float, positionOffsetPx: Int) {
                val params = profile_indicator.getLayoutParams() as FrameLayout.LayoutParams
                //Multiply positionOffset with indicatorWidth to get translation
                val translationOffset = (positionOffset + i) * indicatorWidth
                params.leftMargin = translationOffset.toInt()
                profile_indicator.setLayoutParams(params)
            }
            override fun onPageSelected(i: Int) {}
            override fun onPageScrollStateChanged(i: Int) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val settingsMenuItem = menu.findItem(R.id.profile_settings)
        val s = SpannableString(settingsMenuItem.title)
        s.setSpan(ForegroundColorSpan(Color.WHITE), 0, s.length, 0)
        settingsMenuItem.title = s
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profile_settings->{

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            requireActivity().getWindow().setStatusBarColor(requireActivity().getColor(R.color.orangeColor))
            val decorView: View = (activity as AppCompatActivity).getWindow().getDecorView()
            var systemUiVisibilityFlags = decorView.systemUiVisibility
            systemUiVisibilityFlags = systemUiVisibilityFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            decorView.systemUiVisibility = systemUiVisibilityFlags
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar);
            toolbar.setBackgroundDrawable(ColorDrawable(requireActivity().getColor(R.color.orangeColor)))
            toolbar.setTitleTextColor(requireActivity().getColor(R.color.whiteColor))
        }
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().getWindow().setStatusBarColor(requireActivity().getColor(R.color.whiteColor))
            requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar);
            toolbar.setBackgroundDrawable(ColorDrawable(requireActivity().getColor(R.color.whiteColor)))
            toolbar.setTitleTextColor(requireActivity().getColor(R.color.orangeColor))
        }
    }
}