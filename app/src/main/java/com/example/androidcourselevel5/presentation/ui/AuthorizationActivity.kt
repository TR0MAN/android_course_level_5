package com.example.androidcourselevel5.presentation.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.androidcourselevel5.R
import com.example.androidcourselevel5.databinding.ActivityAuthorizationBinding
import com.example.androidcourselevel5.domain.constants.Const
import com.example.androidcourselevel5.presentation.ui.utils.clear

class AuthorizationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthorizationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setObservers()
        customSymbolTextInputForm()
        setListeners()

    }

    private fun setObservers() {
        with(binding) {

            etEmailFiled.doOnTextChanged { text, _, _, _ ->
                text?.let {
                    if (text.isEmpty()) {
                        tvEmailFiledHelper.clear()
                    } else if (!AuthorizationValidator.validateEmail(text.toString())) {
                        showEmailErrorMessage()
                    } else {
                        tvEmailFiledHelper.clear()
                    }
                }
            }

            etPasswordField.doOnTextChanged { text, _, _, _ ->
                text?.let {
                    if (text.isEmpty()) {
                        tvPasswordFiledHelper.clear()
                    } else {
                        val validationResult = AuthorizationValidator.validatePassword(binding.root.context, text.toString())
                        if (validationResult != getString(R.string.validate_success)) {
                            showPasswordErrorMessage(validationResult)
                        } else {
                            tvPasswordFiledHelper.clear()
                        }
                    }
                }
            }


        }
    }

    private fun showPasswordErrorMessage(message: String) {
        binding.tvPasswordFiledHelper.text = message
        binding.tvPasswordFiledHelper.setTextColor(resources.getColor(R.color.red_color, null))
    }

    private fun showEmailErrorMessage() {
        binding.tvEmailFiledHelper.text = getString(R.string.response_wrong_email)
        binding.tvEmailFiledHelper.setTextColor(resources.getColor(R.color.red_color, null))
    }

    private fun setListeners() {

        binding.btnAuthorizationRegister.setOnClickListener {
            if (AuthorizationValidator.validateEmail(binding.etEmailFiled.text.toString()) &&
                AuthorizationValidator.validatePassword(context = this,
                    password = binding.etPasswordField.text.toString()) == getString(R.string.validate_success)) {
                startActivity(Intent(this, RegistrationActivity::class.java))
            } else {
                Toast.makeText(this,
                    getString(R.string.empty_password_or_email_fields), Toast.LENGTH_SHORT).show()
            }
        }

        // TODO - Only for TEST (DELETE)
        binding.imgFillAuthorisationData.setOnClickListener {
            fillAuthorisationData()
        }
    }

    // replacing the standard password escape character with a large character ('●' '⬤')
    private fun customSymbolTextInputForm() {
        binding.etPasswordField.transformationMethod =
            object : PasswordTransformationMethod() {
                override fun getTransformation(source: CharSequence, view: View): CharSequence {
                    val transformation = super.getTransformation(source, view)
                    return object : CharSequence by transformation {
                        override fun get(index: Int): Char {
                            return if (transformation[index] == '\u2022') {
                                Const.DOT
                            } else {
                                transformation[index]
                            }
                        }
                    }
                }
            }
    }

    // TODO - DELETE method after test (in the end)
    private fun fillAuthorisationData() {

//        // new created contact for testing
//        binding.textInputEmailForm.setText("unit7@email.com")
//        binding.textInputPasswordForm.setText("2@Qwertyu")

        // old contact for testing
        binding.etEmailFiled.setText("unit6@email.com")
        binding.etPasswordField.setText("2@Qwertyu")

        // old contact for testing
//        binding.textInputEmailForm.setText("unit5@email.com")
//        binding.textInputPasswordForm.setText("1!Qqwerty")
    }
}