package com.example.afinal.UserActivity.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.afinal.R
import com.example.afinal.UserActivity.Fragment.Leave

class LeaveAdapter(private val context: Context, private val leaveList: List<Leave>) : BaseAdapter() {

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
            view = LayoutInflater.from(context).inflate(R.layout.leave_item, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        val leave = getItem(position) as Leave
        viewHolder.statusTextView.text = "Status: ${leave.status}"
        viewHolder.daysTextView.text = "Number of Days: ${leave.numberOfDays}"
        return view
    }
    private class ViewHolder(view: View) {
        val statusTextView: TextView = view.findViewById(R.id.statusTextView)
        val daysTextView: TextView = view.findViewById(R.id.daysTextView)
    }
}