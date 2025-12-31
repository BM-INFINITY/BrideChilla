package com.example.bridechilla

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ INITIALIZE FIRST
        auth = FirebaseAuth.getInstance()


        setContentView(R.layout.activity_login)
        val txtSignup = findViewById<TextView>(R.id.txtSignup)

        val fullText = "Don't have an account? Let's Sign Up"
        val spannable = SpannableString(fullText)

        // Make only "Sign Up" bold
        val start = fullText.indexOf("Sign Up")
        val end = start + "Sign Up".length

        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Make only "Sign Up" clickable
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false   // no underline
                    ds.color = Color.parseColor("#59C5C3") // same teal as Figma
                }
            },
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        txtSignup.text = spannable
        txtSignup.movementMethod = LinkMovementMethod.getInstance()


        val etEmail = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<View>(R.id.btnLogin)
        val switchRemember = findViewById<SwitchCompat>(R.id.switchRemember)
        val txtForgot = findViewById<TextView>(R.id.txtForgot)

        // Remember Me
        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        etEmail.setText(prefs.getString("email", ""))
        switchRemember.isChecked = prefs.getBoolean("remember", false)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty()) {
                etEmail.error = "Email required"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Password required"
                return@setOnClickListener
            }

            btnLogin.isEnabled = false

            btnLogin.setOnClickListener {

                val input = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                val collection = if (input.startsWith("+")) "phones" else "usernames"

                FirebaseFirestore.getInstance()
                    .collection(collection)
                    .document(input)
                    .get()
                    .addOnSuccessListener { doc ->

                        if (!doc.exists()) {
                            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }

                        val uid = doc.getString("uid")!!
                        val fakeEmail = "$input@bridechilla.app"

                        auth.signInWithEmailAndPassword(fakeEmail, password)
                            .addOnSuccessListener {
                                startActivity(Intent(this, RoleActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
                            }
                    }
            }

        }

        txtForgot.setOnClickListener {
            ForgotPasswordBottomSheet().show(
                supportFragmentManager,
                "ForgotPassword"
            )
        }
    }
}
