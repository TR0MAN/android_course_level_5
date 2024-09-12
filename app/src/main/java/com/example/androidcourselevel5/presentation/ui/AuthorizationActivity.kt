package com.example.androidcourselevel5.presentation.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import com.example.androidcourselevel5.R
import com.example.androidcourselevel5.data.retrofit.model.UserData
import com.example.androidcourselevel5.databinding.ActivityAuthorizationBinding
import com.example.androidcourselevel5.domain.constants.Const
import com.example.androidcourselevel5.presentation.ui.utils.clear
import com.example.androidcourselevel5.presentation.ui.utils.visibleIf
import com.example.androidcourselevel5.presentation.viewmodel.AuthorizationViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthorizationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthorizationBinding
    private val authorizationViewModel: AuthorizationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkingNeedForAutologin()
        customSymbolTextInputForm()
        setObservers()
        setListeners()
    }

    private fun setObservers() {

        authorizationViewModel.authorisationResultSuccess.observe(this) { userData ->
            if (authorizationViewModel.getSignInButtonState()) {
                launchTransitionToProfile(userData)
                authorizationViewModel.pressSignInButton()
            }
            if (authorizationViewModel.getRegistrationButtonState()) {
                createMessageDialog(userData).show()
                authorizationViewModel.pressRegistrationButton()
            }
        }

        authorizationViewModel.requestProgressBar.observe(this) { visibility ->
            binding.authorisationProgressBar.visibleIf(visibility)
        }

        authorizationViewModel.authorisationResultException.observe(this) { exception ->
            if (exception)
                createErrorSnackbar(getString(R.string.connection_exception_snackbar_message)).show()
        }

        authorizationViewModel.authorisationResultTimeout.observe(this){ isTimeout ->
            if (isTimeout)
                createErrorSnackbar(getString(R.string.connection_timeout_snackbar_message)).show()
        }

        authorizationViewModel.authorisationResultError.observe(this){
            if (authorizationViewModel.getSignInButtonState()) {
                createAlertDialog().show()
                authorizationViewModel.pressSignInButton()
            }
            if (authorizationViewModel.getRegistrationButtonState()) {
                launchRegistrationActivity()
                authorizationViewModel.pressRegistrationButton()
            }
        }

        with(binding) {

            etEmailFiled.doOnTextChanged { text, _, _, _ ->
                text?.let {
                    if (text.isEmpty()) {
                        tvEmailFiledHelper.clear()
                    } else if (!authorizationViewModel.validateEmail(text.toString())) {
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
                        val validationResult = authorizationViewModel.validatePassword(text.toString())
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

    private fun launchTransitionToProfile(userData: UserData) {
        authorizationViewModel.saveAllUserDataToDataStorage(
            userData = userData, password = binding.etPasswordField.text.toString(),
            checkboxIsChecked = binding.checkBoxAuthorizationRememberMe.isChecked)

        startActivity(Intent(this, MainActivity::class.java))
        showTransitionAnimation()
        binding.etEmailFiled.clear()
        binding.etPasswordField.clear()
    }


    private fun setListeners() {

        with(binding) {

            btnAuthorizationRegister.setOnClickListener {
                if (authorizationViewModel.validateEmailAndPassword(
                        email = etEmailFiled.text.toString(),
                        password = etPasswordField.text.toString())) {

                    launchAuthorisation()
                    authorizationViewModel.pressRegistrationButton()

                } else {
                    Toast.makeText(this@AuthorizationActivity,
                        getString(R.string.empty_password_or_email_fields), Toast.LENGTH_SHORT).show()
                }
            }

            tvAuthorizationSignInText.setOnClickListener {
                if (authorizationViewModel.validateEmailAndPassword(
                        email = etEmailFiled.text.toString(),
                        password = etPasswordField.text.toString())) {

                    launchAuthorisation()
                    authorizationViewModel.pressSignInButton()

                } else {
                    Toast.makeText(this@AuthorizationActivity,
                        getString(R.string.empty_password_or_email_fields), Toast.LENGTH_SHORT).show()
                }
            }

            // TODO - Only for TEST (DELETE)
            imgFillAuthorisationData.setOnClickListener {
                fillAuthorisationData()
            }
        }

    }

    private fun createErrorSnackbar(message: String): Snackbar {
        return Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            .setActionTextColor(getColor(R.color.orange_color))
            .setAction(getString(R.string.connection_error_snackbar_action_button_text)) {
                launchAuthorisation()
            }
    }

    private fun createMessageDialog(userData: UserData): AlertDialog.Builder {
        return AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.authorisation_message_dialog_title))
            setMessage(getString(R.string.authorisation_message_dialog_help_message))
            setCancelable(false)
            setPositiveButton(getString(R.string.authorisation_message_dialog_positive_button_text)) { dialog, _ ->
                launchTransitionToProfile(userData)
                dialog.cancel()
            }
        }
    }



    private fun createAlertDialog(): AlertDialog.Builder {
        return AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.authorisation_alert_dialog_title))
            setMessage(getString(R.string.authorisation_alert_dialog_help_message))
            setCancelable(false)
            setPositiveButton(getString(R.string.authorisation_alert_dialog_positive_button_text)) { dialog, _ ->
                launchRegistrationActivity()
                dialog.cancel()
            }
            setNegativeButton(getString(R.string.authorisation_alert_dialog_negative_button_text)) { dialog, _ ->
                dialog.cancel()
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

    private fun launchAuthorisation() {
        authorizationViewModel.authoriseUser(
            email = binding.etEmailFiled.text.toString(),
            password = binding.etPasswordField.text.toString())
    }

    private fun launchRegistrationActivity() {
        startActivity(Intent(this@AuthorizationActivity, RegistrationActivity::class.java).apply {
            putExtra(Const.EMAIL, binding.etEmailFiled.text.toString())
            putExtra(Const.PASSWORD, binding.etPasswordField.text.toString())
            putExtra(Const.CHECKBOX, binding.checkBoxAuthorizationRememberMe.isChecked)
        })
        showTransitionAnimation()
    }

    private fun checkingNeedForAutologin() {
        if (authorizationViewModel.getCheckboxState()) {
            startActivity(Intent(this@AuthorizationActivity, MainActivity::class.java))
        }
    }

    private fun showTransitionAnimation() {
        overridePendingTransition(R.anim.horiz_from_right_to_center,
            R.anim.horiz_from_center_to_left)
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
//        binding.etEmailFiled.setText("unit7@email.com")
//        binding.etPasswordField.setText("2@Qwertyu")

        // old contact for testing
        binding.etEmailFiled.setText("unit6@email.com")
        binding.etPasswordField.setText("2@Qwertyu")

//        // old contact for testing
//        binding.etEmailFiled.setText("unit5@email.com")
//        binding.etPasswordField.setText("1!Qqwerty")
    }
}