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
import com.myproject.library.data.model.Users
import com.myproject.library.view.BooksAdmin
import com.myproject.library.view.EditBookActivity
import java.net.URLDecoder

@Suppress("DEPRECATION")
class UsersAdapter(private val userList: MutableList<Users>) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.itemusers, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val users = userList[position]
        holder.bindView(users)
        // Set image using Glide or Picasso if available
        // Glide.with(holder.itemView).load(currentItem.imageUrl).into(holder.bookImageView)
    }

    override fun getItemCount() = userList.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var currentUser : Users
        private lateinit var adapter: UsersAdapter




        fun bindView(user:Users){
            currentUser = user
           val nama = itemView.findViewById<TextView>(R.id.namaPengguna)

        }
    }
}
