package com.example.green_agriculture.pages.user_address_list

import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.annotation.AutoBinding
import com.example.green_agriculture.adapter.UserAddressListAdapter
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.components.RefreshHeaderWidget
import com.example.green_agriculture.databinding.FragmentUserAddressListBinding
import com.example.green_agriculture.entity.UserAddressItemOption
import com.example.green_agriculture.extend.dp
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UserAddressListFragment : BaseFragment() {
    @AutoBinding
    override lateinit var binding: FragmentUserAddressListBinding

    private val viewModel: UserAddressViewModel by viewModels()

    private lateinit var adapter: UserAddressListAdapter
    override fun initView() {
        super.initView()

        val context = requireContext()
        val refreshHeaderWidget = RefreshHeaderWidget(context)
        binding.smartRefreshLayout.setRefreshHeader(refreshHeaderWidget)
        binding.smartRefreshLayout.setHeaderHeightPx(60.dp.toInt())
        binding.smartRefreshLayout.setEnableRefresh(true)
        binding.smartRefreshLayout.setReboundDuration(300)
        binding.smartRefreshLayout.setReboundInterpolator(DecelerateInterpolator())
        binding.smartRefreshLayout.setEnableLoadMore(false)


        adapter = UserAddressListAdapter(onCheckedItem)
        binding.userAddressList.adapter = adapter
        binding.userAddressList.itemAnimator = null
        binding.userAddressList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onDataObserve() {
        super.onDataObserve()

        lifecycleScope.launch {
            viewModel.displayFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { pagingData ->
                    adapter.submitData(pagingData)
                }
        }

        binding.smartRefreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                adapter.refresh()
            }
        })

        adapter.addLoadStateListener { loadState ->
            if (binding.smartRefreshLayout.isRefreshing) {
                if (loadState.refresh is LoadState.NotLoading) {
                    binding.smartRefreshLayout.finishRefresh(true)
                } else if (loadState.refresh is LoadState.Error) {
                    binding.smartRefreshLayout.finishRefresh(false)
                }
            }
        }
    }

    val onCheckedItem: (UserAddressItemOption) -> Unit = {
        viewModel.handleSetDefaultAddress(it.addressId)
    }
}