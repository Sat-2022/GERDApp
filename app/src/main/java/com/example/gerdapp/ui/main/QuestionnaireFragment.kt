package com.example.gerdapp.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gerdapp.R
import com.example.gerdapp.databinding.FragmentQuestionnaireBinding

class QuestionnaireFragment: Fragment() {

    private var _binding: FragmentQuestionnaireBinding? = null
    private val binding get() = _binding!!

    private lateinit var webView: WebView
    private lateinit var webViewClient: WebViewClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        webView= binding.webview
        webView.loadUrl("https://ithelp.ithome.com.tw/articles/10239717")
        // bind to the JavaScript that runs the webView
        webView.addJavascriptInterface(WebInterface(context), "android")

        // Show website within the app
        webViewClient = WebViewClient()
        webView.webViewClient = webViewClient
    }

    class WebInterface(private val context: Context?) {
        @JavascriptInterface
        fun showToast(toast: String) {
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
        }
    }
}