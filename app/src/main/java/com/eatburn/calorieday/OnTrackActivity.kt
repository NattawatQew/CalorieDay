package com.eatburn.calorieday

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.eatburn.calorieday.R.layout.example_item
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_on_track.*
import java.util.*


class OnTrackActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var mQueryCurrentUser: Query? = null
    private var mQueryCurrentUser2: Query? = null
    private var mEntryList: RecyclerView? = null
    private var mEntryList2: RecyclerView? = null
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
        mQueryCurrentUser = mDatabase!!.child("Food").orderByChild("uid")

        mEntryList = findViewById<View>(R.id.recycler_view1) as RecyclerView
        mEntryList!!.setHasFixedSize(true)
        mEntryList!!.setItemViewCacheSize(5)
        mEntryList!!.layoutManager = LinearLayoutManager(this)

//        FirebaseDatabase.getInstance().reference.child(currentUserId).child("Exercise")


        // We got the Firebase Key from using addvalueEventlistener


        ontrack_profileBtn.setOnClickListener {
            startActivity(Intent(this@OnTrackActivity, ProfileActivity::class.java))
            finish()
        }
        ontrack_mygoalBtn.setOnClickListener {
            startActivity(Intent(this@OnTrackActivity, MyGoalActivity::class.java))
            finish()
        }
        ontrack_addBtn.setOnClickListener {
            startActivity(Intent(this@OnTrackActivity, AddOnActivity::class.java))
            finish()
        }
        ontrack_homeBtn.setOnClickListener {
            startActivity(Intent(this@OnTrackActivity, HomeActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        mAuth!!.addAuthStateListener(mAuthListener!!)
        val firebaseRecyclerAdapter: FirebaseRecyclerAdapter<ExampleItem, ExampleAdapter2> =
            object : FirebaseRecyclerAdapter<ExampleItem, ExampleAdapter2>(
                ExampleItem::class.java,
                example_item,
                ExampleAdapter2::class.java,
                mQueryCurrentUser
            ) {
                override fun populateViewHolder(
                    viewHolder: ExampleAdapter2,
                    model: ExampleItem,
                    position: Int
                ) {
                    viewHolder.setImage(applicationContext, model.images)
                    viewHolder.setMeal(model.Meal)
                    viewHolder.setMenu(model.Menu)
                    viewHolder.setCalories(model.Calories)
                    viewHolder.setDate_and_Time(model.Date_and_Time)
                    viewHolder.setLatitude(applicationContext,model.Latitude)
                    viewHolder.setLongitude(applicationContext,model.Longitude)
                    viewHolder.mKey(model.mKey)
                }
                override fun onBindViewHolder(viewHolder: ExampleAdapter2, position: Int) {
                    super.onBindViewHolder(viewHolder, position)

//                    viewHolder.itemView.setOnClickListener {
//                        val FirebaseKey = (mEntryList!!.findViewHolderForAdapterPosition(position)!!
//                            .itemView.findViewById(R.id.mKey) as TextView).text.toString()
//                        Log.d("mKey",FirebaseKey)
//                        Log.d("Position", viewHolder.adapterPosition.toString())
//                        deleteUserData(FirebaseKey)
//                    }
                    viewHolder.itemView.setOnClickListener {
                        // Create Layout in Dialog
                        val layout = LinearLayout(this@OnTrackActivity)
                        layout.setOrientation(LinearLayout.VERTICAL)
                        // Create EditText and TextView
                        val firstval : EditText = EditText(this@OnTrackActivity)
                        val secondval : EditText = EditText(this@OnTrackActivity)
                        val readtext1 : TextView = MaterialTextView(this@OnTrackActivity)
                        val readtext2 : TextView = MaterialTextView(this@OnTrackActivity)
                        // Get text from TextView  with using position from Adapter Position
                        val Menu = (mEntryList!!.
                        findViewHolderForAdapterPosition(position)!!
                            .itemView.findViewById(R.id.text_view_3) as TextView).text.toString()
                        // Get Firebase Key from String with using position from Adapter Position
                        val FirebaseKey = (mEntryList!!.
                        findViewHolderForAdapterPosition(position)!!
                            .itemView.findViewById(R.id.mKey_Ontrack) as TextView).text.toString()
                        // Get Content from Adapter Position
                        val Calories = (mEntryList!!.
                        findViewHolderForAdapterPosition(position)!!
                            .itemView.findViewById(R.id.text_view_5) as TextView).text.toString()
                        // Add Date with get current date from mobile phone
                        val builder = AlertDialog.Builder(this@OnTrackActivity)
                        // Set the alert dialog title
                        secondval.setInputType(InputType.TYPE_CLASS_NUMBER)
                        builder.setTitle(getString(R.string.update))
                        firstval.setHint(getString(R.string.menu));
                        secondval.setHint(getString(R.string.calories))
                        readtext1.setText(getString(R.string.pre_menu))
                        readtext1.append(": " + Menu)
                        readtext2.setText(getString(R.string.pre_cal))
                        readtext2.append(": " + Calories)
                        // Add View to Layout
                        layout.addView(readtext1)
                        layout.addView(readtext2)
                        layout.addView(firstval)
                        layout.addView(secondval)
                        // Set layout from create
                        builder.setView(layout)
                        builder.setPositiveButton(getString(R.string.update)) { dialog, which ->
                            FirebaseDatabase.getInstance().reference.child(mAuth!!.currentUser!!.uid).child("Food")
                                .child(FirebaseKey).addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                    override fun onDataChange(p0: DataSnapshot) {
                                        // Remove a branch of Diary that related the mKey
                                        FirebaseDatabase.getInstance().reference.child(mAuth!!.currentUser!!.uid).child("Food")
                                            .child(FirebaseKey).child("Menu").setValue(firstval.text.toString()) // Update Title
                                        FirebaseDatabase.getInstance().reference.child(mAuth!!.currentUser!!.uid).child("Food")
                                            .child(FirebaseKey).child("Calories").setValue(secondval.text.toString())// Update content
                                        Log.d("Success", "onSuccess: update database Completed!!")
                                    }
                                })
                        }
                        builder.setNegativeButton(getString(R.string.cancel)){ dialog, which ->
                            Toast.makeText(applicationContext,
                                getString(R.string.not_update),Toast.LENGTH_SHORT).show()
                        }
                        // Display a message on alert dialog
                        builder.setMessage(getString(R.string.Input_update))
                        val dialog: AlertDialog = builder.create()
                        dialog.setIcon(R.mipmap.ic_launcher_round)
                        // Display the alert dialog on app interface
                        dialog.show()
                    }
                    viewHolder.itemView.setOnLongClickListener {
                        val FirebaseKey = (mEntryList!!.findViewHolderForAdapterPosition(position)!!
                            .itemView.findViewById(R.id.mKey_Ontrack) as TextView).text.toString()
                        var img : String? = (mEntryList!!.findViewHolderForAdapterPosition(position)!!
                            .itemView.findViewById(R.id.mImage_Ontrack) as TextView).text.toString()
                        Log.d("Position",position.toString())
                        Log.d("mKey", FirebaseKey)
                        Log.d("mImage", img!!)
                        val builder = AlertDialog.Builder(this@OnTrackActivity)
                        builder.setTitle(getString(R.string.aleart_delete))
                        // Display a message on alert dialog
                        builder.setMessage(getString(R.string.aleart_delete))
                        builder.setPositiveButton(R.string.confirm) { dialog, which ->
                            deleteUserData(FirebaseKey)
//                                    deleteimg(img)
                            mEntryList!!.adapter!!.notifyItemChanged(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position,mEntryList!!.size)
                            notifyDataSetChanged()
                            notifyItemChanged(position)
                            mEntryList!!.adapter!!.notifyDataSetChanged();

                        }
                        // Display a negative button on alert dialog
                        builder.setNegativeButton(R.string.cancel) { dialog, which ->
                        }

                        // Display the alert dialog on app interface
                        val dialog: AlertDialog = builder.create()
                        dialog.setIcon(R.drawable.ic_delete)
                        dialog.show()
                        true
                    }
                }
            }
        mEntryList!!.adapter = firebaseRecyclerAdapter
        mEntryList!!.adapter!!.notifyDataSetChanged();
    }
    private fun deleteUserData(mKey: String) {

        FirebaseDatabase.getInstance().reference.child(mAuth!!.currentUser!!.uid).child("Food").child(mKey).
        addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // No Need to implement in this line of code
                TODO("Not yet implemented")
            }
            override fun onDataChange(p0: DataSnapshot) {
                // Remove a branch of Diary that related the mKey
                FirebaseDatabase.getInstance().reference.child(mAuth!!.currentUser!!.uid).child("Food").child(mKey).removeValue() // It's Work
                Log.d("Success", "onSuccess: deleted database Completed!!")
            }
        })
    }
    private fun deleteimg(path: String) {
        // Delete Image with using reference path

        val photoRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(path)
        photoRef.delete().addOnSuccessListener(object : OnSuccessListener<Void?> {
            override fun onSuccess(aVoid: Void?) {
                // File deleted successfully
                Log.d("Success", "onSuccess: deleted image Completed!!")
            }
        })
    }

}
