package com.example.epicture


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.epicture.Gallery.Image
import com.example.epicture.MainActivity.Companion.access_token
import com.example.epicture.MainActivity.Companion.userName
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_favorite.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class FavFragment : Fragment() {

    private lateinit var favoAdapter: favAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LogoutImg.visibility = View.VISIBLE
        LogoutImg.setImageResource(R.drawable.couleur_noire)

        if (access_token != null) {
            LogoutImg.visibility = View.INVISIBLE
            favoAdapter = favAdapter()
            favorite.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            favorite.layoutManager = LinearLayoutManager(activity)
            favorite.adapter = favoAdapter

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.imgur.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            val Imgurfav = retrofit.create(ImgurFav::class.java)
            Imgurfav.getFav("Bearer $access_token", userName!!)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ favoAdapter.setImages(it.data) } , {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                })
        }

    }

    inner class favAdapter : RecyclerView.Adapter<favAdapter.favViewHolder>() {

        private val imgs : MutableList<Image> = mutableListOf()

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): favViewHolder {
            return favViewHolder(layoutInflater.inflate(R.layout.herve, p0, false))
        }

        override fun getItemCount(): Int {
            return imgs.size
        }

        override fun onBindViewHolder(holder: favViewHolder, p1: Int) {
            holder.bindModel(imgs[p1])
        }

        fun setImages(content: List<Image>) {
            imgs.addAll(content)
            notifyDataSetChanged()
        }

        inner class favViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val imgTtl : TextView = itemView.findViewById(R.id.imageTitle)
            private val imgContent : ImageView = itemView.findViewById(R.id.imageContent)

            fun bindModel(image: Image) {
                imgTtl.text = image.title
                imgTtl.tag = image.id

                if (image.images?.get(0) != null) {
                    when {
                        image.images?.get(0)!!.link?.substring(startIndex = image.images?.get(0)!!.link!!.length-3)!!.compareTo("gif") == 0 -> Glide
                            .with(requireContext())
                            .asGif()
                            .load(image.images?.get(0)!!.link)
                            .apply(RequestOptions().override(300, 300))
                            .into(imgContent)
                        image.images?.get(0)!!.link?.substring(startIndex = image.images?.get(0)!!.link!!.length-3)!!.compareTo("mp4") == 0 -> Glide
                            .with(requireContext())
                            .load(image.images?.get(0)!!.link)
                            .into(imgContent)
                        else -> Glide
                            .with(requireContext())
                            .load(image.images?.get(0)!!.link)
                            .apply(RequestOptions().override(300, 300))
                            .into(imgContent)
                    }
                }
                else {
                    Glide
                        .with(requireContext())
                        .load(image.link)
                        .into(imgContent)
                }
                Log.d("bindModel()", "image link : ${image.link}")

            }
        }
    }

}
