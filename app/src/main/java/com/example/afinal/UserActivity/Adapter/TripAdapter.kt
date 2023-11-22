package com.example.afinal.UserActivity.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.afinal.R
import com.example.afinal.UserActivity.TripInfo

class TripAdapter(private val context: Context, private val tripList: List<TripInfo>) : BaseAdapter() {

    override fun getCount(): Int = tripList.size

    override fun getItem(position: Int): Any = tripList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val trip = tripList[position]

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.tripdetail, null)

        val tripDateTextView: TextView = itemView.findViewById(R.id.tripDate)
        val tripStartTextView : TextView = itemView.findViewById(R.id.tripStart)
        val tripEndTextView : TextView = itemView.findViewById(R.id.tripEnd)
        val tripDistanceTextView : TextView = itemView.findViewById(R.id.tripDistance)
        tripDateTextView.text = trip.timestamp
        tripStartTextView.text = trip.startPoint.toString()
        tripEndTextView.text = trip.endPoint.toString()
        tripDistanceTextView.text = trip.distance.toString()

        return itemView
    }
}