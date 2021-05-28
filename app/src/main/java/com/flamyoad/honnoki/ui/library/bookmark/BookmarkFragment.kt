package com.flamyoad.honnoki.ui.library.bookmark

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.adapter.BookmarkAdapter
import com.flamyoad.honnoki.adapter.BookmarkGroupAdapter
import com.flamyoad.honnoki.databinding.FragmentBookmarkBinding
import com.flamyoad.honnoki.dialog.AddBookmarkGroupDialog
import com.flamyoad.honnoki.dialog.ChangeBookmarkGroupNameDialog
import com.flamyoad.honnoki.dialog.DeleteBookmarkGroupDialog
import com.flamyoad.honnoki.model.BookmarkWithOverview
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity

@ExperimentalPagingApi
class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: BookmarkViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookmarkBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerForContextMenu(binding.btnContextMenu)
        binding.btnContextMenu.setOnClickListener {
            it.showContextMenu()
        }

        val groupAdapter = BookmarkGroupAdapter(
            viewModel::selectBookmarkGroup,
            this::openAddNewBookmarkDialog
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

        val bookmarkAdapter = BookmarkAdapter(this::openBookmark)
        val bookmarkLayoutManager = GridLayoutManager(requireContext(), 3)

        with(binding.listItems) {
            adapter = bookmarkAdapter
            layoutManager = bookmarkLayoutManager
        }

        viewModel.bookmarkGroupsWithCoverImages.observe(viewLifecycleOwner) {
            groupAdapter.submitList(it)
        }

        viewModel.selectedBookmarkGroup().observe(viewLifecycleOwner) {
            binding.header.text = it.name
        }

        viewModel.bookmarkItems.observe(viewLifecycleOwner) {
            bookmarkAdapter.submitList(it)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        val selectedGroup = viewModel.getSelectedBookmarkGroup()
        menu.apply {
            setHeaderTitle(selectedGroup.name)
            add(MENU_CHANGE_GROUP_NAME)
            add(MENU_DELETE_GROUP)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val selectedGroup = viewModel.getSelectedBookmarkGroup()

        val dialog: DialogFragment =
            when (item.title) {
                MENU_CHANGE_GROUP_NAME -> ChangeBookmarkGroupNameDialog.newInstance(selectedGroup)
                MENU_DELETE_GROUP -> DeleteBookmarkGroupDialog.newInstance(selectedGroup)
                else -> throw IllegalArgumentException("Invalid menu option!")
            }

        dialog.show(childFragmentManager, "dialog")
        return true
    }

    private fun openBookmark(bookmark: BookmarkWithOverview) {
        val intent = Intent(requireContext(), MangaOverviewActivity::class.java).apply {
            putExtra(MangaOverviewActivity.MANGA_URL, bookmark.overview.link)
            putExtra(MangaOverviewActivity.MANGA_SOURCE, bookmark.overview.source.toString())
            putExtra(MangaOverviewActivity.MANGA_TITLE, bookmark.overview.mainTitle)
        }
        requireContext().startActivity(intent)
    }

    private fun openAddNewBookmarkDialog() {
        val dialog = AddBookmarkGroupDialog.newInstance()
        dialog.show(childFragmentManager, AddBookmarkGroupDialog.TAG)
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
