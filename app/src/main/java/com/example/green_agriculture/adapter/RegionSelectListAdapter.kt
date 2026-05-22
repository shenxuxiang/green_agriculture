package com.example.green_agriculture.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.green_agriculture.R
import com.example.green_agriculture.databinding.LayoutRegionSelectContentBinding
import com.example.green_agriculture.databinding.LayoutRegionSelectHeaderBinding
import com.example.green_agriculture.entity.RegionData
import com.example.green_agriculture.toolkit.VibratorUtils

class RegionSelectListAdapterData(
    val order: Int? = null,
    val data: RegionData? = null,
    val firstChar: String? = null,
    val type: Int = RegionSelectListAdapter.HEADER,
)

class RegionSelectListAdapter(private val onSelectListener: (RegionData) -> Unit) :
    ListAdapter<RegionSelectListAdapterData, RegionSelectListAdapter.RegionSelectViewHolder>(
        DiffItemCallback()
    ) {
    /**
     * 当前被选中的值
     */
    private var selectedValue = ""

    /**
     * 用户选中时的回调
     */
    private val onSelect: (RegionData) -> Unit = { regionData ->
        /**
         * 此处不需要调用 notifyItemChanged(position)，用户在触发 onSelect 事件时已经更新了样式。
         * 这样可以避免不必要的操作。
         * 此处仍需要更新原先被选中的 ViewHolder 的样式
         */
        val prevPosi =
            if (selectedValue.isEmpty()) {
                RecyclerView.NO_POSITION
            } else {
                currentList.indexOfFirst { it?.data?.value == selectedValue }
            }
        if (prevPosi != RecyclerView.NO_POSITION) notifyItemChanged(prevPosi)
        
        selectedValue = regionData.value
        onSelectListener(regionData)
    }

    /**
     * 执行更新列表操作
     */
    fun updateList(list: List<RegionSelectListAdapterData>, selectedValue: String) {
        // 先更新 selectedValue、在执行 submitList()
        this.selectedValue = selectedValue
        this.submitList(list)
    }

    override fun getItemViewType(position: Int) = getItem(position).type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionSelectViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER -> {
                val binding = LayoutRegionSelectHeaderBinding.inflate(inflater, parent, false)
                RegionSelectViewHolder.Header(binding)
            }

            else -> {
                val binding = LayoutRegionSelectContentBinding.inflate(inflater, parent, false)
                RegionSelectViewHolder.Content(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RegionSelectViewHolder, position: Int) {
        val data = getItem(position)
        when (holder) {
            is RegionSelectViewHolder.Header -> {
                holder.bind(data)
            }

            is RegionSelectViewHolder.Content -> {
                holder.bind(
                    data = data,
                    onSelect = onSelect,
                    isSelected = selectedValue == data.data?.value,
                )
            }
        }
    }

    sealed class RegionSelectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        class Header(private val binding: LayoutRegionSelectHeaderBinding) :
            RegionSelectViewHolder(binding.root) {
            fun bind(data: RegionSelectListAdapterData) {
                binding.title.text = data.firstChar
            }
        }

        class Content(private val binding: LayoutRegionSelectContentBinding) :
            RegionSelectViewHolder(binding.root) {
            fun bind(
                isSelected: Boolean,
                data: RegionSelectListAdapterData,
                onSelect: (RegionData) -> Unit,
            ) {
                val regionData = data.data!!
                if (data.order!! % 2 == 0) {
                    binding.root.setBackgroundResource(R.color.region_select_item_even)
                } else {
                    binding.root.setBackgroundResource(R.color.region_select_item_odd)
                }
                binding.root.isSelected = isSelected
                binding.title.text = regionData.label
                binding.root.setOnClickListener {
                    onSelect(regionData)
                    // 震动提示
                    VibratorUtils.oneShot()
                    binding.root.isSelected = true
                }
            }
        }
    }

    class DiffItemCallback() : DiffUtil.ItemCallback<RegionSelectListAdapterData>() {
        override fun areItemsTheSame(
            v1: RegionSelectListAdapterData,
            v2: RegionSelectListAdapterData,
        ): Boolean {
            return if (v1.type == v2.type) {
                if (v1.type == HEADER) {
                    v1.firstChar == v2.firstChar
                } else {
                    v1.data?.value == v2.data?.value
                }
            } else {
                false
            }
        }

        override fun areContentsTheSame(
            v1: RegionSelectListAdapterData,
            v2: RegionSelectListAdapterData,
        ): Boolean {
            return if (v1.type == HEADER) {
                v1.firstChar == v2.firstChar
            } else {
                v1.data?.value == v2.data?.value
            }
        }
    }

    companion object {
        const val HEADER = 0
        const val CONTENT = 1
    }
}