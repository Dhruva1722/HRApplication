package com.example.afinal.UserActivity.Fragment

//import com.example.afinal.UserActivity.User
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.example.afinal.R
import com.example.afinal.UserActivity.Adapter.LeaveAdapter
import com.example.afinal.UserActivity.ApiService
import com.example.afinal.UserActivity.RetrofitClient
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.textfield.TextInputLayout
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class AttendanceFragment : Fragment() {

    private lateinit var dateTimeTextView: TextView
    private lateinit var daymonthTextView: TextView
    private lateinit var username: TextView
    private lateinit var userStatusTime: TextView
    private lateinit var onsiteIcon: ImageView
    private lateinit var inofficeIcon: ImageView
    private lateinit var presentBtn: LinearLayout
    private lateinit var absentBtn: LinearLayout

    private lateinit var applyBtn : Button
    private lateinit var leaveadapter: ArrayAdapter<String>

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId: String
    private var isPunchedIn: Boolean = false
    private var isDateChanged: Boolean = false

    private val ATTENDANCE_STATUS_KEY = "attendance_status"
    private val LAST_ATTENDANCE_DATE_KEY = "last_attendance_date"

    private var isDatePickerDialogVisible = false
    private var startDatePicker: MaterialDatePicker<Long>? = null

    val apiService = RetrofitClient.getClient().create(ApiService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_attendance, container, false)


        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null) ?: ""
        isPunchedIn = sharedPreferences.getBoolean("isPunchedIn", false)

        val lastAttendanceDate = sharedPreferences.getString(LAST_ATTENDANCE_DATE_KEY, "")

        dateTimeTextView = view.findViewById(R.id.dateTime)
        daymonthTextView = view.findViewById(R.id.dayMonth)
        userStatusTime = view.findViewById(R.id.userTimeOfAttendence)
        presentBtn = view.findViewById(R.id.presentBtn)
        absentBtn = view.findViewById(R.id.absentBtn)
        username = view.findViewById(R.id.Username)

        val listOfLeave = view.findViewById<ListView>(R.id.leaveStatusList)
        leaveadapter = ArrayAdapter(requireContext(), R.layout.leavestatus)
        listOfLeave.adapter = leaveadapter

        fetchLeaveApplications(userId)
        applyBtn = view.findViewById(R.id.applyForLeaveBtn)

        applyBtn.setOnClickListener {
         LeaveApplicationDialog()
        }



        val userEmail = sharedPreferences.getString("userEmail", "")
        val parts = userEmail?.split("@")
        if (parts?.size == 2) {
            val name = parts[0]
            username.text = "Hello, $name!!"
        }

        onsiteIcon = view.findViewById(R.id.onsiteIcon)
        inofficeIcon = view.findViewById(R.id.inofficeIcon)

        val currentDateTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        dateTimeTextView.text = currentDateTime


        val currentDayMonth = SimpleDateFormat("EEEE d,MMM", Locale.getDefault()).format(Date())
        daymonthTextView.text = currentDayMonth

        val savedStatus = sharedPreferences.getString(ATTENDANCE_STATUS_KEY, "")
        if (savedStatus == "On-Site") {
            updateUI(savedStatus)
        } else {
            updateUI("In-Office")
            updateUI(savedStatus ?: "")
        }

        isDateChanged = hasDateChanged(lastAttendanceDate)

        if (isPunchedIn) {
            if (isDateChanged) {
                presentBtn.isEnabled = true
                absentBtn.isEnabled = true
            } else {
                presentBtn.isEnabled = false
                absentBtn.isEnabled = false
            }
        }

        presentBtn.setOnClickListener {
            punchIn()
        }
        absentBtn.setOnClickListener {
            punchOut()
        }

        return view
    }
    private fun showDatePickerDialog() {
        if (!isDatePickerDialogVisible) {
            // Create and configure the MaterialDatePicker
            startDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select a date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build()

            // Set an event listener for when the dialog is dismissed
            startDatePicker?.addOnDismissListener {
                isDatePickerDialogVisible = false
                // Handle any dismissal-related logic
            }

            // Show the MaterialDatePicker
            startDatePicker?.show(childFragmentManager, "StartDatePicker")
            isDatePickerDialogVisible = true
        }
    }
    private fun LeaveApplicationDialog() {

        val builder = AlertDialog.Builder(requireContext())

        val view = layoutInflater.inflate(R.layout.leave_item, null)

        val textStartDate = view.findViewById<TextInputLayout>(R.id.textStartDate)
        val textEndDate = view.findViewById<TextInputLayout>(R.id.textEndDate)
        val applyBtn = view.findViewById<Button>(R.id.applyBtn)

        textStartDate.setOnClickListener {
            showDatePickerDialog()
        }
        textEndDate.setOnClickListener {
            showDatePickerDialog()
        }

        applyBtn.setOnClickListener {

        val startDateStr = textStartDate.editText!!.text.toString()
        val endDateStr = textEndDate.editText!!.text.toString()

            Log.d("LeaveApplicationDialog", "startDateStr: $startDateStr")
            Log.d("LeaveApplicationDialog", "endDateStr: $endDateStr")

            if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val startDate = convertToUnixTimestamp(startDateStr)
            val endDate = convertToUnixTimestamp(endDateStr)

            Log.d("LeaveApplicationDialog", "startDate: $startDate")
            Log.d("LeaveApplicationDialog", "endDate: $endDate")

            if (startDate != null && endDate != null) {

                val startDate = convertToISO8601(startDate)
                val endDate = convertToISO8601(endDate)

                val leaveRequest = LeaveRequest(startDate, endDate)
                Log.d("LeaveApplicationDialog", "LeaveRequest: $leaveRequest")
                val call = apiService.postLeaveRequest(userId, leaveRequest)

                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Leave request submitted successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Failed to submit leave request", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(requireContext(), "Invalid date format", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
    }
    private fun convertToUnixTimestamp(dateStr: String): Long? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return try {
            val date = dateFormat.parse(dateStr)
            date?.time
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }
    private fun convertToISO8601(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(Date(timestamp))
    }
    private fun hasDateChanged(lastAttendanceDate: String?): Boolean {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return currentDate != lastAttendanceDate
    }

    private fun punchIn() {
        val isPunchIn = true

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)

        val attendanceData = AttendanceData(userId, isPunchIn)

        val call = apiService.saveAttendance(attendanceData)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Punched in successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to punch in", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
            }
        })
        saveAttendanceStatus("On-Site")
        saveLastAttendanceDate()

    }

    private fun punchOut() {
        val isPunchIn = false

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)

        val attendanceData = AttendanceData(userId, isPunchIn)

        val call = apiService.saveAttendance(attendanceData)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Punched out successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to punch out", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
            }
        })
        saveAttendanceStatus("In-Office")
        saveLastAttendanceDate()
    }
    private fun saveLastAttendanceDate() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val editor = sharedPreferences.edit()
        editor.putString(LAST_ATTENDANCE_DATE_KEY, currentDate)
        editor.apply()
    }

    private fun saveAttendanceStatus(status: String) {
        val editor = sharedPreferences.edit()
        editor.putString(ATTENDANCE_STATUS_KEY, status)
        editor.apply()
        updateUI(status)
    }

    private fun updateUI(status: String) {
        val statusText = if (status == "On-Site") {
            onsiteIcon.visibility = View.VISIBLE
            inofficeIcon.visibility = View.GONE
            "On-Site"
        } else {
            onsiteIcon.visibility = View.GONE
            inofficeIcon.visibility = View.VISIBLE
            "In-Office"
        }
        val message =
            "You marked $statusText at ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())}."
        userStatusTime.text = message
    }

    private fun fetchLeaveApplications(userId: String) {
        val call = apiService.getLeaveApplications(userId)
        call.enqueue(object : Callback<LeaveResponse> {
            override fun onResponse(call: Call<LeaveResponse>, response: Response<LeaveResponse>) {
                if (response.isSuccessful) {
                    val leaveResponse = response.body()
                    if (leaveResponse != null) {
                        val totalNumberOfDays = leaveResponse.totalNumberOfDays
                        val leaveApplications = leaveResponse.leaveApplications

                        val totalNumberOfDaysTextView = view!!.findViewById<TextView>(R.id.leaveBalanceTv)
                        totalNumberOfDaysTextView.text = "Total Number of Days: $totalNumberOfDays"

                        val leaveListView = view!!.findViewById<ListView>(R.id.leaveStatusList)
                        val leaveAdapter = LeaveAdapter(requireContext(), leaveApplications)
                        leaveListView.adapter = leaveAdapter
                    } else {
                        Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        404 -> "No Data Available for Leave"
                        else -> "API request failed"
                    }
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(call: Call<LeaveResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}

data class AttendanceData(
    val userId: String,
    val isPunchIn: Boolean
)

data class LeaveRequest(
    val startDate: String,
    val endDate: String
)

data class LeaveResponse(
    val totalNumberOfDays: Int,
    val leaveApplications: List<LeaveInfo>
)

data class LeaveInfo(
    val startDate: String,
    val endDate: String,
    val status: String,
    val numberOfDays: Int
)