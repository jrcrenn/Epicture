package com.example.epicture


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.epicture.MainActivity.Companion.access_token
import com.example.epicture.MainActivity.Companion.userName
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mWebView = view.findViewById(R.id.webview) as WebView

        val webSettings = mWebView.settings
        webSettings.javaScriptEnabled = true

        mWebView.webViewClient = WebViewClient()
        mWebView.settings.userAgentString = "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile Safari/537.36"
        mWebView.settings.loadWithOverviewMode = true
        mWebView.settings.allowContentAccess = true

        webview.webViewClient = AuthWebView()
        webview.loadUrl("https://api.imgur.com/oauth2/authorize?client_id=a7f7808a97975da&response_type=token")

        if (access_token != null) {
            Toast.makeText(context, access_token, Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("OverridingDeprecatedMember")
    class AuthWebView : WebViewClient() {

        inner class CallbackData {
            var access_token : String? = null
            var expiresIn: Long = 0
            var tokenType : String? = null
            var refresh_token : String? = null
            var accountUsername: String? = null
            var accountId: Long = 0
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            view?.loadUrl(request?.url.toString())
            return true
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url)
            return true
        }
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)

            Log.d("callbackURL", url)

            if (url.take(43).compareTo("https://www.mempicture.com/oauth2/callback", true) == 1) {
                Log.d("ComparedFind:", "yes")
                val ret = parseCallback(url)
                access_token = ret.access_token
                userName = ret.accountUsername
                view.loadUrl("")
            }
        }

        private fun parseCallback(uri: String): CallbackData {
            val res = CallbackData()
            val parameters = uri.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].split("&".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()

            res.access_token = parameters[0].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            res.expiresIn =
                    java.lang.Long.valueOf(parameters[1].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
            res.tokenType = parameters[2].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            res.refresh_token = parameters[3].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            res.accountUsername = parameters[4].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            res.accountId =
                    java.lang.Long.valueOf(parameters[5].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])

            return res
        }

    }
}
