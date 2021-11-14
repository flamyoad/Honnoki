package com.flamyoad.honnoki

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.common.BaseActivity
import com.flamyoad.honnoki.databinding.ActivityMainBinding
import com.flamyoad.honnoki.utils.extensions.tryOrNull
import kotlinx.coroutines.flow.collectLatest
import java.lang.IllegalArgumentException
import org.koin.androidx.viewmodel.ext.android.viewModel

enum class NavigationFragmentType {
    HOME,
    SEARCH,
    LIBRARY,
    MORE
}

@ExperimentalPagingApi
class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModel()

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

        lifecycleScope.launchWhenResumed {
            viewModel.actionModeEnabled().collectLatest { enabled ->
                binding.bottomNavigation.isVisible = enabled.not()
            }
        }
    }

    override fun onBackPressed() {
        // Remove action mode in Bookmark screen before exiting app
        if (viewModel.actionModeEnabled().value) {
            viewModel.setActionMode(false)
            return
        }

        // Pop fragment in the nav backstack if there is any
        val currentNavFragment =
            supportFragmentManager.fragments.first { it.isVisible } as NavHostFragment
        if (currentNavFragment.navController.backQueue.size > 2) {
            currentNavFragment.navController.navigateUp()
        } else {
            super.onBackPressed()
        }
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
                supportFragmentManager.findFragmentByTag(it.name)
                    ?.let { transaction.hide(it) }
            }

        var fragment = supportFragmentManager.findFragmentByTag(type.name)
        if (fragment == null) {
            fragment = instantiateFragment(type)
            transaction.add(R.id.fragmentContainerView, fragment, type.name)
        } else {
            transaction.show(fragment)
        }

        transaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val CURRENT_FRAGMENT = "currrent_fragment"

        fun startActivity(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }
}
