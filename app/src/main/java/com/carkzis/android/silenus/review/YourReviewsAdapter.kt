package com.carkzis.android.silenus.review

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.carkzis.android.silenus.data.YourReview
import com.carkzis.android.silenus.databinding.YourReviewItemBinding

class YourReviewsAdapter(private val onClickListener: OnClickListener) : ListAdapter<YourReview, YourReviewsAdapter.YourReviewsViewHolder>(YourReviewsDiffCallBack()),
    Filterable {

    // Create blank lists for the normal list, and the filtered version.
    var reviewList : ArrayList<YourReview> = ArrayList()
    var reviewListFiltered : ArrayList<YourReview> = ArrayList()

    override fun onBindViewHolder(holder: YourReviewsViewHolder, position: Int) {
        // TODO: Changed this.
        val review = reviewListFiltered[position]
        holder.itemView.setOnClickListener {
            onClickListener.onClick(review)
        }
        holder.bind(review)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourReviewsViewHolder {
        return YourReviewsViewHolder.from(parent)
    }

    override fun getItemCount(): Int = reviewListFiltered.size

    @SuppressLint("NotifyDataSetChanged")
    fun addItemsToAdapter(items: List<YourReview>) {
        reviewList = items as ArrayList<YourReview>
        reviewListFiltered = reviewList
        notifyDataSetChanged()
    }

    class YourReviewsViewHolder constructor(private var binding: YourReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(item: YourReview) {
                binding.yourReview = item
                binding.executePendingBindings()
            }
            companion object {
                fun from(parent: ViewGroup): YourReviewsViewHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = YourReviewItemBinding.inflate(
                        layoutInflater, parent, false
                    )
                    return YourReviewsViewHolder(binding)
                }
            }
        }

    override fun getFilter(): Filter {
        return object : Filter() {
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val charString = constraint?.toString() ?: ""
            if (charString.isEmpty()) {
                // If the search string is empty, we show all the items (the default).
                reviewListFiltered = reviewList
            } else {
                val filteredList = ArrayList<YourReview>()
                // Checking date, establishment and location currently.
                reviewList.filter {
                    it.dateAdded.toString().lowercase().contains(constraint!!.toString().lowercase())
                            || it.establishment!!.lowercase().contains(constraint.toString().lowercase())
                            || it.location!!.lowercase().contains(constraint.toString().lowercase())
                }.forEach {
                    filteredList.add(it)
                }
                reviewListFiltered = filteredList
            }

            return Filter.FilterResults().apply { values = reviewListFiltered }

        }

        @SuppressLint("NotifyDataSetChanged")
        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults?) {
            reviewListFiltered = if (results?.values == null) {
                ArrayList()
            } else {
                results.values as ArrayList<YourReview>
            }
            notifyDataSetChanged()
        }
    }

    }

    /**
     * Basically, we provide a higher-order function into the class.  onClick will perform this
     * function, which takes a YourReview data class, performs a job and returns Unit (nothing).
     */
    class OnClickListener(val clickListener: (review: YourReview) -> Unit) {
        fun onClick(review: YourReview) = clickListener(review)
    }

}

class YourReviewsDiffCallBack : DiffUtil.ItemCallback<YourReview>() {
    override fun areItemsTheSame(oldItem: YourReview, newItem: YourReview): Boolean {
        return oldItem.dateAdded == newItem.dateAdded
    }

    override fun areContentsTheSame(oldItem: YourReview, newItem: YourReview): Boolean {
        return oldItem == newItem
    }
}