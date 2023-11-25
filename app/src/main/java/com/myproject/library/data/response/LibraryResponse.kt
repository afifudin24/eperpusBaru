package com.myproject.library.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LibraryResponse(

    @field:SerializedName("listStory")
    val listlibrary: List<ListLibrary>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
) : Parcelable

@Parcelize
data class ListLibrary(

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("lon")
    val lon: Double,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("lat")
    val lat: Double
) : Parcelable
