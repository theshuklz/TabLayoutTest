package com.example.tablayouttest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tablayouttest.databinding.ActivityMainBinding
import com.example.tablayouttest.databinding.FragmentPageBinding
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private var prevTab: TabLayout.Tab? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupTabsWithViewPager()
    }

    private fun setupTabsWithViewPager() {
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.isUserInputEnabled = false
        viewPager.adapter = ViewPagerAdapter(this)

        tabLayout.addTab(tabLayout.newTab().setText("Tab-1"))
        tabLayout.addTab(tabLayout.newTab().setText("Tab-2"))
        tabLayout.addTab(tabLayout.newTab().setText("Tab-3"))
        tabLayout.addTab(tabLayout.newTab().setText("Tab-4"))

        prevTab = tabLayout.getTabAt(0)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                //this ensures that the new tab is not shown as selected yet until user presses OK
                tabLayout.setScrollPosition(prevTab!!.position, 0f, false)

                if (tab.position == 1)
                    showMyDialog(
                        onPositiveClick = {
                            onTabChangeSuccess(tab)
                        },
                        onNegativeClick = { selectTabWithoutTriggeringListener(prevTab) }
                    )
                else {
                    onTabChangeSuccess(tab)
                }
            }

            private fun selectTabWithoutTriggeringListener(tab: TabLayout.Tab?) {
                tabLayout.removeOnTabSelectedListener(this)
                tab?.let {
                    tabLayout.selectTab(it)
                }
                tabLayout.addOnTabSelectedListener(this)
            }

            private fun onTabChangeSuccess(tab: TabLayout.Tab) {
                if (prevTab != tab)
                    prevTab = tab
                viewPager.currentItem = tab.position
                //this updates the tabSelected animation to happen only when user pressed OK
                tabLayout.setScrollPosition(tab.position, 0f, true)
            }

            private fun showMyDialog(onNegativeClick: () -> Unit, onPositiveClick: () -> Unit) {
                AlertDialog.Builder(this@MainActivity)
                    .apply {
                        setCancelable(false)
                        setTitle("Do you really want to continue?")
                        setPositiveButton("Ok") { _, _ ->
                            onPositiveClick()
                        }
                        setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                            onNegativeClick()
                        }
                    }.create()
                    .show()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }


    inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount() = 4
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> PageFragment.newInstance("Page 1")
                1 -> PageFragment.newInstance("Page 2")
                2 -> PageFragment.newInstance("Page 3")
                3 -> PageFragment.newInstance("Page 4")
                else -> throw IllegalStateException("Invalid position $position")
            }
        }
    }

    class PageFragment : Fragment() {

        private lateinit var binding: FragmentPageBinding
        private lateinit var fragmentName: String

        companion object {
            private const val ARG_PAGE_TITLE = "pageTitle"

            fun newInstance(pageTitle: String): PageFragment {
                val fragment = PageFragment()
                val args = Bundle()
                args.putString(ARG_PAGE_TITLE, pageTitle)
                fragment.arguments = args
                return fragment
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding = FragmentPageBinding.inflate(inflater, container, false)
            val textView = binding.pageTitleTextView
            fragmentName = arguments?.getString(ARG_PAGE_TITLE) ?: "Unknown"
            textView.text = fragmentName
            Log.d(TAG, "onCreateView: $fragmentName")
            return binding.root
        }

        override fun onResume() {
            super.onResume()
            Log.d(TAG, "onResume: $fragmentName")
        }
    }
}