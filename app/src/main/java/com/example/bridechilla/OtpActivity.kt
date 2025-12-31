package com.example.bridechilla




import com.google.firebase.auth.PhoneAuthProvider
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class OtpActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val etOtp = findViewById<EditText>(R.id.etOtp)
        val btnVerify = findViewById<View>(R.id.btnNext)

        val vid = intent.getStringExtra("vid")!!
        val username = intent.getStringExtra("username")!!
        val phone = intent.getStringExtra("phone")!!
        val password = intent.getStringExtra("password")!!

        btnVerify.setOnClickListener {

            val otp = etOtp.text.toString().trim()
            val credential = PhoneAuthProvider.getCredential(vid, otp)

            auth.signInWithCredential(credential).addOnSuccessListener {

                val fakeEmail = "$username@bridechilla.app"

                auth.currentUser?.delete()

                auth.createUserWithEmailAndPassword(fakeEmail, password)
                    .addOnSuccessListener { result ->

                        val uid = result.user!!.uid

                        val userData = mapOf(
                            "username" to username,
                            "phone" to phone
                        )

                        db.collection("users").document(uid).set(userData)
                        db.collection("usernames").document(username).set(mapOf("uid" to uid))
                        db.collection("phones").document(phone).set(mapOf("uid" to uid))

                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
            }
        }
    }
}
