package com.silverguard.sample

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.silverguard.cam.core.config.SilverguardCAM
import com.silverguard.cam.core.model.RequestListUrlModel
import com.silverguard.cam.core.model.RequestUrlModel
import com.silverguard.cam.core.navigator.CAMSdkNavigator

class MainActivity : AppCompatActivity(), CAMSdkNavigator {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SilverguardCAM.configure(this, "Bearer 3|14sa2lC4r0jEKLqUpBWcGowIbkt30ziyNJqWvniQ49b50f69")

        val button = findViewById<Button>(R.id.btn_open_fragment)
        button.setOnClickListener {
            val request = RequestUrlModel(
                transaction_id = generateRandomId(),
                transaction_amount = 150.0,
                transaction_time = "2025-07-11 11:10:00",
                transaction_description = "Pagamento via PIX",
                reporter_client_name = "John Doe",
                reporter_client_id = 123456789L,
                contested_participant_id = "123456",
                counterparty_client_name = "Maria dos Santos",
                counterparty_client_id = 987654321L,
                counterparty_client_key = "DEST_KEY_1",
                protocol_id = "PROT_2025_001",
                pix_auto = true,
                client_id = "CLI_456789",
                client_since = "2020-01-15",
                client_birth = "1985-03-22",
                autofraud_risk = true
            )
            SilverguardCAM.createRequest(this, request)
        }

        val buttonList = findViewById<Button>(R.id.btn_get_requests_list)
        buttonList.setOnClickListener {
            val requestList = RequestListUrlModel(
                reporter_client_id = "12345678901"
            )
            SilverguardCAM.getRequests(this, requestList)
        }
    }

    private fun generateRandomId(length: Int = 10): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    override fun onBackFromCAMSdk(origin: String?) {
        Toast.makeText(this, "Comando 'back' vindo da $origin", Toast.LENGTH_SHORT).show()
    }
}