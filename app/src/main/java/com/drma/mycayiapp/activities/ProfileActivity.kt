package com.drma.mycayiapp.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.drma.mycayiapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso


class ProfileActivity : AppCompatActivity() {

    private lateinit var saveBtn: Button
    private lateinit var userNameET: EditText
    private lateinit var userBioET: EditText
    private lateinit var profileImageView: ImageView
    private var GalleyPick: Int = 1
    private lateinit var ImageUri: Uri
    private lateinit var userProfileImgRef: StorageReference
    private lateinit var downloadUrl: String
    private lateinit var userRef: DatabaseReference
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        bind()

        profileImageView.setOnClickListener() {
            var GalleryIntent = Intent()
            GalleryIntent.setAction(Intent.ACTION_GET_CONTENT)
            GalleryIntent.setType("image/*")
            startActivityForResult(GalleryIntent, GalleyPick)
        }

        saveBtn.setOnClickListener() {
            saveUserData()
        }

        retrieveUserInfo()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GalleyPick && resultCode == Activity.RESULT_OK && data!!.data != null) {
            ImageUri = data.data!!
            profileImageView.setImageURI(ImageUri)
        }
    }

    private fun saveUserData() {
        val getUserName: String = userNameET.text.toString()
        val getUserStatus: String = userBioET.text.toString()

        if (ImageUri == null) {

            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .hasChild("image")
                    ) {
                        saveInfoOnlyWithoutImage()
                    } else {
                        Toast.makeText(
                            this@ProfileActivity,
                            "Por favor seleccione una foto primero",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }

            })

        } else if (getUserName.equals("")) {
            Toast.makeText(this, "Nombre de usuario obligatorio", Toast.LENGTH_SHORT).show()
        } else if (getUserStatus.equals("")) {
            Toast.makeText(this, "Estado de usuario obligatorio", Toast.LENGTH_SHORT).show()
        } else {
            progressDialog.setTitle("Ajustes de cuenta")
            progressDialog.setMessage("Por favor espere.")
            progressDialog.show()

            val filePath: StorageReference? =
                userProfileImgRef.child(FirebaseAuth.getInstance().currentUser!!.uid)

            val uploadTask: UploadTask = filePath!!.putFile(ImageUri)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                downloadUrl = filePath.downloadUrl.toString()
                filePath.downloadUrl

            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    downloadUrl = task.result.toString()
                    var profileMap: HashMap<String, String> = HashMap<String, String>()
                    profileMap.put("uid", FirebaseAuth.getInstance().currentUser!!.uid)
                    profileMap.put("name", getUserName)
                    profileMap.put("status", getUserStatus)
                    profileMap.put("image", downloadUrl)

                    userRef.child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .updateChildren(profileMap as Map<String, String>).addOnCompleteListener{task->
                            if (task.isSuccessful) {
                                var intent: Intent = Intent(this@ProfileActivity, OpponentsActivity::class.java)
                                startActivity(intent)
                                finish()
                                progressDialog.dismiss()
                                Toast.makeText(
                                    this@ProfileActivity,
                                    "Perfil actualizado",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                }
            }

        }

    }

    private fun saveInfoOnlyWithoutImage() {

        var getUserName: String = userNameET.text.toString()
        var getUserStatus: String = userBioET.text.toString()


        if (getUserName.equals("")) {
            Toast.makeText(this, "Nombre de usuario obligatorio", Toast.LENGTH_SHORT).show()
        } else if (getUserStatus.equals("")) {
            Toast.makeText(this, "Estado de usuario obligatorio", Toast.LENGTH_SHORT).show()
        } else {
            progressDialog.setTitle("Ajustes de cuenta")
            progressDialog.setMessage("Por favor espere.")
            progressDialog.show()

            var profileMap: HashMap<String, Any> = HashMap<String, Any>()
            profileMap.put("uid", FirebaseAuth.getInstance().currentUser!!.uid)
            profileMap.put("name", getUserName)
            profileMap.put("status", getUserStatus)


            userRef.child(FirebaseAuth.getInstance().currentUser!!.uid)
                .updateChildren(profileMap as Map<String, Any>).addOnCompleteListener() {
                    if (it.isSuccessful) {
                        var intent: Intent = Intent(this@ProfileActivity, OpponentsActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                        Toast.makeText(this@ProfileActivity, "Perfil actualizado", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun bind() {
        progressDialog = ProgressDialog(this)
        userRef = FirebaseDatabase.getInstance().reference.child("Users")
        userProfileImgRef = FirebaseStorage.getInstance().reference.child("Users Image")
        saveBtn = findViewById(R.id.save_settings_btn)
        userNameET = findViewById(R.id.username_settings)
        userBioET = findViewById(R.id.bio_settings)
        profileImageView = findViewById(R.id.ImageUsername)
    }

    private fun retrieveUserInfo(){

        userRef.child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    var imageDB:String = p0.child("image").getValue().toString()
                    var nameDB:String = p0.child("name").getValue().toString()
                    var bioDB:String = p0.child("status").getValue().toString()
                    userNameET.setText(nameDB)
                    userBioET.setText(bioDB)
                    Picasso.get().load(imageDB).placeholder(R.drawable.profile_image).into(profileImageView)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }


}
