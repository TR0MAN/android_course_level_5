package com.example.androidcourselevel5.presentation.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.androidcourselevel5.R
import com.example.androidcourselevel5.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setListeners()
    }

    private fun setListeners() {

        binding.btnSaveUserData.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}