package com.myproject.library.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.myproject.library.R
import com.myproject.library.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val firestore = FirebaseFirestore.getInstance()
            val usersCollection = firestore.collection("users")


            // Ambil dokumen pengguna berdasarkan UID
            val userDoc = usersCollection.document(uid!!)
            userDoc.get().addOnSuccessListener { documentSnapshot ->

                if (documentSnapshot.exists()) {
                    // Dokumen pengguna ditemukan
                    val userData = documentSnapshot.data
                    val isadmin = userData?.get("is_admin") as? Boolean

                    if (isadmin == true) {
                        val fragmentManager = supportFragmentManager
                        val transaction = fragmentManager.beginTransaction()
                        val fragment = HomeAdminFragment()
                        transaction.replace(R.id.fragment, fragment) // Ganti fragment di dalam FragmentContainerView
                        transaction.addToBackStack(null) // Tidak perlu menambahkan transaksi ke back stack
                        transaction.commit()

                    }else {
                        val fragmentManager = supportFragmentManager
                        val transaction = fragmentManager.beginTransaction()
                        val fragment = HomeUserFragment()
                        transaction.replace(R.id.fragment, fragment)
                        transaction.addToBackStack(null) // Jika ingin menambahkan transaksi ke back stack
                        transaction.commit()


                    }
                }

            }
        }
    }
}
