package com.example.bridechilla

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ForgotPasswordBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.bottomsheet_forgot_password,
            container,
            false
        )

        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val btnSend = view.findViewById<Button>(R.id.btnSend)

        btnSend.setOnClickListener {

            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                etEmail.error = "Email required"
                return@setOnClickListener
            }

            dismiss()

            startActivity(
                Intent(requireContext(), OtpActivity::class.java)
            )
        }

        return view
    }
}


