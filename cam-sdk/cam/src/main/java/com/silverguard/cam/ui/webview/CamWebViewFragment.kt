package com.silverguard.cam.ui.webview

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.silverguard.cam.core.navigator.CamSdkNavigator
import com.silverguard.cam.databinding.FragmentCamWebViewBinding

class CamWebViewFragment : Fragment() {

    private var _binding: FragmentCamWebViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var bridge: WebAppBridge
    private var navigator: CamSdkNavigator? = null
    private var pendingPermissionRequest: PermissionRequest? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigator = when {
            parentFragment is CamSdkNavigator -> parentFragment as CamSdkNavigator
            context is CamSdkNavigator -> context as CamSdkNavigator
            else -> null
        }
    }

    private val requestAudioPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val micGranted = permissions[Manifest.permission.RECORD_AUDIO] == true

        when {
            micGranted -> {
                pendingPermissionRequest?.grant(
                    arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE)
                )
                bridge.sendActionToWeb(
                    command = MIC_PERMISSION,
                    payload = mapOf(PERMISSION_STATUS to AUTHORIZED)
                )
            }

            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                pendingPermissionRequest?.deny()
                bridge.sendActionToWeb(
                    command = MIC_PERMISSION,
                    payload = mapOf(PERMISSION_STATUS to DENIED)
                )
            }

            else -> {
                pendingPermissionRequest?.deny()
                bridge.sendActionToWeb(
                    command = MIC_PERMISSION,
                    payload = mapOf(PERMISSION_STATUS to DENIED_PERMANENTLY)
                )
            }
        }

        pendingPermissionRequest = null
    }

    private val requestLibraryPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->

        when {
            //Permissão concedida
            isGranted -> {
                bridge.sendActionToWeb(
                    command = LIBRARY_PERMISSION,
                    payload = mapOf(PERMISSION_STATUS to AUTHORIZED)
                )
            }

            //Usuário negou, mas ainda podemos pedir novamente
            shouldShowRequestPermissionRationale(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                bridge.sendActionToWeb(
                    command = LIBRARY_PERMISSION,
                    payload = mapOf(PERMISSION_STATUS to DENIED)
                )
            }

            // Permissão negada permanentemente – usuário marcou "Não perguntar novamente"
            else -> {
                bridge.sendActionToWeb(
                    command = LIBRARY_PERMISSION,
                    payload = mapOf(PERMISSION_STATUS to DENIED_PERMANENTLY)
                )
            }
        }
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
        _binding = FragmentCamWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner
        ) {
            if (binding.camWebView.canGoBack()) {
                binding.camWebView.goBack()
            } else {
                navigator?.onBackFromCamSdk("hardware")
                requireActivity().finish()
            }
        }
        initScreen()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initScreen() {
        val url = arguments?.getString("url") ?: "file:///android_asset/bridge.html"

        binding.camWebView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            webViewClient = WebViewClient()
            webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest) {
                    val requiresMic =
                        request.resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)

                    if (!requiresMic) {
                        request.deny()
                        return
                    }

                    val micGranted = ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED

                    if (micGranted) {
                        request.grant(request.resources)
                    } else {
                        pendingPermissionRequest = request
                        requestAudioPermissions.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                    }
                }

                override fun onShowFileChooser(
                    webView: android.webkit.WebView,
                    filePathCallback: ValueCallback<Array<Uri>>,
                    fileChooserParams: FileChooserParams
                ): Boolean {
                    fileCallback?.onReceiveValue(null)

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
                            Manifest.permission.RECORD_AUDIO
                        )
                    )
                },
                requestLibraryPermission = {
                    if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.S_V2) {
                        requestLibraryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    } else {
                        bridge.sendActionToWeb(LIBRARY_PERMISSION, mapOf(PERMISSION_STATUS to AUTHORIZED))
                    }
                },
                onBackCommand = { origin ->
                    requireActivity().runOnUiThread {
                        navigator?.onBackFromCamSdk(origin)
                        requireActivity().finish()
                    }
                },
                openSettings = {
                    openAppSettings()
                }
            )

            addJavascriptInterface(bridge, BRIDGE)
            loadUrl(url)
        }
    }

    override fun onDestroyView() {
        binding.camWebView.apply {
            stopLoading()
            webChromeClient = null
            webViewClient = WebViewClient()
            removeAllViews()
            destroy()
        }
        super.onDestroyView()
        _binding = null
    }

    private fun openAppSettings() {
        val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }

    companion object PermissionStatus {
        const val AUTHORIZED = "authorized"
        const val DENIED = "denied"
        const val DENIED_PERMANENTLY = "denied_permanently"
        const val BRIDGE = "AndroidBridge"
        const val MIC_PERMISSION = "microphonePermission"
        const val LIBRARY_PERMISSION = "libraryPermission"
        const val PERMISSION_STATUS = "status"
    }
}