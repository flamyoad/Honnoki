package com.flamyoad.honnoki.ui.onboarding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.common.adapter.BaseListAdapter
import com.flamyoad.honnoki.databinding.OnboardingUiModeItemBinding
import com.flamyoad.honnoki.ui.onboarding.model.SelectedUiMode

class UiModeAdapter(val listener: Listener) :
    BaseListAdapter<SelectedUiMode, OnboardingUiModeItemBinding>(COMPARATOR) {

    interface Listener {
        fun onUiModeClick(item: SelectedUiMode)
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> OnboardingUiModeItemBinding
        get() = OnboardingUiModeItemBinding::inflate

    override fun onBind(holder: BaseViewHolder, item: SelectedUiMode) {
        with(holder.binding) {
            val context = root.context
            cardView.strokeWidth = if (item.isSelected) 4 else 0
            txtTitle.text = item.uiMode.getString(context)
            Glide.with(this.root)
                .load(item.uiMode.getDrawable(context))
                .dontAnimate()
                .into(imageView)
        }
    }

    override fun onItemClick(item: SelectedUiMode?) {
        super.onItemClick(item)
        listener.onUiModeClick(item ?: return)
    }

    companion object {
        private val COMPARATOR =
            object : DiffUtil.ItemCallback<SelectedUiMode>() {
                override fun areItemsTheSame(
                    oldItem: SelectedUiMode,
                    newItem: SelectedUiMode
                ): Boolean {
                    return oldItem.uiMode == newItem.uiMode
                }

                override fun areContentsTheSame(
                    oldItem: SelectedUiMode,
                    newItem: SelectedUiMode
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}