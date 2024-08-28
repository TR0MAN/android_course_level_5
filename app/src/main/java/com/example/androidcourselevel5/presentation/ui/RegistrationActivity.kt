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
import com.example.androidcourselevel5.R
import com.example.androidcourselevel5.data.retrofit.model.CreateUserModel
import com.example.androidcourselevel5.databinding.ActivityRegistrationBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var addContactImageResult: ActivityResultLauncher<Intent>
    private val calendar by lazy { Calendar.getInstance() }

    // Maybe get data from STORAGE
    private var email: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get real data email and password
        email = "test@mail.com"
        password = "123456789"

        setActivityResultContract()
        setListeners()

        // hide the automatically pop-up keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
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
                if (checkingFieldsOnNoData()) {
                    // get data from all fields and send response to server
                    val newUser = prepareDataForServerResponse()

                    // send response for create new user
                    // if all OK, then go to Fragment Settings
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

    private fun prepareDataForServerResponse(): CreateUserModel {
        return CreateUserModel(
            email = email!!,
            password = password!!,
            name = binding.etUserName.text.toString(),
            phone = binding.etPhone.text.toString(),
            address = binding.etAddress.text.toString(),
            career = binding.etCareer.text.toString(),
            birthday = ActivityHelper.getBirthday(binding.etBirthday.text.toString()),
            image = null
        )
    }

    private fun showDatePickerDialog() {
        DatePickerDialog(
            this,
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

    private fun checkingFieldsOnNoData(): Boolean {
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