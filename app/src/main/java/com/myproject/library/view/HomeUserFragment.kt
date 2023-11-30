package com.myproject.library.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

import com.myproject.library.R
import com.myproject.library.adapter.UsersBookAdapter
import com.myproject.library.data.model.Books


class HomeUserFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val view = inflater.inflate(R.layout.fragment_home_user, container, false)

        val users = view.findViewById<ImageView>(R.id.backMenu)
        users.setOnClickListener{
            val intent = Intent(context, ProfileActivity::class.java)
            startActivity(intent)
        }

        val db = FirebaseFirestore.getInstance()
        val bookList = mutableListOf<Books>() // Ganti Book dengan model Anda
        val collectionReference = db.collection("books") // Ganti 'books' dengan nama koleksi Anda

        collectionReference.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val book = document.toObject(Books::class.java)
                    bookList.add(book)
                }
                // Setelah Anda mendapatkan semua data, atur RecyclerView dan adapter di sini
                val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView) // Ganti dengan ID RecyclerView Anda
                val layoutManager = GridLayoutManager(context, 2)
                recyclerView.layoutManager = layoutManager
                val adapter = UsersBookAdapter(bookList, object : UsersBookAdapter.OnItemClickListener {
                    override fun onItemClick(book: Books) {
                        val intent = Intent(context, DetailActivity::class.java)
                        intent.putExtra("BOOK_ID", book)
                        intent.putExtra("PDF_URL", book.urlPdf)
//                        intent.setType("application/pdf")
//                        intent.setData(Uri.parse(book.urlPdf))
                        startActivity(intent)
                    }
                })
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Gagal", Toast.LENGTH_SHORT).show()
                // Handle kegagalan pengambilan data dari Firestore
            }


        return view
    }


    companion object {
        fun newInstance(): HomeUserFragment {
            return HomeUserFragment()
        }
    }
}