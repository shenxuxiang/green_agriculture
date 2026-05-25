package com.example.green_agriculture.components

import android.app.Dialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.green_agriculture.R
import com.example.green_agriculture.adapter.RegionSelectListAdapter
import com.example.green_agriculture.adapter.RegionSelectListAdapterData
import com.example.green_agriculture.databinding.LayoutRegionSelectModalBinding
import com.example.green_agriculture.entity.RegionData
import com.example.green_agriculture.entity.SelectedRegionItemOption
import com.example.green_agriculture.extend.dp
import com.example.green_agriculture.pages.main.MainViewModel
import com.example.green_agriculture.toolkit.CommonUtils
import com.example.green_agriculture.toolkit.LogUtils
import com.example.green_agriculture.toolkit.Toast
import com.github.promeg.pinyinhelper.Pinyin
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegionSelectModal(
    private val corner: Int,
    private val maskOpacity: Float,
    private val initialValue: List<SelectedRegionItemOption>? = null,
    private val onConfirmCallback: ((List<SelectedRegionItemOption>) -> Unit)? = null,
) : BottomSheetDialogFragment() {
    private lateinit var binding: LayoutRegionSelectModalBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val regionData: StateFlow<List<RegionData>> by lazy { mainViewModel.regionData }
    private val regionSelectListAdapter = RegionSelectListAdapter {
        selectedRegionData.value = selectedRegionData.value.toMutableList().apply {
            if (isNotEmpty() && (last().children?.isEmpty() ?: true)) removeLastOrNull()
            add(it)
        }.toList()

        resetSelectedViews()
        resetRecyclerView()
    }

    private val modalGradientDrawable = GradientDrawable().apply {
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
    val selectedRegionData = MutableStateFlow<List<RegionData>>(emptyList())

    /**
     * 数据加载中
     */
    var isLoading = MutableStateFlow(true)

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

        binding.root.background = modalGradientDrawable

        if (regionData.value.isEmpty()) {
            lifecycleScope.launch {
                try {
                    regionData.collect {
                        if (it.isNotEmpty()) {
                            initializer(it, initialValue)
                            cancel()
                        }
                    }
                } catch (t: Throwable) {
                    LogUtils.d(t)
                }
            }
        } else {
            initializer(regionData.value, initialValue)
        }
    }

    /**
     * 判断 recyclerView 展示列表是否需要更新
     * 如果 selectedRegionData.value 为空，则应该展示最顶层的行政区域
     * 如果 selectedRegionData.value 的最后一项的 children 为空，
     * 那么当前选中的区域就是最底层的行政区域了，这种情况下，不需要更新展示列表
     */
    fun resetRecyclerView() {
        val regionList = if (selectedRegionData.value.isEmpty()) {
            computeDisplayGroups(regionData.value)
        } else {
            val children = selectedRegionData.value.lastOrNull()?.children
            if (children != null) computeDisplayGroups(children) else null
        }

        if (regionList != null) regionSelectListAdapter.updateList(regionList)
    }

    /**
     * 更新被选中的视图
     * 每当 selectedRegionData 发生变化时，都需要调用该方法，更新选中视图
     */
    fun resetSelectedViews() {
        val length = selectedRegionData.value.size

        binding.selectedContainer.removeViews(
            1,
            binding.selectedContainer.childCount - 1
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
            button.onClickListener = { onDeleteSelectedItem(it) }

            binding.selectedContainer.addView(button, params)
        }

        binding.selectedContainerFlow.referencedIds = ids.toIntArray()
    }

    /**
     * 删除选中项
     */
    private fun onDeleteSelectedItem(view: View) {
        val index = view.getTag(R.id.child_order) as Int
        selectedRegionData.value = selectedRegionData.value.subList(0, index)
        resetSelectedViews()
        resetRecyclerView()
    }

    /**
     * 计算展示分组；
     * 先将列表 List<RegionData> 以首字母进行分组，得到 {A: [...], B: [...], C: [...]}；
     * 然后再转换成 List<RegionSelectListAdapterData>
     */
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

    /**
     * 初始化器
     */
    fun initializer(regionData: List<RegionData>, initialValue: List<SelectedRegionItemOption>?) {
        isLoading.value = false
        selectedRegionData.value = if (initialValue?.isNotEmpty() ?: false) {
            CommonUtils.findRegionByCode(
                regionData,
                initialValue.map { it.value },
            ) ?: emptyList()
        } else emptyList()

        val regionList: List<RegionSelectListAdapterData> =
            if (selectedRegionData.value.isEmpty()) {
                computeDisplayGroups(regionData)
            } else {
                val item = selectedRegionData.value.findLast { it.children?.isNotEmpty() ?: false }
                computeDisplayGroups(item!!.children!!)
            }

        regionSelectListAdapter.updateList(regionList, initialValue?.lastOrNull()?.label ?: "")
        resetSelectedViews()
    }

    /**
     * 点击确认按钮时触发
     */
    val onConfirm: (View) -> Unit = {
        if (selectedRegionData.value.isEmpty()) {
            Toast.showWarn("请选择行政区域", rootView = (binding.root.rootView as ViewGroup))
        } else {
            // 将数据回传给调用发
            onConfirmCallback?.invoke(
                selectedRegionData.value.map {
                    SelectedRegionItemOption(
                        label = it.label,
                        value = it.value,
                        fullName = it.fullName,
                    )
                }
            )
            dismiss()
        }
    }

    companion object {
        const val TAG = "###REGION_SELECT_MODAL###"

        fun show(
            initialValue: List<SelectedRegionItemOption>? = null,
            fragmentManager: FragmentManager,
            maskOpacity: Float = 0.4f,
            corner: Int = 10,
            onConfirm: (List<SelectedRegionItemOption>) -> Unit,
        ) {
            val modal = RegionSelectModal(
                corner = corner,
                maskOpacity = maskOpacity,
                initialValue = initialValue,
                onConfirmCallback = onConfirm
            )

            modal.show(fragmentManager, TAG)
        }
    }
}
