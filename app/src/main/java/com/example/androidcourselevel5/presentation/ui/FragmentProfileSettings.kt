package com.example.androidcourselevel5.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.androidcourselevel5.R
import com.example.androidcourselevel5.databinding.FragmentProfileSettingsBinding
import com.example.androidcourselevel5.presentation.ui.utils.visibleIf
import com.example.androidcourselevel5.presentation.viewmodel.SettingsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentProfileSettings : Fragment() {

    private lateinit var binding: FragmentProfileSettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUserDataToUI()
        checkingNeedForAutologin()
        setObservers()
        setListeners()

    }
    private fun setObservers() {

        settingsViewModel.settingsResultSuccess.observe(requireActivity()) { userData ->
            settingsViewModel.refreshTokens(accessToken = userData.accessToken,
                refreshToken = userData.refreshToken)
        }

        settingsViewModel.settingsResultError.observe(requireActivity()){
            createErrorSnackbar(requireActivity().getString(R.string.settings_request_error_snackbar_message)).show()
        }

        settingsViewModel.settingsResultException.observe(requireActivity()) { exception ->
            if (exception)
                createErrorSnackbar(getString(R.string.connection_exception_snackbar_message)).show()
        }

        settingsViewModel.settingsResultTimeout.observe(requireActivity()){ isTimeout ->
            if (isTimeout)
                createErrorSnackbar(getString(R.string.connection_timeout_snackbar_message)).show()
        }

        settingsViewModel.requestProgressBar.observe(requireActivity()) { visibility->
            binding.settingsProgressBar.visibleIf(visibility)
        }

    }

    private fun setListeners() {
        binding.btnMyProfileLogOut.setOnClickListener {
            settingsViewModel.clearStorageData()
            requireActivity().finish()
        }
    }

    private fun setUserDataToUI() {
        settingsViewModel.getDataForSetToUI().also { list->
            binding.tvProfileName.text = list[0]
            binding.tvProfileProfession.text = list[1]
            binding.tvProfileAddress.text = list[2]
        }
    }

    private fun checkingNeedForAutologin() {
        if (settingsViewModel.getCheckboxState())
            settingsViewModel.refreshUserData()
    }

    private fun createErrorSnackbar(message: String): Snackbar {
        return Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            .setActionTextColor(requireActivity().getColor(R.color.orange_color))
            .setAction(getString(R.string.connection_error_snackbar_action_button_text)) {
                settingsViewModel.refreshUserData()
            }
    }
}