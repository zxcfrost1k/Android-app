package com.example.guessplayer.chapter_tools

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guessplayer.R

class FootballClubAdapter(private val footballClubList: List<FootballClub>):
    RecyclerView.Adapter<FootballClubAdapter.FootballClubViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FootballClubViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fc_recyclerview, parent, false)
        return FootballClubViewHolder(view)

    }

    override fun onBindViewHolder(
        holder: FootballClubViewHolder,
        position: Int
    ) {
        val footballClub = footballClubList[position]
        holder.footballClubImage.setImageResource(footballClub.clubImage!!)
        holder.transferYearTV.text = footballClub.transferYear
    }

    override fun getItemCount(): Int {
        return footballClubList.size
    }

    class FootballClubViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val footballClubImage: ImageView = itemView.findViewById(R.id.imageview)
        val transferYearTV: TextView = itemView.findViewById(R.id.textView)
    }
}