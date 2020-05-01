package com.eatburn.calorieday

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    lateinit var mDatabase: DatabaseReference
    private val TAG:String = "Profile Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        val user = mAuth!!.currentUser

        result_emailData.text = user!!.email
        result_uidData.text = user.uid

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val users = firebaseAuth.currentUser
            if (users == null) {
                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                finish()
            }
        }

        val userInfoListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                result_signOutBtn.setOnClickListener {
                    mAuth!!.signOut()
                    startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
                    finish()
                }
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                get value form real time database into the string
                result_nameData_Head.text = dataSnapshot.child(user.uid).child("UserInfo").child("Username").getValue(
                    String::class.java)
                result_nameData.text = dataSnapshot.child(user.uid).child("UserInfo").child("Username").getValue(
                    String::class.java)
                result_genderData.text = dataSnapshot.child(user.uid).child("UserInfo").child("Gender").getValue(
                    String::class.java)
                result_ageData.text = dataSnapshot.child(user.uid).child("UserInfo").child("Age").getValue(
                    String::class.java)
                result_heightData.text = dataSnapshot.child(user.uid).child("UserInfo").child("Height").getValue(
                    String::class.java)
                result_weightData.text = dataSnapshot.child(user.uid).child("UserInfo").child("Weight").getValue(
                    String::class.java)
                result_bmiData.text = dataSnapshot.child(user.uid).child("Calories").child("BMI").getValue(
                    Double::class.java).toString()
                val image = dataSnapshot.child(user.uid).child("UserInfo").child("images").getValue(String::class.java)
                val requestOptions = RequestOptions
                    .placeholderOf(R.drawable.user)
                    .error(R.drawable.user)
                val profile_img = findViewById<View>(R.id.img_profile) as ImageView
                Glide.with(applicationContext).setDefaultRequestOptions(requestOptions).load(image).into(profile_img)
            }
        }
        mDatabase.addValueEventListener(userInfoListener)

        result_updateBtn.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, GenderActivity::class.java))
        }

        result_signOutBtn.setOnClickListener {
            popup()
        }

        profile_mygoalBtn.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, MyGoalActivity::class.java))
        }
        profile_homeBtn.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, HomeActivity::class.java))
        }
        profile_addBtn.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, AddOnActivity::class.java))
        }
        profile_bookBtn.setOnClickListener{
            startActivity(Intent(this@ProfileActivity, OnTrackActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener { mAuthListener }
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) { mAuth!!.removeAuthStateListener { mAuthListener } }
    }

    private fun popup() {
        val builder = android.app.AlertDialog.Builder(this)
        with(builder)
        {
            setTitle(getString(R.string.alert_signout))
            setPositiveButton(getString(R.string.confirm)) {
                    dialog, which ->
                startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
                mAuth!!.signOut()
                Toast.makeText(this@ProfileActivity, "Signed out!", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Signed out!")
                finish()
            }
            setNeutralButton(getString(R.string.cancel), null).show()
        }
    }

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK) { moveTaskToBack(true) }
//        return super.onKeyDown(keyCode, event)
//    }
}

