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
import com.example.androidcourselevel5.presentation.ui.utils.gone
import com.example.androidcourselevel5.presentation.ui.utils.visible
import com.example.androidcourselevel5.presentation.ui.utils.visibleIf
import com.example.androidcourselevel5.presentation.viewmodel.AddContactViewModel
import com.example.androidcourselevel5.presentation.viewmodel.SharedViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
        Log.d("TAG", "ADD Contact -> onCreateView")
        binding = FragmentAddContactBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG", "ADD Contact -> onViewCreated")

        setObservers()
        setListeners()
        getListWithAllUsers()
    }

    private fun setObservers() {
        addContactViewModel.getUsersListResultSuccess.observe(requireActivity()) { allUsersList ->
            createRecyclerViewAdapter()
            recyclerAdapter.submitList(allUsersList)
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

        // IF ADD OK, THEN ??? i have updated contact lis here
        // maybe i need to refresh USERs LIST with already added contact
        // DON'T NEED THIS OBSERVER
//        sharedViewModel.contactList.observe(requireActivity()) {
//            // TODO - PROBLEM, need to resolve it
//            Log.d("TAG", "ADD Contact -> UPDATE NOW Shared ViewModel [counter = $counter]")
//            sharedViewModel.updateUserContactList(it)
//        }

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

    }


    private fun setListeners() {

        binding.toolbarAddContact.imgBackAddContact.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbarAddContact.imgOpenSearchFiled.setOnClickListener {

//            viewModel.isActiveSearchAddContact.value = true
        }

        binding.toolbarAddContact.imgCloseSearchFiled.setOnClickListener {

//            viewModel.isActiveSearchAddContact.value = false
        }

        binding.toolbarAddContact.edSearchAddContact.doOnTextChanged { text, _, _, _ ->
            // TODO
        }

        // listener for getting contact id for adding new contact to contact list
        val resultOfAddingContact =
            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Int>(Const.RESULT_KEY)

        // observer for adding new contact to contact list
        resultOfAddingContact?.observe(viewLifecycleOwner) { id ->
            if (id != null) {
                sharedViewModel.addContact(id)
            }
        }

    }
    private fun getListWithAllUsers() {
        addContactViewModel.getListWithAllUsers()
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

    private fun createRecyclerViewAdapter(userInContactList: List<Int> = emptyList()) {
        recyclerAdapter = UsersAdapter(this, userInContactList)
        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerViewContacts.adapter = recyclerAdapter
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

    companion object {
        const val GET_CONTACTS = 1
        const val ADD_CONTACT = 2
    }

    override fun onStart() {
        super.onStart()
        Log.d("TAG", "ADD Contact -> onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("TAG", "ADD Contact -> onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("TAG", "ADD Contact -> onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG", "ADD Contact -> onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("TAG", "ADD Contact -> onDestroyView")
    }
}
