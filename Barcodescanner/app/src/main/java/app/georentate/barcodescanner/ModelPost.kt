package app.georentate.barcodescanner

import com.google.gson.annotations.SerializedName

data class ModelPost(
    @SerializedName("id")
    val id: Int,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("bodyText")
    val bodyText: String
)