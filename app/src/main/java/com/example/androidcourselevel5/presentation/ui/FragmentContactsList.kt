package com.example.androidcourselevel5.presentation.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidcourselevel5.R
import com.example.androidcourselevel5.data.retrofit.model.Contact
import com.example.androidcourselevel5.databinding.FragmentContactsListBinding
import com.example.androidcourselevel5.presentation.adapter.ContactAdapter
import com.example.androidcourselevel5.presentation.adapter.ExtendedElementClickListener
import com.example.androidcourselevel5.presentation.ui.utils.gone
import com.example.androidcourselevel5.presentation.ui.utils.visible
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

        sharedViewModel.contactList.observe(requireActivity()){
            createRecyclerViewAdapter(multiSelectState = false, selectedContacts = null)
            recyclerAdapter.submitList(it)
        }

        contactListViewModel.contactListResultSuccess.observe(requireActivity()) { contactList ->
            if (contactIdForRestore != DEFAULT_ID_FOR_RESTORE) {
                createErrorSnackbar(getString(R.string.restore_snackbar_text_message), RESTORE_CONTACT).show()
            }
            if (contactList.isNotEmpty()) {
                binding.emptyContactsContainer.gone()
                binding.recyclerViewContacts.visible()
                Log.d("TAG", "ContactList -> UPDATE NOW Shared ViewModel")
                sharedViewModel.updateUserContactList(contactList)

            } else {
                binding.recyclerViewContacts.gone()
                binding.emptyContactsContainer.visible()
            }
        }

        contactListViewModel.contactListResultError.observe(requireActivity()) {
            Log.d("TAG", "ContactList -> ListResultError")
            createErrorSnackbar(requireActivity().getString(R.string.request_error_toast_message,
                it.body()?.message, it.body()?.code), GET_CONTACTS_LIST).show()
        }

        contactListViewModel.contactListResultException.observe(requireActivity()){ exception ->
            // need for showing snackbar with deleting after rotate
            if (!contactListViewModel.isSuccessDeletion()) {
                contactListViewModel.showContactDeletingSnackbar()
            } else if (exception) {
                Log.d("TAG", "ContactList -> ListResultException")
                createErrorSnackbar(getString(R.string.connection_exception_snackbar_message), GET_CONTACTS_LIST).show()
            }
        }

        contactListViewModel.contactListResultTimeout.observe(requireActivity()){ isTimeout ->
            if (isTimeout) {
                Log.d("TAG", "ContactList -> ListResultTimeout")
                createErrorSnackbar(getString(R.string.connection_timeout_snackbar_message), GET_CONTACTS_LIST).show()
            }
        }

        contactListViewModel.requestProgressBar.observe(requireActivity()) { visibility ->
            binding.contactListProgressBar.visibleIf(visibility)
        }

        contactListViewModel.deleteContactResultError.observe(requireActivity()) {
            Log.d("TAG", "ContactList -> deleteResultError")
            createErrorSnackbar(requireActivity().getString(R.string.request_error_toast_message,
                it.message(), it.code()), DELETE_CONTACT).show()
        }

        contactListViewModel.deleteContactResultException.observe(requireActivity()){ exception ->
            Log.d("TAG", "exception -> $exception")
            if (exception) {
                Log.d("TAG", "ContactList -> deleteResultException")
                createErrorSnackbar(getString(R.string.connection_exception_snackbar_message), DELETE_CONTACT).show()
            }
        }

        contactListViewModel.deleteContactResultTimeout.observe(requireActivity()){ isTimeout ->
            if (isTimeout) {
                Log.d("TAG", "ContactList -> deleteResultTimeout")
                createErrorSnackbar(getString(R.string.connection_timeout_snackbar_message), DELETE_CONTACT).show()
            }
        }


        sharedViewModel.addContactResultError.observe(requireActivity()) {
            createErrorSnackbar(requireActivity().getString(R.string.request_error_toast_message,
                it.message(), it.code()), RESTORE_CONTACT).show()
        }

        sharedViewModel.addContactResultException.observe(requireActivity()) { exception ->
            if (exception)
                createErrorSnackbar(getString(R.string.connection_exception_snackbar_message),RESTORE_CONTACT).show()
        }

        sharedViewModel.addContactResultTimeout.observe(requireActivity()){ isTimeout ->
            if (isTimeout)
                createErrorSnackbar(getString(R.string.connection_timeout_snackbar_message), RESTORE_CONTACT).show()
        }

    }

    private fun getCurrentUserContactsList() {
        if (sharedViewModel.tabLayoutVisibility.value == true) {
            contactListViewModel.getUserContactsList()
        }

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


    private fun createRecyclerViewAdapter(multiSelectState: Boolean, selectedContacts:List<Int>?) {
        binding.imgDeleteManyContacts.visibleIf(multiSelectState)
        recyclerAdapter = ContactAdapter(this,
            multiSelectState = multiSelectState,
            selectedContactsForDeleting = selectedContacts)
        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewContacts.adapter = recyclerAdapter

    }

    override fun onElementProfileClick(contact: Contact) {
        // FOR go to PROFILE
        Toast.makeText(requireActivity(), "GO to PROFILE [id=${contact.id}]", Toast.LENGTH_SHORT).show()
    }

    override fun onElementLongClick(contactId: Int) {
        Toast.makeText(requireActivity(), "MULTISELECT state ON", Toast.LENGTH_SHORT).show()
    }

    override fun onElementChecked(checkBoxState: Boolean, contactId: Int) {
        TODO("Not yet implemented")
    }

    override fun onElementClickAction(contact: Contact) {
        // FOR DELETE
        contactIdForRestore = contact.id
        contactListViewModel.saveContactIdForDelete(contactID = contact.id)
        contactListViewModel.deleteFromContacts()
    }

    private fun createErrorSnackbar(message: String, action: Int): Snackbar {
        return Snackbar.make(binding.snackbarCoordinator, message, Snackbar.LENGTH_INDEFINITE)
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

    companion object {
        const val GET_CONTACTS_LIST = 1
        const val DELETE_CONTACT = 2
        const val RESTORE_CONTACT = 3
        const val DEFAULT_ID_FOR_RESTORE = -1

    }
}