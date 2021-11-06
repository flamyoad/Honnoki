package com.flamyoad.honnoki.viewmodel.download

import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.repository.download.DownloadRepository
import com.flamyoad.honnoki.rules.MainCoroutineRule
import com.flamyoad.honnoki.ui.download.DownloadPickerViewModel
import com.flamyoad.honnoki.ui.download.model.DownloadChapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DownloadPickerViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var db: AppDatabase

    @Mock
    private lateinit var downloadRepository: DownloadRepository

    private lateinit var viewModel: DownloadPickerViewModel

    private val chapterList: List<DownloadChapter> =
        (1..25).toList().map { generateFakeChapter(it.toLong()) }

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel =
            DownloadPickerViewModel(db, downloadRepository, TestCoroutineScope())
    }

    @Test
    fun `When long press a smaller chapter, return range from (Current Chapter) to (Previous Chapter)`() {
        val actual = viewModel.findChapterRange(
            currentChapter = generateFakeChapter(1),
            previousChapter = generateFakeChapter(4),
            listOfChapters = chapterList
        )
        val expected = listOf<Long>(1, 2, 3).map { generateFakeChapter(it) }
        Assert.assertEquals(actual, expected)
    }

    @Test
    fun `When long press a larger chapter, return range from (Current Chapter) to (Larger Chapter)`() {
        val actual = viewModel.findChapterRange(
            currentChapter = generateFakeChapter(8),
            previousChapter = generateFakeChapter(5),
            listOfChapters = chapterList
        )
        val expected = listOf<Long>(6, 7, 8).map { generateFakeChapter(it) }
        Assert.assertEquals(actual, expected)
    }

    companion object {
        fun generateFakeChapter(number: Long): DownloadChapter {
            return DownloadChapter(
                number,
                number.toString(),
                number.toDouble(),
                "",
                "",
                false,
                false,
                "",
                false,
                -1,
            )
        }
    }
}