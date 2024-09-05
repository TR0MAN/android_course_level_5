package com.example.androidcourselevel5.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.androidcourselevel5.databinding.FragmentProfileSettingsBinding
import com.example.androidcourselevel5.presentation.viewmodel.SettingsViewModel
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
        setListeners()

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
        if (settingsViewModel.getCheckboxState()) {
            // send LOGIN request to server (Retrofit)
        }

    }

}