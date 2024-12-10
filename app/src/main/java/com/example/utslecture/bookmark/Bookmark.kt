package com.example.utslecture.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.utslecture.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Bookmark : Fragment() {
    private lateinit var searchView: SearchView
    private lateinit var politicsCard: CardView
    private lateinit var financeCard: CardView
    private lateinit var educationCard: CardView
    private lateinit var healthCard: CardView
    private lateinit var category: String
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var newsList: List<News>

    data class News(
        val id: String,
        val title: String,
        val content: String,
        val category: String,
        val imageUrl: String,
        var isBookmarked: Boolean = false
    )

    class NewsAdapter(
        private val newsList: List<News>,
        private val onBookmarkClicked: (News) -> Unit
    ) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

        inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val titleTextView = itemView.findViewById<TextView>(R.id.titleTextView)
            private val contentTextView = itemView.findViewById<TextView>(R.id.contentTextView)
            private val bookmarkImageView = itemView.findViewById<ImageView>(R.id.bookmarkImageView)

            fun bind(news: News) {
                titleTextView.text = news.title
                contentTextView.text = news.content

                val bookmarkIcon = if (news.isBookmarked) {
                    R.drawable.bookmark_added
                } else {
                    R.drawable.bookmark_button
                }
                bookmarkImageView.setImageResource(bookmarkIcon)

                bookmarkImageView.setOnClickListener {
                    news.isBookmarked = !news.isBookmarked
                    onBookmarkClicked(news)
                    notifyItemChanged(adapterPosition)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.blog_item, parent, false)
            return NewsViewHolder(view)
        }

        override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
            holder.bind(newsList[position])
        }

        override fun getItemCount(): Int = newsList.size
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookmark, container, false)
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.queryHint = "Search"

        view.findViewById<CardView>(R.id.politics_card).setOnClickListener {
            navigateToCategory("Politics")
        }

        view.findViewById<CardView>(R.id.finance_card).setOnClickListener {
            navigateToCategory("Finance")
        }

        view.findViewById<CardView>(R.id.education_card).setOnClickListener {
            navigateToCategory("Education")
        }

        view.findViewById<CardView>(R.id.health_card).setOnClickListener {
            navigateToCategory("Health")
        }

        return view
    }

    private fun navigateToCategory(category: String) {
        val bundle = Bundle().apply {
            putString("category", category)
        }
        findNavController().navigate(R.id.Category, bundle)
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(newsList) { news ->
            handleBookmark(news)
        }
        newsRecyclerView.adapter = newsAdapter
    }

    private fun handleBookmark(news: News) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val bookmarkRef = db.collection("users").document(userId).collection("bookmarks").document(news.id)

        if (news.isBookmarked) {
            bookmarkRef.set(news)
        } else {
            bookmarkRef.delete()
        }
    }
}
