package vn.com.line.linedemo.network.model


import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("image")
    val image: List<String?>? = listOf(),
    @SerializedName("title")
    val title: String? = ""
)