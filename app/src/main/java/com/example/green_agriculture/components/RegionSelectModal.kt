package com.example.green_agriculture.components

import android.app.Dialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.green_agriculture.R
import com.example.green_agriculture.adapter.RegionSelectListAdapter
import com.example.green_agriculture.adapter.RegionSelectListAdapterData
import com.example.green_agriculture.databinding.LayoutRegionSelectModalBinding
import com.example.green_agriculture.entity.RegionData
import com.example.green_agriculture.extend.dp
import com.example.green_agriculture.toolkit.LogUtils
import com.github.promeg.pinyinhelper.Pinyin
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegionSelectModal(
    private val corner: Int,
    private val maskOpacity: Float,
    private val regionData: StateFlow<List<RegionData>>,
    private val initialValue: List<String> = emptyList(),
) : BottomSheetDialogFragment() {
    private lateinit var binding: LayoutRegionSelectModalBinding
    private val regionSelectListAdapter = RegionSelectListAdapter {
        selectedRegionData.value = selectedRegionData.value.toMutableList().apply {
            if (isNotEmpty() && (last().children?.isEmpty() ?: true)) removeLastOrNull()
            add(it)
        }.toList()

        handleRefreshSelectedRegionContainer()


    }

    fun handleRefreshSelectedRegionContainer() {
        val length = selectedRegionData.value.size

        binding.selectedRegionContainer.removeViews(
            1,
            binding.selectedRegionContainer.childCount - 1
        )

        val ctx = requireContext()
        val ids = ArrayList<Int>()
        for (i in 0 until length) {
            val button = ButtonWidget(ctx)
            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                36.dp.toInt(),
            )

            ids.add(i)
            button.id = i
            button.corner = 6.dp
            button.setTag(R.id.child_order, i)
            button.button.text = selectedRegionData.value[i].label

            button.onClickListener = { view ->
                val index = view.getTag(R.id.child_order) as Int
                selectedRegionData.value = selectedRegionData.value.subList(0, index)
                handleRefreshSelectedRegionContainer()
            }

            binding.selectedRegionContainer.addView(button, params)
        }

        binding.selectedRegionContainerFlow.referencedIds = ids.toIntArray()
        
        if (selectedRegionData.value.isEmpty()) {
            initializeData(regionData.value)
        } else {
            val children = selectedRegionData.value.lastOrNull()?.children
            if (children != null) initializeData(children)
        }
    }

    private val bgGradientDrawable = GradientDrawable().apply {
        setColor(0xFFFFFFFF.toInt())
        cornerRadii = floatArrayOf(
            corner.dp, corner.dp,
            corner.dp, corner.dp,
            0f, 0f,
            0f, 0f,
        )
    }

    /**
     * 被选中的数据
     */
    var selectedRegionData = MutableStateFlow<List<RegionData>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.BottomSheetWidgetTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState).apply {
            setCancelable(true)
            setCanceledOnTouchOutside(false)
            window?.setDimAmount(maskOpacity)
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = LayoutRegionSelectModalBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = regionSelectListAdapter
        binding.lifecycleOwner = this
        binding.modal = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.root.background = bgGradientDrawable
    }

    private fun computeDisplayGroups(regions: List<RegionData>): List<RegionSelectListAdapterData> {
        val regionList = ArrayList<RegionSelectListAdapterData>()
        // 展示分组
        val displayGroups = regions.groupBy { Pinyin.toPinyin(it.label, "").take(1) }.toSortedMap()
        displayGroups.forEach { (key, value) ->
            regionList.add(
                RegionSelectListAdapterData(
                    order = 0,
                    firstChar = key,
                    type = RegionSelectListAdapter.HEADER,
                )
            )

            value.forEachIndexed { index, item ->
                regionList.add(
                    RegionSelectListAdapterData(
                        type = RegionSelectListAdapter.CONTENT,
                        order = index,
                        data = item,
                    )
                )
            }
        }
        return regionList
    }

    fun initializeData(regionData: List<RegionData>) {
        // 这里执行数据初始化的工作
        val regionList = computeDisplayGroups(regionData)
        regionSelectListAdapter.updateList(regionList, "")
    }

    fun findRegionDataOfDescendants(
        resourceData: List<RegionData>,
        resourceIds: List<String>,
    ): List<RegionData> {
        var match: RegionData?
        var children = resourceData
        val result = ArrayList<RegionData>()
        val ids = resourceIds.toMutableList()

        while (ids.isNotEmpty() && children.isNotEmpty()) {
            val id = ids.first()
            ids.removeAt(0)
            match = children.find { it.value == id }

            if (match != null) result.add(match)
            children = match?.children ?: emptyList()
        }

        return result
    }

    /**
     * 点击确认按钮时触发
     */
    val onConfirm: (View) -> Unit = {
        dismiss()
    }

    companion object {
        const val TAG = "###REGION_SELECT_MODAL###"

        inline fun show(
            regionData: StateFlow<List<RegionData>>,
            fragmentManager: FragmentManager,
            maskOpacity: Float = 0.4f,
            corner: Int = 10,
        ) {
            val modal = RegionSelectModal(
                corner = corner,
                regionData = regionData,
                maskOpacity = maskOpacity,
                initialValue = listOf("11", "1101", "110101", "110101001")
            )

            if (regionData.value.isEmpty()) {
                modal.lifecycleScope.launch {
                    try {
                        regionData.collect {
                            if (it.isNotEmpty()) {
                                modal.initializeData(it)
                                cancel()
                            }
                        }
                    } catch (t: Throwable) {
                        LogUtils.d(t)
                    }
                }
            } else {
                modal.initializeData(regionData.value)
            }

            modal.show(fragmentManager, TAG)
        }
    }
}
