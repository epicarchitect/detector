package kolmachikhin.alexander.detector.ui.base.adapters.lists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kolmachikhin.alexander.detector.controllers.Model

abstract class ModelListAdapter<M: Model, VH: ModelListAdapter<M, VH>.ViewHolder<M>> : RecyclerView.Adapter<VH>() {

    fun areItemsTheSame(oldItem: M, newItem: M) = oldItem.id == newItem.id

    abstract fun areContentsTheSame(oldItem: M, newItem: M): Boolean

    abstract val itemLayout: Int
    abstract fun createViewHolder(view: View): VH

    var onClickListener: ((model: M, position: Int) -> Unit)? = null

    fun onClick(l: ((model: M, position: Int) -> Unit)?) {
        onClickListener = l
    }

    var onLongClickListener: ((model: M, position: Int) -> Unit)? = null

    fun onLongClick(l: ((model: M, position: Int) -> Unit)?) {
        onLongClickListener = l
    }

    var list: List<M> = ArrayList()
        set(newList) {
            val diffCallback = DiffCallback(field, newList)
            val result = DiffUtil.calculateDiff(diffCallback)
            field = newList
            result.dispatchUpdatesTo(this)
        }

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(list[holder.adapterPosition])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        createViewHolder(LayoutInflater.from(parent.context).inflate(itemLayout, parent, false))

    override fun getItemCount() = list.size

    inner class DiffCallback(val oldList: List<M>, val newList: List<M>) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldPosition: Int, newPosition: Int) =
            areItemsTheSame(oldList[oldPosition], newList[newPosition])

        override fun areContentsTheSame(oldPosition: Int, newPosition: Int) =
            areContentsTheSame(oldList[oldPosition], newList[newPosition])

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

    }

    abstract inner class ViewHolder<M: Model>(view: View) : RecyclerView.ViewHolder(view) {

        init {
            view.setOnClickListener {
                onClickListener?.invoke(list[adapterPosition], adapterPosition)
            }

            view.setOnLongClickListener {
                onLongClickListener?.invoke(list[adapterPosition], adapterPosition)
                true
            }
        }

        abstract fun bind(model: M)

    }

}