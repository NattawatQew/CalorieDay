package com.eatburn.calorieday

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_breakfast.*
import kotlinx.android.synthetic.main.activity_dinner.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DinnerActivity : AppCompatActivity() {

    val PERMISSION_ID = 42

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Granted. Start getting the location information
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        val user = mAuth!!.currentUser
        val sdf = SimpleDateFormat("yyyy/M/dd")
        val date: String = sdf.format(Date())
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        var count: Long?
                        val locationListener = object : ValueEventListener {
                            override fun onCancelled(databaseError: DatabaseError) {}
                            override fun onDataChange(dataSnapshot: DataSnapshot){
                                count = dataSnapshot.child(user!!.uid).child(date).child("Dinner").child("Total").value as Long?
                                mDatabase.child(user!!.uid).child(date).child("Dinner").child(count.toString()).child("Latitude").setValue(location.latitude)
                                mDatabase.child(user!!.uid).child(date).child("Dinner").child(count.toString()).child("Longitude").setValue(location.longitude)
                            }
                        }
                        mDatabase.addValueEventListener(locationListener)
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            val user = mAuth!!.currentUser
            val sdf = SimpleDateFormat("yyyy/M/dd")
            val date: String = sdf.format(Date())
            var count: Long?
            val locationListener = object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot){
                    count = dataSnapshot.child(user!!.uid).child(date).child("Dinner").child("Total").value as Long?
                    mDatabase.child(user!!.uid).child(date).child("Dinner").child(count.toString()).child("Latitude").setValue(mLastLocation.latitude)
                    mDatabase.child(user!!.uid).child(date).child("Dinner").child(count.toString()).child("Longitude").setValue(mLastLocation.longitude)
                }
            }
            mDatabase.addValueEventListener(locationListener)
        }
    }

    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    lateinit var mDatabase: DatabaseReference
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val TAG:String = "Dinner Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dinner)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        val user = mAuth!!.currentUser

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val users = firebaseAuth.currentUser
            if (users == null) {
                startActivity(Intent(this@DinnerActivity, LoginActivity::class.java))
                finish()
            }
        }

        val sdf = SimpleDateFormat("yyyy/M/dd")
        val date: String = sdf.format(Date())
        var count: Long?

        val dinnerListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot){
//                pull the value from the database by using dataSnapshot and getValue
                val df: DateFormat = SimpleDateFormat("EEE, d MMM yyyy, HH:mm")
                val currentdate = df.format(Calendar.getInstance().time)
//                TIME_DIARY.text = currentdate.toString()
                count = dataSnapshot.child(user!!.uid).child(date).child("Dinner").child("Total").value as Long?
                if (count == null){
                    mDatabase.child(user.uid).child(date).child("Dinner").child("Total").setValue(0)
                }
                dinner_submitBtn.setOnClickListener {
                    if (count == null){
                        count = 0
                    }
                    count = count?.plus(1)
                    mDatabase.child(user.uid).child(date).child("Dinner").child("Total").setValue(count)
                    val menu = dinner_menuEditText.text.toString().trim(){it <= ' '}
                    val cal = dinner_calEditText.text.toString().trim(){it <= ' '}
                    if (menu.isEmpty()){
                        Toast.makeText(this@DinnerActivity, "Please enter your Menu.", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Menu was empty!")
                        return@setOnClickListener
                    }
                    if (cal.isEmpty()){
                        Toast.makeText(this@DinnerActivity, "Please enter your Calories.", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Cal was empty!")
                        return@setOnClickListener
                    }
                    mDatabase.child(user!!.uid).child(date).child("Dinner").child(count.toString()).child("Menu").setValue(menu)
                    mDatabase.child(user!!.uid).child(date).child("Dinner").child(count.toString()).child("Calories").setValue(cal)
                    mDatabase.child(user!!.uid).child(date).child("Dinner").child(count.toString()).child("Date and Time").setValue(currentdate.toString())
                    Toast.makeText(this@DinnerActivity, "Add Dinner success", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Add Dinner success")
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this@DinnerActivity)
                    getLastLocation()
                    startActivity(Intent(this@DinnerActivity, HomeActivity::class.java))
                }
            }
        }
        mDatabase.addValueEventListener(dinnerListener)
    }
}
