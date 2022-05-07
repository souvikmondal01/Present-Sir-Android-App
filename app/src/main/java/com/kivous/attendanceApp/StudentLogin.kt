package com.kivous.attendanceApp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth


class StudentLogin : AppCompatActivity() {
    private var isShowPass = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_login)
        window.statusBarColor = ContextCompat.getColor(this, R.color.purple_10)

        val ivBackArrow: ImageView = findViewById(R.id.iv_back_arrow)
        val ivWhiteBackground: ImageView = findViewById(R.id.iv_white_background)
        val tvForgotPassword: TextView = findViewById(R.id.tv_forgot_password)
        val tvRegister: TextView = findViewById(R.id.tv_Register)
        val btnLogin: Button = findViewById(R.id.btn_login)
        val etEmail: EditText = findViewById(R.id.et_email)
        val etPassword: EditText = findViewById(R.id.et_password)
        val pbLogin: ProgressBar = findViewById(R.id.pb_login)
        val ivEye: ImageView = findViewById(R.id.iv_eye)

        auth = FirebaseAuth.getInstance()
        val token = getSharedPreferences("username", Context.MODE_PRIVATE)

        ivBackArrow.setOnClickListener {
            finish()
        }
        ivWhiteBackground.setOnClickListener {
            closeKeyBoard()
        }
        tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show()
        }
        tvRegister.setOnClickListener {
            val intent = Intent(this, StudentRegister::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            when {
                etEmail.text.isEmpty() -> {
                    etEmail.error = "enter email"
                }
                etPassword.text.isEmpty() -> {
                    etPassword.error = "enter password"
                }
                else -> {
                    pbLogin.visibility = View.VISIBLE
                    val email = etEmail.text.toString().trim()
                    val password = etPassword.text.toString().trim()
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                auth = FirebaseAuth.getInstance()
                                val currentUser = auth.currentUser!!.uid
                                pbLogin.visibility = View.GONE

                                db.collection("users").document(currentUser).get()
                                    .addOnSuccessListener { t ->
                                        val c = t.getString("student")
                                        if (c == "1") {
                                            val intent = Intent(this, StudentDashBoard::class.java)
                                            startActivity(intent)
                                            val editor = token.edit()
                                            editor.putString("login_email", currentUser)
                                            editor.apply()
                                            finishAffinity()

                                        } else if (c == "0") {
                                            val intent = Intent(this, TeacherDashBoard::class.java)
                                            startActivity(intent)
                                            val editor = token.edit()
                                            editor.putString("login_email", currentUser)
                                            editor.apply()
                                            finishAffinity()
                                        } else {
                                            val intent = Intent(this, MainActivity::class.java)
                                            startActivity(intent)
                                            finishAffinity()
                                        }
                                    }
                            } else {
                                pbLogin.visibility = View.GONE
                                Toast.makeText(
                                    this, "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                }
            }

        }

        ivEye.setOnClickListener {
            isShowPass = !isShowPass
            showPassword(isShowPass, etPassword, ivEye)
        }
        showPassword(isShowPass, etPassword, ivEye)

    }


    //     Function for close keyboard
    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showPassword(isShow: Boolean, et: EditText, iv: ImageView) {
        if (isShow) {
            et.transformationMethod = HideReturnsTransformationMethod.getInstance()
            iv.setImageResource(R.drawable.ic_visibility)
        } else {
            et.transformationMethod = PasswordTransformationMethod.getInstance()
            iv.setImageResource(R.drawable.ic_visibility_off)
        }
        et.setSelection(et.text.toString().length)
    }


}