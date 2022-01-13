package com.example.caloriescalculator.login_register

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.caloriescalculator.MainActivity
import com.example.caloriescalculator.R
import com.example.caloriescalculator.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "LoginActivity"
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("user_id")
        val userEmail = intent.getStringExtra("email_id")

        binding.etLogin.setText(userEmail)

        binding.loginBtn.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.etLogin.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this,
                        "Please enter email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this,
                        "Please enter password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    val email: String = binding.etLogin.text.toString().trim { it <= ' ' }
                    val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            if (isUserVerified()){

                                Toast.makeText(
                                    this,
                                    "You are log in successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                                intent.putExtra("email_id", email)
                                startActivity(intent)
                                finish()

                            }else if(!isUserVerified()){
                                Toast.makeText(
                                    this,
                                    "Email not verified!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val verificationEmailDialog = LayoutInflater.from(this).inflate(R.layout.send_verify_email_dialog, null)
                                showAlertDialog("Account verification", "Send" ,verificationEmailDialog, View.OnClickListener {
                                    emailVerification()
                                })
                            }
                        } else {
                            Toast.makeText(
                                this,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            }
        }

        binding.tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.tvResetPassword.setOnClickListener {
            val resetPasswordDialog = LayoutInflater.from(this).inflate(R.layout.reset_email_dialog, null)
            showAlertDialog("Forget password?", "Reset", resetPasswordDialog, View.OnClickListener {
                val etResetPassEmail = resetPasswordDialog.findViewById<EditText>(R.id.etResetPassEmail)
                if (etResetPassEmail.text.isEmpty()){
                    etResetPassEmail.error = "Required Field!"
                    return@OnClickListener
                }
                FirebaseAuth.getInstance().sendPasswordResetEmail(etResetPassEmail.text.toString()).addOnCompleteListener{ task ->
                    if (task.isSuccessful){
                        Toast.makeText(this, "Reset email sent.", Toast.LENGTH_SHORT).show()
                    }

                    if (!task.isSuccessful){
                        Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

    }

    private fun emailVerification() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!
        firebaseUser.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(
                    this,
                    "Verification email has been sent to: ${firebaseUser.email}. \n Check your spam folder if cannot see the message",
                    Toast.LENGTH_SHORT
                ).show()
            }else if (!task.isSuccessful){
                Log.d(TAG, "onFailure: Email not sent ${task.exception?.message.toString()}")
            }
        }
    }

    private fun isUserVerified(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser!!.isEmailVerified
    }

    private fun showAlertDialog(title : String, positiveBtnTxt: String ,view : View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton(positiveBtnTxt){_,_->
                positiveClickListener.onClick(null)
            }.show()
    }
}