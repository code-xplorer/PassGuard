package com.ismail.creatvt.passguard.passwordlist

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import com.ismail.creatvt.passguard.R
import com.ismail.creatvt.passguard.helpers.dp
import com.ismail.creatvt.passguard.manager.password.PasswordManager

class MoveItemCallback(private val passwordManager: PasswordManager) : ItemTouchHelper.SimpleCallback(UP or DOWN, 0) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        passwordManager.movePasswords(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            (viewHolder as? PasswordsAdapter.PasswordsViewHolder)?.itemView?.findViewById<CardView>(R.id.password_item_card)?.cardElevation = viewHolder?.itemView?.context?.dp(2f)?:20f
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        (viewHolder as? PasswordsAdapter.PasswordsViewHolder)?.itemView?.findViewById<CardView>(R.id.password_item_card)?.cardElevation = viewHolder?.itemView?.context?.dp(0f)?:0f
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }
}