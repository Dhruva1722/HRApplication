package com.example.afinal.UserActivity.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.R
import com.example.afinal.UserActivity.Employee

class EmployeeAdapter: RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    private var employees: List<Employee> = emptyList()

    fun submitList(newList: List<Employee>) {
        employees = newList
        notifyDataSetChanged()
        Log.d("EmployeeAdapter", "List size:List type: ${employees.javaClass.simpleName}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.manager_list, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(employees[position])
    }

    override fun getItemCount(): Int {
        return employees.size
    }

    class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private val textViewContact: TextView = itemView.findViewById(R.id.textViewContactno)
        private val textViewDesignation: TextView = itemView.findViewById(R.id.textViewDesignation)

        fun bind(employee: Employee) {
            textViewName.text = employee.Emp_name
            textViewContact.text = employee.Emp_contact_No
            textViewDesignation.text = employee.Emp_designation
        }
    }
}