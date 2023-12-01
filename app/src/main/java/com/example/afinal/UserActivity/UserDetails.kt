package com.example.afinal.UserActivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
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
        uploadButton = findViewById(R.id.uploadButton)
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

//            val imageFile = File(selectedImagePath)
//            val imageRequestBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
//            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name ,imageRequestBody)

//            val imageUrlRequestBody = RequestBody.create(MediaType.parse("image/png"), imageUrl)
//            val imageNameRequestBody = RequestBody.create(MediaType.parse("text/plain"), imageName)

            val imageFile = File(imageUrl)
            val imageRequestBody = RequestBody.create("image/png".toMediaTypeOrNull(), imageFile)
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)
            val imageNameRequestBody = RequestBody.create(
                "image/png".toMediaTypeOrNull(),
                imageName!!
            )

            val requestData = "User ID: $userId\n" +
                    "Transport Type: $Transport_type\n" +
                    "Total Expense: $Total_expense\n" +
                    "Food: $Food\n" +
                    "Water: $Water\n" +
                    "Hotel: $Hotel\n" +
                    "Other Transport: $Other_Transport\n" +
                    "image data: $imageRequestBody\n"+
                    "Image File Name: $imageName"

            Log.d("UserDetails", "Data being sent to API:\n$requestData")

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
                imagePart,
                imageNameRequestBody,
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

        val uploadBtn = findViewById<Button>(R.id.uploadButton)
        uploadBtn.setOnClickListener {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK) {
            // Get the selected image's URI
            val selectedImageUri: Uri? = data?.data

            if (selectedImageUri != null) {
                // Convert the URI to an image URL
                imageUrl = selectedImageUri.toString()

                // Compress the image before uploading
                val compressedImageFile = compressImage(getRealPathFromURI(selectedImageUri))

                Log.d("+++++++++", "onActivityResult: $compressedImageFile ***** $imageName")

//                val imageNameRequestBody =
//                    RequestBody.create(MediaType.parse(""), imageName)

                // Now, use `imagePart` and `imageNameRequestBody` for further processing.
            } else {
                Toast.makeText(this, "Failed to get the selected image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun compressImage(imagePath: String?): String {
        val options = BitmapFactory.Options()
        options.inSampleSize = 2 // You can adjust this value as needed to control image quality

        val sourceBitmap = BitmapFactory.decodeFile(imagePath, options)
        val compressedImagePath = createImageFile(sourceBitmap)

        sourceBitmap.recycle()

        return compressedImagePath
    }

    private fun createImageFile(bitmap: Bitmap): String {
        val file = File(cacheDir, "compressed_image.jpg")
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private fun loadAndConvertImageToByteBuffer(imagePath: String?): ByteBuffer {
        if (imagePath == null) {
            return ByteBuffer.allocate(0)
        }

        try {
            val inputStream = FileInputStream(imagePath)
            val channel = inputStream.channel
            val size = channel.size()
            val buffer = ByteBuffer.allocate(size.toInt())

            channel.read(buffer)
            channel.close()
            inputStream.close()

            buffer.flip()
            return buffer
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ByteBuffer.allocate(0)
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val imagePath = cursor?.getString(columnIndex!!)
        cursor?.close()
        return imagePath
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