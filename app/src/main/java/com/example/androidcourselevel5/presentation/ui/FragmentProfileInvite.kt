package com.example.androidcourselevel5.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.androidcourselevel5.R
import com.example.androidcourselevel5.databinding.FragmentProfileInviteBinding
import com.example.androidcourselevel5.domain.constants.Const
import com.example.androidcourselevel5.presentation.ui.utils.showWithGlide


class FragmentProfileInvite : Fragment() {

    private lateinit var binding: FragmentProfileInviteBinding
    private val args: FragmentProfileInviteArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileInviteBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setContactDataToUI()
        setButtonListeners()
    }

    private fun setContactDataToUI() {
        with(binding) {
            tvProfileName.text = args.informationAboutContact.name
            tvProfileProfession.text = args.informationAboutContact.career
            tvProfileAddress.text = args.informationAboutContact.address
            binding.imgProfileInviteMainPhoto.showWithGlide(args.informationAboutContact.image)
        }
    }

    private fun setButtonListeners() {
        binding.btnInviteAddToContacts.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(Const.RESULT_KEY,
                args.informationAboutContact.id)
            findNavController().popBackStack()
        }

        // back to previous fragment
        binding.customToolbarProfile.imgBackToolbarProfile.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}