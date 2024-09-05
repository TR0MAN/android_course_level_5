package com.example.androidcourselevel5.presentation.ui

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.androidcourselevel5.R
import com.example.androidcourselevel5.data.retrofit.model.CreateUserModel
import com.example.androidcourselevel5.databinding.ActivityRegistrationBinding
import com.example.androidcourselevel5.domain.constants.Const
import com.example.androidcourselevel5.presentation.viewmodel.RegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var addContactImageResult: ActivityResultLauncher<Intent>
    private val calendar by lazy { Calendar.getInstance() }
    private val registrationViewModel: RegistrationViewModel by viewModels()

    private var email: String = Const.DEFAULT_STRING_VALUE
    private var password: String = Const.DEFAULT_STRING_VALUE
    private var checkBox: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let { getDataFromIntent(it) }

        setActivityResultContract()
        setListeners()

        // hide the automatically pop-up keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun getDataFromIntent(intent: Intent) {
        email = intent.getStringExtra(Const.EMAIL) ?: Const.DEFAULT_STRING_VALUE
        password = intent.getStringExtra(Const.PASSWORD) ?: Const.DEFAULT_STRING_VALUE
        checkBox = intent.getBooleanExtra(Const.CHECKBOX, false)
    }

    private fun setActivityResultContract() {
        addContactImageResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {
                if (it.resultCode == RESULT_OK) {
                    val avatarImageUri = it?.data?.data
                    binding.imgAvatar.setImageURI(avatarImageUri)
                }
            })
    }

    private fun setListeners() {

        with(binding) {
            btnSaveUserData.setOnClickListener {
                if (checkingFieldsOnData()) {
                    // get data from all fields and send response to server
                    val newUser = prepareDataForServerRequest()
                    Log.d("TAG", "newUser = $newUser")

                    // send request for create new user
                    // if all OK, then save USER DATA to storage and go to Fragment Settings
                    saveUserDataToStorage()
                    startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))
                } else {
                    Toast.makeText(this@RegistrationActivity,
                        getString(R.string.registration_activity_fields_must_be_filed_message), Toast.LENGTH_SHORT).show()
                }
            }

            etBirthday.setOnClickListener {
                showDatePickerDialog()
            }

            // choose avatar image
            imgAddImageIcon.setOnClickListener {
                val photoPickerIntent =
                    Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "image/*" }
                addContactImageResult.launch(photoPickerIntent)
            }

            // TODO - only for quick test (DELETE after test)
            imgFillAllDataFields.setOnClickListener {
                etUserName.setText("Polina LiveDatova")
                etCareer.setText("Director")
                etPhone.setText("050-555-66-77")
                etAddress.setText("Ukraine, Lviv, Peremoga street, 17")
                etBirthday.setText("23/10/2004")
            }

        }
    }

    private fun saveUserDataToStorage() {
        registrationViewModel.saveAllUserData(
            email = email, password = password, checkbox = checkBox,
            userName = binding.etUserName.text.toString(),
            career = binding.etCareer.text.toString(),
            address = binding.etAddress.text.toString()
        )
    }

    private fun prepareDataForServerRequest(): CreateUserModel {
        return CreateUserModel(
            email = email,
            password = password,
            name = binding.etUserName.text.toString(),
            phone = binding.etPhone.text.toString(),
            address = binding.etAddress.text.toString(),
            career = binding.etCareer.text.toString(),
            birthday = registrationViewModel.getBirthday(binding.etBirthday.text.toString()),
            image = null
        )
    }

    private fun showDatePickerDialog() {
        DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year,month,day)
                }
                checkingSelectedDate(currentDate = calendar.time, selectedDate = selectedDate.time)

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)
        ).show()
    }

    private fun checkingSelectedDate(currentDate: Date, selectedDate: Date) {
        if (selectedDate > currentDate) {
            Toast.makeText(this,
                getString(R.string.registration_activity_birthday_wrong_date_message), Toast.LENGTH_SHORT).show()
        } else {
            val formatted = SimpleDateFormat("dd/MM/yyyy").format(selectedDate.time)
            binding.etBirthday.setText(formatted.toString())
        }
    }

    private fun checkingFieldsOnData(): Boolean {
        with(binding) {
            if (etUserName.text.toString().isEmpty() || etUserName.text.toString().isBlank())
                return false
            if (etCareer.text.toString().isEmpty() || etCareer.text.toString().isBlank())
                return false
            if (etPhone.text.toString().isEmpty() || etPhone.text.toString().isBlank())
                return false
            if (etAddress.text.toString().isEmpty() || etAddress.text.toString().isBlank())
                return false
            if (etBirthday.text.toString().isEmpty() || etBirthday.text.toString().isBlank())
                return false
        }
        return true
    }

}