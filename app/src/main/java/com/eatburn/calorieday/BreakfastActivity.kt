package com.eatburn.calorieday

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_breakfast.*
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.timer


class BreakfastActivity : AppCompatActivity() {

    //    for image
    private val PICK_IMAGE_REQUEST = 234
    //    for location service
    val PERMISSION_ID = 42

    private var filePath: Uri? = null
    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    lateinit var mDatabase: DatabaseReference
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val TAG:String = "Breakfast Activity"
    private var mEntryTitle: EditText? = null
    private var mEntryContent: EditText? = null


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
    private fun getLastLocation(mKey: String) {
        val user = mAuth!!.currentUser
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
//                        findViewById<TextView>(R.id.latTextView).text = location.latitude.toString()
//                        findViewById<TextView>(R.id.lonTextView).text = location.longitude.toString()
                        val locationListener = object : ValueEventListener {
                            override fun onCancelled(databaseError: DatabaseError) { finish() }
                            override fun onDataChange(dataSnapshot: DataSnapshot){

                                mDatabase.child(user!!.uid).child("Food").child(mKey).child("Latitude").setValue(location.latitude.toString())
                                mDatabase.child(user!!.uid).child("Food").child(mKey).child("Longitude").setValue(location.longitude.toString())
                            }
                        }
                        mDatabase.addListenerForSingleValueEvent(locationListener)
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                finish()
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
//            findViewById<TextView>(R.id.latTextView).text = mLastLocation.latitude.toString()
//            findViewById<TextView>(R.id.lonTextView).text = mLastLocation.longitude.toString()
            val user = mAuth!!.currentUser
            val locationListener = object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) { finish() }
                override fun onDataChange(dataSnapshot: DataSnapshot){
                    mDatabase.child(user!!.uid).child("Food").child(mDatabase.key.toString()).child("Latitude").setValue(mLastLocation.latitude)
                    mDatabase.child(user!!.uid).child("Food").child(mDatabase.key.toString()).child("Longitude").setValue(mLastLocation.longitude)

                }
            }
            mDatabase.addListenerForSingleValueEvent(locationListener)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breakfast)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        var mKey = mDatabase.database.reference.push().key.toString()
        val user = mAuth!!.currentUser

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val users = firebaseAuth.currentUser
            if (users == null) {
                startActivity(Intent(this@BreakfastActivity, LoginActivity::class.java))
                finish()
            }
        }

        mEntryTitle = findViewById<View>(R.id.breakfast_menuEditText) as EditText
        mEntryContent = findViewById<View>(R.id.breakfast_calEditText) as EditText
        val breakfastListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot){
//                pull the value from the database by using dataSnapshot and getValue
                val df: DateFormat = SimpleDateFormat("EEE, d MMM yyyy, HH:mm")
                val currentdate = df.format(Calendar.getInstance().time)

                val  timestamp = Calendar.getInstance()
                timestamp[Calendar.HOUR_OF_DAY] = 0
                timestamp[Calendar.MINUTE] = 0
                timestamp[Calendar.SECOND] = 0
                timestamp[Calendar.MILLISECOND] = 0
//                val currentdate = DateFormat.getDateInstance(DateFormat.FULL).calendar.time

//                TIME_DIARY.text = currentdate.toString()
                breakfast_submitBtn.setOnClickListener {
                    val menu = mEntryTitle!!.text.toString().trim{ it <= ' '}
                    val cal = mEntryContent!!.text.toString().trim{ it <= ' '}
                    when {
                        menu.isEmpty() -> {
                            Toast.makeText(this@BreakfastActivity, "Please enter your Menu.", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "Menu was empty!")
                            return@setOnClickListener
                        }
                        cal.isEmpty() -> {
                            Toast.makeText(this@BreakfastActivity, "Please enter your Calories.", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "Cal was empty!")
                            return@setOnClickListener
                        }
                        else -> {
                            mDatabase.child(user!!.uid).child("Food").child(mKey).child("Menu").setValue(menu.toString())
                            mDatabase.child(user!!.uid).child("Food").child(mKey).child("Calories").setValue(cal.toString())
                            mDatabase.child(user!!.uid).child("Food").child(mKey).child("Date_and_Time").setValue(currentdate.toString())
                            mDatabase.child(user!!.uid).child("Food").child(mKey).child("Meal").setValue("Breakfast".toString())
                            mDatabase.child(user!!.uid).child("Food").child(mKey).child("Timestamp").setValue(timestamp.timeInMillis.toString())
                            mDatabase.child(user!!.uid).child("Food").child(mKey).child("mKey").setValue(mKey.toString())
                            mDatabase.child(user!!.uid).child("Food").child(mKey).child("uid").setValue(mAuth!!.currentUser!!.uid.toString())
                            Toast.makeText(this@BreakfastActivity, "Add breakfast success", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "Add breakfast success")
                            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this@BreakfastActivity)
                            getLastLocation(mKey)
                            uploadFile(mKey)
                            startActivity(Intent(this@BreakfastActivity, HomeActivity::class.java))
                            finish()
                        }
                    }
                }
            }
        }
        mDatabase.addValueEventListener(breakfastListener)

        breakfast_addimg.setOnClickListener {
            showFileChooser()
        }
    }

    private fun showFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                breakfast_img.setImageBitmap(bitmap)
                text_hint.text = filePath?.path!!.substring(filePath?.path!!.lastIndexOf("/")+1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadFile(mKey:String){
        if(filePath != null)
        {
            val storage = FirebaseStorage.getInstance()
            var storageRef = storage.reference
            var spaceRef = storageRef.child("images/" + mAuth?.uid.toString()+ "/" + filePath?.path!!.substring(filePath?.path!!.lastIndexOf("/")+1).toString())
            Toast.makeText(applicationContext, "File Uploaded ", Toast.LENGTH_LONG).show();
            spaceRef.putFile(filePath!!).addOnSuccessListener( OnSuccessListener<UploadTask.TaskSnapshot>() {
                spaceRef.downloadUrl.addOnCompleteListener {


                    mDatabase!!.child(mAuth!!.currentUser!!.uid).child("Food").child(mKey).child("images").setValue(it.result.toString())
                    mDatabase!!.child(mAuth!!.currentUser!!.uid).child("Food").child(mKey).child("uid").setValue(mAuth!!.currentUser!!.uid.toString())
                }
            })
                .addOnFailureListener(OnFailureListener{
                })
                .addOnProgressListener(OnProgressListener {
                })
        }
        else
        {
            mDatabase!!.child(mAuth!!.currentUser!!.uid).child("Food").child(mKey).child("images").setValue("null")
            Toast.makeText(this, "No File Upload" , Toast.LENGTH_SHORT).show()
        }
    }
}
