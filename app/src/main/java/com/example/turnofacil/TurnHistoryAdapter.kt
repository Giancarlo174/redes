package com.example.turnofacil

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TurnHistoryAdapter(
    private val historyList: List<AdminActivity.TurnHistoryItem>,
    private val listener: OnTurnActionListener
) : RecyclerView.Adapter<TurnHistoryAdapter.ViewHolder>() {

    interface OnTurnActionListener {
        fun onCancelTurn(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_turn_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = historyList[position]
        
        holder.textViewTurnNumber.text = item.turnNumber
        holder.textViewTime.text = item.time
        holder.textViewStatus.text = item.status

        // Configurar colores según el estado
        when (item.status) {
            "En espera" -> {
                holder.cardStatusBadge.setCardBackgroundColor(Color.parseColor("#FFF9E6"))
                holder.textViewStatus.setTextColor(Color.parseColor("#F39C12"))
                holder.buttonCancel.visibility = View.VISIBLE
                holder.imageCheckmark.visibility = View.GONE
            }
            "Atendido" -> {
                holder.cardStatusBadge.setCardBackgroundColor(Color.parseColor("#E8F8F5"))
                holder.textViewStatus.setTextColor(Color.parseColor("#1ABC9C"))
                holder.buttonCancel.visibility = View.GONE
                holder.imageCheckmark.visibility = View.VISIBLE
            }
            "Cancelado" -> {
                holder.cardStatusBadge.setCardBackgroundColor(Color.parseColor("#FADBD8"))
                holder.textViewStatus.setTextColor(Color.parseColor("#E74C3C"))
                holder.buttonCancel.visibility = View.GONE
                holder.imageCheckmark.visibility = View.GONE
            }
        }

        // Listener para cancelar turno
        holder.buttonCancel.setOnClickListener {
            listener.onCancelTurn(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = historyList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTurnNumber: TextView = itemView.findViewById(R.id.textViewHistoryTurnNumber)
        val textViewTime: TextView = itemView.findViewById(R.id.textViewHistoryTime)
        val textViewStatus: TextView = itemView.findViewById(R.id.textViewHistoryStatus)
        val cardStatusBadge: CardView = itemView.findViewById(R.id.cardStatusBadge)
        val buttonCancel: ImageButton = itemView.findViewById(R.id.buttonCancelTurn)
        val imageCheckmark: ImageView = itemView.findViewById(R.id.imageCheckmark)
    }
}
