package com.flamyoad.honnoki.ui.library.bookmark

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.BookmarkAdapter
import com.flamyoad.honnoki.adapter.BookmarkGroupAdapter
import com.flamyoad.honnoki.databinding.FragmentBookmarkBinding
import com.flamyoad.honnoki.model.Bookmark
import java.security.PrivateKey

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

        val groupAdapter = BookmarkGroupAdapter()
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

        val bookmarkAdapter = BookmarkAdapter(this::openBookmark)

        viewModel.bookmarkGroupsWithCoverImages.observe(viewLifecycleOwner) {
            groupAdapter.submitList(it)
        }

        viewModel.bookmarkItems.observe(viewLifecycleOwner) {

        }
    }

    private fun openBookmark(bookmark: Bookmark) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic fun newInstance() = BookmarkFragment()
    }
}
