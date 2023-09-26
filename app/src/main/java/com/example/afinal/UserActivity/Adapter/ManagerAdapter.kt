package com.example.afinal.UserActivity.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.afinal.R
import com.example.afinal.UserActivity.Manager

class ManagerAdapter (context: Context, resource: Int, objects: List<Manager>) :
    ArrayAdapter<Manager>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val listItemView = inflater.inflate(R.layout.manager_list, parent, false)

        val manager = getItem(position)

        val textViewName = listItemView.findViewById<TextView>(R.id.textViewName)
        val textViewEmail = listItemView.findViewById<TextView>(R.id.textViewEmail)
        val textViewContactNo = listItemView.findViewById<TextView>(R.id.textViewContactno)
        val textViewDepartment = listItemView.findViewById<TextView>(R.id.textViewDepartment)

        // Set data from the Manager object to the views
        textViewName.text = manager?.name ?: "Unknown"
        textViewEmail.text = manager?.email ?: "Email not available"
        textViewContactNo.text = manager?.contact_no ?: "...."
        textViewDepartment.text = manager?.department ?: "...."


        return listItemView
    }
}