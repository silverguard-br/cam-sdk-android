package com.silverguard.cam.ui.webview

import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import org.json.JSONObject

class WebAppBridge(
    private val context: Context,
    private val webView: WebView,
    private val requestAudioPermissions: () -> Unit,
    private val requestLibraryPermission: () -> Unit,
    private val onBackCommand: () -> Unit
) {
    @JavascriptInterface
    fun postMessage(message: String) {
        try {
            val json = JSONObject(message)
            val command = json.getString("command")

            when (command) {
                "requestMicrophonePermission" -> {
                    requestAudioPermissions()
                }
                "askForLibrary" -> {
                    requestLibraryPermission()
                }
                "back" -> {
                    onBackCommand()
                }
                else -> {
                    Toast.makeText(context, "Comando desconhecido: $command", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao interpretar comando JS", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendActionToWeb(command: String, payload: Map<String, String>? = null) {
        val js = """
            window.nativeBridge.onMessage(${JSONObject(mapOf("command" to command, "payload" to payload))});
        """.trimIndent()
        webView.post {
            webView.evaluateJavascript(js, null)
        }
    }
}