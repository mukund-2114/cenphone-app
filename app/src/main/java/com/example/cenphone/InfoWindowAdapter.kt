// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429
package com.example.cenphone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        return null // The default info window will not be used
    }

    override fun getInfoContents(marker: Marker): View {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)

        // Retrieve the location details from the marker
        val details = marker.tag as? LocationDetail
        if (details != null) {
            view.findViewById<TextView>(R.id.infoTitle).text = details.name
            view.findViewById<TextView>(R.id.infoAddress).text = details.address
            view.findViewById<TextView>(R.id.infoPhone).text = details.phone
            view.findViewById<TextView>(R.id.infoHours).text = details.hours
            view.findViewById<TextView>(R.id.infoWebsite).text = details.website
        }

        return view
    }
}
