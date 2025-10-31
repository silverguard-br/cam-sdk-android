package com.silverguard.sample

import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.silverguard.cam.core.config.SilverguardCam
import com.silverguard.cam.core.model.CamRequestListUrlModel
import com.silverguard.cam.core.model.CamRequestUrlModel
import com.silverguard.cam.core.navigator.CamSdkNavigator
import com.silverguard.cam.core.styles.CamDefaultColors
import com.silverguard.cam.core.styles.CamColorsInterface
import com.silverguard.cam.core.styles.CamDefaultFonts
import com.silverguard.cam.core.styles.CamFontStyles
import com.silverguard.cam.core.styles.CamFontsInterface

class MainActivity : AppCompatActivity(), CamSdkNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SampleKoinInitializer.init(this)

        SilverguardCam.configure(this, "3|14sa2lC4r0jEKLqUpBWcGowIbkt30ziyNJqWvniQ49b50f69")

        SilverguardCam.setColors(CamDefaultColors(CustomCamColors()))
        SilverguardCam.setFonts(CamDefaultFonts(CustomCamFonts()))

        val button = findViewById<Button>(R.id.btn_open_fragment)
        button.setOnClickListener {
            val request = CamRequestUrlModel(
                transaction_id = generateRandomId(),
                transaction_amount = 150.0,
                transaction_time = "2025-10-11 11:10:00",
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
            SilverguardCam.createRequest(this, request)
        }

        val buttonList = findViewById<Button>(R.id.btn_get_requests_list)
        buttonList.setOnClickListener {
            val requestList = CamRequestListUrlModel(
                reporter_client_id = "12345678901"
            )
            SilverguardCam.getRequests(this, requestList)
        }
    }

    private fun generateRandomId(length: Int = 10): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    override fun onBackFromCamSdk(origin: String?) {
        Toast.makeText(this, "Comando 'back' vindo da $origin", Toast.LENGTH_SHORT).show()
    }
}

class CustomCamColors : CamColorsInterface {
    override val background = "#F8F8F8".toColorInt()
    override val primary = "#FF9800".toColorInt()
    override val label = "#212121".toColorInt()
    override val buttonTitle = "#FFFFFF".toColorInt()
    override val buttonEnabled = "#FF9800".toColorInt()
    override val buttonDisabled = "#BDBDBD".toColorInt()
}

class CustomCamFonts : CamFontsInterface {
    override val button = CamFontStyles(
        size = 14f,
        style = Typeface.BOLD
    )
    override val body = CamFontStyles(
        size = 14f,
        style = Typeface.NORMAL
    )
    override val headline2 = CamFontStyles(
        size = 24f,
        style = Typeface.BOLD
    )
    override val headline3 = CamFontStyles(
        size = 20f,
        style = Typeface.BOLD
    )
}