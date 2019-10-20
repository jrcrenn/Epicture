package com.example.epicture

import com.example.epicture.Gallery.SimpleGallery
import io.reactivex.Observable
import retrofit2.http.*

interface ImgurApi {

    @Headers("Authorization: Client-ID a7f7808a97975da")
    @GET("3/gallery/top/viral/{pagenumber}/week?showViral=true&mature=true&album_previews=true")
    fun getImages(@Path("pagenumber") pagenumber : Int) : Observable<SimpleGallery>
}

interface ImgurFav {
    @GET("3/account/{accountname}/favorites")
    fun getFav(@Header("Authorization")accesstoken : String, @Path("accountname") accountname : String) : Observable<SimpleGallery>
}

interface ImgurUser {
    @Headers("Authorization: Client-ID a7f7808a97975da")
    @GET("3/account/{accountname}")
    fun getUser(@Path("accountname") String : Any) : retrofit2.Call<User>
}

interface ImgurUploads {
    @GET("3/account/{accountname}/images")
    fun getUploads(@Header("Authorization")accesstoken : String, @Path("accountname") String: Any) : Observable<SimpleGallery>
}

interface ImgurSearch {
    @Headers("Authorization: Client-ID a7f7808a97975da")
    @GET("3/gallery/search/")
    fun getSearcher(@Query("q") query: String): Observable<SimpleGallery>
}