package com.silverguard.cam.ui.webview

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
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

    private val requestAudioPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val micGranted = permissions[Manifest.permission.RECORD_AUDIO] == true
        val modifyGranted = permissions[Manifest.permission.MODIFY_AUDIO_SETTINGS] == true
        val granted = micGranted && modifyGranted
        bridge.sendActionToWeb("microphonePermission", mapOf("status" to if (granted) "authorized" else "denied"))
    }

    private val requestLibraryPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        bridge.sendActionToWeb("libraryPermission", mapOf("status" to if (isGranted) "authorized" else "denied"))
    }

    private var fileCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooserLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val resultUri = WebChromeClient.FileChooserParams.parseResult(result.resultCode, data)
            fileCallback?.onReceiveValue(resultUri)
        } else {
            fileCallback?.onReceiveValue(null)
        }
        fileCallback = null
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
        val url = arguments?.getString("url") ?: "file:///android_asset/bridge.html"

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
                    Log.d("WebView", "Permission requested: ${request.resources.joinToString()}")

                    val requiresMic = request.resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)
                    if (requiresMic) {
                        val micGranted = ContextCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                        val modifyGranted = ContextCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (micGranted && modifyGranted) {
                            request.grant(request.resources)
                        } else {
                            requestAudioPermissions.launch(
                                arrayOf(
                                    Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.MODIFY_AUDIO_SETTINGS
                                )
                            )
                        }
                    } else {
                        request.deny()
                    }
                }

                override fun onShowFileChooser(
                    webView: android.webkit.WebView,
                    filePathCallback: ValueCallback<Array<Uri>>,
                    fileChooserParams: FileChooserParams
                ): Boolean {
                    fileCallback?.onReceiveValue(null) // clear previous

                    fileCallback = filePathCallback
                    val intent = fileChooserParams.createIntent()
                    fileChooserLauncher.launch(intent)
                    return true
                }
            }

            bridge = WebAppBridge(
                requireContext(),
                this,
                requestAudioPermissions = {
                    requestAudioPermissions.launch(
                        arrayOf(
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.MODIFY_AUDIO_SETTINGS
                        )
                    )
                },
                requestLibraryPermission = {
                    if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.S_V2) {
                        requestLibraryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    } else {
                        bridge.sendActionToWeb("libraryPermission", mapOf("status" to "authorized"))
                    }
                }
            )

            addJavascriptInterface(bridge, "NativeInterface")

            loadUrl("file:///android_asset/bridge.html")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}