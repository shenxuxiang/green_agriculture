package com.example.green_agriculture.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.green_agriculture.R
import com.example.green_agriculture.components.SwiperWidgetOptionItem
import com.example.green_agriculture.databinding.LayoutSwiperViewItemBinding

private class DiffItemCallback : DiffUtil.ItemCallback<SwiperWidgetOptionItem>() {
    override fun areItemsTheSame(
        oldItem: SwiperWidgetOptionItem,
        newItem: SwiperWidgetOptionItem,
    ): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(
        oldItem: SwiperWidgetOptionItem,
        newItem: SwiperWidgetOptionItem,
    ): Boolean {
        return oldItem == newItem
    }
}

/**
 * SwiperView 的适配器
 */
class SwiperWidgetAdapter() :
    ListAdapter<SwiperWidgetOptionItem, SwiperWidgetAdapter.SwiperItemViewHolder>(DiffItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwiperItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_swiper_view_item,
            parent,
            false
        )

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
            .load(option.url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.image)
    }

    class SwiperItemViewHolder(binding: LayoutSwiperViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val image = binding.image
    }
}