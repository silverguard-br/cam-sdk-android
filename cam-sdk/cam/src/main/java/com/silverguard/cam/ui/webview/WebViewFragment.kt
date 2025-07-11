package com.silverguard.cam.ui.webview

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.silverguard.cam.databinding.FragmentWebViewBinding

class WebViewFragment : Fragment() {

    private var _binding: FragmentWebViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var bridge: WebAppBridge
    private var pendingPermissionRequest: PermissionRequest? = null

    private val requestMicrophonePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        handlePermissionResult(isGranted)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScreen()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initScreen() {
        val url = arguments?.getString("url") ?: return

        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            webViewClient = WebViewClient()
            webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest) {
                    if (request.resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                            == PackageManager.PERMISSION_GRANTED) {
                            request.grant(request.resources)
                        } else {
                            pendingPermissionRequest = request
                            requestMicrophonePermission.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    } else {
                        request.deny()
                    }
                }
            }

            bridge = WebAppBridge(requireContext(), this) {
                requestMicrophonePermission.launch(Manifest.permission.RECORD_AUDIO)
            }

            addJavascriptInterface(bridge, "AndroidBridge")

            loadUrl(url)
        }
    }

    private fun handlePermissionResult(isGranted: Boolean) {
        val request = pendingPermissionRequest
        if (request != null) {
            if (isGranted) {
                request.grant(request.resources)
            } else {
                request.deny()
            }
            pendingPermissionRequest = null
        }

        // Notifica a WebView sobre o resultado da permissão
        bridge.sendActionToWeb(
            binding.webView,
            "microphonePermission",
            mapOf("status" to if (isGranted) "authorized" else "denied")
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
