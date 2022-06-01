package com.smartbin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var map_frame: FrameLayout
    var status: String = "Loading Data"
    var lat: Double = 0.0
    var lng: Double = 0.0
    lateinit var loc: LatLng
    lateinit var gMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var bin = findViewById<ImageView>(R.id.bin)
        var bin_text = findViewById<TextView>(R.id.bin_text)
        map_frame = findViewById(R.id.map_frame)

        val database = Firebase.database
        val mDatabase = database.getReference("bins")

        mDatabase.child("1").child("status").get().addOnSuccessListener {
            status = it.value.toString()
            mDatabase.child("1").child("lat").get().addOnSuccessListener {
                lat = it.value as Double
                mDatabase.child("1").child("lng").get().addOnSuccessListener {
                    lng = it.value as Double

                    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
                    mapFragment?.getMapAsync(this)

                    bin_text.text = status

                    var bin_pic = "@drawable/bin1"
                    when (status) {
                        "Less than Half" -> {
                            bin_pic = "@drawable/bin2"
                        }
                        "More than Half" -> {
                            bin_pic = "@drawable/bin3"
                        }
                        "Full" -> {
                            bin_pic = "@drawable/bin4"
                        }
                    }
                    val bin_pic_res = resources.getIdentifier(bin_pic, null, packageName)
                    bin.setImageResource(bin_pic_res)
                }.addOnFailureListener{}
            }.addOnFailureListener{}
        }.addOnFailureListener{}

        bin_text.text = status
    }

    fun openMap(view: View){
        map_frame.visibility = View.VISIBLE

        val cameraPosition = CameraPosition.Builder().target(loc).zoom(17f).bearing(90f).tilt(30f).build()
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onBackPressed() {
        map_frame.visibility = View.GONE
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        loc = LatLng(lat, lng)
        googleMap.addMarker(MarkerOptions().position(loc).title("Bin"))
    }
}