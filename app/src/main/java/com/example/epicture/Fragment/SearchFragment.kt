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
import com.example.epicture.MainActivity.Companion.searchs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class SearchFragment : Fragment() {

    private lateinit var gllryAdapter: galleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ask = searchs

        gllryAdapter = galleryAdapter()
        gallery.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        gallery.layoutManager = LinearLayoutManager(activity)
        gallery.adapter = gllryAdapter

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.imgur.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        val searcher = retrofit.create(ImgurSearch::class.java)
        if (ask != null) {
            searcher.getSearcher(ask)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({gllryAdapter.setImages(it.data)},
                    {
                        Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    })
        }
    }

    inner class galleryAdapter : RecyclerView.Adapter<galleryAdapter.galleryViewHolder>() {

        private val imgs: MutableList<Image> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): galleryViewHolder {
            return galleryViewHolder(layoutInflater.inflate(R.layout.herve, parent, false))
        }

        override fun getItemCount(): Int {
            return imgs.size
        }

        override fun onBindViewHolder(holder: galleryViewHolder, position: Int) {
            holder.bindModel(imgs[position])
        }

        fun setImages(content: List<Image>) {
            imgs.addAll(content)
            notifyDataSetChanged()
        }

        inner class galleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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
