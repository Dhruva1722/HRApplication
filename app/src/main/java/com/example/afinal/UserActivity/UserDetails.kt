package com.example.afinal.UserActivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import com.example.afinal.MainActivity
import com.example.afinal.R
import com.google.android.material.textfield.TextInputEditText
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.ByteBuffer


class UserDetails : AppCompatActivity() {

    private lateinit var helpBtn: ImageView

    private lateinit var savebtn: Button

    private var selectedImagePath: String? = null
    private lateinit var selectedImageUri: Uri

    private lateinit var radioGroup: RadioGroup
    private lateinit var busRadio: RadioButton
    private lateinit var bikeRadio: RadioButton
    private lateinit var trainRadio: RadioButton
    private lateinit var flightRadio: RadioButton
    private lateinit var uploadButton: Button
    private lateinit var imgContainer: RelativeLayout
    private lateinit var billInput : TextInputEditText


    private val IMAGE_PICK_REQUEST = 300

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
//        billInput = findViewById(R.id.billInput)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null) ?: ""

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)


        savebtn.setOnClickListener {

            val Transport_type = "bus"
            val Total_expense = "2000"
            val Food = "300"
            val Water = "20"
            val Other_Transport = "300"
            val Hotel = "2000"
            val images = "sdhskjgh"
            val ImageName = "name.png"

            val userExpense = UserExpense(
                Transport_type,
                Total_expense,
                Food,
                Water,
                Hotel,
                Other_Transport,
                images,
                ImageName
            )

            val call = apiService.saveUserExpense(userExpense)
            Log.d("---------------", "onCreateView: points ${userExpense}")

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val message = response.body()?.string() // Get any response message from the server
                        Toast.makeText(this@UserDetails, " successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@UserDetails, "Fail", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@UserDetails, "Netwrok error ", Toast.LENGTH_SHORT).show()
                }
            })

        }

        val helpBtn = findViewById<ImageView>(R.id.helpBtn)
        helpBtn.setOnClickListener { v ->
            showPopupMenu(v)
        }

        val uploadButton = findViewById<Button>(R.id.uploadButton)
        uploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_REQUEST)
        }
    }



    private fun loadAndConvertImageToByteArray(imagePath: String?): Pair<ByteArray, String?> {
        if (imagePath == null) {
            return Pair(ByteArray(0), null)
        }
        try {
            val inputStream = FileInputStream(imagePath)
            val channel = inputStream.channel
            val size = channel.size()
            val byteArray = ByteArray(size.toInt())

            val buffer = ByteBuffer.allocate(size.toInt())
            channel.read(buffer)
            channel.close()
            inputStream.close()

            buffer.flip()
            val imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1)
            buffer.get(byteArray)
            Log.d("---------------", "onCreate: User Details : $imageName")
            return Pair(byteArray, imageName)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Pair(ByteArray(0), null)
    }

//    private fun showSuccessMessage() {
//        val thankYouTextView = findViewById<TextView>(R.id.thankYouTextView)
//        val successIconImageView = findViewById<ImageView>(R.id.successIconImageView)
//
//        thankYouTextView.visibility = View.VISIBLE
//        successIconImageView.visibility = View.VISIBLE
//    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.help_menu) // Inflate the menu resource

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

data class UserExpense(
    val Transport_type: String,
    val Total_expense: String,
    val Food: String,
    val Water: String,
    val Hotel: String,
    val Other_Transport: String,
    val images : String,
    val ImageName :String
)
data class ImageData(
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