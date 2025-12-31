package com.example.bridechilla

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RoleActivity : AppCompatActivity() {

    private var selectedRole = "customer"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role)

        val btnCustomer = findViewById<Button>(R.id.btnCustomer)
        val btnConsultant = findViewById<Button>(R.id.btnConsultant)
        val btnContinue = findViewById<Button>(R.id.btnContinue)

        btnCustomer.setOnClickListener {
            selectedRole = "customer"
            btnCustomer.setBackgroundResource(R.drawable.bg_button_selected)
            btnCustomer.setTextColor(Color.WHITE)

            btnConsultant.setBackgroundResource(R.drawable.bg_button_unselected)
            btnConsultant.setTextColor(Color.BLACK)
        }

        btnConsultant.setOnClickListener {
            selectedRole = "consultant"
            btnConsultant.setBackgroundResource(R.drawable.bg_button_selected)
            btnConsultant.setTextColor(Color.WHITE)

            btnCustomer.setBackgroundResource(R.drawable.bg_button_unselected)
            btnCustomer.setTextColor(Color.BLACK)
        }

        btnContinue.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("role", selectedRole)
            startActivity(intent)
        }
    }
}

