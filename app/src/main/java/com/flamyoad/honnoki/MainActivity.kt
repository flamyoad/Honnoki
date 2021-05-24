package com.flamyoad.honnoki

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.databinding.ActivityMainBinding
import com.flamyoad.honnoki.ui.home.HomeFragment
import com.flamyoad.honnoki.ui.library.LibraryFragment
import com.flamyoad.honnoki.ui.search.SimpleSearchFragment

@ExperimentalPagingApi
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // R.layout.activity_main

        if (savedInstanceState == null) {
            showFragment(HomeFragment.newInstance())
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            val fragment: BaseFragment = when (it.itemId) {
                R.id.home -> HomeFragment.newInstance()
                R.id.library -> LibraryFragment.newInstance()
                R.id.search -> SimpleSearchFragment.newInstance()
                else -> throw NotImplementedError("")
            }

            showFragment(fragment)
            return@setOnNavigationItemSelectedListener true
        }

        binding.bottomNavigation.setOnNavigationItemReselectedListener {
            when (it.itemId) {

            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    private fun showFragment(fragment: BaseFragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        val currentFragment = supportFragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment)
        }

        var fragmentTemp = supportFragmentManager.findFragmentByTag(fragment.getTitle())
        if (fragmentTemp == null) {
            fragmentTemp = fragment
            fragmentTransaction.add(R.id.fragmentContainerView, fragmentTemp, fragment.getTitle())
        } else {
            fragmentTransaction.show(fragmentTemp)
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp)
            .setReorderingAllowed(true)
            .commitNow()
    }
}
