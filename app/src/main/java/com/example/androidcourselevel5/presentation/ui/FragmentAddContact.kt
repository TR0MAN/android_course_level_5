package com.example.androidcourselevel5.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidcourselevel5.R
import com.example.androidcourselevel5.data.retrofit.model.Contact
import com.example.androidcourselevel5.databinding.FragmentAddContactBinding
import com.example.androidcourselevel5.presentation.adapter.ElementClickListener
import com.example.androidcourselevel5.presentation.adapter.UsersAdapter


class FragmentAddContact : Fragment(), ElementClickListener {

    private lateinit var binding: FragmentAddContactBinding

    private val recyclerAdapter by lazy { initRecyclerViewAdapter() }

    // TODO - Delete after test
    var testList: MutableList<Contact> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddContactBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO - Delete after test
        createTestContactList()
//        convertContactToExtContact(testList, listOf(1,3,6,7,8))
        recyclerAdapter.submitList(testList)

        setListeners()
    }



    private fun setListeners() {

        // temporary solution for checking navigation to ProfileInvite fragment
        // change later, use with RecyclerView Adapter
        binding.toolbarAddContact.tvMainTextAddContact.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentAddContact_to_fragmentProfileInvite)
        }

    }

    override fun onElementClickAction(contact: Contact) {
        recyclerAdapter.setUserList(listOf(1,4))
//        Toast.makeText(requireActivity(),"[${contact.id}] ${contact.name}", Toast.LENGTH_SHORT).show()
    }

    private fun initRecyclerViewAdapter(): UsersAdapter {
        val recyclerView = UsersAdapter(this, mutableListOf())
        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewContacts.adapter = recyclerView
        return recyclerView
    }

    // OLD variant
//    private fun createAdapter(multiSelectState: Boolean?, usersInContactList: List<Int>?,
//                              filteredContactsList: List<Contact>? = null) {
//        recyclerViewAdapter = ContactAdapter(this, multiSelectState, usersInContactList, null)
//        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(requireContext())
//        binding.recyclerViewContacts.adapter = recyclerViewAdapter
//        if (filteredContactsList == null)
//            recyclerViewAdapter.submitList(viewModel.listOfAllUsers.value)
//        else
//            recyclerViewAdapter.submitList(filteredContactsList)
//    }

    // TODO - Delete after test
    private fun createTestContactList() {
        testList.add(Contact(1, "Anna Annova", "Designer", "Kiev", null))
        testList.add(Contact(2, "Bill Billov", "Driver", "Odessa", null))
        testList.add(Contact(3, "Caren Carenova", "Policeman", "Dnipro", null))
        testList.add(Contact(4, "Danil Danilov", "Artist", "Lviv", null))
        testList.add(Contact(5, "Emma Emmova", "Doctor", "London", null))

    }

}
