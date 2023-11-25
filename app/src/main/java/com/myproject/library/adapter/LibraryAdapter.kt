package com.myproject.library.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.myproject.library.data.response.ListLibrary
import com.myproject.library.databinding.ItemLibraryBinding
import com.myproject.library.view.DetailActivity

class LibraryAdapter(private val libaryList: List<ListLibrary>) :
    RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>() {

    inner class LibraryViewHolder(private val binding: ItemLibraryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: ListLibrary) {
            with(binding) {
                nameContent.text = user.name
                imageContent.loadImage(user.photoUrl)
            }

            itemView.setOnClickListener { view ->
                val intent = Intent(view.context, DetailActivity::class.java)
//                intent.putExtra(DetailActivity.EXTRA_STORY, user)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.imageContent, "photo"),
                        Pair(binding.nameContent, "name"),
                    )

                view.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val view = ItemLibraryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LibraryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return libaryList.size
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(libaryList.get(position))
    }

    fun ImageView.loadImage(url: String) {
        Glide.with(this.context)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(this)
    }
}