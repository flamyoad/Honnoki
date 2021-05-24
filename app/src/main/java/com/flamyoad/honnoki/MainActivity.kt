package com.flamyoad.honnoki

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.databinding.ActivityMainBinding
import com.flamyoad.honnoki.ui.home.HomeFragment
import com.flamyoad.honnoki.ui.library.LibraryFragment
import com.flamyoad.honnoki.ui.search.SearchFragment

@ExperimentalPagingApi
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // R.layout.activity_main

        if (savedInstanceState == null) {
            pushFragment(HomeFragment.newInstance())
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            val fragment: BaseFragment = when (it.itemId) {
                R.id.home -> HomeFragment.newInstance()
                R.id.library -> LibraryFragment.newInstance()
                R.id.search -> SearchFragment.newInstance()
                else -> throw NotImplementedError("")
            }

            val currentFragment =
                supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
            if (currentFragment?.tag == fragment.getTitle()) {
                return@setOnNavigationItemSelectedListener true
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

    private fun pushFragment(fragment: BaseFragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment, fragment.getTitle())
            .addToBackStack("stack")
            .commit()
    }

    private fun showFragment(fragment: BaseFragment) {
        val fragmentNotExists =
            supportFragmentManager.findFragmentByTag(fragment.getTitle()) == null
        if (fragmentNotExists) {
            pushFragment(fragment)
        } else {
            supportFragmentManager.popBackStack(fragment.id, 0)
        }
    }
}
