package com.simple.travelsharing.view.driver.selectPlace

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.simple.travelsharing.R
import com.simple.travelsharing.utils.Utils
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mCurrentLat = 0.0
    private var mCurrentLng = 0.0

    private val REQUEST_LOCATION = 1
    var locationManager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        try {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    101
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setMapsLocation()
        initOnClick()
    }

    private fun initGetCurrentLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS()
        } else {
            getCurrentLocation()
        }
    }

    private fun OnGPS() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("จีพีเอส ถูกปิดอยู่กรุณาเปิด จีพีเอส เพื่อใช้งานฟันก์ชั่น").setCancelable(false)
            .setPositiveButton("ตกลง"
            ) { dialog, which -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton("ยกเลิก"
            ) { dialog, which -> dialog.cancel() }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
        } else {
            val locationGPS: Location? =
                locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (locationGPS != null) {
                mCurrentLat = locationGPS.latitude
                mCurrentLng = locationGPS.longitude

                val latLng = LatLng(mCurrentLat, mCurrentLng)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setMapsLocation() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addresses.isNotEmpty()) {
            address = addresses[0]
            addressText = address.getAddressLine(0)
        } else {
            addressText = "its not appear"
        }
        return addressText
    }


    private fun initOnClick() {
        viewBack.setOnClickListener {
            finish()
        }

        btnConfirm.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra("result", tvTitlePlace.text.toString())
            returnIntent.putExtra("latitude", mCurrentLat.toString())
            returnIntent.putExtra("longitude", mCurrentLng.toString())
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        initGetCurrentLocation()
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        googleMap.isMyLocationEnabled = true

        selectedLocation()

        setCameraMoveListener()
    }

    private fun setCameraMoveListener() {
        mMap.setOnCameraIdleListener {
            mMap.clear()
            val latLngPlace = mMap.cameraPosition.target

            if (latLngPlace.latitude != 0.0 && latLngPlace.longitude != 0.0) {
                if (mCurrentLat != latLngPlace.latitude && mCurrentLng != latLngPlace.longitude) {
                    try {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngPlace))
                        mCurrentLat = latLngPlace.latitude
                        mCurrentLng = latLngPlace.longitude

                        val geocoder = Geocoder(this, Locale("th", "TH"))
                        val addresses: List<Address>

                        addresses = geocoder.getFromLocation(mCurrentLat, mCurrentLng, 1)
                        if (addresses.isNotEmpty()) {
                            if (addresses[0].getAddressLine(0) != null) {
                                val mLocation = addresses[0].getAddressLine(0)
                                tvTitlePlace.text = mLocation
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        val latLngPlace = LatLng(mCurrentLat, mCurrentLng)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngPlace))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14f))
    }

    private fun selectedLocation() {
        val status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(baseContext)

        if (status != ConnectionResult.SUCCESS) {
            ///if play services are not available
            val requestCode = 10
            val dialog: Dialog = GooglePlayServicesUtil.getErrorDialog(
                status, this,
                requestCode
            )
            dialog.show()
        } else {
            // Enabling MyLocation Layer of Google Map
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mMap.isMyLocationEnabled = true
            val latLng = LatLng(mCurrentLat, mCurrentLng)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
            tvTitlePlace.text = getAddress(latLng)
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> {}
                PackageManager.PERMISSION_DENIED -> Utils.dialogMessage(this,resources.getString(R.string.location_permission_denied_message))
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }
}