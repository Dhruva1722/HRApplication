package com.example.afinal.UserActivity.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.afinal.R
import com.example.afinal.UserActivity.ApiService
import com.example.afinal.UserActivity.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class EventFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var apiService: ApiService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view =  inflater.inflate(R.layout.fragment_event, container, false)


      listView = view.findViewById(R.id.eventListView)

        fetchEvents()

        return view
    }
    private fun fetchEvents() {
        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        val call = apiService.getEvents()

        val originalDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss") // Change this to match the API's date format
        val desiredDateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm a") // Change this to your desired date format

        call.enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    val events = response.body()
                    if (events != null) {
                        // Create a custom adapter that uses the custom layout
                        val adapter = object : ArrayAdapter<Event>(
                            requireContext(),
                            R.layout.list_item_event,  // Custom layout for each item
                            R.id.textViewTitle,        // ID of the title TextView in the custom layout
                            events
                        ) {
                            override fun getView(
                                position: Int,
                                convertView: View?,
                                parent: ViewGroup
                            ): View {
                                val itemView = super.getView(position, convertView, parent)
                                val event = getItem(position)

                                // Set the title and formatted date in the custom layout
                                val titleTextView = itemView.findViewById<TextView>(R.id.textViewTitle)
                                val dateTextView = itemView.findViewById<TextView>(R.id.textViewDate)

                                titleTextView.text = event?.title

                                // Parse and format the date
                                try {
                                    val originalDate = originalDateFormat.parse(event?.date)
                                    val formattedDate = desiredDateFormat.format(originalDate)
                                    dateTextView.text = formattedDate
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    dateTextView.text = "Date Error"
                                }

                                return itemView
                            }
                        }

                        listView.adapter = adapter
                    }
                } else {
                    Toast.makeText(requireContext(), "Data Not Find", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }


//    private fun fetchEvents() {
//        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
//        val call = apiService.getEvents()
//
//        call.enqueue(object : Callback<List<Event>> {
//            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
//                if (response.isSuccessful) {
//                    val events = response.body()
//                    if (events != null) {
//                        val eventTitles = events.map { it.title }.toTypedArray()
//                        val adapter = ArrayAdapter(
//                            requireContext(),
//                            android.R.layout.simple_list_item_1,
//                            eventTitles
//                        )
//                        listView.adapter = adapter
//                    }
//                } else {
//                    Toast.makeText(requireContext(), "Data Not Find", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
//                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
}
data class Event(val title: String, val date: String)