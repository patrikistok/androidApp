package eu.mcomputing.mobv.zadanie.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity
import eu.mcomputing.mobv.zadanie.utils.ItemDiffCallback
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.interfaces.ItemInterface

class FeedAdapter(private val itemInterface: ItemInterface) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    private var items: List<UserEntity> = listOf()

    // ViewHolder poskytuje odkazy na zobrazenia v každej položke
    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // Táto metóda vytvára nový ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_item, parent, false)
        return FeedViewHolder(view)
    }

    // Táto metóda prepojí dáta s ViewHolderom
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.item_text).text = items[position].name
        Picasso.get()
            .load("https://upload.mcomputing.eu/" + items[position].photo)
            .resize(100, 100)
            .centerCrop()
            .placeholder(R.drawable.baseline_person_2_24)
            .into(holder.itemView.findViewById<ImageView>(R.id.item_image))
        holder.itemView.setOnClickListener{
            itemInterface.onItemClick(items[position])
        }
    }

    // Vracia počet položiek v zozname
    override fun getItemCount() = items.size

    fun updateItems(newItems: List<UserEntity>) {
        val diffCallback = ItemDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

}