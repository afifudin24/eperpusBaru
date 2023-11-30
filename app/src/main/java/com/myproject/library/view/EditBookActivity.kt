package com.myproject.library.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.opengl.Visibility
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.myproject.library.R
import com.myproject.library.databinding.ActivityEditBookBinding

@Suppress("DEPRECATION")
class EditBookActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityEditBookBinding
    private val PICK_IMAGE_REQUEST = 1
    private val PICK_PDF_REQUEST = 2
    private var selectedImageUri: Uri? = null
    private var selectedPdfUri: Uri? = null
    private var selectedSpinner:String = ""
    private val restartDelay: Long = 500 // Jeda waktu sebelum restart (dalam milidetik)
    private val handler = Handler()
    private var kocak = 0
    private var kacok = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_book)

        //kembali
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            onBackPressed()
        }

        val bookId = intent.getStringExtra("book_id")
        val bookTitle = intent.getStringExtra("book_title")
        val bookAuthor = intent.getStringExtra("book_author")
        val bookCategory = intent.getStringExtra("book_category")
        val bookImageUrl = intent.getStringExtra("book_image_url")
        val bookPdfUrl = intent.getStringExtra("book_pdf_url")


        var idbuku = findViewById<EditText>(R.id.idbuku)
        var judul = findViewById<TextInputEditText>(R.id.namabuku_txt)
        var penulis = findViewById<TextInputEditText>(R.id.penulis_txt)
        var imageView: ImageView = findViewById(R.id.uploadGambar)
        var pdfFile : TextView = findViewById(R.id.pdfFile)

        pdfFile.visibility = View.VISIBLE
        idbuku.setText(bookId)
        pdfFile.text = bookPdfUrl
        judul.setText(bookTitle)
        penulis.setText(bookAuthor)
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference.child("images/"+bookImageUrl)
        val pdfRef : StorageReference = storage.reference.child("pdfs/"+bookPdfUrl)


        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()

                Glide.with(this)
                    .load(downloadUrl)
                    .into(imageView) // Ganti dengan nama ImageView yang kamu gunakan
            }
            .addOnFailureListener { exception ->
                // Gagal mendapatkan URL
                // Handle kesalahan di sini
            }
//
//        pdfRef.getFile(localFile)
//            .addOnSuccessListener { taskSnapshot ->
//                // File PDF berhasil diunduh ke localFile
//                // Sekarang Anda bisa menggunakan file tersebut, misalnya menampilkannya atau menyimpannya ke penyimpanan lokal
//
//                // Contoh: Menampilkan PDF menggunakan intent
//                val pdfIntent = Intent(Intent.ACTION_VIEW)
//                pdfIntent.setDataAndType(Uri.fromFile(localFile), "application/pdf")
//                pdfIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
//                startActivity(pdfIntent)
//            }
//            .addOnFailureListener { exception ->
//                // Gagal mengunduh file PDF
//                // Handle kesalahan di sini
//            }

        val data = arrayOf("Kategori","Pendidikan", "Sosial", "Agama", "Politik")

        val spinner: Spinner = findViewById(R.id.spinner) // Ganti dengan ID Spinner Anda
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val kataKunci = bookCategory

// Temukan posisi kata kunci dalam array data
        var posisi = -1
        for (i in data.indices) {
            if (data[i] == kataKunci) {
                posisi = i
                break
            }
        }
        if (posisi != -1) {
            spinner.setSelection(posisi)
            selectedSpinner = kataKunci.toString()
        }



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

//        fun restart(){
//            judul.text!!.clear()
//            penulis.text!!.clear()
//            imageView.setImageResource(R.drawable.baseline_image_24)
//            pdfFile.text = " "
//            pdfFile.visibility = View.GONE
//            spinner.setSelection(0)
//
//        }
@SuppressLint("SuspiciousIndentation")
fun updateBuku(docId : String, namabuku : String, penulis : String, kategori: String, urlImage: String, urlPdf: String){
            val db = FirebaseFirestore.getInstance()
            val data = hashMapOf<String, Any>(
                "judulbuku" to namabuku,
                "penulis" to penulis,
                "kategori" to kategori

            )

            if (urlImage == "kosong" && urlPdf == "kosong") {
                // Tidak perlu menambahkan data tambahan
            } else if (urlImage == "kosong") {
                data["urlPdf"] = urlPdf
            } else if (urlPdf == "kosong") {
                data["urlImage"] = urlImage
            } else {
                data["urlImage"] = urlImage
                data["urlPdf"] = urlPdf
            }

            val docRef = db.collection("books").document(docId)
            docRef.update(data)
                .addOnSuccessListener {
                    val message = "Berhasil Update Data"
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
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Konfirmasi Perubahan")
            builder.setMessage("Anda yakin ingin menyimpan perubahan ini?")

            builder.setPositiveButton("Ya") { _, _ ->
                // Aksi perbarui data dilakukan di sini
                val random = generateRandomText(10)
                var namagambar = random + ".jpg"
                var namapdf = random + ".pdf"
                val judulbuku = findViewById<TextInputEditText>(R.id.namabuku_txt).text.toString()
                val penulisbuku = findViewById<TextInputEditText>(R.id.penulis_txt).text.toString()
                val id = findViewById<EditText>(R.id.idbuku).text.toString()

                if(kocak == 1 && kacok == 1){
                    selectedImageUri?.let { imageUri ->
                        uploadImageToFirebase(imageUri, namagambar)
                    }
                    selectedPdfUri?.let{
                            pdfUri ->  uploadPDFToFirebase(pdfUri, namapdf)
                    }
                    updateBuku(id, judulbuku, penulisbuku,selectedSpinner, namagambar, namapdf)
                }else if(kocak == 1 && kacok == 0){
                    selectedPdfUri?.let{
                            pdfUri ->  uploadPDFToFirebase(pdfUri, namapdf)
                    }
                    namagambar = "kosong"
                    updateBuku(id, judulbuku, penulisbuku, selectedSpinner, namagambar, namapdf)
                }else if(kacok == 1 && kocak == 0){
                    selectedImageUri?.let { imageUri ->
                        uploadImageToFirebase(imageUri, namagambar)

                    }
                    namapdf = "kosong"
                    updateBuku(id, judulbuku, penulisbuku, selectedSpinner, namagambar, namapdf)
                }else{
                    namagambar = "kosong"
                    namapdf = "kosong"
                    updateBuku(id, judulbuku, penulisbuku, selectedSpinner, namagambar, namapdf)
                }
//            updateBuku(id, judulbuku, penulisbuku, selectedSpinner, namagambar, namapdf)

            }

            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss() // Batalkan aksi perubahan
            }

            val dialog = builder.create()
            dialog.show()


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
            kacok = 1

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
            kocak = 1

        }
    }
    private fun uploadImageToFirebase(imageUri: Uri, namaImage: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images") // Ganti dengan folder yang kamu inginkan di Firebase Storage
        val fileRef = imagesRef.child("${namaImage}") // Ganti dengan nama file yang diinginkan

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