package com.example.afinal.UserActivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.afinal.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile_Page : AppCompatActivity() {


    private lateinit var userId: String
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var empIDLayout : EditText
    private lateinit var empNameLayout: EditText
    private lateinit var empNumberLayout: EditText
    private lateinit var empExpertiseLayout: EditText
    private lateinit var empEmailIdLayout: EditText
    private lateinit var empDepartmentLayout: EditText
    private lateinit var empDesignationLayout: EditText
    private lateinit var empBloodLayout: EditText
    private lateinit var empDOBLayout: EditText
    private lateinit var empDOJLayout: EditText
    private lateinit var empQualificationLayout: EditText
    private lateinit var empCountryLayout: EditText
    private lateinit var empCityLayout: EditText
    private lateinit var empStateLayout: EditText

    private lateinit var uploadBtn : ImageView
    private lateinit var profileImage: ImageView
    private val IMAGE_PICK_REQUEST = 123

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private lateinit var editBtn : ImageView


    val apiService = RetrofitClient.getClient().create(ApiService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)


        empIDLayout = findViewById(R.id.empID)
        empNameLayout = findViewById(R.id.empName)
        empNumberLayout = findViewById(R.id.empNumber)
        empExpertiseLayout = findViewById(R.id.empExpertise)
        empEmailIdLayout= findViewById(R.id.empEmailId)
        empDepartmentLayout = findViewById(R.id.empDepartment)
        empDesignationLayout = findViewById(R.id.empDesignation)
        empBloodLayout = findViewById(R.id.empBloodGroup)
        empDOBLayout = findViewById(R.id.empDOB)
        empDOJLayout= findViewById(R.id.empDOJ)
        empQualificationLayout = findViewById(R.id.empQualification)
        empCountryLayout = findViewById(R.id.empCountry)
        empCityLayout= findViewById(R.id.empCity)
        empStateLayout = findViewById(R.id.empState)



        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null) ?: ""


        fetchUserData()

        editBtn = findViewById(R.id.idBtnEditData)
        editBtn.setOnClickListener {
            // Check if user is initialized
            showEditProfilePopup()
        }



    }


    private fun fetchUserData(){



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

        val saveButton = view.findViewById<ImageView>(R.id.saveEmpData)

        empIDLayout = view.findViewById(R.id.empID)
        empNameLayout =  view.findViewById(R.id.empName)
        empNumberLayout =  view.findViewById(R.id.empNumber)
        empExpertiseLayout =  view.findViewById(R.id.empExpertise)
        empEmailIdLayout=  view.findViewById(R.id.empEmailId)
        empDepartmentLayout =  view.findViewById(R.id.empDepartment)
        empDesignationLayout =  view.findViewById(R.id.empDesignation)
        empBloodLayout =  view.findViewById(R.id.empBloodGroup)
        empDOBLayout =  view.findViewById(R.id.empDOB)
        empDOJLayout=  view.findViewById(R.id.empDOJ)
        empQualificationLayout =  view.findViewById(R.id.empQualification)
        empCountryLayout =  view.findViewById(R.id.empCountry)
        empCityLayout=  view.findViewById(R.id.empCity)
        empStateLayout =  view.findViewById(R.id.empState)

        profileImage = findViewById(R.id.profileImage)
        uploadBtn = findViewById(R.id.uploadImageBtn)
        uploadBtn.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(galleryIntent)
        }


        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                // Handle the selected image URI
                handleImageSelection(selectedImageUri)
            }
        }

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

        saveButton.setOnClickListener {

            val updatedEmployeeData =UserData(
                Emp_name = empNameLayout.text.toString(),
                Emp_contact_No = empNumberLayout.text.toString(),
                Emp_city = empCityLayout.text.toString(),
                Emp_state = empStateLayout.text.toString(),
                Emp_blood_group= empBloodLayout.text.toString(),
                Emp_qualification = empQualificationLayout.text.toString(),
                Emp_expertise = empExpertiseLayout.text.toString(),
                Emp_country = empCountryLayout.text.toString()
            )

            // Call the Retrofit API to update the employee details
            val apiService = RetrofitClient.getClient().create(ApiService::class.java)
            val call = apiService.updateUserData(userId, updatedEmployeeData)

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // Update successful
                        Toast.makeText(this@Profile_Page, "Employee details updated", Toast.LENGTH_SHORT).show()
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
        dialog.dismiss()
    }
    private fun handleImageSelection(imageUri: Uri?) {
        profileImage.setImageURI(imageUri)
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
    @SerializedName("profileImage") val profileImage: ProfileImage
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

