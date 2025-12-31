package com.example.bridechilla


import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class SignUpActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSignup = findViewById<View>(R.id.btnSignup)

        btnSignup.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (!Regex("^[a-zA-Z0-9_]{4,}$").matches(username)) {
                etUsername.error = "Invalid username"
                return@setOnClickListener
            }

            if (!phone.startsWith("+91") || phone.length < 13) {
                etPhone.error = "Invalid phone number"
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPassword.error = "Password too short"
                return@setOnClickListener
            }

            checkUsernameAndPhone(username, phone, password)
            PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phone)   // MUST be like +919876543210
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            // Auto verification (rare)
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            Toast.makeText(
                                this@SignUpActivity,
                                e.message ?: "OTP failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            val intent = Intent(this@SignUpActivity, OtpActivity::class.java)
                            intent.putExtra("vid", verificationId)
                            intent.putExtra("username", username)
                            intent.putExtra("phone", phone)
                            intent.putExtra("password", password)
                            startActivity(intent)
                        }
                    })
                    .build()
            )

        }
    }

    private fun checkUsernameAndPhone(username: String, phone: String, password: String) {

        db.collection("usernames").document(username).get()
            .addOnSuccessListener { userDoc ->
                if (userDoc.exists()) {
                    Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show()
                } else {
                    sendOtp(phone, username, password)
                }
            }
    }

    private fun sendOtp(phone: String, username: String, password: String) {

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@SignUpActivity, e.message, Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                val intent = Intent(this@SignUpActivity, OtpActivity::class.java)
                intent.putExtra("vid", verificationId)
                intent.putExtra("username", username)
                intent.putExtra("password", password)
                intent.putExtra("phone", phone)
                startActivity(intent)
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                TODO("Not yet implemented")
            }
        }

        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build()
        )
    }
}
