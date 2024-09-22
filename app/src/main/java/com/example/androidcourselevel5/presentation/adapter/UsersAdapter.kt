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

class UsersAdapter(
    private val clickListener: ClickListener,
    private var usersInContactList: List<Int>
) : ListAdapter<Contact, UsersAdapter.UserViewHolder>(ContactDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ElementRecyclerViewBinding.inflate(inflater, parent, false)

        binding.imgContactAddToContacts.setOnClickListener {
            val contact = it.tag as Contact
            (clickListener as ElementClickListener).onElementClickAction(contact)
        }

        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
        with(holder.binding) {
            imgContactAddToContacts.tag = getItem(position)
        }
    }

    inner class UserViewHolder(
        val binding: ElementRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.tvContactName.text = contact.name.toString()
            binding.tvContactCareer.text = contact.career.toString()

            Glide.with(binding.imgContactAvatar.context)
                .load(contact.image)
                .circleCrop()
                .placeholder(R.drawable.default_avatar)
                .into(binding.imgContactAvatar)

            if (usersInContactList.isNotEmpty()) {
                if (usersInContactList.contains(contact.id)) {
                    binding.imgContactInList.visible()
                    binding.imgContactAddToContacts.gone()
                } else {
                    binding.imgContactInList.gone()
                    binding.imgContactAddToContacts.visible()
                }
            } else {
                binding.imgContactInList.gone()
                binding.imgContactAddToContacts.visible()
            }
        }
    }

    // TODO - DELETE AFTER TEST
//    fun setUserList(list: List<Int>){
//        usersInContactList.addAll(list)
//        notifyDataSetChanged()
//    }
}