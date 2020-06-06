package com.drma.mycayiapp.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.drma.mycayiapp.R
import com.drma.mycayiapp.chat.modelclasses.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_visit_user_profile.*

class VisitUserProfileActivity : AppCompatActivity() {

    private var userVisitId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_user_profile)

        userVisitId = intent.getStringExtra("visit_id")

        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userVisitId)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user = p0.getValue(Users::class.java)

                    username_display.text = user!!.getusername()
                    Picasso.get().load(user.getprofile()).into(profile_display)
                    Picasso.get().load(user.getcover()).into(cover_display)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}