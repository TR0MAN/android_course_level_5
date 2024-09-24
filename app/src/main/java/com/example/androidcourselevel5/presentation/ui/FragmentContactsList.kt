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
import com.example.androidcourselevel5.databinding.FragmentContactsListBinding
import com.example.androidcourselevel5.presentation.adapter.ContactAdapter
import com.example.androidcourselevel5.presentation.adapter.ExtendedElementClickListener
import com.example.androidcourselevel5.presentation.ui.utils.clear
import com.example.androidcourselevel5.presentation.ui.utils.goneIf
import com.example.androidcourselevel5.presentation.ui.utils.visibleIf
import com.example.androidcourselevel5.presentation.viewmodel.ContactsListViewModel
import com.example.androidcourselevel5.presentation.viewmodel.SharedViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentContactsList : Fragment(), ExtendedElementClickListener {

    private lateinit var binding: FragmentContactsListBinding

    private val contactListViewModel: ContactsListViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var recyclerAdapter: ContactAdapter

    private var contactIdForRestore = DEFAULT_ID_FOR_RESTORE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsListBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        setListeners()
        getCurrentUserContactsList()
    }

    private fun setObservers() {

        sharedViewModel.contactList.observe(requireActivity()){ list ->
            if (sharedViewModel.tabLayoutVisibility.value == false) {
                createRecyclerViewAdapter(multiSelectState = true,
                    selectedContacts = contactListViewModel.listOfContactsForGroupDeleting.value,
                    listOfContacts = sharedViewModel.contactList.value)
            } else if (contactListViewModel.isActiveSearchField.value == true) {
                checkListAndShowResult(contactListViewModel.getFilteredList())
            } else {
                createRecyclerViewAdapter(multiSelectState = false, selectedContacts = null,
                    listOfContacts = list)
            }
        }

        contactListViewModel.listOfContactsForGroupDeleting.observe(requireActivity()) { list->
            if (list.isEmpty()) {
                sharedViewModel.setTabLayoutVisibility(visibility = true)
                contactListViewModel.setGroupDeletingState(state = false)
                createRecyclerViewAdapter(multiSelectState = false, selectedContacts = null,
                    listOfContacts = sharedViewModel.contactList.value)
            }
        }

        contactListViewModel.contactListResultSuccess.observe(requireActivity()) { contactList ->
            // case for restore contact after deleting
            if (contactIdForRestore != DEFAULT_ID_FOR_RESTORE) {
                createErrorSnackbar(getString(R.string.restore_snackbar_text_message), RESTORE_CONTACT).show()
            }

            // case for deleting group of contacts (after rotate)
            if (contactListViewModel.getGroupDeletingState()) {
                if (contactListViewModel.listOfContactsForGroupDeleting.value?.isEmpty() == false) {
                    contactListViewModel.deleteMultipleContact()
                }
            }

            // case for normal showing list of contacts
            if (contactList.isNotEmpty()) {
                showRecyclerView(true)
                sharedViewModel.updateUserContactList(contactList)
            } else {
                showRecyclerView(false)
            }
        }

        contactListViewModel.contactListResultError.observe(requireActivity()) {
            createErrorSnackbar(requireActivity().getString(R.string.request_error_toast_message,
                it.body()?.message, it.body()?.code), GET_CONTACTS_LIST).show()
        }

        contactListViewModel.contactListResultException.observe(requireActivity()){ exception ->
            // need for showing snackbar with deleting after rotate
            if (!contactListViewModel.isSuccessDeletion()) {
                contactListViewModel.showContactDeletingSnackbar()
            } else if (exception) {
                createErrorSnackbar(getString(R.string.connection_exception_snackbar_message), GET_CONTACTS_LIST).show()
            }
        }

        contactListViewModel.contactListResultTimeout.observe(requireActivity()){ isTimeout ->
            if (isTimeout)
                createErrorSnackbar(getString(R.string.connection_timeout_snackbar_message), GET_CONTACTS_LIST).show()
        }

        contactListViewModel.requestProgressBar.observe(requireActivity()) { visibility ->
            binding.contactListProgressBar.visibleIf(visibility)
        }

        contactListViewModel.deleteContactResultError.observe(requireActivity()) {
            createErrorSnackbar(requireActivity().getString(R.string.request_error_toast_message,
                it.message(), it.code()), DELETE_CONTACT).show()
        }

        contactListViewModel.deleteContactResultException.observe(requireActivity()){ exception ->
            if (exception)
                createErrorSnackbar(getString(R.string.connection_exception_snackbar_message), DELETE_CONTACT).show()
        }

        contactListViewModel.deleteContactResultTimeout.observe(requireActivity()){ isTimeout ->
            if (isTimeout)
                createErrorSnackbar(getString(R.string.connection_timeout_snackbar_message), DELETE_CONTACT).show()
        }

        sharedViewModel.addContactResultError.observe(requireActivity()) {
            createErrorSnackbar(requireActivity().getString(R.string.request_error_toast_message,
                it.message(), it.code()), RESTORE_CONTACT).show()
        }

        sharedViewModel.addContactResultException.observe(requireActivity()) { exception ->
            // TODO - triggered it
            if (exception)
                createErrorSnackbar(getString(R.string.connection_exception_snackbar_message),RESTORE_CONTACT).show()
        }

        sharedViewModel.addContactResultTimeout.observe(requireActivity()){ isTimeout ->
            if (isTimeout)
                createErrorSnackbar(getString(R.string.connection_timeout_snackbar_message), RESTORE_CONTACT).show()
        }

        contactListViewModel.isActiveSearchField.observe(requireActivity()) { visibility->
            binding.toolbarContactList.containerSearchContactList.visibleIf(visibility)
            binding.toolbarContactList.containerTextContactList.goneIf(visibility)
        }

    }

    private fun getCurrentUserContactsList() {
        if (sharedViewModel.tabLayoutVisibility.value == true) {
            // TODO - DELETE
            Log.d("TAG", "ContactList -> getCurrentUserContactsList -> tabLayoutVisibility[${sharedViewModel.tabLayoutVisibility.value}]")
            contactListViewModel.getUserContactsList()
        }
    }

    private fun setListeners() {
        with(binding) {
            tvAddNewContact.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentViewPager_to_fragmentAddContact)
            }

            imgDeleteManyContacts.setOnClickListener {
                contactListViewModel.setGroupDeletingState(state = true)
                contactListViewModel.deleteMultipleContact()
            }

            toolbarContactList.imgSearchContactList.setOnClickListener {
                contactListViewModel.setSearchFieldVisibility(visibility = true)
            }

            toolbarContactList.imgCloseContactList.setOnClickListener {
                toolbarContactList.edSearchContactList.clear()
                contactListViewModel.setSearchFieldVisibility(visibility = false)
            }

            toolbarContactList.edSearchContactList.doOnTextChanged { text, _, _, _ ->
                if (text?.isNotEmpty() == true) {
                    val filteredList =
                        contactListViewModel.contactListResultSuccess.value?.filter {
                            it.name?.contains(text,true) == true
                        }
                    contactListViewModel.saveFilteredList(filteredList)
                    showFilteredContactsList(filteredList)
                } else {
                    showNormalContactsList()
                }
            }
        }

    }

    private fun showFilteredContactsList(filteredList: List<Contact>?) {
        filteredList?.let { it ->
            checkListAndShowResult(it)
        }
    }

    private fun checkListAndShowResult(list: List<Contact>) {
        if (list.isNotEmpty()) {
            showRecyclerView(true)
            createRecyclerViewAdapter(multiSelectState = false,
                selectedContacts = null, listOfContacts = list)
        } else {
            showRecyclerView(false)
        }
    }

    private fun showRecyclerView(visibility: Boolean) {
        binding.recyclerViewContacts.visibleIf(visibility)
        binding.emptyContactsContainer.goneIf(visibility)
    }


    private fun showNormalContactsList() {
        contactListViewModel.contactListResultSuccess.value?.let {
            showRecyclerView(true)
            createRecyclerViewAdapter(multiSelectState = false,
                selectedContacts = null, listOfContacts = it)
        }
    }

    private fun createRecyclerViewAdapter(multiSelectState: Boolean, selectedContacts:List<Int>?,
                                          listOfContacts: List<Contact>?) {
        binding.imgDeleteManyContacts.visibleIf(multiSelectState)
        recyclerAdapter = ContactAdapter(this, multiSelectState = multiSelectState,
            selectedContactsForDeleting = selectedContacts)
        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewContacts.adapter = recyclerAdapter
        recyclerAdapter.submitList(listOfContacts)
    }

    private fun createErrorSnackbar(message: String, action: Int): Snackbar {
        return Snackbar.make(binding.snackbarCoordinator, message,
            when(action) {
                RESTORE_CONTACT -> SNACKBAR_DURATION
                else -> Snackbar.LENGTH_INDEFINITE
            })
            .setActionTextColor(requireActivity().getColor(R.color.orange_color))
            .setAction(
                when(action) {
                    RESTORE_CONTACT -> getString(R.string.restore_snackbar_action_button_text)
                    else -> { getString(R.string.connection_error_snackbar_action_button_text) }
                }) {
                when(action) {
                    GET_CONTACTS_LIST -> contactListViewModel.getUserContactsList()
                    DELETE_CONTACT -> contactListViewModel.deleteFromContacts()
                    RESTORE_CONTACT -> sharedViewModel.addContact(contactIdForRestore)
                }
            }
    }

    override fun onElementProfileClick(contact: Contact) {
        val destinationPointWithData = FragmentViewPagerDirections
            .actionFragmentViewPagerToFragmentContactInfo(contactInformation = contact)
        findNavController().navigate(destinationPointWithData)
    }

    // listener for actions with "image buttons" (delete ot add)
    override fun onElementClickAction(contact: Contact) {
        contactIdForRestore = contact.id
        contactListViewModel.saveContactIdForDelete(contactID = contact.id)
        contactListViewModel.deleteFromContacts()
    }

    // listener for switch display mode (single or group deleting)
    override fun onElementLongClick(contactId: Int) {
        sharedViewModel.setTabLayoutVisibility(visibility = false)
        contactListViewModel.addContactToDeletingList(contactId)
        createRecyclerViewAdapter(multiSelectState = true,
            selectedContacts = contactListViewModel.listOfContactsForGroupDeleting.value,
            listOfContacts = sharedViewModel.contactList.value)
    }

    override fun onElementChecked(checkBoxState: Boolean, contactId: Int) {
        if (checkBoxState) {
            contactListViewModel.addContactToDeletingList(contactID = contactId)
        } else {
            contactListViewModel.removeContactFromDeletingList(contactID = contactId)
        }
    }

    companion object {
        const val GET_CONTACTS_LIST = 1
        const val DELETE_CONTACT = 2
        const val RESTORE_CONTACT = 3
        const val DEFAULT_ID_FOR_RESTORE = -1
        const val SNACKBAR_DURATION = 5000
    }
}