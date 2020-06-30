package com.drma.mycayiapp.chat.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.drma.mycayiapp.R
import com.drma.mycayiapp.chat.modelclasses.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import io.fabric.sdk.android.services.common.CommonUtils.checkPermission
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.fragment_setting.view.*
import kotlinx.android.synthetic.main.fragment_setting.view.user_id

class SettingFragment : Fragment() {

    private val PermissionRequest = 10
    var usersRefrence: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private val RequestCode = 438
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker: String?= ""
    private var permissions = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private var textId : String? = ""
    private var textCorreo : String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersRefrence = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        usersRefrence!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user : Users? = p0.getValue(Users::class.java)
                    if(context!=null){
                        view.username_profile.text = user!!.getusername()
                        view.user_id.text = user!!.getuid()
                        view.user_email.text= firebaseUser!!.email.toString()
                        Picasso.get().load(user.getprofile()).into(view.profile_image)
                        Picasso.get().load(user.getcover()).into(view.cover_image)
                        Picasso.get().load(user.getimage1()).into(view.image1)
                        Picasso.get().load(user.getimage2()).into(view.image2)
                        Picasso.get().load(user.getimage3()).into(view.image3)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        view.profile_image.setOnClickListener{
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                //preguntar si tiene permiso
                if (checkPermission(context,permissions)) {
                    pickImage()
                } else {
                    val permisoArchivos = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permisoArchivos, PermissionRequest)
                }
            }

        }

        view.image1.setOnClickListener{
            coverChecker = "image1"
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                //preguntar si tiene permiso
                if (checkPermission(context,permissions)) {
                    pickImage()
                } else {
                    val permisoArchivos = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permisoArchivos, PermissionRequest)
                }
            }

        }

        view.image2.setOnClickListener{
            coverChecker = "image2"
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                //preguntar si tiene permiso
                if (checkPermission(context,permissions)) {
                    pickImage()
                } else {
                    val permisoArchivos = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permisoArchivos, PermissionRequest)
                }
            }

        }

        view.image3.setOnClickListener{
            coverChecker = "image3"
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                //preguntar si tiene permiso
                if (checkPermission(context,permissions)) {
                    pickImage()
                } else {
                    val permisoArchivos = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permisoArchivos, PermissionRequest)
                }
            }

        }

        view.cover_image.setOnClickListener{
            coverChecker = "cover"
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                //preguntar si tiene permiso
                if (checkPermission(context,permissions)) {
                    pickImage()
                } else {
                    val permisoArchivos = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permisoArchivos, PermissionRequest)
                }
            }
        }

        view.user_id.setOnLongClickListener {
            textId = user_id.text.toString()
            val clipboard = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            var clipData = ClipData.newPlainText("Id", textId)
            clipboard.primaryClip = clipData
            Toast.makeText(context, "Id copiado en el portapapeles", Toast.LENGTH_LONG).show()
            return@setOnLongClickListener false
        }


        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PermissionRequest ->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    pickImage()
                else
                    Toast.makeText(context, "No se puede acceder a las imagenes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==RequestCode && resultCode==Activity.RESULT_OK && data?.data!= null){
            imageUri= data?.data
            try {
                uploadImageToDatabase()
            } catch (e:Exception){
                Toast.makeText(activity, "La imagen pesa demasiado.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Image is uploading, please wait...")
        progressBar.show()

        if(imageUri != null){
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener{task ->
                if(task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if(coverChecker == "cover"){
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = url
                        usersRefrence!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    }
                    else if(coverChecker == "image1"){
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["image1"] = url
                        usersRefrence!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    }
                    else if(coverChecker == "image2"){
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["image2"] = url
                        usersRefrence!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    }
                    else if(coverChecker == "image3"){
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["image3"] = url
                        usersRefrence!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    }
                    else{
                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["profile"] = url
                        usersRefrence!!.updateChildren(mapProfileImg)
                        coverChecker = ""
                    }
                    progressBar.dismiss()
                }
            }
        }
    }

}