package com.eatburn.calorieday

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.eatburn.calorieday.R.layout.example_item
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_on_track.*


class OnTrackActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var mQueryCurrentUser: Query? = null
    private var mEntryList: RecyclerView? = null
    private var mStorageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_track)

        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val users = firebaseAuth.currentUser
            if (users == null) {
                startActivity(Intent(this@OnTrackActivity, LoginActivity::class.java))
                finish()
            }
        }

        val currentUserId = mAuth!!.currentUser!!.uid
        Log.d("UID", mAuth!!.currentUser!!.uid)
//        FirebaseDatabase.getInstance().reference.child("Diary_Logs")
        mDatabase = FirebaseDatabase.getInstance().reference.child(currentUserId)
        mStorageRef = FirebaseStorage.getInstance().getReference(mAuth!!.currentUser!!.uid)
        mDatabase!!.keepSynced(true) // always sync the data from firebase when data is changed
        mQueryCurrentUser = mDatabase!!.child("Food").orderByChild("Food")


        mEntryList = findViewById<View>(R.id.recycler_view) as RecyclerView
        mEntryList!!.setHasFixedSize(true)
        mEntryList!!.layoutManager = LinearLayoutManager(this)


        mQueryCurrentUser!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val key = ds.key
                    Log.d("MSG", key)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Cancel", databaseError.message)
            }
        })
        ontrack_profileBtn.setOnClickListener {
            startActivity(Intent(this@OnTrackActivity, ProfileActivity::class.java))
        }
        ontrack_mygoalBtn.setOnClickListener {
            startActivity(Intent(this@OnTrackActivity, MyGoalActivity::class.java))
        }
        ontrack_addBtn.setOnClickListener {
            startActivity(Intent(this@OnTrackActivity, AddOnActivity::class.java))
        }
        ontrack_homeBtn.setOnClickListener {
            startActivity(Intent(this@OnTrackActivity, HomeActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        mAuth!!.addAuthStateListener(mAuthListener!!)
        val firebaseRecyclerAdapter: FirebaseRecyclerAdapter<ExampleItem, ExampleAdapter> =
            object : FirebaseRecyclerAdapter<ExampleItem, ExampleAdapter>(
                ExampleItem::class.java,
                example_item,
                ExampleAdapter::class.java,
                mQueryCurrentUser
            ) {
                override fun populateViewHolder(
                    viewHolder: ExampleAdapter,
                    model: ExampleItem,
                    position: Int
                ) {
                    viewHolder.setImage(applicationContext, model.images)
                    viewHolder.setMeal(model.Meal)
                    viewHolder.setMenu(model.Menu)
                    viewHolder.setCalories(model.Calories)
                    viewHolder.setDate_and_Time(model.Date_and_Time)
                    viewHolder.setLatitude(model.Latitude)
                    viewHolder.setLongitude(model.Longitude)
                }
            }
        mEntryList!!.adapter = firebaseRecyclerAdapter
    }
}
