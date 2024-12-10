package com.example.utslecture.bookmark

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.utslecture.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private fun setupRecyclerView(newsRecyclerView: RecyclerView, newsList: List<Bookmark.News>) {
    // Inisialisasi adapter dengan daftar berita dan fungsi untuk menangani bookmark
    val newsAdapter = Bookmark.NewsAdapter(newsList) { news ->
        handleBookmark(news) // Fungsi callback untuk menangani perubahan bookmark
    }

    // Konfigurasi RecyclerView
    newsRecyclerView.apply {
        adapter = newsAdapter
        layoutManager = LinearLayoutManager(context) // Gunakan konteks dari RecyclerView
        setHasFixedSize(true) // Optimalkan performa jika ukuran tidak berubah
    }
}

fun handleBookmark(news: Bookmark.News) {
    // Inisialisasi Firebase
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    // Referensi ke koleksi bookmark di Firestore
    val bookmarkRef = db.collection("users").document(userId).collection("bookmarks").document(news.id)

    if (news.isBookmarked) {
        // Jika sudah di-bookmark, hapus dari Firestore
        bookmarkRef.delete()
            .addOnSuccessListener {
                news.isBookmarked = false
                // Tambahkan logika untuk mengupdate UI jika perlu
            }
            .addOnFailureListener { e ->
                // Tangani error jika gagal menghapus
                e.printStackTrace()
            }
    } else {
        // Jika belum di-bookmark, tambahkan ke Firestore
        val bookmarkData = mapOf(
            "id" to news.id,
            "title" to news.title,
            "content" to news.content,
            "category" to news.category,
            "imageUrl" to news.imageUrl
        )
        bookmarkRef.set(bookmarkData)
            .addOnSuccessListener {
                news.isBookmarked = true
                // Tambahkan logika untuk mengupdate UI jika perlu
            }
            .addOnFailureListener { e ->
                // Tangani error jika gagal menyimpan
                e.printStackTrace()
            }
    }
}


@SuppressLint("MissingInflatedId")
fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    val view = inflater.inflate(R.layout.fragment_bookmark, container, false)

    // Temukan RecyclerView dari tampilan
    val newsRecyclerView = view.findViewById<RecyclerView>(R.id.newsRecyclerView)

    // Contoh data list berita, ganti dengan data asli Anda
    val sampleNewsList = listOf(
        Bookmark.News("1", "Berita 1", "Isi berita 1", "Kategori 1", "", false),
        Bookmark.News("2", "Berita 2", "Isi berita 2", "Kategori 2", "", true)
    )

    // Panggil fungsi untuk mengatur RecyclerView
    setupRecyclerView(newsRecyclerView, sampleNewsList)

    return view
}

fun findViewById(newsRecyclerView: Int) {
    TODO("Not yet implemented")
}
