package com.myproject.library.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.myproject.library.R
import java.io.File

class PdfViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)

        val pdfView: PDFView = findViewById(R.id.pdfView)

        val pdfUrl = intent.getStringExtra("PDF_URL")
        val storage = Firebase.storage
        val storageRef = storage.reference
        val pdfRef = storageRef.child("/pdfs/" + pdfUrl ?: "sample.pdf")


        val localFile = File.createTempFile("temp", "pdf")
        Log.d("aduh", "$pdfUrl, $storageRef, $pdfRef, $localFile")

        pdfRef.getFile(localFile)
            .addOnSuccessListener {
                // File PDF berhasil diunduh
                // Tampilkan PDF menggunakan WebView
                displayPDFOnline(pdfView, localFile)
                Toast.makeText(this, "berhasil", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Gagal mengunduh file PDF
                // Handle kegagalan di sini
                Toast.makeText(this, "gagal", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayPDFOnline(pdfView: PDFView, pdfUrl: File) {
        pdfView.fromFile(pdfUrl)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .defaultPage(0)
            .load()
    }
}
