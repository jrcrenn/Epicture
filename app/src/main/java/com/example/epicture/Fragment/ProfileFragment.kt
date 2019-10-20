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
import kotlinx.android.synthetic.main.fragment_profile.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ProfileFragment : Fragment() {

    private lateinit var gllryAdapter: galleryupAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogoutImg.visibility = View.VISIBLE
        UserName.visibility = View.INVISIBLE
        userIcon.visibility = View.INVISIBLE
        useReput.visibility = View.INVISIBLE
        uploadText.visibility = View.INVISIBLE

        LogoutImg.setImageResource(R.drawable.couleur_noire)

        if (access_token != null) {
            LogoutImg.visibility = View.INVISIBLE

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.imgur.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            val retrouser = retrofit.create(ImgurUser::class.java)
            val userdata = retrouser.getUser(userName!!)
            userdata.enqueue(object : retrofit2.Callback<User> {
                override fun onFailure(call: Call<User>, t: Throwable) {
                    TODO("not implemented")
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    val retcall = response.body()

                    if (retcall != null) {
                        val avatar : ImageView = view.findViewById(R.id.userIcon)
                        val username : TextView = view.findViewById(R.id.UserName)
                        val reputation : TextView = view.findViewById(R.id.useReput)

                        username.text = retcall.data.url
                        reputation.text = retcall.data.reputation_name

                        Glide
                            .with(requireContext())
                            .load(retcall.data.avatar)
                            .into(avatar)

                        UserName.visibility = View.VISIBLE
                        userIcon.visibility = View.VISIBLE
                        useReput.visibility = View.VISIBLE
                        uploadText.visibility = View.VISIBLE
                    }
                }

            })
            gllryAdapter = galleryupAdapter()
            galleryup.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            galleryup.layoutManager = LinearLayoutManager(activity)
            galleryup.adapter = gllryAdapter

            val retrofit2 : Retrofit = Retrofit.Builder()
                .baseUrl("https://api.imgur.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            val userup = retrofit2.create(ImgurUploads::class.java)
            userup.getUploads("Bearer $access_token", userName!!)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ gllryAdapter.setImages(it.data)},
                    {
                        Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    })
        }
    }

    inner class galleryupAdapter : RecyclerView.Adapter<galleryupAdapter.galleryViewHolder>() {

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
