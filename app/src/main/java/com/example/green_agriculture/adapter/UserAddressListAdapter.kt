package com.example.green_agriculture.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.green_agriculture.databinding.LayoutUserAddressListItemBinding
import com.example.green_agriculture.entity.UserAddressItemOption

class UserAddressListAdapter(private val onCheckedItem: (UserAddressItemOption) -> Unit) :
    PagingDataAdapter<UserAddressItemOption, UserAddressListAdapter.ItemViewHolder>(Diff()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            LayoutUserAddressListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, onCheckedItem) }
    }

    class ItemViewHolder(private val binding: LayoutUserAddressListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: UserAddressItemOption, onChecked: (UserAddressItemOption) -> Unit) {
            binding.checkbox.checkedValue = data.defaultFlag
            binding.userAddress.text = data.address
            binding.userName.text = data.username
            binding.userPhone.text = data.phone
            binding.checkbox.onCheckedChange = {
                onChecked(data)
            }
            binding.checkbox.isClickable = !data.defaultFlag
        }
    }

    class Diff : DiffUtil.ItemCallback<UserAddressItemOption>() {
        override fun areItemsTheSame(
            oldItem: UserAddressItemOption,
            newItem: UserAddressItemOption,
        ): Boolean {
            return oldItem.addressId == newItem.addressId
        }

        override fun areContentsTheSame(
            oldItem: UserAddressItemOption,
            newItem: UserAddressItemOption,
        ): Boolean {
            return oldItem == newItem
        }
    }
}