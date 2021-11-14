package com.flamyoad.honnoki.ui.onboarding

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.common.BaseActivity
import com.flamyoad.honnoki.data.UiMode
import com.flamyoad.honnoki.databinding.ActivityOnboardingBinding
import com.flamyoad.honnoki.ui.onboarding.adapter.OnboardingFragmentAdapter
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingActivity : BaseActivity() {

    private val viewModel: OnboardingViewModel by viewModel()

    private var _binding: ActivityOnboardingBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewPagerAdapter by lazy { OnboardingFragmentAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.button.setOnClickListener {
//            viewModel.setOnboardingCompleted()
//            navigator.showHome(this)
//        }
        with(binding) {
            viewPager.adapter = viewPagerAdapter
            dotsIndicator.setViewPager2(viewPager)
        }
        observeUi()
    }

    private fun observeUi() {
        lifecycleScope.launchWhenResumed {
            viewModel.selectedUiMode().collectLatest {
//                val color = when (it) {
//                    UiMode.DARK -> R.color.onboarding_background_dark
//                    else -> R.color.onboarding_background_light
//                }
//                binding.rootLayout.setBackgroundResource(color)
                if (it == null) return@collectLatest
                when (it) {
                    UiMode.DARK -> AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                    )
                    else -> AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                    )
                }
            }
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, OnboardingActivity::class.java)
            context.startActivity(intent)
        }
    }
}