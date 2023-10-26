package com.example.afinal.UserActivity.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.R
//import com.example.afinal.UserActivity.Fragment.LeaveInfo

//class LeaveAdapter: ListAdapter<LeaveInfo, LeaveAdapter.ViewHolder>(LeaveInfoDiffCallback()) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.leave_item, parent, false)
//        return ViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val currentItem = getItem(position)
//        holder.bind(currentItem)
//    }
//
//    fun updateData(leaveApplications: List<LeaveInfo>) {
//
//    }
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val textStartDate: TextView = itemView.findViewById(R.id.textStartDate)
//        private val textEndDate: TextView = itemView.findViewById(R.id.textEndDate)
//        private val textStatus: TextView = itemView.findViewById(R.id.textStatus)
//        private val textNumberOfDays: TextView = itemView.findViewById(R.id.textNumberOfDays)
//
//        fun bind(leaveInfo: LeaveInfo) {
//            textStartDate.text = "Start Date: ${leaveInfo.startDate}"
//            textEndDate.text = "End Date: ${leaveInfo.endDate}"
//            textStatus.text = "Status: ${leaveInfo.status}"
//            textNumberOfDays.text = "Number of Days: ${leaveInfo.numberOfDays}"
//        }
//    }
//
//    private class LeaveInfoDiffCallback : DiffUtil.ItemCallback<LeaveInfo>() {
//        override fun areItemsTheSame(oldItem: LeaveInfo, newItem: LeaveInfo): Boolean {
//            return oldItem == newItem
//        }
//
//        override fun areContentsTheSame(oldItem: LeaveInfo, newItem: LeaveInfo): Boolean {
//            return oldItem == newItem
//        }
//    }
//}
