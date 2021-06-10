package com.flamyoad.honnoki.ui.library.bookmark

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.customListAdapter
import com.flamyoad.honnoki.NavigationMenuListener
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.BookmarkAdapter
import com.flamyoad.honnoki.adapter.BookmarkDialogAdapter
import com.flamyoad.honnoki.adapter.BookmarkGroupAdapter
import com.flamyoad.honnoki.databinding.FragmentBookmarkBinding
import com.flamyoad.honnoki.dialog.AddBookmarkGroupDialog
import com.flamyoad.honnoki.dialog.ChangeBookmarkGroupNameDialog
import com.flamyoad.honnoki.dialog.DeleteBookmarkGroupDialog
import com.flamyoad.honnoki.data.model.BookmarkWithOverview
import com.flamyoad.honnoki.ui.library.LibraryViewModel
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.ClassCastException

@ExperimentalPagingApi
class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: BookmarkViewModel by viewModel()
    private val parentViewModel: LibraryViewModel by sharedViewModel()

    private val dialogMoveBookmarksAdapter by lazy { BookmarkDialogAdapter({}) }

    private var listener: NavigationMenuListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as NavigationMenuListener
        } catch (ignored: ClassCastException) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initBookmarkGroups()
        initBookmarkItems()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (viewModel.actionModeEnabled) {
            startActionMode()
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.apply {
            setHeaderTitle(viewModel.bookmarkGroupName)
            add(MENU_CHANGE_GROUP_NAME)
            add(MENU_DELETE_GROUP)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val selectedGroupId = viewModel.getSelectedBookmarkGroupId()

        val dialog: DialogFragment =
            when (item.title) {
                MENU_CHANGE_GROUP_NAME -> ChangeBookmarkGroupNameDialog.newInstance(selectedGroupId)
                MENU_DELETE_GROUP -> DeleteBookmarkGroupDialog.newInstance(selectedGroupId)
                else -> throw IllegalArgumentException("Invalid menu option!")
            }

        dialog.show(childFragmentManager, "dialog")
        return true
    }

    private fun initUi() {
        registerForContextMenu(binding.btnContextMenu)
        binding.btnContextMenu.setOnClickListener {
            it.showContextMenu()
        }

        binding.btnMoveTo.setOnClickListener {
            openMoveBookmarkDialog()
        }

        binding.btnDelete.setOnClickListener {
            openDeleteBookmarkDialog()
        }

        lifecycleScope.launchWhenResumed {
            viewModel.tickedItems().collectLatest {
                if (it.isEmpty()) {
                    exitActionMode()
                } else {
                    binding.btnMoveTo.text =
                        resources.getString(R.string.bookmark_actionmode_btn_moveto, it.size)
                    binding.btnDelete.text =
                        resources.getString(R.string.bookmark_actionmode_btn_delete, it.size)
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            parentViewModel.shouldCancelActionMode().collectLatest { yes ->
                if (yes) {
                    exitActionMode()
                }
            }
        }
    }

    private fun initBookmarkGroups() {
        val groupAdapter = BookmarkGroupAdapter(
            viewModel::selectBookmarkGroup,
            this::openAddNewBookmarkDialog,
        )

        val groupLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        with(binding.listGroups) {
            adapter = groupAdapter
            layoutManager = groupLayoutManager
            addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    when (e.action) {
                        MotionEvent.ACTION_DOWN -> {
                            parent?.requestDisallowInterceptTouchEvent(true)
                        }
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            })
        }

        viewModel.bookmarkGroupsWithInfo.observe(viewLifecycleOwner) {
            groupAdapter.submitList(it)
        }
    }

    private fun initBookmarkItems() {
        val bookmarkAdapter = BookmarkAdapter(this::clickBookmark, this::enterActionMode)
        val bookmarkLayoutManager = GridLayoutManager(requireContext(), 3)

        with(binding.listItems) {
            adapter = bookmarkAdapter
            layoutManager = bookmarkLayoutManager
        }

        viewModel.bookmarkItems.observe(viewLifecycleOwner) {
            bookmarkAdapter.submitList(it)
        }

        lifecycleScope.launchWhenResumed {
            viewModel.selectedBookmarkGroup.collect {
                binding.header.text = it?.name ?: ""
            }
        }
    }

    private fun enterActionMode(item: BookmarkWithOverview) {
        startActionMode()
        clickBookmark(item)
    }

    private fun startActionMode() {
        viewModel.actionModeEnabled = true

        binding.actionModeBar.visibility = View.VISIBLE
        listener?.hideNavMenu()
    }

    private fun exitActionMode() {
        viewModel.actionModeEnabled = false

        binding.actionModeBar.visibility = View.GONE
        listener?.showNavMenu()

        viewModel.clearTickedBookmarks()
    }

    private fun clickBookmark(bookmark: BookmarkWithOverview) {
        if (viewModel.actionModeEnabled) {
            viewModel.tickBookmark(bookmark)
        } else {
            val intent = Intent(requireContext(), MangaOverviewActivity::class.java).apply {
                putExtra(MangaOverviewActivity.MANGA_URL, bookmark.overview.link)
                putExtra(MangaOverviewActivity.MANGA_SOURCE, bookmark.overview.source.toString())
                putExtra(MangaOverviewActivity.MANGA_TITLE, bookmark.overview.mainTitle)
            }
            requireContext().startActivity(intent)
        }
    }

    private fun openAddNewBookmarkDialog() {
        val dialog = AddBookmarkGroupDialog.newInstance()
        dialog.show(childFragmentManager, AddBookmarkGroupDialog.TAG)
    }

    private fun openMoveBookmarkDialog() {

    }

    private fun openDeleteBookmarkDialog() {
        MaterialDialog(requireContext())
            .title(text = "Delete Bookmarks")
            .message(text = "Are you sure you want to remove the selected bookmark?")
            .show {
                positiveButton(text = "Confirm") {
                    viewModel.deleteBookmarks()
                    exitActionMode()
                }
            }
            .lifecycleOwner(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val MENU_CHANGE_GROUP_NAME = "Change Name"
        private const val MENU_DELETE_GROUP = "Delete Bookmark Group"

        @JvmStatic
        fun newInstance() = BookmarkFragment()
    }
}
