package com.flamyoad.honnoki

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.cache.CoverCache
import com.flamyoad.honnoki.databinding.ActivityMainBinding
import com.flamyoad.honnoki.ui.home.HomeFragment
import com.flamyoad.honnoki.ui.library.LibraryFragment
import com.flamyoad.honnoki.ui.options.OptionsFragment
import com.flamyoad.honnoki.ui.search.SimpleSearchFragment
import timber.log.Timber
import java.lang.IllegalArgumentException

@ExperimentalPagingApi
class MainActivity : AppCompatActivity(), NavigationMenuListener {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // R.layout.activity_main

        if (savedInstanceState == null) {
            showFragment(HomeFragment.newInstance())
        }
        
        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            val fragment: BaseFragment = when (it.itemId) {
                R.id.home -> HomeFragment.newInstance()
                R.id.library -> LibraryFragment.newInstance()
                R.id.search -> SimpleSearchFragment.newInstance()
                R.id.more -> OptionsFragment.newInstance()
                else -> throw IllegalArgumentException("No such fragment")
            }

            showFragment(fragment)
            return@setOnNavigationItemSelectedListener true
        }

        binding.bottomNavigation.setOnNavigationItemReselectedListener {
            when (it.itemId) {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onBackPressed() {
        val baseFragment = supportFragmentManager.primaryNavigationFragment as? BaseFragment

        baseFragment?.let {
            if (it.ignoreDefaultBackPressAction) {
                baseFragment.onBackPressAction()
                return
            }
        }

        super.onBackPressed()
        finish()
    }

    private fun showFragment(fragment: BaseFragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        val currentFragment = supportFragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment)
        }

        var fragmentTemp = supportFragmentManager.findFragmentByTag(fragment.bottomBarTitle)
        if (fragmentTemp == null) {
            fragmentTemp = fragment
            fragmentTransaction.add(R.id.fragmentContainerView, fragmentTemp, fragment.bottomBarTitle)
        } else {
            fragmentTransaction.show(fragmentTemp)
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp)
            .setReorderingAllowed(true)
            .commitNow()
    }

    override fun hideNavMenu() {
        binding.bottomNavigation.isVisible = false
    }

    override fun showNavMenu() {
        binding.bottomNavigation.isVisible = true
    }
}
