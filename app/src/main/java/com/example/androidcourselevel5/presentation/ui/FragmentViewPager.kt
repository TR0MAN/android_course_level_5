package com.example.androidcourselevel5.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.androidcourselevel5.R
import com.example.androidcourselevel5.databinding.FragmentViewPagerBinding
import com.example.androidcourselevel5.presentation.adapter.ViewPagerAdapter
import com.example.androidcourselevel5.presentation.ui.utils.visibleIf
import com.example.androidcourselevel5.presentation.viewmodel.SharedViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentViewPager : Fragment() {

    private lateinit var binding: FragmentViewPagerBinding
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        initTabLayout()
        setObservers()
    }

    private fun initViewPager() {
        val tabs = Tab.entries
        with(binding) {
            viewPager.adapter = ViewPagerAdapter(requireActivity(), tabs.map { it.tabFragment })
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                with(tabs[position]) {
                    tab.text = name
                    tab.setIcon(tabIcon)
                }
            }.attach()
        }
    }

    private fun initTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.icon?.alpha = SELECTED_TAB_TRANSPARENCY_LEVEL
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.icon?.alpha = UNSELECTED_TAB_TRANSPARENCY_LEVEL
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setObservers() {
        sharedViewModel.tabLayoutVisibility.observe(viewLifecycleOwner) { visibility ->
            binding.tabLayout.visibleIf(visibility)
        }
    }

    companion object {
        const val SELECTED_TAB_TRANSPARENCY_LEVEL = 250
        const val UNSELECTED_TAB_TRANSPARENCY_LEVEL = 70
    }

    private enum class Tab(val tabIcon: Int, val tabFragment: Fragment ) {
        PROFILE(R.drawable.ic_view_pager_profile, FragmentProfileSettings()),
        CONTACTS(R.drawable.ic_view_pager_contact, FragmentContactsList())
    }

}