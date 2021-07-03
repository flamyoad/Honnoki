package com.flamyoad.honnoki.ui.library.bookmark

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.flamyoad.honnoki.MainViewModel
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.cache.CoverCache
import com.flamyoad.honnoki.data.entities.BookmarkWithOverview
import com.flamyoad.honnoki.data.exception.NullEntityIdException
import com.flamyoad.honnoki.databinding.FragmentBookmarkBinding
import com.flamyoad.honnoki.dialog.AddBookmarkGroupDialog
import com.flamyoad.honnoki.dialog.ChangeBookmarkGroupNameDialog
import com.flamyoad.honnoki.dialog.DeleteBookmarkGroupDialog
import com.flamyoad.honnoki.dialog.MoveBookmarkDialog
import com.flamyoad.honnoki.ui.library.bookmark.adapter.BookmarkAdapter
import com.flamyoad.honnoki.ui.library.bookmark.adapter.BookmarkGroupAdapter
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalPagingApi
class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: BookmarkViewModel by viewModel()

    private val mainViewModel: MainViewModel by sharedViewModel()

    private val coverCache: CoverCache by inject()

    private var actionModeEnabled = false

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

        // Restore the dialog fragment callback on screen rotation, if exists
        val moveBookmarkDialog = childFragmentManager.findFragmentByTag(MoveBookmarkDialog.TAG)
        moveBookmarkDialog?.let { setMoveBookmarkDialogCallback(it as MoveBookmarkDialog) }
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
        val selectedGroupName = viewModel.bookmarkGroupName ?: ""

        val dialog: DialogFragment =
            when (item.title) {
                MENU_CHANGE_GROUP_NAME -> ChangeBookmarkGroupNameDialog.newInstance(selectedGroupId)
                MENU_DELETE_GROUP -> DeleteBookmarkGroupDialog.newInstance(
                    selectedGroupId,
                    selectedGroupName
                )
                else -> throw IllegalArgumentException("Invalid menu option!")
            }

        val dialogTag = when (item.title) {
            MENU_CHANGE_GROUP_NAME -> ChangeBookmarkGroupNameDialog.TAG
            MENU_DELETE_GROUP -> DeleteBookmarkGroupDialog.TAG
            else -> throw IllegalArgumentException("Invalid menu option!")
        }

        dialog.show(childFragmentManager, dialogTag)
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
                    mainViewModel.setActionMode(false)
                } else {
                    binding.btnMoveTo.text = resources.getString(R.string.bookmark_actionmode_btn_moveto, it.size)
                    binding.btnDelete.text = resources.getString(R.string.bookmark_actionmode_btn_delete, it.size)
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            mainViewModel.actionModeEnabled().collectLatest { enabled ->
                if (enabled) {
                    startActionMode()
                } else {
                    exitActionMode()
                }
            }
        }
    }

    private fun initBookmarkGroups() {
        val groupAdapter = BookmarkGroupAdapter(coverCache, viewModel::selectBookmarkGroup, this::openAddNewBookmarkDialog)
        val groupLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

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
        val bookmarkAdapter = BookmarkAdapter(coverCache, this::onBookmarkClick, this::enterActionMode)
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

    /**
     * Ticks manga if currently is in action mode. Otherwise, go to next screen
     */
    private fun onBookmarkClick(bookmark: BookmarkWithOverview) {
        if (actionModeEnabled) {
            viewModel.tickBookmark(bookmark)
        } else {
            MangaOverviewActivity.startActivity(
                context = requireContext(),
                mangaUrl = bookmark.overview.link,
                mangaSource = bookmark.overview.source!!,
                mangaTitle = bookmark.overview.mainTitle
            )
        }
    }

    private fun enterActionMode(item: BookmarkWithOverview) {
        startActionMode()
        onBookmarkClick(item)
    }

    private fun startActionMode() {
        mainViewModel.setActionMode(true)
        actionModeEnabled = true

        val btmSlide = Slide(Gravity.BOTTOM).apply {
            addTarget(binding.actionModeBar)
        }
        TransitionManager.beginDelayedTransition(binding.root, btmSlide)
        binding.actionModeBar.visibility = View.VISIBLE
    }

    private fun exitActionMode() {
        mainViewModel.setActionMode(false)
        actionModeEnabled = false

        binding.actionModeBar.visibility = View.GONE
        viewModel.clearTickedBookmarks()
    }

    private fun openAddNewBookmarkDialog() {
        val dialog = AddBookmarkGroupDialog.newInstance()
        dialog.show(childFragmentManager, AddBookmarkGroupDialog.TAG)
    }

    private fun openMoveBookmarkDialog() {
        val selectedBookmarkIds = viewModel.tickedItems().value
            .map { it.id ?: throw NullEntityIdException() }

        val dialog = MoveBookmarkDialog.newInstance(selectedBookmarkIds)
        dialog.show(childFragmentManager, MoveBookmarkDialog.TAG)

        dialog.setFragmentResultListener(MoveBookmarkDialog.REQUEST_KEY) { key, bundle ->
            if (key == MoveBookmarkDialog.REQUEST_KEY) {
                if (bundle.getBoolean(MoveBookmarkDialog.MOVED_SUCCESSFULLY)) {
                    viewModel.clearTickedBookmarks()
                }
            }
        }

        setMoveBookmarkDialogCallback(dialog)
    }

    /**
     * This method has to be invoked on dialog creation & screen rotation because the callback
     * will only remain active until the LifecycleOwner reaches the Lifecycle.State.DESTROYED state
     */
    private fun setMoveBookmarkDialogCallback(dialog: MoveBookmarkDialog) {
        dialog.setFragmentResultListener(MoveBookmarkDialog.REQUEST_KEY) { key, bundle ->
            if (key == MoveBookmarkDialog.REQUEST_KEY) {
                if (bundle.getBoolean(MoveBookmarkDialog.MOVED_SUCCESSFULLY)) {
                    viewModel.clearTickedBookmarks()
                }
            }
        }
    }

    private fun openDeleteBookmarkDialog() {
        MaterialDialog(requireContext())
            .title(text = "Delete Bookmarks")
            .message(text = "Are you sure you want to remove the selected bookmark?")
            .show {
                positiveButton(text = "Confirm") {
                    viewModel.deleteBookmarks()
                    mainViewModel.setActionMode(false)
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
