    package com.example.afinal.MapActivity

    import android.annotation.SuppressLint
    import android.content.Context
    import android.content.Intent
    import android.graphics.Color
    import android.hardware.Sensor
    import android.hardware.SensorEvent
    import android.hardware.SensorEventListener
    import android.hardware.SensorManager
    import android.location.Address
    import android.location.Geocoder
    import android.media.MediaPlayer
    import android.os.Bundle
    import android.os.SystemClock
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.MenuItem
    import android.view.View
    import android.widget.Button
    import android.widget.EditText
    import android.widget.ImageView
    import android.widget.PopupMenu
    import android.widget.TextView
    import android.widget.Toast
    import androidx.appcompat.app.AlertDialog
    import androidx.appcompat.app.AppCompatActivity
    import com.example.afinal.R
    import com.example.afinal.UserActivity.ComplaintActivity
    import com.example.afinal.UserActivity.Fragment.HomeFragment
    import com.example.afinal.UserActivity.HelpActivity
    import com.example.afinal.UserActivity.UserDetails
    import com.example.afinal.databinding.ActivityMapsBinding
    import com.google.android.gms.maps.CameraUpdateFactory
    import com.google.android.gms.maps.GoogleMap
    import com.google.android.gms.maps.MapsInitializer
    import com.google.android.gms.maps.OnMapReadyCallback
    import com.google.android.gms.maps.SupportMapFragment
    import com.google.android.gms.maps.model.BitmapDescriptor
    import com.google.android.gms.maps.model.BitmapDescriptorFactory
    import com.google.android.gms.maps.model.LatLng
    import com.google.android.gms.maps.model.LatLngBounds
    import com.google.android.gms.maps.model.MarkerOptions
    import com.google.android.gms.maps.model.PolylineOptions
    import okhttp3.Call
    import okhttp3.Callback
    import okhttp3.OkHttpClient
    import okhttp3.Request
    import okhttp3.Response
    import org.json.JSONException
    import org.json.JSONObject
    import java.io.IOException
    import kotlin.math.log2
    import kotlin.math.sqrt


    class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

        private lateinit var map: GoogleMap
        private lateinit var binding: ActivityMapsBinding

        private lateinit var exitbtn: Button
        private lateinit var helpBtn: ImageView
        private lateinit var userCurrentAddress: TextView

        private val presenter = MapPresenter(this)

        private val locationProvider by lazy { LocationProvider(this) }


        private lateinit var geocoder: Geocoder
        private var startPoint: String? = null
        private var endPoint: String? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            geocoder = Geocoder(this)
            binding = ActivityMapsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            exitbtn = findViewById(R.id.ExitBtn)
            // Find and initialize the TextView widgets
            userCurrentAddress = findViewById(R.id.userCurrentAddress)


            val origin = intent.getStringExtra("origin")
            val destination = intent.getStringExtra("destination")


            // Now you have the values of origin and destination, use them as needed
