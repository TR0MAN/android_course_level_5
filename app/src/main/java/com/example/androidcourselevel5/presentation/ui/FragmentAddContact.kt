package com.example.androidcourselevel5.presentation.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidcourselevel5.R
import com.example.androidcourselevel5.data.retrofit.model.Contact
import com.example.androidcourselevel5.databinding.FragmentAddContactBinding
import com.example.androidcourselevel5.domain.constants.Const
import com.example.androidcourselevel5.presentation.adapter.ElementClickListener
import com.example.androidcourselevel5.presentation.adapter.UsersAdapter
import com.example.androidcourselevel5.presentation.ui.utils.clear
import com.example.androidcourselevel5.presentation.ui.utils.gone
import com.example.androidcourselevel5.presentation.ui.utils.goneIf
import com.example.androidcourselevel5.presentation.ui.utils.visible
import com.example.androidcourselevel5.presentation.ui.utils.visibleIf
import com.example.androidcourselevel5.presentation.viewmodel.AddContactViewModel
import com.example.androidcourselevel5.presentation.viewmodel.SharedViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentAddContact : Fragment(), ElementClickListener {

    private lateinit var binding: FragmentAddContactBinding
    private lateinit var recyclerAdapter: UsersAdapter

    private val addContactViewModel:AddContactViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddContactBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        setListeners()
        getListWithAllUsers()
    }

    private fun setObservers() {

        addContactViewModel.getUsersListResultSuccess.observe(requireActivity()) { allUsersList ->
            if (allUsersList.isNotEmpty()) {
                showRecyclerView(true)

                if (addContactViewModel.isActiveSearchField.value == true) {
                    checkListAndShowResult(addContactViewModel.getFilteredList())
                } else {
                    createRecyclerViewAdapter(usersList = allUsersList,
                        userInContactList = getContactListID(sharedViewModel.contactList.value))
                }
            } else {
                showRecyclerView(false)
            }
        }

        addContactViewModel.getUsersListResultError.observe(requireActivity()) {
            createErrorSnackbar(requireActivity().getString(R.string.request_error_toast_message,
                it.body()?.message, it.body()?.code), GET_CONTACTS).show()
        }

        addContactViewModel.getUsersListResultException.observe(requireActivity()){ exception ->
            if (exception)
                createErrorSnackbar(getString(R.string.connection_exception_snackbar_message), GET_CONTACTS).show()
        }

        addContactViewModel.getUsersListResultTimeout.observe(requireActivity()){ isTimeout ->
            if (isTimeout)
                createErrorSnackbar(getString(R.string.connection_timeout_snackbar_message), GET_CONTACTS).show()
        }

        addContactViewModel.requestProgressBar.observe(requireActivity()) { visibility ->
            binding.addContactProgressBar.visibleIf(visibility)
        }

        sharedViewModel.contactList.observe(requireActivity()) {
            addContactViewModel.updateUsersList()
        }

        sharedViewModel.addContactResultError.observe(requireActivity()) {
            createErrorSnackbar(requireActivity().getString(R.string.request_error_toast_message,
                it.body()?.message, it.body()?.code), ADD_CONTACT).show()
        }

        sharedViewModel.addContactResultException.observe(requireActivity()) { exception ->
            if (exception)
                createErrorSnackbar(getString(R.string.connection_exception_snackbar_message), ADD_CONTACT).show()
        }

        sharedViewModel.addContactResultTimeout.observe(requireActivity()){ isTimeout ->
            if (isTimeout)
                createErrorSnackbar(getString(R.string.connection_timeout_snackbar_message), ADD_CONTACT).show()
        }

        sharedViewModel.requestProgressBar.observe(requireActivity()) { visibility ->
            binding.addContactProgressBar.visibleIf(visibility)
        }

        addContactViewModel.isActiveSearchField.observe(requireActivity()) {visibility ->
            binding.toolbarAddContact.containerSearchAddContact.visibleIf(visibility)
            binding.toolbarAddContact.containerTextAddContact.goneIf(visibility)
        }

    }

    private fun setListeners() {
        with(binding.toolbarAddContact) {

            imgBackAddContact.setOnClickListener {
                findNavController().popBackStack()
            }

            imgOpenSearchFiled.setOnClickListener {
                addContactViewModel.setSearchFieldVisibility(true)
            }

            imgCloseSearchFiled.setOnClickListener {
                edSearchAddContact.clear()
                addContactViewModel.setSearchFieldVisibility(false)
            }

            edSearchAddContact.doOnTextChanged { text, _, _, _ ->
                if (text?.isNotEmpty() == true) {
                    val filteredList =
                        addContactViewModel.getUsersListResultSuccess.value?.filter {
                            it.name?.contains(text,true) == true
                    }
                    addContactViewModel.saveFilteredList(filteredList)
                    showFilteredUsersList(filteredList)
                } else {
                    showAllUsersList()
                }
            }
        }

        // listener for getting contact id for adding new contact to contact list
        val resultOfAddingContact =
            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Int>(Const.RESULT_KEY)

        // observer for adding new contact to contact list
        resultOfAddingContact?.observe(viewLifecycleOwner) { id ->
            if (id != null) {
                val alreadyInList = sharedViewModel.contactList.value?.map {it.id }?.contains(id)
                if (alreadyInList == false) {
                    sharedViewModel.addContact(id)
                }
            }
        }

    }

    private fun showAllUsersList() {
        addContactViewModel.getUsersListResultSuccess.value?.let {
            showRecyclerView(true)
            createRecyclerViewAdapter(
                usersList = it,
                userInContactList = getContactListID(sharedViewModel.contactList.value))
        }
    }

    private fun showFilteredUsersList(filteredList: List<Contact>?) {
        filteredList?.let { list ->
            checkListAndShowResult(list)
        }
    }

    private fun checkListAndShowResult(list: List<Contact>) {
        if (list.isNotEmpty()) {
            showRecyclerView(true)
            createRecyclerViewAdapter(usersList = list,
                userInContactList = getContactListID(sharedViewModel.contactList.value))
        } else {
            showRecyclerView(false)
        }
    }

    private fun getListWithAllUsers() {
        addContactViewModel.getListWithAllUsers()
    }

    private fun getContactListID(contactList: List<Contact>?): List<Int> {
        contactList?.let { list ->
            return list.map {it.id }
        } ?: return emptyList()
    }

    private fun showRecyclerView(visibility: Boolean) {
        binding.recyclerViewContacts.visibleIf(visibility)
        binding.noContactsContainer.goneIf(visibility)
    }

    override fun onElementClickAction(contact: Contact) {
        val destinationPointWithData = FragmentAddContactDirections
            .actionFragmentAddContactToFragmentProfileInvite(informationAboutContact = contact)
        findNavController().navigate(destinationPointWithData)
    }

    private fun createErrorSnackbar(message: String, action: Int ): Snackbar {
        return Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            .setActionTextColor(requireActivity().getColor(R.color.orange_color))
            .setAction(getString(R.string.connection_error_snackbar_action_button_text)) {
                when(action){
                    GET_CONTACTS -> addContactViewModel.getListWithAllUsers()
                    ADD_CONTACT -> sharedViewModel.addToContactList()
                }
            }
    }

    private fun createRecyclerViewAdapter(usersList: List<Contact>,
                                          userInContactList: List<Int> = emptyList()) {
        recyclerAdapter = UsersAdapter(this, userInContactList)
        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewContacts.adapter = recyclerAdapter
        recyclerAdapter.submitList(usersList)
    }

    companion object {
        const val GET_CONTACTS = 1
        const val ADD_CONTACT = 2
    }

}
