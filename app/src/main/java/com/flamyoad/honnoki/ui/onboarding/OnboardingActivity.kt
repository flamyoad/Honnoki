package com.flamyoad.honnoki.ui.onboarding

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flamyoad.honnoki.common.BaseActivity
import com.flamyoad.honnoki.databinding.ActivityOnboardingBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingActivity : BaseActivity() {

    private val viewModel: OnboardingViewModel by viewModel()

    private var _binding: ActivityOnboardingBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            viewModel.setOnboardingCompleted()
            navigator.showHome(this)
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, OnboardingActivity::class.java)
            context.startActivity(intent)
        }
    }
}