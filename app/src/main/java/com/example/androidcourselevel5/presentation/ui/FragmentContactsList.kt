package com.example.androidcourselevel5.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidcourselevel5.R
import com.example.androidcourselevel5.data.retrofit.model.Contact
import com.example.androidcourselevel5.databinding.FragmentContactsListBinding
import com.example.androidcourselevel5.presentation.adapter.ClickListener
import com.example.androidcourselevel5.presentation.adapter.ContactAdapter
import com.example.androidcourselevel5.presentation.adapter.ExtendedElementClickListener
import com.example.androidcourselevel5.presentation.adapter.UsersAdapter

class FragmentContactsList : Fragment(), ExtendedElementClickListener {

    private lateinit var binding: FragmentContactsListBinding

    private val recyclerAdapter by lazy { initRecyclerViewAdapter() }

    // TODO - Delete after test
    var testList: MutableList<Contact> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsListBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createTestContactList()

        recyclerAdapter.submitList(testList)


        setListeners()

    }

    private fun setListeners() {
        binding.tvAddNewContact.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentViewPager_to_fragmentAddContact)
        }

        // temporary solution for checking navigation to ContactProfile fragment
        // change later, use with RecyclerView Adapter
        binding.toolbarContactList.tvMainTextContactList.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentViewPager_to_fragmentContactInfo)
        }
    }


    private fun initRecyclerViewAdapter(): ContactAdapter {
        val recyclerView = ContactAdapter(this, multiSelectState = false, selectedContacts = null)
        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewContacts.adapter = recyclerView
        return recyclerView
    }

    // TODO - Delete after test
    private fun createTestContactList() {
        testList.add(Contact(1, "Anna Annova", "Designer", "Kiev", null))
        testList.add(Contact(2, "Bill Billov", "Driver", "Odessa", null))
        testList.add(Contact(3, "Caren Carenova", "Policeman", "Dnipro", null))
        testList.add(Contact(4, "Danil Danilov", "Artist", "Lviv", null))
        testList.add(Contact(5, "Emma Emmova", "Doctor", "London", null))

    }

    override fun onElementProfileClick(contact: Contact) {
        Toast.makeText(requireActivity(), "GO to PROFILE [id=${contact.id}]", Toast.LENGTH_SHORT).show()
    }

    override fun onElementLongClick(contactId: Int) {
        Toast.makeText(requireActivity(), "MULTISELECT state ON", Toast.LENGTH_SHORT).show()
    }

    override fun onElementChecked(checkBoxState: Boolean, contactId: Int) {
        TODO("Not yet implemented")
    }

    override fun onElementClickAction(contact: Contact) {
        Toast.makeText(requireActivity(), "CONTACT DELETED", Toast.LENGTH_SHORT).show()
    }
}