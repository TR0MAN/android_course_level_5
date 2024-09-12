package com.example.androidcourselevel5.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidcourselevel5.R
import com.example.androidcourselevel5.data.retrofit.model.Contact
import com.example.androidcourselevel5.databinding.ElementRecyclerViewBinding
import com.example.androidcourselevel5.presentation.ui.utils.gone
import com.example.androidcourselevel5.presentation.ui.utils.visible

class ContactAdapter(
    private val clickListener: ClickListener,
    private val multiSelectState: Boolean,
    private val selectedContacts: List<Int>?) :
    ListAdapter<Contact, ContactAdapter.ContactViewHolder>(ContactDiffUtilCallback()) {

    inner class ContactViewHolder(
        val binding: ElementRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(contact: Contact) {
                binding.tvContactName.text = contact.name.toString()
                binding.tvContactCareer.text = contact.career.toString()

                Glide.with(binding.imgContactAvatar.context)
                    .load(contact.image)
                    .circleCrop()
                    .placeholder(R.drawable.default_avatar)
                    .into(binding.imgContactAvatar)

                when (multiSelectState) {
                    true -> {
                        binding.checkboxForDelete.visible()
                        binding.root.setBackgroundResource(R.drawable.element_view_style_gray)
                        selectedContacts?.let {
                            binding.checkboxForDelete.isChecked = selectedContacts.contains(contact.id)
                        }
                    }
                    false -> {
                        binding.imgContactDelete.visible()
                    }
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ElementRecyclerViewBinding.inflate(inflater, parent, false)

        when (multiSelectState) {
            true -> {
                binding.root.setOnClickListener {
                    val contact = it.tag as Contact
                    var checkBoxState = binding.checkboxForDelete.isChecked
                    if (!checkBoxState) {
                        binding.checkboxForDelete.isChecked = true
                        checkBoxState = true
                    } else {
                        binding.checkboxForDelete.isChecked = false
                        checkBoxState = false
                    }
                    (clickListener as ExtendedElementClickListener).onElementChecked(checkBoxState, contact.id)
                }
            }
            false -> {
                binding.imgContactDelete.setOnClickListener {
                    val contact = it.tag as Contact
                    (clickListener as ExtendedElementClickListener).onElementClickAction(contact)
                }
                binding.root.setOnClickListener {
                    val contact = it.tag as Contact
                    (clickListener as ExtendedElementClickListener).onElementProfileClick(contact)
                }
            }
        }

        if (!multiSelectState) {
            binding.root.setOnLongClickListener {
                val contact = it.tag as Contact
                (clickListener as ExtendedElementClickListener).onElementLongClick(contact.id)
                return@setOnLongClickListener true
            }
        }

        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
        with(holder.binding) {
            imgContactDelete.tag = getItem(position)
            root.tag = getItem(position)
        }
    }
}
