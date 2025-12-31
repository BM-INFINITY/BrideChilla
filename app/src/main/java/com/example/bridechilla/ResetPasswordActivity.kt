package com.example.bridechilla

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val etPass = findViewById<EditText>(R.id.etPassword)
        val etConfirm = findViewById<EditText>(R.id.etConfirm)

        findViewById<View>(R.id.btnDone).setOnClickListener {
            if (etPass.text.toString() != etConfirm.text.toString()) {
                etConfirm.error = "Passwords do not match"
                return@setOnClickListener
            }

            Toast.makeText(this, "Password reset successful", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
