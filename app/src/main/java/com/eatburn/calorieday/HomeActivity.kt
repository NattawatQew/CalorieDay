package com.eatburn.calorieday

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.nav_header.*
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    lateinit var mDatabase: DatabaseReference
    private val TAG:String = "Home Activity"
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase!!.keepSynced(true)

        val user = mAuth!!.currentUser
        val currentDate = Calendar.getInstance()
//        val id = UUID.randomUUID().toString()
        var water: Long?

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val users = firebaseAuth.currentUser
            if (users == null) {
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finish()
            }
        }

//        val df = SimpleDateFormat("EEE, d MMM yyyy, HH:mm")
        val df = SimpleDateFormat("d MM yyyy")
        val date: String = df.format(Calendar.getInstance().time)

        val userInfoListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                get value form real time database into the string
                home_nameData.text = dataSnapshot.child(user!!.uid).child("UserInfo").child("Username").getValue(
                    String::class.java)
                home_calData.text = dataSnapshot.child(user.uid).child("Calories").child("Cal per day").getValue(
                    Double::class.java)?.toInt().toString()
                home_waterData.text = dataSnapshot.child(user.uid).child("Water").child(date).getValue(Double::class.java)?.toInt().toString()
                nav_profile.text = dataSnapshot.child(user!!.uid).child("UserInfo").child("Username").getValue(
                    String::class.java)
            }
        }
        mDatabase.addValueEventListener(userInfoListener)

        var time = currentDate.get(Calendar.HOUR_OF_DAY);//startActivity(Intent(this@HomeActivity, HomeActivity::class.java))


        //                pull the value from the database by using dataSnapshot and getValue
        when (time) {
            in 5..11 -> {
                home_timeData.text = getString(R.string.msg_greet_good_morning)
            }
            in 12..17 -> {
                home_timeData.text = getString(R.string.msg_greet_good_afternoon)
            }
            in 18..23 -> {
                home_timeData.text = getString(R.string.msg_greet_good_evening)
            }
            in 0..4 -> {
                home_timeData.text = getString(R.string.msg_greet_good_night)
            }
        }

        home_profileBtn.setOnClickListener {
            startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
        }
        home_mygoalBtn.setOnClickListener {
            startActivity(Intent(this@HomeActivity, MyGoalActivity::class.java))
        }
        home_addBtn.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AddOnActivity::class.java))
        }
        home_bookBtn.setOnClickListener{
            startActivity(Intent(this@HomeActivity, OnTrackActivity::class.java))
        }
        val waterListener = object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot){


//                pull the value from the database by using dataSnapshot and getValue
                water = dataSnapshot.child(user!!.uid).child("Water").child(date).value as Long?
                if (water == null){
                    mDatabase.child(user.uid).child("Water").child(date).setValue(0)
                }
                home_plusWater.setOnClickListener {
                    if (water == null){
                        water = 0
                    }
                    water = water?.plus(1)
                    mDatabase.child(user!!.uid).child("Water").child(date).setValue(water)
                    //startActivity(Intent(this@HomeActivity, HomeActivity::class.java))
                    if (water!! > 8){
                        water = 8
                        mDatabase.child(user.uid).child("Water").child(date).setValue(water)
                        Toast.makeText(this@HomeActivity, "You already drank 8 glasses of water", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "already drank 8 glasses of water")
                        return@setOnClickListener
                    }
                    return@setOnClickListener
                }
                home_MinusWater.setOnClickListener {
                    water = water?.minus(1)
                    mDatabase.child(user!!.uid).child("Water").child(date).setValue(water)
                    //startActivity(Intent(this@HomeActivity, HomeActivity::class.java))
                    if (water!! < 0){
                        water = 0
                        mDatabase.child(user.uid).child("Water").child(date).setValue(water)
                        Toast.makeText(this@HomeActivity, "Must have at least 0 glasses of water", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Must have at least 0 glasses of water")
                        return@setOnClickListener
                    }
                    return@setOnClickListener
                }
            }
        }

        mDatabase.addValueEventListener(waterListener)


        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById<NavigationView>(R.id.nav_view).apply{
            setNavigationItemSelectedListener(this@HomeActivity)
        }
//
        home_setting.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("Drawer", "Item Clicked! ${item.itemId}")
        when (item.itemId) {
            R.id.nav_translate -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_changecolor -> {
                Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener { mAuthListener }
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) { mAuth!!.removeAuthStateListener { mAuthListener } }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) { moveTaskToBack(true) }
        return super.onKeyDown(keyCode, event)
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
}
