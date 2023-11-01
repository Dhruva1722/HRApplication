package com.example.afinal.UserActivity.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.afinal.R
import com.example.afinal.UserActivity.Fragment.LeaveInfo



class LeaveAdapter(private val context: Context, private val leaveList: List<LeaveInfo>) : BaseAdapter() {

    override fun getCount(): Int {
        return leaveList.size
    }

    override fun getItem(position: Int): Any {
        return leaveList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.leavestatus, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val leave = leaveList[position]

        viewHolder.numOfDays.text = " ${leave.numberOfDays}"
        viewHolder.status.text = " ${leave.status}"
        viewHolder.startDate.text = " ${leave.startDate}"
        viewHolder.endDate.text = " ${leave.endDate}"

        // Handle button click here if needed
        viewHolder.cancelLeaveBtn.setOnClickListener {
            // Handle cancel button click
        }
        return view
    }

    private class ViewHolder(view: View) {
        val numOfDays: TextView = view.findViewById(R.id.numOfDays)
        val status: TextView = view.findViewById(R.id.status)
        val startDate: TextView = view.findViewById(R.id.startDate)
        val endDate: TextView = view.findViewById(R.id.endDate)
        val cancelLeaveBtn: ImageView = view.findViewById(R.id.cancelLeaveBtn)
    }
}





