package com.example.afinal.UserActivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.afinal.R
import com.google.android.material.textfield.TextInputLayout
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer


class UserDetails : AppCompatActivity() {
    private lateinit var savebtn: Button

    private lateinit var radioGroup: RadioGroup
    private lateinit var busRadio: RadioButton
    private lateinit var bikeRadio: RadioButton
    private lateinit var trainRadio: RadioButton
    private lateinit var flightRadio: RadioButton
    private lateinit var uploadButton: Button
    private lateinit var totalExpense : TextInputLayout
    private lateinit var foodInput : TextInputLayout
    private lateinit var waterInput : TextInputLayout
    private lateinit var hotelInput : TextInputLayout
    private lateinit var otherInput : TextInputLayout



    private lateinit var imageByteBuffer: ByteBuffer

//    private var selectedImageUri: String? = null


    private var imageUrl: String? = null
    private var imageName : String? = null
    private val IMAGE_PICK_REQUEST = 126

    private lateinit var sharedPreferences: SharedPreferences
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)


        radioGroup = findViewById(R.id.idRadioGroup)
//        uploadButton = findViewById(R.id.uploadButton)
        savebtn = findViewById(R.id.saveToDatabase)

        busRadio = findViewById(R.id.idBtnBusRadio)
        bikeRadio = findViewById(R.id.idBtnBikeRadio)
        trainRadio = findViewById(R.id.idBtnTrainRadio)
        flightRadio = findViewById(R.id.idBtnFlightRadio)
        totalExpense = findViewById(R.id.billInput)
        foodInput = findViewById(R.id.foodBill)
        waterInput = findViewById(R.id.waterInput)
        hotelInput = findViewById(R.id.hotelInput)
        otherInput = findViewById(R.id.otherExp)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null) ?: ""

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)


        savebtn.setOnClickListener {
              val Transport_type = when {
                            busRadio.isChecked -> "Bus"
                            bikeRadio.isChecked -> "Bike"
                            trainRadio.isChecked -> "Train"
                            flightRadio.isChecked -> "Flight"
                            else -> ""
                        }

            val  Total_expense = totalExpense.editText!!.text.toString()
            val Food = foodInput.editText!!.text.toString()
            val Water = waterInput.editText!!.text.toString()
            val Hotel = hotelInput.editText!!.text.toString()
            val  Other_Transport = otherInput.editText!!.text.toString()
            // Convert string values to RequestBody
            val userIdRequestBody = RequestBody.create(null, userId!!)
            val transportTypeRequestBody = RequestBody.create(null, Transport_type)
            val totalexpenseTypeRequestBody = RequestBody.create(null,Total_expense)
            val fuelInLitersRequestBody = RequestBody.create(null, "0") // Set as needed for Car or Bike
            val foodRequestBody = RequestBody.create(null, Food)
            val waterRequestBody = RequestBody.create(null, Water)
            val hotelRequestBody = RequestBody.create(null, Hotel)
            val otherTransportRequestBody = RequestBody.create(null, Other_Transport)

            // Assuming this is inside your activity or fragment
            val imageResourceId = R.drawable.amico // Replace 'amico' with the actual name of your drawable
            val imageBitmap = BitmapFactory.decodeResource(resources, imageResourceId)
            val imageFile = convertBitmapToFile(imageBitmap, "amico.png")
            val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)


            val requestData = "User ID: $userId\n" +
                    "Transport Type: $Transport_type\n" +
                    "Total Expense: $Total_expense\n" +
                    "Food: $Food\n" +
                    "Water: $Water\n" +
                    "Hotel: $Hotel\n" +
                    "Other Transport: $Other_Transport\n" +
                    "image data: $imagePart\n"

           Log.d("UserDetails", "Data being sent to API: ${requestData}")

            // Make the API request
            apiService.saveFormData(
                userIdRequestBody,
                transportTypeRequestBody,
                totalexpenseTypeRequestBody,
                fuelInLitersRequestBody,
                foodRequestBody,
                waterRequestBody,
                hotelRequestBody,
                otherTransportRequestBody,
                imagePart
            ).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // Handle a successful response
                        Toast.makeText(this@UserDetails, "Data saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle an unsuccessful response
                        Toast.makeText(this@UserDetails, "Failed to save data", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // Handle the network error
                    Toast.makeText(this@UserDetails, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
        }

        val helpBtn = findViewById<ImageView>(R.id.helpBtn)
        helpBtn.setOnClickListener { v ->
            showPopupMenu(v)
        }

//        val uploadBtn = findViewById<Button>(R.id.uploadButton)
//        uploadBtn.setOnClickListener {
//            openImagePicker()
//        }
    }

    private fun convertBitmapToFile(bitmap: Bitmap, fileName: String): File {
        val file = File(cacheDir, fileName)
        file.createNewFile()

        // Convert bitmap to byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        // Write the bytes in file
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(byteArray)
        fileOutputStream.flush()
        fileOutputStream.close()

        return file
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.help_menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_help -> {
                    val intent = Intent(this, HelpActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_complain -> {
                    val intent = Intent(this, ComplaintActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}

data class FormData(
    val userId :String,
    val Transport_type: String,
    val Total_expense: String,
    val Food: String,
    val Water: String,
    val Hotel: String,
    val Other_Transport: String,
    val images: ImageData,
    val ImageName: String
)

data class ImageData(
    val data: String,
    val contentType: String
)









//        savebtn.setOnClickListener {
//
//            val Transport_type = when {
//                busRadio.isChecked -> "Bus"
//                bikeRadio.isChecked -> "Bike"
//                trainRadio.isChecked -> "Train"
//                flightRadio.isChecked -> "Flight"
//                else -> ""
//            }
//
//            val byteArray = ByteArray(imageByteBuffer.remaining())
//            imageByteBuffer.get(byteArray)
//
//            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)
//
//            val images = ImageData(data = base64Image, contentType = "image/*")
//
//            val  Total_expense = totalExpense.editText!!.text.toString()
//            val Food = foodInput.editText!!.text.toString()
//            val Water = waterInput.editText!!.text.toString()
//            val Hotel = hotelInput.editText!!.text.toString()
//            val  Other_Transport = otherInput.editText!!.text.toString()
//
//            val ImageName = selectedImageUri.lastPathSegment
//
//            val formData = FormData(
//                userId!!,
//                Transport_type,
//                Total_expense,
//                Food,
//                Water,
//                Hotel,
//                Other_Transport,
//                images,
//                ImageName!!
//            )
//            Log.d("-----", "onCreate: ${formData}")
//            apiService.saveFormData(formData).enqueue(object : Callback<Void> {
//                override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                    if (response.isSuccessful) {
//                        // Handle a successful response
//                        Toast.makeText(this@UserDetails, "Data saved successfully", Toast.LENGTH_SHORT).show()
//                    } else {
//                        // Handle an unsuccessful response
//                        Toast.makeText(this@UserDetails, "Failed to save data", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                override fun onFailure(call: Call<Void>, t: Throwable) {
//                    // Handle the network error
//                    Toast.makeText(this@UserDetails, "Network error", Toast.LENGTH_SHORT).show()
//                }
//            })
//
//        }