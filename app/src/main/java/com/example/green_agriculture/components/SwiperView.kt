package com.example.green_agriculture.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.core.app.ComponentActivity
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.green_agriculture.R
import com.example.green_agriculture.databinding.SwiperViewItemLayoutBinding
import com.example.green_agriculture.pages.main.MainViewModel

data class SwiperViewItemOption(val url: String)

class SwiperView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private val viewPager: ViewPager2
    private val adapter = SwiperViewAdapter()
    private val mainViewModel by (context as ComponentActivity).viewModels<MainViewModel>()

    var options: List<SwiperViewItemOption> = emptyList()
        set(value) {
            if (value == field) return
            field = value
            adapter.submitList(value)
        }

    var index: Int = 0
        set(value) {
            if (field == value) return
            field = value

            viewPager.setCurrentItem(index, true)
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.swiper_view_layout, this, true).apply {
            viewPager = findViewById(R.id.view_pager)
            viewPager.offscreenPageLimit = 3
            viewPager.isUserInputEnabled = true
            viewPager.adapter = adapter
        }

        //Log.d("GA_APP", "parentViewPager2: ${mainViewModel.uiState.value.viewPager2}")
        setOnTouchListener { v, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                //mainViewModel.uiState.value.viewPager2?.isUserInputEnabled = false
                v.parent.requestDisallowInterceptTouchEvent(true)
            } else if (event.actionMasked == MotionEvent.ACTION_UP) {
                //mainViewModel.uiState.value.viewPager2?.isUserInputEnabled = true
                v.parent.requestDisallowInterceptTouchEvent(false)
            }

            true
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("options")
        fun setOptionsAttr(view: SwiperView, options: List<SwiperViewItemOption>) {
            view.options = options
        }

        @JvmStatic
        @BindingAdapter("index")
        fun setIndexAttr(view: SwiperView, index: Int) {
            view.index = index
        }
    }
}

class SwiperViewAdapter() :
    ListAdapter<SwiperViewItemOption, SwiperViewAdapter.SwiperItemViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwiperItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.swiper_view_item_layout,
            parent,
            false
        )

        val binding = SwiperViewItemLayoutBinding.inflate(
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

    class DiffCallback : DiffUtil.ItemCallback<SwiperViewItemOption>() {
        override fun areItemsTheSame(
            oldItem: SwiperViewItemOption,
            newItem: SwiperViewItemOption,
        ): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(
            oldItem: SwiperViewItemOption,
            newItem: SwiperViewItemOption,
        ): Boolean {
            return oldItem == newItem
        }

    }

    class SwiperItemViewHolder(binding: SwiperViewItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val image = binding.image
    }
}