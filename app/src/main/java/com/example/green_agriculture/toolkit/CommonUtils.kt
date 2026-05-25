package com.example.green_agriculture.toolkit

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import com.example.green_agriculture.entity.RegionData

object CommonUtils {
    /**
     * 获取当前 Activity
     */
    fun getActivity(context: Context): AppCompatActivity? {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is AppCompatActivity) return ctx
            ctx = ctx.baseContext
        }

        return null
    }

    fun networkImageUrl(url: String): String {
        if (Regex("""^https?://""").matches(url)) return url

        return if (url.startsWith("/")) {
            "${AppEnv.BASE_URL}$url"
        } else {
            "${AppEnv.BASE_URL}/$url"
        }
    }

    /**
     * 查找所有上级行政区域
     */
    fun findAllParentRegions(
        resourceTree: List<RegionData>,
        regionCode: String,
    ): List<RegionData>? {
        return findAllParentRegionCodes(regionCode)?.let {
            findRegionByCode(resourceTree, it)
        }
    }

    /**
     * 返回所有上级的 regionCode 集合（包含自身）
     */
    fun findAllParentRegionCodes(regionCode: String): List<String>? {
        return when (regionCode.length) {
            2 -> {
                listOf(regionCode)
            }

            4 -> {
                listOf(
                    regionCode.take(2),
                    regionCode,
                )
            }

            6 -> {
                listOf(
                    regionCode.take(2),
                    regionCode.take(4),
                    regionCode,
                )
            }

            9 -> {
                listOf(
                    regionCode.take(2),
                    regionCode.take(4),
                    regionCode.take(6),
                    regionCode,
                )
            }

            12 -> {
                listOf(
                    regionCode.take(2),
                    regionCode.take(4),
                    regionCode.take(6),
                    regionCode.take(9),
                    regionCode,
                )
            }

            else -> null
        }
    }

    /**
     * 通过 RegionCode 查找匹配的 RegionData 对象，
     * @param resourceTree 查询范围
     * @param regionCodes 目标 code，一个集合，排在后面的比须是前面的子项
     * @return List<RegionData>?
     */
    fun findRegionByCode(
        resourceTree: List<RegionData>,
        regionCodes: List<String>,
    ): List<RegionData>? {
        var match: RegionData?
        var children = resourceTree
        val result = ArrayList<RegionData>()
        val ids = regionCodes.toMutableList()

        while (ids.isNotEmpty() && children.isNotEmpty()) {
            val id = ids.first()
            ids.removeAt(0)
            match = children.find { it.value == id }

            if (match != null) result.add(match)
            children = match?.children ?: emptyList()
        }

        return result.takeIf { it.isNotEmpty() }
    }
}