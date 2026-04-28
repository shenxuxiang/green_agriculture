package com.example.green_agriculture.adapter

import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.green_agriculture.databinding.LayoutPolicyInformationItemBinding
import com.example.green_agriculture.entity.PolicyInformationItemOption
import com.example.green_agriculture.toolkit.CalculateUtils
import com.example.green_agriculture.toolkit.CommonUtils


class PolicyInformationListAdepter() :
    ListAdapter<PolicyInformationItemOption, PolicyInformationListAdepter.ViewHolder>(
        DiffItemCallback()
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutPolicyInformationItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: LayoutPolicyInformationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(option: PolicyInformationItemOption) {
            val ctx = binding.root.context
            val corner = RoundedCorners(CalculateUtils.dpToPx(6, ctx).toInt())
            Glide.with(ctx)
                .load(CommonUtils.networkImageUrl(option.url))
                .transform(corner)
                .into(binding.avatar)

            binding.title.text = "${option.title}是否合适的回复是地方还是兑换积分"
            binding.updateTime.text = option.updateTime
        }

        init {
            binding.root.background = GradientDrawable().apply {
                setColor(0xFFFFFFFF.toInt())
                cornerRadius = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    10f,
                    binding.root.context.resources.displayMetrics
                )
            }
        }
    }

    class DiffItemCallback : DiffUtil.ItemCallback<PolicyInformationItemOption>() {
        override fun areItemsTheSame(
            oldItem: PolicyInformationItemOption,
            newItem: PolicyInformationItemOption,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PolicyInformationItemOption,
            newItem: PolicyInformationItemOption,
        ): Boolean {
            return oldItem == newItem
        }
    }
}

class PolicyInformationListItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.layoutManager?.getPosition(view)

        if (position != null && position != RecyclerView.NO_POSITION) {
            if (position > 0) {
                outRect.top = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    12f,
                    parent.context.resources.displayMetrics
                ).toInt()
            }
        }
    }
}