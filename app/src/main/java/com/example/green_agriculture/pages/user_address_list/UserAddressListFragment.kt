package com.example.green_agriculture.pages.user_address_list

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.annotation.AutoBinding
import com.example.green_agriculture.adapter.UserAddressListAdapter
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentUserAddressListBinding
import com.example.green_agriculture.entity.UserAddressItemOption
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
    }

    val onCheckedItem: (UserAddressItemOption) -> Unit = {
        viewModel.handleSetDefaultAddress(it.addressId)
    }
}