import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.myproject.library.R
import com.myproject.library.data.model.Books
import com.myproject.library.view.BooksAdmin
import com.myproject.library.view.EditBookActivity

@Suppress("DEPRECATION")
class BookAdapter(private val bookList: MutableList<Books>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.itemcard, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val books = bookList[position]
        holder.bindView(books)
        // Set image using Glide or Picasso if available
        // Glide.with(holder.itemView).load(currentItem.imageUrl).into(holder.bookImageView)
    }

    override fun getItemCount() = bookList.size

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var currentBook : Books
        private lateinit var adapter: BookAdapter
        val titleTextView: TextView = itemView.findViewById(R.id.judultxt)
        val authorTextView: TextView = itemView.findViewById(R.id.penulistxt)
        val kategoriView: TextView = itemView.findViewById(R.id.kategoritxt)
        val imageurl: ImageView = itemView.findViewById(R.id.gambarbuku)
        val hapus : ImageView = itemView.findViewById(R.id.hapus)
        val edit : ImageView = itemView.findViewById(R.id.edit)


        init {
            hapus.setOnClickListener {
                val builder = AlertDialog.Builder(itemView.context)
                builder.setTitle("Konfirmasi Hapus")
                builder.setMessage("Anda yakin ingin menghapus data ini?")

                builder.setPositiveButton("Ya") { _, _ ->
                    currentBook?.let { book ->
                        // Aksi hapus data dilakukan di sini
                        val db = FirebaseFirestore.getInstance()
                        val collection = db.collection("books")

                        // Hapus item berdasarkan ID
                        collection.document(book.id).delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    itemView.context,
                                    "Data berhasil dihapus",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(itemView.context, BooksAdmin::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                itemView.context.startActivity(intent)

                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(
                                    itemView.context,
                                    "Gagal menghapus data: $exception",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }

                builder.setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss() // Batalkan aksi hapus
                }

                val dialog = builder.create()
                dialog.show()
            }

            edit.setOnClickListener{
                currentBook?.let{book ->
                    val intent = Intent(itemView.context, EditBookActivity::class.java)
                    intent.putExtra("book_id", book.id)
                    intent.putExtra("book_title", book.judulbuku)
                    intent.putExtra("book_author", book.penulis)
                    intent.putExtra("book_category", book.kategori)
                    intent.putExtra("book_image_url", book.urlImage)
                    intent.putExtra("book_pdf_url", book.urlPdf)
                    itemView.context.startActivity(intent)

                }
            }
        }

        fun bindView(book:Books){
            currentBook = book
                titleTextView.text = book.judulbuku
                authorTextView.text = book.penulis
                kategoriView.text = book.kategori
            val gambar = book.urlImage
            val storage = FirebaseStorage.getInstance()
            val storageRef: StorageReference = storage.reference.child("images/"+gambar)
            storageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()

                    Glide.with(itemView.context)
                        .load(downloadUrl)
                        .into(imageurl) // Ganti dengan nama ImageView yang kamu gunakan
                }
                .addOnFailureListener { exception ->
                    // Gagal mendapatkan URL
                    // Handle kesalahan di sini
                }

        }
    }
}
