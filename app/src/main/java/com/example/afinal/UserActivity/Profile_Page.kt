package com.example.afinal.UserActivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.afinal.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile_Page : AppCompatActivity() {


    private lateinit var userId: String
    private lateinit var sharedPreferences: SharedPreferences

    private val IMAGE_PICK_REQUEST = 123

    private lateinit var editBtn : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)


        editBtn = findViewById(R.id.idBtnEditData)
        editBtn.setOnClickListener {
            showEditProfilePopup()
        }


        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null) ?: ""


     fetchUserData()

    }

    private fun fetchUserData(){

        val empIDLayout: EditText = findViewById(R.id.empID)
        val empNameLayout: EditText = findViewById(R.id.empName)
        val empNumberLayout: EditText = findViewById(R.id.empNumber)
        val empExpertiseLayout: EditText = findViewById(R.id.empExpertise)
        val empEmailIdLayout: EditText = findViewById(R.id.empEmailId)
        val empDepartmentLayout: EditText = findViewById(R.id.empDepartment)
        val empDesignationLayout: EditText = findViewById(R.id.empDesignation)
        val empBloodLayout: EditText = findViewById(R.id.empBloodGroup)
        val empDOBLayout: EditText = findViewById(R.id.empDOB)
        val empDOJLayout: EditText = findViewById(R.id.empDOJ)
        val empQualificationLayout: EditText = findViewById(R.id.empQualification)
        val empCountryLayout: EditText = findViewById(R.id.empCountry)
        val empCityLayout: EditText = findViewById(R.id.empCity)
        val empStateLayout: EditText = findViewById(R.id.empState)

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)

        val call: Call<User> = apiService.getUserData(userId)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user: User? = response.body()
                    empIDLayout.setText(user?.emp_ID)
                    empNameLayout.setText(user?.emp_name)
                    empEmailIdLayout.setText(user?.email)
                    empNumberLayout.setText(user?.emp_contact_No)
                    empDepartmentLayout.setText(user?.emp_department)
                    empExpertiseLayout.setText(user?.emp_expertise)
                    empDesignationLayout.setText(user?.emp_designation)
                    empDOBLayout.setText(user?.emp_DOB)
                    empDOJLayout.setText(user?.emp_joining_date)
                    empBloodLayout.setText(user?.emp_blood_group)
                    empQualificationLayout.setText(user?.emp_qualification)
                    empCountryLayout.setText(user?.emp_country)
                    empCityLayout.setText(user?.emp_city)
                    empStateLayout.setText(user?.emp_state)
                } else {
                    Toast.makeText(this@Profile_Page, "No Data Available", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@Profile_Page, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEditProfilePopup() {
        // Create a dialog builder
        val builder = AlertDialog.Builder(this)
        val view: View = layoutInflater.inflate(R.layout.edit_profile_popup, null)

        // Access the "Save" button
        val saveButton = view.findViewById<ImageView>(R.id.saveEmpData)



        val empNameLayout: EditText = view.findViewById(R.id.empName)
        val empNumberLayout: EditText = view.findViewById(R.id.empNumber)
        val empExpertiseLayout: EditText = view.findViewById(R.id.empExpertise)
        val empEmailIdLayout: EditText = view.findViewById(R.id.empEmailId)
        val empDepartmentLayout: EditText = view.findViewById(R.id.empDepartment)
        val empDesignationLayout: EditText = view.findViewById(R.id.empDesignation)
        val empBloodLayout: EditText = view.findViewById(R.id.empBloodGroup)
        val empDOBLayout: EditText = view.findViewById(R.id.empDOB)
        val empDOJLayout: EditText = view.findViewById(R.id.empDOJ)
        val empQualificationLayout: EditText = view.findViewById(R.id.empQualification)
        val empCountryLayout: EditText = view.findViewById(R.id.empCountry)
        val empCityLayout: EditText = view.findViewById(R.id.empCity)
        val empStateLayout: EditText = view.findViewById(R.id.empState)

        fetchUserData()

        // Set a click listener for the "Save" button
        saveButton.setOnClickListener {
            // Get the updated employee information from the UI elements
            val updatedEmployeeData =UserData(
                Emp_name = empNameLayout.text.toString().takeIf { it.isNotBlank() } ?: "",
                Emp_contact_No = empNumberLayout.text.toString().takeIf { it.isNotBlank() } ?: "",
                Emp_city = empCityLayout.text.toString().takeIf { it.isNotBlank() } ?: "",
                Emp_state = empStateLayout.text.toString().takeIf { it.isNotBlank() } ?: "",
                Emp_blood_group= empBloodLayout.text.toString().takeIf { it.isNotBlank() } ?: "",
                Emp_qualification = empQualificationLayout.text.toString().takeIf { it.isNotBlank() } ?: "",
                Emp_expertise = empExpertiseLayout.text.toString().takeIf { it.isNotBlank() } ?: "",
                Emp_country = empCountryLayout.text.toString().takeIf { it.isNotBlank() } ?: ""
            )

            // Call the Retrofit API to update the employee details
            val apiService = RetrofitClient.getClient().create(ApiService::class.java)
            val call = apiService.updateUserData(userId, updatedEmployeeData)

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // Update successful
                        Toast.makeText(this@Profile_Page, "Employee details updated", Toast.LENGTH_SHORT).show()
                        // Dismiss the dialog
//                        dialog.dismiss()
                    } else {
                        // Update failed
                        Toast.makeText(this@Profile_Page, "Failed to update employee details", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // Network error
                    Toast.makeText(this@Profile_Page, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
        }


        // Create the dialog
        builder.setView(view)
        val dialog = builder.create()





        // Show the dialog
        dialog.show()
    }

}
data class User(
    @SerializedName("Emp_ID") val emp_ID: String,
    @SerializedName("Emp_name") val emp_name: String,
    @SerializedName("email") val email: String,
    @SerializedName("Emp_contact_No") val emp_contact_No: String,
    @SerializedName("Emp_department") val emp_department: String,
    @SerializedName("Emp_city") val emp_city: String,
    @SerializedName("Emp_state") val emp_state: String,
    @SerializedName("Emp_DOB") val emp_DOB: String,
    @SerializedName("Emp_joining_date") val emp_joining_date: String,
    @SerializedName("Emp_blood_group") val emp_blood_group: String,
    @SerializedName("Emp_qualification") val emp_qualification: String,
    @SerializedName("Emp_expertise") val emp_expertise: String,
    @SerializedName("Emp_country") val emp_country: String,
    @SerializedName("Emp_designation") val emp_designation: String,
//    @SerializedName("profileImage") val profileImage: ProfileImage
)

data class ProfileImage(
    @SerializedName("data") val data: String,
    @SerializedName("contentType") val contentType: String
)

data class UserData(
    val Emp_name : String,
    val Emp_contact_No: String,
    val Emp_expertise : String,
    val Emp_blood_group : String,
    val Emp_qualification : String,
    val Emp_city : String,
    val Emp_state : String,
    val Emp_country : String
)

