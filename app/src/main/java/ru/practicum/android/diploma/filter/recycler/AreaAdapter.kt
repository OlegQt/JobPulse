package ru.practicum.android.diploma.filter.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.practicum.android.diploma.databinding.ItemViewHolderAreaBinding
import ru.practicum.android.diploma.filter.domain.models.AbstarctData

class AreaAdapter(
    private val areaList: MutableList<AbstarctData>,
    private val onItemClickListener: Clickable
) : Adapter<AreaAdapter.AreaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaViewHolder {
        val bindingImpl =
            ItemViewHolderAreaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AreaViewHolder(bindingImpl)
    }

    override fun getItemCount() = areaList.size

    override fun onBindViewHolder(holder: AreaViewHolder, position: Int) {
        holder.bindInfo(areaList[position], onItemClickListener)
    }

    fun changeData(newDataList: List<AbstarctData>) {
        // TODO: insert diffUtil if needed
        val diffResult = DiffUtil.calculateDiff(AreaDiffCallback(areaList, newDataList))
        areaList.clear()
        areaList.addAll(newDataList)
        diffResult.dispatchUpdatesTo(this)

        //areaList.clear()
        //areaList.addAll(newDataList)
        //this.notifyDataSetChanged()
    }

    class AreaViewHolder(val binding: ItemViewHolderAreaBinding) : ViewHolder(binding.root) {
        fun bindInfo(area: AbstarctData, onItemClickListener: Clickable) {
            binding.txtAreaName.text = area.name
            binding.txtAreaName.setOnClickListener {
                onItemClickListener.onClick(area)
            }
        }
    }

    fun interface Clickable {
        fun onClick(clickedAreaModel: AbstarctData)
    }

    class AreaDiffCallback(
        private val oldList: List<AbstarctData>,
        private val newList: List<AbstarctData>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
