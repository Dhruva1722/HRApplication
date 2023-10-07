package com.example.afinal.UserActivity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
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
import com.example.afinal.MainActivity
import com.example.afinal.R
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.ByteBuffer

class UserDetails : AppCompatActivity() {

    private lateinit var helpBtn: ImageView

    private lateinit var logoutbtn: Button
    private lateinit var savebtn: Button

    private var selectedImagePath: String? = null
    private lateinit var selectedImageUri: Uri

    private lateinit var radioGroup: RadioGroup
    private lateinit var busRadio: RadioButton
    private lateinit var bikeRadio: RadioButton
    private lateinit var trainRadio: RadioButton
    private lateinit var flightRadio: RadioButton
    private lateinit var uploadButton: ImageView
    private lateinit var imgContainer: RelativeLayout
    private lateinit var billInput : TextInputEditText

    private lateinit var imageByteBuffer: ByteBuffer

    private val IMAGE_PICK_REQUEST = 126

    private lateinit var sharedPreferences: SharedPreferences
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)


        radioGroup = findViewById(R.id.idRadioGroup)
        uploadButton = findViewById(R.id.uploadButton)
        savebtn = findViewById(R.id.saveBtn)

        busRadio = findViewById(R.id.idBtnBusRadio)
        bikeRadio = findViewById(R.id.idBtnBikeRadio)
        trainRadio = findViewById(R.id.idBtnTrainRadio)
        flightRadio = findViewById(R.id.idBtnFlightRadio)
        imgContainer = findViewById(R.id.imageContainer)
        billInput = findViewById(R.id.billInput)




        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null) ?: ""


        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        savebtn.setOnClickListener {

            val Transport_type = when (radioGroup.checkedRadioButtonId) {
                R.id.idBtnBusRadio -> "Bus"
                R.id.idBtnBikeRadio -> "Bike"
                R.id.idBtnTrainRadio -> "Train"
                R.id.idBtnFlightRadio -> "Flight"
                else -> ""
            }
            val  Total_expense = billInput.text.toString()
            // Check if the user has selected a transportation type and entered total expense.
            if (Transport_type.isNotEmpty() && Total_expense.isNotEmpty() && selectedImagePath != null) {

                val byteArray = ByteArray(imageByteBuffer.remaining())
                imageByteBuffer.get(byteArray)

                val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

                val images = ImageData(data = base64Image, contentType = "image/*")

                val transportationData = TransportationData(
                    userId!!,
                    Transport_type,
                    Total_expense,
                    images
                )
                Log.d("LocationData", "------------" + transportationData)
                // Call the API to save the data.
                if (transportationData != null) {
                    apiService.saveTransportationData(transportationData).enqueue(object : Callback<Any> {
                        override fun onResponse(call: Call<Any>, response: Response<Any>) {
                            if (response.isSuccessful) {
                                showImagePopup(base64Image)
                                Toast.makeText(applicationContext, " data saved", Toast.LENGTH_SHORT).show()
                                showSuccessMessage()
                                val intent = Intent(this@UserDetails, MainActivity::class.java)
                                startActivity(intent)

                            } else {
                                Toast.makeText(applicationContext, " data fail to saved", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Any>, t: Throwable) {
                            Toast.makeText(applicationContext, " network error", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            } else {
                // Display an error message if any required fields are empty.
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            }
        }
        uploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_REQUEST)
        }

        helpBtn = findViewById(R.id.helpBtn)

        helpBtn.setOnClickListener { v ->
            showPopupMenu(v)
        }
    }
    private fun overridePendingTransition(slideLeft: Int) {

    }


    private fun showSuccessMessage() {
        val thankYouTextView = findViewById<TextView>(R.id.thankYouTextView)
        val successIconImageView = findViewById<ImageView>(R.id.successIconImageView)

        thankYouTextView.visibility = View.VISIBLE
        successIconImageView.visibility = View.VISIBLE
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.help_menu) // Inflate the menu resource

        // Set a listener for menu item clicks
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_help -> {
                    // Handle Help action
                    val intent = Intent(this, HelpActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.action_complain -> {
                    // Handle Feedback action
                    val intent = Intent(this, ComplaintActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data!!
            selectedImagePath = getRealPathFromURI(selectedImageUri)

            // Load the image as a ByteBuffer
            imageByteBuffer = loadAndConvertImageToByteBuffer(selectedImagePath)
        }
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


    private fun showImagePopup(imageBase64: String) {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_image, null)

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()

        val imageView = dialogView.findViewById<ImageView>(R.id.imageView)

        val decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        imageView.setImageBitmap(bitmap)

        dialog.show()
    }
}


data class TransportationData(
    val userId : String,
    val Transport_type: String,
    val Total_expense: String,
      val images: ImageData
)

data class ImageData(
    val data: String,
    val contentType: String
)