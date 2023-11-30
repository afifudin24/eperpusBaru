package com.myproject.library.data.model

import java.io.Serializable

data class Books (
    val id: String = "",
    val judulbuku: String = "",
    val penulis: String = "",
    val kategori : String = "",
    val urlImage: String = "",
    val urlPdf : String = " "
    ) : Serializable