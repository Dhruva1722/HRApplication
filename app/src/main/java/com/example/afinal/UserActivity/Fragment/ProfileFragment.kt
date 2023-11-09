package com.example.afinal.UserActivity.Fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.example.afinal.R
import com.example.afinal.UserActivity.ApiService
import com.example.afinal.UserActivity.RetrofitClient
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment() {

    private lateinit var userId: String
    private lateinit var sharedPreferences: SharedPreferences

    private val IMAGE_PICK_REQUEST = 123
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view = inflater.inflate(R.layout.fragment_profile, container, false)


       sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null) ?: ""

        val empIDLayout: TextInputLayout = view.findViewById(R.id.empID)
        val empNameLayout :TextInputLayout = view.findViewById(R.id.empName)
        val empNumberLayout : TextInputLayout = view.findViewById(R.id.empNumber)
        val empExpertiseLayout : TextInputLayout = view.findViewById(R.id.empExpertise)
        val empEmailIdLayout: TextInputLayout = view.findViewById(R.id.empEmailId)
        val empDepartmentLayout: TextInputLayout = view.findViewById(R.id.empDepartment)
        val empDesignationLayout : TextInputLayout = view.findViewById(R.id.empDesignation)
        val empBloodLayout : TextInputLayout = view.findViewById(R.id.empBloodGroup)
        val empDOBLayout: TextInputLayout = view.findViewById(R.id.empDOB)
        val empDOJLayout: TextInputLayout = view.findViewById(R.id.empDOJ)
        val empQualificationLayout : TextInputLayout = view.findViewById(R.id.empQualification)
        val empCountryLayout : TextInputLayout = view.findViewById(R.id.empCountry)

        val imageBtn : ImageView = view.findViewById(R.id.uploadImageBtn)

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

                    empIDLayout.editText?.setText(user?.emp_ID)
                    empNameLayout.editText?.setText(user?.emp_name)
                    empEmailIdLayout.editText?.setText(user?.email)
                    empNumberLayout.editText?.setText(user?.emp_contact_No)
                    empDepartmentLayout.editText?.setText(user?.emp_department)
                    empExpertiseLayout.editText?.setText(user?.emp_expertise)
                    empDesignationLayout.editText?.setText(user?.emp_designation)
                    empDOBLayout.editText?.setText(user?.emp_DOB)
                    empDOJLayout.editText?.setText(user?.emp_joining_date)
                    empBloodLayout.editText?.setText(user?.emp_blood_group)
                    empQualificationLayout.editText?.setText(user?.emp_qualification)

                } else {
                    Toast.makeText(requireContext(), "No Data Available", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
            }
        })

        return view
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