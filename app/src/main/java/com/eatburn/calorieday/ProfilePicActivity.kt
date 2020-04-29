package com.eatburn.calorieday

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_profile_pic.*
import java.io.IOException

class ProfilePicActivity : AppCompatActivity() {

    // Value for request to open gallery
    private val PICK_IMAGE_REQUEST = 234

    //a Uri object to store file path
    private var filePath: Uri? = null
    private var storageReference: StorageReference? = null
    private var mDatabase: DatabaseReference? = null
    var mAuth: FirebaseAuth? = null
    var name: DataSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_pic)
        pic_continueBtn.setOnClickListener {
            uploadFile()
            startActivity(Intent(this@ProfilePicActivity, HelthInfoActivity::class.java))
        }
        custom_photo_btn.setOnClickListener {
            showFileChooser()
        }
        mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase!!.keepSynced(true)
        storageReference = FirebaseStorage.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
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
                img_id.setImageBitmap(bitmap)
                text_hint.text = filePath?.path!!.substring(filePath?.path!!.lastIndexOf("/")+1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadFile(){
        if(filePath != null)
        {
            val storage = FirebaseStorage.getInstance()
            var storageRef = storage.reference
            var imagesRef: StorageReference? = storageRef.child(mAuth!!.currentUser!!.uid).child("UserInfo")
            var spaceRef = storageRef.child("images/" + mAuth?.uid.toString()+ "/" + filePath?.path!!.substring(filePath?.path!!.lastIndexOf("/")+1).toString())
            Toast.makeText(applicationContext, "File Uploaded ", Toast.LENGTH_LONG).show();
            spaceRef.putFile(filePath!!).addOnSuccessListener( OnSuccessListener<UploadTask.TaskSnapshot>() {
                spaceRef.downloadUrl.addOnCompleteListener {
                    mDatabase!!.child(mAuth!!.currentUser!!.uid).child("UserInfo").child("images").setValue(it.result.toString())
                    mDatabase!!.child(mAuth!!.currentUser!!.uid).child("UserInfo").child("uid").setValue(mAuth!!.currentUser!!.uid)
                }
            })
                .addOnFailureListener(OnFailureListener{
                })
                .addOnProgressListener(OnProgressListener {
                })
        }
        else
        {
            Toast.makeText(this, "No File Upload" , Toast.LENGTH_SHORT).show()
        }
    }
}
