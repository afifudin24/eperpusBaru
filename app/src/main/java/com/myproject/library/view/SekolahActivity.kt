package com.myproject.library.view

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.myproject.library.R
import com.myproject.library.databinding.ActivitySekolahBinding
import okio.Path.Companion.toPath

@Suppress("DEPRECATION")
class SekolahActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySekolahBinding
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    private var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sekolah)

        val kembali = findViewById<ImageView>(R.id.kembalikemenu)
        kembali.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("school")
        collectionRef
            .limit(1) // Batasi hasil query hanya 1 dokumen
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val firstDocument = documents.documents[0]
                    val schoolName =
                        firstDocument.getString("namaschool") // Ganti 'nama_sekolah' dengan nama field/atribut yang menyimpan nama sekolah
                    val addressname = firstDocument.getString("alamat")
                    val gambar = firstDocument.getString("image")
                    id = firstDocument.getString("id").toString()

                    gambar!!.toPath()
                    if (schoolName != null) {
                        //ganti nama sekolah
                        val namaschool = findViewById<EditText>(R.id.namaSekolah)
                        namaschool.setText(schoolName)
                        //ganti alamat sekolah
                        val alamat = findViewById<TextView>(R.id.alamat)
                        alamat.text = addressname


                        //ganti gambar
                        val storage = FirebaseStorage.getInstance()
                        val storageRef: StorageReference = storage.reference.child(gambar)

                        val imageViewFirebase =
                            findViewById<ImageView>(R.id.imagelogo) // Ganti dengan ID ImageView yang kamu gunakan

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

        val uploadFrame = findViewById<FrameLayout>(R.id.frame)
        uploadFrame.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        fun editFoto(docId: String, imageUrl : String) {
            val updateData = hashMapOf("image" to imageUrl)
            val collectionRef = FirebaseFirestore.getInstance().collection("school")
            collectionRef.document(docId)
                .update(updateData as Map<String, Any>)
                .addOnSuccessListener {
                    val message = "Berhasil Update Logo"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(applicationContext, message, duration)
                    toast.show()
                }
                .addOnFailureListener { exception ->
                    val message = "Gagal Update Logo"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(applicationContext, message, duration)
                    toast.show()
                }

        }

        fun editData(docId: String, namasekolah : String, alamat : String){
            val updateData = hashMapOf(

                "namaschool" to namasekolah,
                "alamat" to alamat

                )


            val collectionRef = FirebaseFirestore.getInstance().collection("school")
            collectionRef.document(docId)
                .update(updateData as Map<String, Any>)
                .addOnSuccessListener {
                    val message = "Berhasil Update Data"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(applicationContext, message, duration)
                    toast.show()
                }
                .addOnFailureListener { exception ->
                    val message = "Gagal Update Data"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(applicationContext, message, duration)
                    toast.show()
                }
        }

        val editData = findViewById<Button>(R.id.editData)
        editData.setOnClickListener {
            val namaschool = findViewById<EditText>(R.id.namaSekolah)
            var nms = namaschool.text

            //ganti alamat sekolah
            val alamat = findViewById<TextView>(R.id.alamat)
            var alamt = alamat.text
            editData(id, nms.toString(), alamt.toString())
        }

        fun generateRandomText(length: Int): String {
            val charPool: List<Char> =
                ('a'..'z') + ('A'..'Z') + ('0'..'9') // Karakter yang akan digunakan
            return (1..length)
                .map { kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
        }

        val editfoto = findViewById<Button>(R.id.editfoto)
        editfoto.setOnClickListener {
            val random = generateRandomText(10)
            var namagambar = random + ".jpg"
            selectedImageUri?.let { imageUri ->
                uploadImageToFirebase(imageUri, namagambar)
            }
            editFoto(id, namagambar)
        }


    }

    private fun uploadImageToFirebase(imageUri: Uri, namaImage: String) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        val fileRef = storageRef.child(namaImage) // Men

        fileRef.putFile(imageUri)
            .addOnSuccessListener {

                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    // Gunakan URL unduhan untuk keperluan yang diinginkan
                    Log.d("Download URL", downloadUrl)
                }
            }
            .addOnFailureListener {

            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageView: ImageView = findViewById(R.id.imagelogo)
//        val textDragDrop: TextView = findViewById(R.id.teks)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            imageView.setImageURI(imageUri)
            selectedImageUri = imageUri




        }
    }

}