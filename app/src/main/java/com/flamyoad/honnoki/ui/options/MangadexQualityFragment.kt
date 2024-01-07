package com.flamyoad.honnoki.ui.options

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.findNavController
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.parser.model.MangadexQualityMode
import com.flamyoad.honnoki.ui.theme.HonnokiAppTheme
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MangadexQualityFragment : Fragment() {

    private val viewModel: OptionsViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                HonnokiAppTheme {
                    MangadexQualityScreen(
                        isBackArrowVisible = findNavController().previousBackStackEntry != null,
                        onBackArrowPressed = { findNavController().navigateUp() },
                        onCheckboxSelected = viewModel::setMangadexQuality,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MangadexQualityScreen(
        isBackArrowVisible: Boolean,
        onBackArrowPressed: () -> Unit,
        onCheckboxSelected: (MangadexQualityMode) -> Unit,
        viewModel: OptionsViewModel
    ) {
        val qualityList by viewModel.mangadexQualityOptionList.collectAsStateWithLifecycle()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.mangadex_quality_screen_title))
                    },
                    navigationIcon = {
                        if (isBackArrowVisible) {
                            IconButton(onClick = onBackArrowPressed) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = stringResource(id = R.string.back)
                                )
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            Surface(modifier = Modifier.padding(innerPadding)) {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 0.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    qualityList.forEach { quality ->
                        MangadexQualitySwitch(
                            checkboxText = when (quality.mode) {
                                MangadexQualityMode.DATA -> stringResource(R.string.mangadex_quality_option_original)
                                MangadexQualityMode.DATA_SAVER -> stringResource(R.string.mangadex_quality_option_compressed)
                            },
                            isChecked = quality.isSelected,
                            onCheckedChange = {
                                onCheckboxSelected(quality.mode)
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun MangadexQualitySwitch(
        modifier: Modifier = Modifier,
        checkboxText: String,
        isChecked: Boolean,
        onCheckedChange: (Boolean) -> Unit
    ) {
        return Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = checkboxText)
            Spacer(Modifier.size(12.dp))
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MangadexQualityFragment()
    }
}