package com.silverguard.cam.ui.webview

import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast

class WebAppBridge(
    private val context: Context,
    private val webView: WebView,
    private val onRequestMicrophonePermission: () -> Unit
) {
    @JavascriptInterface
    fun postMessage(message: String) {
        try {
            val json = org.json.JSONObject(message)
            val command = json.getString("command")

            when (command) {
                "requestMicrophonePermission" -> {
                    onRequestMicrophonePermission()
                }
                else -> {
                    Toast.makeText(context, "Comando desconhecido: $command", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao interpretar comando JS", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendActionToWeb(webView: WebView, command: String, payload: Map<String, String>? = null) {
        val payloadJson = payload?.entries
            ?.joinToString(",") { "\"${it.key}\":\"${it.value}\"" }
            ?: ""

        val jsPayload = if (payloadJson.isNotEmpty()) "{ $payloadJson }" else "{}"

        val js = """
        window.nativeBridge?.onMessage({
            command: "$command",
            payload: $jsPayload
        });
    """.trimIndent()

        webView.post {
            webView.evaluateJavascript(js, null)
        }
    }
}