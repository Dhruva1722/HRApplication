package com.example.afinal.UserActivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.afinal.R
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile_Page : AppCompatActivity() {


    private lateinit var userId: String
    private lateinit var sharedPreferences: SharedPreferences

    private val IMAGE_PICK_REQUEST = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)


        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null) ?: ""

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

        val imageBtn: ImageView = findViewById(R.id.uploadImageBtn)

        imageBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_REQUEST)
        }

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        val call: Call<User> = apiService.getUserData(userId)


        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user: User? = response.body()
                    empIDLayout.setText(user?.emp_ID)
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
}
data class User(
    @SerializedName("password") val password: String,
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
    @SerializedName("profileImage") val profileImage: ProfileImage
)

data class ProfileImage(
    @SerializedName("data") val data: String,
    @SerializedName("contentType") val contentType: String
)

data class UserData(
    val userId : String,
    val  Emp_name : String,
    val Emp_contact_No: String,
    val Emp_expertise : String,
    val Emp_blood_group : String,
    val Emp_qualification : String,
    val Emp_city : String,
    val Emp_state : String,
    val Emp_country : String
)

