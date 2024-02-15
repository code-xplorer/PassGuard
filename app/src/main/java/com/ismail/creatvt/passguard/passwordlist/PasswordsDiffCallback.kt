package com.ismail.creatvt.passguard.passwordlist

import androidx.recyclerview.widget.DiffUtil
import com.ismail.creatvt.passguard.model.Password

class PasswordsDiffCallback : DiffUtil.ItemCallback<Password>() {

    override fun areItemsTheSame(oldItem: Password, newItem: Password): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Password, newItem: Password): Boolean {
        return oldItem.username == newItem.username &&
                oldItem.website == newItem.website
    }

}