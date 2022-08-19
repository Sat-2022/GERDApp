package com.example.gerdapp.ui.questionnaire

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gerdapp.MainActivity
import com.example.gerdapp.R
import com.example.gerdapp.databinding.FragmentQuestionnaireBinding

class QuestionnaireFragment: Fragment() {

    private var _binding: FragmentQuestionnaireBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationViewVisibility = View.GONE

    private lateinit var webView: WebView
    private lateinit var webViewClient: WebViewClient
    private lateinit var webSettings: WebSettings
    private lateinit var cookieManager: CookieManager

    private fun setBottomNavigationVisibility() {
        val mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)
        mainActivity.setActionBarExpanded(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setBottomNavigationVisibility()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentQuestionnaireBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = binding.webview
        webView.loadUrl(getString(R.string.questionnaire_url))
        // bind to the JavaScript that runs the webView
        webView.addJavascriptInterface(WebInterface(context), "android")

        // Show website within the app
        webViewClient = WebViewClient()
        webView.webViewClient = webViewClient

        webSettings = webView.settings
        webSettings.setSupportZoom(true) // allow zoom in / out
        webSettings.javaScriptEnabled = true // enable JavaScript

        webView.webChromeClient = WebChromeClient()

        cookieManager = CookieManager.getInstance()
        cookieManager.getCookie(getString(R.string.questionnaire_url))
        cookieManager.removeAllCookies(null)
        cookieManager.flush()
    }

    class WebInterface(private val context: Context?) {
        @JavascriptInterface
        fun showToast(toast: String) {
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
        }
    }
}