// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429
package com.example.cenphone

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.cenphone.databinding.ActivityDeliveryTypeBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class DeliveryType : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDeliveryTypeBinding
    private lateinit var sharedPreferences: SharedPreferences


    // Centennial College locations with details
    private val locations = listOf(
        "Centennial College Progress Campus" to LocationDetail(
            "Progress Campus",
            "941 Progress Ave, Scarborough, ON M1G 3T8",
            "+1 416-438-2216",
            "9AM - 5PM",
            "centennialcollege.ca",
            LatLng(43.7861, -79.2128)
        ),
        "Centennial College Morningside Campus" to LocationDetail(
            "Morningside Campus",
            "755 Morningside Ave, Scarborough, ON M1C 4Z4",
            "+1 416-438-2216",
            "9AM - 5PM",
            "centennialcollege.ca",
            LatLng(43.7869576, -79.1928649)
        ),
        "Centennial College Ashtonbee Campus" to LocationDetail(
            "Ashtonbee Campus",
            "75 Ashtonbee Rd, Scarborough, ON M1L 4N4",
            "+1 416-438-2216",
            "9AM - 5PM",
            "centennialcollege.ca",
            LatLng(43.7318426, -79.2924922)
        ),
        "Centennial College Story Arts Centre" to LocationDetail(
            "Story Arts Centre",
            "951 Carlaw Ave, Toronto, ON M4K 3M2",
            "+1 416-438-2256",
            "9AM - 5PM",
            "centennialcollege.ca",
            LatLng(43.6848, -79.3491)
        ),
        "Centennial College - Downsview Campus" to LocationDetail(
            "Downsview Campus",
            "65 Carl Hall Rd, Toronto, ON M3K 2C1",
            "+1 416-289-5152",
            "9AM - 5PM",
            "centennialcollege.ca",
            LatLng(43.7473, -79.476)
        )
    )

    // Map for markers to associate them with details
    private val markerMap = mutableMapOf<Marker, LocationDetail>()

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("CheckoutData", MODE_PRIVATE)
        // Hide map and list initially
        binding.mapContainer.visibility = View.GONE
        binding.placeList.visibility = View.GONE
        binding.infoBox.visibility = View.GONE

        // Door Delivery selection
        binding.cardDoorDelivery.setOnClickListener {
            // Navigate to the next screen
            startActivity(Intent(this, CartActivity::class.java))
        }

        // Store Pickup selection
        binding.cardStorePickup.setOnClickListener {
            binding.mapContainer.visibility = View.VISIBLE
            binding.placeList.visibility = View.VISIBLE
        }

        val backButton = findViewById<ImageView>(R.id.backButton)

        // Set an OnClickListener to navigate back
        backButton.setOnClickListener {
            onBackPressed()
        }
        // Initialize the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val mapSwitch: Switch = findViewById(R.id.switchMapType)

        mapSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Set the map type to satellite view
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            } else {
                // Set the map type to normal view
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }

        // Set up the list of places
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locations.map { it.first })
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.placeList.adapter = spinnerAdapter

        binding.saveButton.setOnClickListener {
            val selectedLocation = binding.placeList.selectedItem.toString()
            val editor = sharedPreferences.edit()

            editor.putString("storeLocation", selectedLocation)
            editor.apply()
            // Move to the next screen
            startActivity(Intent(this, CartActivity::class.java)) // Change to the next activity as needed
        }

        // Handle list item clicks
        binding.placeList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLocation = locations[position].second
                val marker = markerMap.entries.find { it.value == selectedLocation }?.key
                marker?.let {
                    // Show the info window for the selected marker
                    it.showInfoWindow()

                    // Optionally, you can animate the camera to the selected marker
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it.position, 15f))
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Optionally handle if no item is selected
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Set the map type to satellite view
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        // Enable zoom controls
        mMap.uiSettings.isZoomControlsEnabled = true

        // Add markers for all Centennial College locations
        locations.forEach { (placeName, details) ->
            val marker = mMap.addMarker(MarkerOptions().position(details.latLng).title(placeName))
            marker?.let {
                // Store the location details in the marker's tag
                marker.tag = details
                // Store the marker for easy access later when clicking on the list
                markerMap[it] = details
            }
        }

        // Move camera to show all markers (zoom level can be adjusted)
        val firstLocation = locations.first().second.latLng
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12f))

        // Set custom info window adapter
        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this))

        // Handle marker clicks/
        mMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow() // Show the custom info window
            true
        }
    }



    private fun onMarkerSelected(marker: Marker) {
        val details = markerMap[marker] ?: return

        // Log the details to ensure the data is being passed
        Log.d("DeliveryType", "Marker selected: ${details.name}")

        // Update the info box visibility and content
        binding.infoBox.visibility = View.VISIBLE
        binding.infoTitle.text = details.name
        binding.infoAddress.text = details.address
        binding.infoPhone.text = details.phone
        binding.infoHours.text = details.hours
        binding.infoWebsite.text = details.website

        // Move camera to marker
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 15f))
    }


}

// Data class for location details
data class LocationDetail(
    val name: String,
    val address: String,
    val phone: String,
    val hours: String,
    val website: String,
    val latLng: LatLng
)
