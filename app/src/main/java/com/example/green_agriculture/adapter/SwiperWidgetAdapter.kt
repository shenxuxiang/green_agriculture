package com.example.green_agriculture.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.green_agriculture.databinding.LayoutSwiperViewItemBinding
import com.example.green_agriculture.entity.SwiperWidgetItemOption
import com.example.green_agriculture.toolkit.CommonUtils

/**
 * SwiperView 的适配器
 */
class SwiperWidgetAdapter() :
    ListAdapter<SwiperWidgetItemOption, SwiperWidgetAdapter.SwiperItemViewHolder>(DiffItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwiperItemViewHolder {
        val binding = LayoutSwiperViewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return SwiperItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SwiperItemViewHolder, position: Int) {
        val option = getItem(position)
        Glide.with(holder.itemView.context)
            .load(CommonUtils.networkImageUrl(option.url))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.image)
    }

    class SwiperItemViewHolder(binding: LayoutSwiperViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val image = binding.image
    }

    class DiffItemCallback : DiffUtil.ItemCallback<SwiperWidgetItemOption>() {
        override fun areItemsTheSame(
            oldItem: SwiperWidgetItemOption,
            newItem: SwiperWidgetItemOption,
        ): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(
            oldItem: SwiperWidgetItemOption,
            newItem: SwiperWidgetItemOption,
        ): Boolean {
            return oldItem == newItem
        }
    }
}