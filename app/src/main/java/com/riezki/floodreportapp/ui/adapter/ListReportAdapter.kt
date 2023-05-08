package com.riezki.floodreportapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.riezki.floodreportapp.R
import com.riezki.floodreportapp.databinding.ItemListReportBinding
import com.riezki.floodreportapp.model.CloudDataEntity


class ListReportAdapter : ListAdapter<CloudDataEntity, ListReportAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CloudDataEntity>() {
            override fun areItemsTheSame(oldItem: CloudDataEntity, newItem: CloudDataEntity): Boolean =
                oldItem.id == newItem.id && oldItem.loc == newItem.loc && oldItem.image == newItem.image


            override fun areContentsTheSame(oldItem: CloudDataEntity, newItem: CloudDataEntity): Boolean =
                oldItem == newItem

        }
    }

    class ViewHolder(private val binding: ItemListReportBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CloudDataEntity) {
            with(binding) {
                binding.namaLokasi.text = item.loc
                binding.imageReportId.load(item.image){
                    crossfade(true)
                    placeholder(R.drawable.ic_download)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemListReportBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }
}
