package com.example.entregas.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.entregas.R
import com.example.entregas.ui.adapter.ReleaseAdapter.*
import com.example.entregas.data.room.ReleaseEntity
import com.example.entregas.databinding.RecycleListExtractBinding
import com.example.entregas.util.toCoinString

class ReleaseAdapter(private val onClick:(releaseEntity:ReleaseEntity)->Unit): RecyclerView.Adapter<ReleaseViewHolder>() {
    private val data:MutableList<ReleaseEntity> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReleaseViewHolder {
        return ReleaseViewHolder.inflate(parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ReleaseViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, onClick)
    }

    fun updateItems(releaseList: List<ReleaseEntity>){
        data.clear()
        data.addAll(releaseList)
        notifyDataSetChanged()
    }

    class ReleaseViewHolder(private val binding: RecycleListExtractBinding):RecyclerView.ViewHolder(binding.root){
        private val date = binding.txvDate
        private val op = binding.txvOp
        private val value = binding.txvValue
        private val desc = binding.txvDesc

        companion object{
            fun inflate(viewGroup: ViewGroup):ReleaseViewHolder{
                val binding = RecycleListExtractBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false)
                return ReleaseViewHolder(binding)
            }
        }

        fun bind(release: ReleaseEntity, onClick: (release: ReleaseEntity) -> Unit) {
            configureColor(release)
            date.text = release.releaseDate
            op.text = release.releaseOp
            value.text = release.releaseValue.toCoinString()
            desc.text = release.releaseDesc

            binding.root.setOnClickListener { onClick(release) }
        }

        private fun configureColor(release: ReleaseEntity) {
            if (release.releaseOp == "E") {
                binding.root.setBackgroundColor(binding.root.context.getColor(R.color.secondary))
            } else {
                binding.root.setBackgroundColor(binding.root.context.getColor(R.color.secondary_dark))
            }
        }
    }
}