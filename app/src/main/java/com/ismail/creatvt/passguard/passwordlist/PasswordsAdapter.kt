package com.ismail.creatvt.passguard.passwordlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.ismail.creatvt.passguard.R
import com.ismail.creatvt.passguard.model.Password
import com.ismail.creatvt.passguard.helpers.setWebsiteIcon

class PasswordsAdapter(
    passwords: LiveData<List<Password>>,
    owner: LifecycleOwner,
    private var onPasswordClick: (Password) -> Unit
) : RecyclerView.Adapter<PasswordsAdapter.PasswordsViewHolder>() {

    private val differ = AsyncListDiffer(this, PasswordsDiffCallback())

    init {
        passwords.observe(owner) {
            differ.submitList(it)
        }
    }

    class PasswordsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordsViewHolder {
        return PasswordsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.password_list_item,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: PasswordsViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<MaterialCardView>(R.id.password_item_card).setOnClickListener {
                onPasswordClick(differ.currentList[holder.adapterPosition])
            }
            findViewById<ImageView>(R.id.websiteIcon).setWebsiteIcon(
                differ.currentList[holder.adapterPosition].website
            )
            findViewById<TextView>(R.id.websiteText).apply {
                text = differ.currentList[position].website.websiteName
                isSelected = true
            }
            findViewById<TextView>(R.id.usernameText).apply {
                text = differ.currentList[position].username
                isSelected = true
            }
        }
    }

    override fun getItemCount() = differ.currentList.size

}