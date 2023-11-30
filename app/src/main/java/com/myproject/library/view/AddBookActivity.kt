package com.myproject.library.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.storage.FirebaseStorage
import com.myproject.library.R
import com.google.firebase.firestore.FirebaseFirestore



@Suppress("DEPRECATION")
class AddBookActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private val PICK_PDF_REQUEST = 2
    private var selectedImageUri: Uri? = null
    private var selectedPdfUri: Uri? = null
    private var selectedSpinner:String = ""
    private val restartDelay: Long = 500 // Jeda waktu sebelum restart (dalam milidetik)
    private val handler = Handler()
//    val runnable = Runnable {
//        // Panggil fungsi yang ingin dijalankan setelah jeda di sini
//        resetSpinnerToInitialState()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
        // db colelction

        val data = arrayOf("Kategori","Pendidikan", "Sosial", "Agama", "Politik")

        val spinner: Spinner = findViewById(R.id.spinner) // Ganti dengan ID Spinner Anda
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedSpinner = parent.getItemAtPosition(position).toString()
                // Lakukan sesuatu dengan nilai yang dipilih dari Spinner (selectedValue)
                Log.d("SpinnerValue", "Nilai yang dipilih: $selectedSpinner")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Tidak ada item yang dipilih
            }
        }

        //fungsi acak
        fun generateRandomText(length: Int): String {
            val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') // Karakter yang akan digunakan
            return (1..length)
                .map { kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
        }
        val judul = findViewById<TextInputEditText>(R.id.namabuku_txt)
        val penulis = findViewById<TextInputEditText>(R.id.penulis_txt)
        val textpdf = findViewById<TextView>(R.id.pdfFile)
        val imageView: ImageView = findViewById(R.id.uploadGambar)
        fun restart(){
            judul.text!!.clear()
            penulis.text!!.clear()
            imageView.setImageResource(R.drawable.baseline_image_24)
            textpdf.text = " "
            textpdf.visibility = View.GONE
            spinner.setSelection(0)

        }
        fun addBuku(namabuku : String, penulis : String, kategori: String, urlImage: String, urlPdf: String){
            val db = FirebaseFirestore.getInstance()
            val docId = generateRandomText(5)
            val data = hashMapOf(
                "id" to docId,
                "judulbuku" to namabuku,
                "penulis" to penulis,
                "kategori" to kategori,
                "urlImage" to urlImage,
                "urlPdf" to urlPdf
                // ... tambahkan data lainnya sesuai kebutuhan
            )
            val docRef = db.collection("books").document(docId)
            docRef.set(data)
                .addOnSuccessListener {
                    val message = "Berhasil Menambahkan Data"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(applicationContext, message, duration)
                    toast.show()
                    handler.postDelayed({


                        val intent = Intent(this, BooksAdmin::class.java)
                        startActivity(intent)
                        finish()
                    }, restartDelay)
                }
                .addOnFailureListener { e ->
                    val message = "Gagal"
                    val duration = Toast.LENGTH_SHORT

                    val toast = Toast.makeText(applicationContext, message, duration)
                    toast.show()
                }
        }

        //pick image
        val uploadFrame: FrameLayout = findViewById(R.id.frameupload)

        val uploadpdf : Button = findViewById(R.id.btnChooseFile)

        uploadpdf.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(Intent.createChooser(intent, "Pilih File PDF"), PICK_PDF_REQUEST)
        }

        uploadFrame.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
        fun restartActivity(activity: Activity) {
            val intent = Intent(activity, activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
            activity.finish()
        }


        val tambah = findViewById<Button>(R.id.tambahdata)
        tambah.setOnClickListener {
            val random = generateRandomText(10)
            val namagambar = random + ".jpg"
            val namapdf = random + ".pdf"
            val judulbuku = findViewById<TextInputEditText>(R.id.namabuku_txt).text.toString()
            val penulisbuku = findViewById<TextInputEditText>(R.id.penulis_txt).text.toString()


            selectedImageUri?.let { imageUri ->
                uploadImageToFirebase(imageUri, namagambar)
            }
            selectedPdfUri?.let{
                pdfUri ->  uploadPDFToFirebase(pdfUri, namapdf)
            }
            addBuku(judulbuku, penulisbuku,selectedSpinner, namagambar, namapdf)


        }



    }

    @SuppressLint("Range")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageView: ImageView = findViewById(R.id.uploadGambar)
//        val textDragDrop: TextView = findViewById(R.id.teks)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            imageView.setImageURI(imageUri)
            selectedImageUri = imageUri

        }
        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val pdfUri: Uri = data.data!!
            val cursor: Cursor? = contentResolver.query(pdfUri, null, null, null, null)
            var displayName = ""
            cursor?.use {
                it.moveToFirst()
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
            val textpdf = findViewById<TextView>(R.id.pdfFile)
            textpdf.text = displayName
            textpdf.visibility = View.VISIBLE
            selectedPdfUri = pdfUri

        }
    }
    private fun uploadImageToFirebase(imageUri: Uri, namaImage: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images") // Ganti dengan folder yang kamu inginkan di Firebase Storage
        val fileRef = imagesRef.child("${namaImage}") // Ganti dengan nama file yang diinginkan

        fileRef.putFile(imageUri)
            .addOnSuccessListener {
                // Gambar berhasil diunggah
                // Dapatkan URL unduhan gambar yang diunggah
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    // Gunakan URL unduhan untuk keperluan yang diinginkan
                    Log.d("Download URL", downloadUrl)
                }
            }
            .addOnFailureListener {
                // Gagal mengunggah gambar
                // Handle kesalahan di sini
            }
    }
    private fun uploadPDFToFirebase(pdfUri: Uri, pdfName: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val pdfsRef = storageRef.child("pdfs") // Ganti dengan folder yang kamu inginkan di Firebase Storage
        val fileRef = pdfsRef.child(pdfName) // Ganti dengan nama file yang diinginkan

        fileRef.putFile(pdfUri)
            .addOnSuccessListener {
                // PDF berhasil diunggah
                // Dapatkan URL unduhan PDF yang diunggah
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    // Gunakan URL unduhan untuk keperluan yang diinginkan
                    Log.d("Download URL PDF", downloadUrl)
                }
            }
            .addOnFailureListener {
                // Gagal mengunggah PDF
                // Handle kesalahan di sini
            }
    }

}