package com.silverguard.sample

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.silverguard.cam.core.config.SilverguardCAM
import com.silverguard.cam.core.model.Bank
import com.silverguard.cam.core.model.BankCustomer
import com.silverguard.cam.core.model.RequestUrlModel
import com.silverguard.cam.core.model.Transaction

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SilverguardCAM.configure(this, "Bearer 3|14sa2lC4r0jEKLqUpBWcGowIbkt30ziyNJqWvniQ49b50f69")

        val button = findViewById<Button>(R.id.btn_open_fragment)
        button.setOnClickListener {
            val request = RequestUrlModel(
                transaction = Transaction(generateRandomId(), 100, "2025-07-11 11:10:00"),
                destination_bank = Bank("BCO ITAUBANK S.A.", "60394079", "479"),
                origin_bank_customer = BankCustomer("John Doe", "12345678901", "cpf"),
                destination_bank_customer = BankCustomer("Fulano de Tal", "12345678901234", "cnpj")
            )
            SilverguardCAM.launch(this, request)
        }
    }

    private fun generateRandomId(length: Int = 10): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}