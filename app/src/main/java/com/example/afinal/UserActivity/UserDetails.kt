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
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


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



    private val PICK_IMAGES_REQUEST = 1
    private val selectedImageUris = mutableListOf<Uri>()

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

            val totalExpense = totalExpense.editText!!.text.toString()
            val food = foodInput.editText!!.text.toString()
            val water = waterInput.editText!!.text.toString()
            val hotel = hotelInput.editText!!.text.toString()
            val otherTransport = otherInput.editText!!.text.toString()
            val imageName = ""
            val imageBase64 = ""

            val formData = FormData(
                userId!!,
                Transport_type,
                totalExpense,
                food,
                water,
                hotel,
                otherTransport,
                FormDataImages(imageBase64, "image/jpeg"), // Update with the correct image content type
                imageName
            )
            Log.d("-----", "onCreate: ${formData}")
            apiService.saveFormData(formData).enqueue(object : Callback<Void> {
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

    fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, PICK_IMAGES_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.clipData != null) {
                    // Multiple images are selected
                    val count = data.clipData!!.itemCount
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        selectedImageUris.add(imageUri)
                    }
                } else if (data.data != null) {
                    // Single image is selected
                    val imageUri = data.data
                    selectedImageUris.add(imageUri!!)
                }

            }
        }
    }

    private fun getRealPathFromURI(contentUri: Uri?): String {
        val cursor = contentResolver.query(contentUri!!, null, null, null, null)
        if (cursor == null) {
            Log.d("ImageErro", "getRealPathFromURI: ${cursor} ")
            return ""
        }

        cursor.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                if (columnIndex >= 0) {
                    val filePath = cursor.getString(columnIndex)
                    return filePath ?: ""
                }
            }
        }

        return ""
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
    val images: FormDataImages,
    val ImageName: String
)

data class FormDataImages(
    val data: String,
    val contentType: String
)










//override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//    super.onActivityResult(requestCode, resultCode, data)
//    if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
//        selectedImageUri = data.data!!
//        val selectedImageUri = getRealPathFromURI(selectedImageUri)
//        if (selectedImageUri != null) {
//            selectedImagePath = selectedImageUri
//        } else {
//            Toast.makeText(applicationContext, "Getting image error", Toast.LENGTH_SHORT).show()
//        }
//    }
//}

//private fun getRealPathFromURI(uri: Uri): String? {
//    val projection = arrayOf(MediaStore.Images.Media.DATA)
//    val cursor = contentResolver.query(uri, projection, null, null, null)
//    val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//    cursor?.moveToFirst()
//    val imagePath = cursor?.getString(columnIndex!!)
//    cursor?.close()
//    return imagePath
//}



//        savebtn.setOnClickListener {
//            val transportType = when {
//                busRadio.isChecked -> "Bus"
//                bikeRadio.isChecked -> "Bike"
//                trainRadio.isChecked -> "Train"
//                flightRadio.isChecked -> "Flight"
//                else -> ""
//            }
//            val totalExpense = billInput.text.toString()
//
//            // Manually create a sample ImageData object (replace with your actual image data)
//            val images = ImageData("base64_encoded_image_data", "image/jpeg")
//            val imageName = "sample_image.jpg"
//
//            val userId = "651e4e37b38144033cf2fae5"
//
//            val transportationData = TransportationData(
//                userId,
//                transportType,
//                totalExpense,
//                images,
//                imageName
//            )
//
//            Log.d("---------------", "onCreate: User Details : $transportationData")
//
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val response = apiService.saveTransportationData(transportationData)
//                    if (response.isSuccessful) {
//                        withContext(Dispatchers.Main) {
//                            Toast.makeText(applicationContext, "Data saved", Toast.LENGTH_SHORT).show()
//                            showSuccessMessage()
//                            val handler = Handler()
//                            handler.postDelayed({
//                                val intent = Intent(this@UserDetails, MainActivity::class.java)
//                                startActivity(intent)
//                            }, 3000)
//                        }
//                    } else {
//                        withContext(Dispatchers.Main) {
//                            Toast.makeText(applicationContext, "Failed to save data", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(applicationContext, "Network error", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//  }


//
//    uploadButton.setOnClickListener {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(intent, IMAGE_PICK_REQUEST)
//    }
//
//    helpBtn = findViewById(R.id.helpBtn)
//
//    helpBtn.setOnClickListener { v ->
//        showPopupMenu(v)
//    }
//}
//
//override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//    super.onActivityResult(requestCode, resultCode, data)
//    if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
//        selectedImageUri = data.data!!
//        val selectedImageUri = getRealPathFromURI(selectedImageUri)
//        if (selectedImageUri != null) {
//            val imageInfo = loadAndConvertImageToByteArray(selectedImageUri)
//            Log.d("**********", "onCreate:Image info: ${imageInfo}")
//        } else {
//            Toast.makeText(applicationContext, "Getting image error", Toast.LENGTH_SHORT).show()
//        }
//    }
//}
//
//private fun loadAndConvertImageToByteArray(imagePath: String?): Pair<ByteArray, String?> {
//    if (imagePath == null) {
//        return Pair(ByteArray(0), null)
//    }
//    try {
//        val inputStream = FileInputStream(imagePath)
//        val channel = inputStream.channel
//        val size = channel.size()
//        val byteArray = ByteArray(size.toInt())
//
//        val buffer = ByteBuffer.allocate(size.toInt())
//        channel.read(buffer)
//        channel.close()
//        inputStream.close()
//
//        buffer.flip()
//        val imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1)
//        return Pair(byteArray, imageName)
//    } catch (e: FileNotFoundException) {
//        e.printStackTrace()
//    } catch (e: IOException) {
//        e.printStackTrace()
//    }
//    return Pair(ByteArray(0), null)
//}
//
//private fun getRealPathFromURI(uri: Uri): String? {
//    val projection = arrayOf(MediaStore.Images.Media.DATA)
//    val cursor = contentResolver.query(uri, projection, null, null, null)
//    val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//    cursor?.moveToFirst()
//    val imagePath = cursor?.getString(columnIndex!!)
//    cursor?.close()
//    return imagePath
//}