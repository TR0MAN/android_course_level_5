package com.example.androidcourselevel5.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.androidcourselevel5.R
import com.example.androidcourselevel5.databinding.FragmentContactsListBinding

class FragmentContactsList : Fragment() {

    private lateinit var binding: FragmentContactsListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsListBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()

    }

    private fun setListeners() {
        binding.tvAddNewContact.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentViewPager_to_fragmentAddContact)
        }

        // temporary solution for checking navigation to ContactProfile
        // change later, use with RecyclerView Adapter
        binding.toolbarContactList.tvMainTextContactList.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentViewPager_to_fragmentContactInfo)
        }
    }

}