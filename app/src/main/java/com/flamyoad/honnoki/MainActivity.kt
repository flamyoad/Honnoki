package com.flamyoad.honnoki

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.databinding.ActivityMainBinding
import java.lang.IllegalArgumentException

@ExperimentalPagingApi
class MainActivity : AppCompatActivity(), NavigationMenuListener {

    private enum class NavigationFragmentType {
        HOME,
        SEARCH,
        LIBRARY,
        MORE
    }

    private var _binding: ActivityMainBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            showFragment(NavigationFragmentType.HOME)
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            val type = when (it.itemId) {
                R.id.home -> NavigationFragmentType.HOME
                R.id.library -> NavigationFragmentType.LIBRARY
                R.id.search -> NavigationFragmentType.SEARCH
                R.id.more -> NavigationFragmentType.MORE
                else -> throw IllegalArgumentException("No such type")
            }
            showFragment(type)
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
    }

    private fun instantiateFragment(type: NavigationFragmentType): NavHostFragment {
        return when (type) {
            NavigationFragmentType.HOME -> NavHostFragment.create(R.navigation.home)
            NavigationFragmentType.SEARCH -> NavHostFragment.create(R.navigation.search)
            NavigationFragmentType.LIBRARY -> NavHostFragment.create(R.navigation.library)
            NavigationFragmentType.MORE -> NavHostFragment.create(R.navigation.more)
        }
    }

    private fun showFragment(type: NavigationFragmentType) {
        val transaction = supportFragmentManager.beginTransaction()

        NavigationFragmentType.values()
            .filter { it != type }
            .forEach { it ->
                supportFragmentManager.findFragmentByTag(it.name)?.let { transaction.hide(it) }
            }

        var fragmentTemp = supportFragmentManager.findFragmentByTag(type.name)
        if (fragmentTemp == null) {
            fragmentTemp = instantiateFragment(type)
            transaction.add(
                R.id.fragmentContainerView,
                fragmentTemp,
                type.name
            )
        } else {
            transaction.show(fragmentTemp)
        }

        transaction.commit()
    }

    override fun hideNavMenu() {
        binding.bottomNavigation.isVisible = false
    }

    override fun showNavMenu() {
        binding.bottomNavigation.isVisible = true
    }
}
