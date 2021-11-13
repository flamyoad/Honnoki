package com.flamyoad.honnoki.ui.reader

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.flamyoad.honnoki.databinding.FragmentImageBinding
import com.flamyoad.honnoki.parser.model.MangadexQualityMode
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.ui.reader.model.ReaderPage
import com.flamyoad.honnoki.utils.ui.MangaImageFragmentViewTarget

class ImageFragment : Fragment() {

    private var _binding: FragmentImageBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val link: String? by lazy { arguments?.getString(LINK) }
    private val dataSaverLink: String? by lazy {
        arguments?.getString(
            DATASAVER_LINK
        )
    }
    private val pageNumber: Int? by lazy { arguments?.getInt(PAGE_NUMBER) }
    private val source: Source? by lazy { arguments?.getParcelable(SOURCE) }
    private val quality: MangadexQualityMode? by lazy {
        arguments?.getParcelable(
            MANGADEX_QUALITY
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadImage()
    }

    private fun loadImage() {
        with(binding) {
            errorContainer.isVisible = false
            progressBarContainer.isVisible = true

            txtPageNumber.text = pageNumber.toString()

            imageView.setOnImageEventListener(object :
                SubsamplingScaleImageView.DefaultOnImageEventListener() {
                override fun onReady() {
                    super.onReady()
                    statusContainer.isVisible = false
                }
            })

            Glide.with(root)
                .download(getImageUrl())
                .timeout(15000) // 15 seconds
                .into(MangaImageFragmentViewTarget(this))
        }
    }

    private fun getImageUrl(): GlideUrl {
        val url = when {
            source == Source.MANGAKALOT -> {
                GlideUrl(link) { mapOf("Referer" to "https://manganelo.com/") }
            }
            source == Source.MANGADEX && quality == MangadexQualityMode.DATA_SAVER -> {
                GlideUrl(dataSaverLink ?: link)
            }
            else -> {
                GlideUrl(link)
            }
        }
        return url
    }

    companion object {
        private const val LINK = "link"
        private const val DATASAVER_LINK = "datasaver_link"
        private const val PAGE_NUMBER = "page_number"
        private const val SOURCE = "source"
        private const val MANGADEX_QUALITY = "mangadex_quality"

        @JvmStatic
        fun newInstance(
            readerPage: ReaderPage,
            source: Source,
            mangadexQuality: MangadexQualityMode
        ) =
            ImageFragment().apply {
                arguments = Bundle().apply {
                    val link: String?
                    val dataSaverLink: String?
                    val pageNumber: Int

                    when (readerPage) {
                        is ReaderPage.Value -> {
                            link = readerPage.page.link
                            dataSaverLink = readerPage.page.linkDataSaver
                            pageNumber = readerPage.page.number
                        }
                        is ReaderPage.Ads -> {
                            link = ""
                            dataSaverLink = ""
                            pageNumber = 0
                        }
                    }
                    putString(LINK, link)
                    putString(DATASAVER_LINK, dataSaverLink)
                    putInt(PAGE_NUMBER, pageNumber)
                    putParcelable(SOURCE, source)
                    putParcelable(MANGADEX_QUALITY, mangadexQuality)
                }
            }
    }
}