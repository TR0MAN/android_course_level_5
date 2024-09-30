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
import com.example.androidcourselevel5.databinding.FragmentContactInfoBinding
import com.example.androidcourselevel5.presentation.ui.utils.showWithGlide

class FragmentContactInfo : Fragment() {

    private lateinit var binding: FragmentContactInfoBinding
    private val args: FragmentContactInfoArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setContactDataToUI()
        setButtonListeners()
    }

    private fun setButtonListeners() {
        binding.customToolbarProfile.imgBackToolbarProfile.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setContactDataToUI() {
        with(binding) {
            tvProfileName.text = args.contactInformation.name
            tvProfileProfession.text = args.contactInformation.career
            tvProfileAddress.text = args.contactInformation.address
            binding.imgContactInfoMainPhoto.showWithGlide(args.contactInformation.image)
        }
    }

}