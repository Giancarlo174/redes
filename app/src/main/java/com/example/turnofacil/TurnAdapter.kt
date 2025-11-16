package com.example.turnofacil

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class TurnAdapter(private val turns: List<Turn>) : RecyclerView.Adapter<TurnAdapter.TurnViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TurnViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_turn, parent, false)
        return TurnViewHolder(view)
    }

    override fun onBindViewHolder(holder: TurnViewHolder, position: Int) {
        val turn = turns[position]
        holder.bind(turn)
    }

    override fun getItemCount(): Int = turns.size

    class TurnViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val turnCard: MaterialCardView = itemView.findViewById(R.id.turnCard)
        private val textViewTurnNumber: TextView = itemView.findViewById(R.id.textViewTurnItemNumber)
        private val textViewStatus: TextView = itemView.findViewById(R.id.textViewTurnItemStatus)

        fun bind(turn: Turn) {
            textViewTurnNumber.text = turn.turnNumber
            textViewStatus.text = turn.status

            // Convert dp to pixels for strokeWidth
            val density = itemView.context.resources.displayMetrics.density
            val strokeWidthPx = (1 * density).toInt()

            if (turn.isAttending) {
                turnCard.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.softgreen))
                turnCard.strokeWidth = strokeWidthPx
                turnCard.strokeColor = ContextCompat.getColor(itemView.context, R.color.softgreenborder)
                textViewStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
                textViewTurnNumber.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
            } else {
                turnCard.setCardBackgroundColor(Color.WHITE)
                turnCard.strokeWidth = strokeWidthPx
                turnCard.strokeColor = ContextCompat.getColor(itemView.context, R.color.light_grey_border)
                textViewStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.grey))
                textViewTurnNumber.setTextColor(ContextCompat.getColor(itemView.context, R.color.grey))
            }
        }
    }
}