//            Log.d("MapsActivity", "Origin: $origin, Destination: $destination")

            requestDirections(origin, destination)

            exitbtn.setOnClickListener {

                val dialogView = LayoutInflater.from(this).inflate(R.layout.messagepopup, null)
                val builder = AlertDialog.Builder(this)
                builder.setView(dialogView)
                val dialog = builder.create()

                val yesButton = dialogView.findViewById<TextView>(R.id.yesButton)
                val noButton = dialogView.findViewById<TextView>(R.id.noButton)

                yesButton.setOnClickListener {
                    val intent = Intent(this, HomeFragment::class.java)
                    startActivity(intent)
                }

                noButton.setOnClickListener {
                    val intent = Intent(this, UserDetails::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_down, R.anim.slide_up)
                    dialog.dismiss()
                }

                dialog.show()
            }
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)


            binding.btnStartStop.setOnClickListener {

                if (binding.btnStartStop.text == getString(R.string.start_label)) {
                    startTracking()
                    binding.btnStartStop.setText(com.example.afinal.R.string.stop_label)
                } else {
                    stopTracking()
                    binding.btnStartStop.setText(com.example.afinal.R.string.start_label)
                }
            }
            presenter.onViewCreated()
            helpBtn = findViewById(R.id.helpBtn)

            helpBtn.setOnClickListener { v ->
                showPopupMenu(v)
            }

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
            // Show the popup menu
            popupMenu.show()
        }

        fun searchLocation(view: View) {
            val locationSearch = findViewById<EditText>(R.id.edt_search)
            val location = locationSearch.text.toString()
            var addressList: List<Address>? = null

            if (location.isNotEmpty()) {
                val geocoder = Geocoder(this)
                try {
                    addressList = geocoder.getFromLocationName(location, 1)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (addressList != null && addressList.isNotEmpty()) {
                    val address = addressList[0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    map.addMarker(MarkerOptions().position(latLng).title(location))
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                    Toast.makeText(
                        applicationContext,
                        "${address.latitude} ${address.longitude}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(applicationContext, "Location not found", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }


        override fun onMapReady(googleMap: GoogleMap) {
            map = googleMap

            presenter.ui.observe(this) { ui ->
                updateUi(ui)
            }

            presenter.onMapLoaded()
            map.uiSettings.isZoomControlsEnabled = true


            if (startPoint != null && endPoint != null) {
                // Convert startPoint and endPoint to LatLng (you may need to geocode them)
                val startLatLng = getLatLngFromAddress(startPoint!!)
                val endLatLng = getLatLngFromAddress(endPoint!!)

                // Create a list of LatLng objects representing the route
                val routeLocations = mutableListOf(startLatLng, endLatLng)

                // Add markers for starting and ending locations
                map.addMarker(MarkerOptions().position(startLatLng).title("origin"))
                map.addMarker(MarkerOptions().position(endLatLng).title("Destination"))

                // Draw the route between starting and ending locations
                drawRoute(routeLocations,Color.BLUE)
            }
        }
        private fun getLatLngFromAddress(addressStr: String): LatLng {
            val geocoder = Geocoder(this)
            val addressList = geocoder.getFromLocationName(addressStr, 1)
            if (addressList != null) {
                if (addressList.isNotEmpty()) {
                    val address = addressList[0]
                    return LatLng(address.latitude, address.longitude)
                }
            }
            // Handle the case where addressStr couldn't be geocoded
            return LatLng(0.0, 0.0) // Default to (0, 0) if geocoding fails
        }

        private fun startTracking() {
            binding.container.txtPace.text = ""
            binding.container.txtDistance.text = ""
            binding.container.txtTime.base = SystemClock.elapsedRealtime()
            binding.container.txtTime.start()
//            map.clear()

            presenter.startTracking()

            // Use your LocationProvider's getUserLocation function
            locationProvider.getUserLocation()

            // Now, you can observe liveLocation to get user's location
//            locationProvider.liveLocation.observe(this, { userLocation ->
//                val accuracy = 0.0f
//                val zoomLevel = calculateZoomLevel(accuracy) // Calculate your zoom level
//                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoomLevel))
           // })

        }

//        private fun calculateZoomLevel(accuracy: Float): Float {
//            // Example calculation - you can adjust this based on your requirements
//            val zoomLevel = 15.0f - log2(accuracy) // You may need to import kotlin.math.log2
//
//            return if (zoomLevel < 1) 1.0f else zoomLevel
//        }

        private fun stopTracking() {
            presenter.stopTracking()
            binding.container.txtTime.stop()
        }

        @SuppressLint("MissingPermission")
        private fun updateUi(ui: Ui) {
            if (ui.currentLocation != null && ui.currentLocation != map.cameraPosition.target) {
                map.isMyLocationEnabled = true
                //            map.mapType = GoogleMap.MAP_TYPE_HYBRID
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(ui.currentLocation, 17f))

                // Get the address for the current location
                val geocoder = Geocoder(this)
                val addresses = geocoder.getFromLocation(
                    ui.currentLocation.latitude,
                    ui.currentLocation.longitude,
                    1
                )
                val address = addresses?.firstOrNull()

                // Set the address in the TextView
                userCurrentAddress.text = address?.getAddressLine(0) ?: "Unknown"
                val markerSnippet = "Address: ${address?.getAddressLine(0) ?: "Unknown"}"
                // Add marker at the current location
                map.clear()
                map.addMarker(
                    MarkerOptions()
                        .position(ui.currentLocation)
                        .title(markerSnippet)
                        .snippet("Lat: ${ui.currentLocation.latitude}, Lng: ${ui.currentLocation.longitude}")
                )
            }

            binding.container.txtDistance.text = ui.formattedDistance
            //        binding.container.txtFuelConsumption.text = getString(
            //            R.string.fuel_consumption_label, ui.fuelConsumption)
            binding.container.txtPace.text = ui.formattedPace
            val color = Color.BLUE
            drawRoute(ui.userPath, color)

        }

        private fun drawRoute(locations: List<LatLng>, color: Int) {
            val polylineOptions = PolylineOptions()
                .addAll(locations)
                .color(color)

            map.addPolyline(polylineOptions)

        }



        private fun requestDirections(origin: String?, destination: String?) {
            val client = OkHttpClient()

            val startPointLatLng = getLatAndLngFromAddress(origin!!)
            val endPointLatLng = getLatAndLngFromAddress(destination!!)

            Log.d("--------", "requestDirections: ${startPointLatLng}  ${endPointLatLng}")

            if (startPointLatLng != null && endPointLatLng != null) {
                val (startPointLat, startPointLng) = startPointLatLng
                val (endPointLat, endPointLng) = endPointLatLng

                val url =
                    "https://trueway-directions2.p.rapidapi.com/FindDrivingRoute?stops=$startPointLat,$startPointLng%3B$endPointLat,$endPointLng"
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("X-RapidAPI-Key", "b75b1af2dbmshcf32852a614c58cp1280cajsne0e57708a417")
                    .addHeader("X-RapidAPI-Host", "trueway-directions2.p.rapidapi.com")
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Toast.makeText(this@MapsActivity, "Getting error", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            val jsonData = response.body?.string()
                            Log.d("-----------", "onResponse: ${jsonData}")

                            val directionsData = parseDirections(jsonData)
                            val points = directionsData.first
                            val distance = directionsData.second
                            val duration = directionsData.third

                            val distanceInMeters =distance .toDouble()

                            Log.d("-----------", "onResponse: ${points}, Distance: $distance")

                            val distanceInKm = distanceInMeters/1000.0
                            runOnUiThread {
                                drawPolyline(points)
                                if (distance.isNotEmpty()) {
                                    // Update the TextView with the distance
                                    val distanceTxt = findViewById<TextView>(R.id.txtDistance)
                                    distanceTxt.text = "Distance: $distanceInKm km  Duration : ${duration}"
                                    Toast.makeText(this@MapsActivity, "Distance: $distance", Toast.LENGTH_LONG).show()
                                }

                                if (distance.isNotEmpty()) {
                                    Toast.makeText(this@MapsActivity, "Distance: $distance", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                })
                Log.d("--------", "requestDirections: $url")
            }
        }

        private fun parseDirections(jsonData: String?): Triple<List<LatLng>, String, String> {
            val points = ArrayList<LatLng>()
            var distance = ""
            val duration = ""

            try {
                if (jsonData.isNullOrBlank()) {
                    Log.e("Parse Directions", "JSON data is null or blank")
                    return Triple(points, distance, duration)
                }

                val jsonObject = JSONObject(jsonData)
                val route = jsonObject.optJSONObject("route")

                if (route == null) {
                    Log.e("Parse Directions", "No route found in JSON data")
                    return Triple(points, distance, duration)
                }

                val geometry = route.optJSONObject("geometry")

                if (geometry == null) {
                    Log.e("Parse Directions", "No geometry found in JSON data")
                    return Triple(points, distance, duration)
                }

                val coordinates = geometry.optJSONArray("coordinates")

                if (coordinates == null || coordinates.length() == 0) {
                    Log.e("Parse Directions", "No coordinates found in JSON data")
                    return Triple(points, distance, duration)
                }

                for (i in 0 until coordinates.length()) {
                    val coordinate = coordinates.optJSONArray(i)

                    if (coordinate != null && coordinate.length() == 2) {
                        val lat = coordinate.getDouble(0)
                        val lng = coordinate.getDouble(1)
                        points.add(LatLng(lat, lng))
                    }
                }
                distance = route.optString("distance", "")

            } catch (e: JSONException) {
                e.printStackTrace()
                Log.e("Parse Directions", "Error parsing JSON data: ${e.message}")
            }

            return  return Triple(points, distance, duration)
        }

        fun getLatAndLngFromAddress(address: String): Pair<Double, Double>? {
            try {
                val addresses: List<Address> = geocoder.getFromLocationName(address, 1)!!
                if (addresses.isNotEmpty()) {
                    val latitude = addresses[0].latitude
                    val longitude = addresses[0].longitude
                    return Pair(latitude, longitude)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        private fun drawPolyline(points: List<LatLng>?) {
            if (points != null && points.isNotEmpty()) {
                val polylineOptions = PolylineOptions()
                    .addAll(points)
                    .color(Color.BLUE)
                    .width(7f)

                map.addPolyline(polylineOptions)

                val builder = LatLngBounds.Builder()
                for (point in points) {
                    builder.include(point)
                }
                val bounds = builder.build()

                addMarker(
                    points.first(),
                    "origin",
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
                addMarker(
                    points.last(),
                    "destination",
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )

                val padding = 50
                val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)

                map.animateCamera(cameraUpdate)

                try {
                    MapsInitializer.initialize(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Log.e("Draw Polyline", "No valid points to draw")
            }
        }

        private fun addMarker(latLng: LatLng, title: String, icon: BitmapDescriptor) {
            val markerOptions = MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(icon)

            map.addMarker(markerOptions)
        }

    }

