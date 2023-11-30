package com.myproject.library.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.myproject.library.R
import okio.Path.Companion.toPath


class HomeAdminFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_admin, container, false)

        //kebuku
        val keBuku = view.findViewById<CardView>(R.id.keBuku)
        val kePengguna = view.findViewById<CardView>(R.id.kePengguna)
        val keSekolah = view.findViewById<CardView>(R.id.keSekolah)
        val keLogout = view.findViewById<CardView>(R.id.kelogout)

        keBuku.setOnClickListener {
            val intent = Intent(requireContext(), BooksAdmin::class.java)
            startActivity(intent)

        }
        kePengguna.setOnClickListener {
            val intent = Intent(requireContext(), PenggunaActivity::class.java)
            startActivity(intent)
        }
        keSekolah.setOnClickListener {
            val intent = Intent(requireContext(),  SekolahActivity::class.java)
            startActivity(intent)
        }
        keLogout.setOnClickListener {
            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signOut()

            // Kembali ke halaman login (misalnya LoginActivity)
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }

        fun ambilnama(){
            firebaseAuth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()

            // Mendapatkan UID pengguna yang sedang login
            val currentUserUid = firebaseAuth.currentUser?.uid

            // Mengambil data siswa dari Firestore berdasarkan UID pengguna
            if (currentUserUid != null) {
                val usersRef = firestore.collection("users")
                usersRef.document(currentUserUid).get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            // Data ditemukan, akses data nama siswa
                            val namaSiswa = documentSnapshot.getString("name")
                            val helo = requireView().findViewById<TextView>(R.id.helo)

                            helo.text = "Helo ${namaSiswa}!"
                        } else {
                            // Data tidak ditemukan
                            Log.d("Info", "Data tidak ditemukan untuk UID: $currentUserUid")
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Gagal mengambil data dari Firestore
                        Log.e("Error", "Gagal mengambil data: $exception")
                    }
            }
        }


        ambilnama()
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("school")
        collectionRef
            .limit(1) // Batasi hasil query hanya 1 dokumen
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val firstDocument = documents.documents[0]
                    val schoolName = firstDocument.getString("namaschool") // Ganti 'nama_sekolah' dengan nama field/atribut yang menyimpan nama sekolah
                    val addressname = firstDocument.getString("alamat")
                    val gambar = firstDocument.getString("image")
                    gambar!!.toPath()
                    if (schoolName != null) {
                        //ganti nama sekolah
                        val namaschool = view.findViewById<TextView>(R.id.namasekolah)
                        namaschool.text = schoolName
                        //ganti alamat sekolah
                        val alamat = view.findViewById<TextView>(R.id.alamatsekolah)
                        alamat.text = addressname

                        //ganti gambar
                        val storage = FirebaseStorage.getInstance()
                        val storageRef: StorageReference = storage.reference.child(gambar)

                        val imageViewFirebase = view.findViewById<ImageView>(R.id.logoImage) // Ganti dengan ID ImageView yang kamu gunakan

                        storageRef.downloadUrl
                            .addOnSuccessListener { uri ->
                                val downloadUrl = uri.toString()

                                Glide.with(this)
                                    .load(downloadUrl)
                                    .into(imageViewFirebase) // Ganti dengan nama ImageView yang kamu gunakan
                            }
                            .addOnFailureListener { exception ->
                                // Gagal mendapatkan URL
                                // Handle kesalahan di sini
                            }

                    } else {

                    }
                } else {
                    // Koleksi kosong
                    // Tindakan yang sesuai jika tidak ada data
                    Log.d("Firestore", "No documents found in 'school' collection")
                }
            }
            .addOnFailureListener { exception ->
                // Penanganan kesalahan jika query gagal
                Log.e("Firestore", "Error getting documents: ", exception)
            }



        return view
    }

    companion object {
    }
}