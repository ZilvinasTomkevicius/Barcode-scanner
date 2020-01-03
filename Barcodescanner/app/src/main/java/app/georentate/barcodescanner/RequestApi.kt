package app.georentate.barcodescanner

import retrofit2.Call
import retrofit2.http.GET

interface RequestApi {

    @GET("posts")
    fun getData(): Call<ArrayList<ModelPost>>
}