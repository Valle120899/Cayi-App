package com.drma.mycayiapp.chat

import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.drma.mycayiapp.R
import com.drma.mycayiapp.chat.modelclasses.Users
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_visit_user_profile.*
import java.text.SimpleDateFormat
import java.util.*


class VisitUserProfileActivity : AppCompatActivity() {

    private var userVisitId: String = ""
    private var FriendRequestRef: DatabaseReference? = null
    var UsersRef: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var senderUserId: String = ""
    private var receiverUserId:String? = ""
    private var CURRENT_STATE:String = "not_friends"
    private var FriendsRef : DatabaseReference? = null
    private var saveCurrentDate : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_user_profile)

        userVisitId = intent.getStringExtra("visit_id")

        mAuth = FirebaseAuth.getInstance()
        senderUserId = mAuth!!.currentUser!!.uid
        UsersRef = FirebaseDatabase.getInstance().reference.child("Users")
        FriendRequestRef = FirebaseDatabase.getInstance().reference.child("FriendRequests")
        FriendsRef = FirebaseDatabase.getInstance().reference.child("Friends")


        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userVisitId)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user = p0.getValue(Users::class.java)

                    username_display.text = user!!.getusername()
                    Picasso.get().load(user!!.getprofile()).into(profile_display)
                    Picasso.get().load(user!!.getcover()).into(cover_display)

                    MaintananceofButtons();
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        if (!senderUserId.equals(receiverUserId))
        {
            send_friend_request_btn.setOnClickListener {

                send_friend_request_btn.setEnabled(false)
                if (CURRENT_STATE.equals("not_friends"))
                {
                    SendFriendRequestToaPerson()
                }
                if (CURRENT_STATE.equals("request_sent"))
                {
                    CancelFriendRequest()
                }
                if(CURRENT_STATE == "request_received"){
                    AcceptFriendRequest()
                }
                if(CURRENT_STATE.equals("friends")){
                    UnfriendAnExistingFriend();
                }
            }
        }
        else
        {
            decline_request_btn.visibility = INVISIBLE
            send_friend_request_btn.visibility = INVISIBLE
        }
    }

    private fun UnfriendAnExistingFriend() {
        FriendsRef!!.child(senderUserId).child(userVisitId)
            .removeValue()
            .addOnCompleteListener{task ->
                if (task.isSuccessful)
                {
                    FriendsRef!!.child(userVisitId).child(senderUserId)
                        .removeValue()
                        .addOnCompleteListener{task ->
                            if (task.isSuccessful)
                            {
                                send_friend_request_btn.setEnabled(true)
                                CURRENT_STATE = "not_friends"
                                send_friend_request_btn.setText("Send Friend Request")
                                decline_request_btn.visibility = View.INVISIBLE
                                decline_request_btn.isEnabled = false
                            }
                        }
                }
            }
    }

    private fun SendFriendRequestToaPerson() {
        FriendRequestRef!!.child(senderUserId).child(userVisitId)
            .child("request_type").setValue("sent")
            .addOnCompleteListener{ task ->
                    if (task.isSuccessful())
                    {
                        FriendRequestRef!!.child(userVisitId).child(senderUserId)
                            .child("request_type").setValue("received")
                            .addOnCompleteListener { task ->
                                    if (task.isSuccessful())
                                    {
                                        send_friend_request_btn.setEnabled(true)
                                        CURRENT_STATE = "request_sent"
                                        send_friend_request_btn.setText("Cancel friend request")
                                        decline_request_btn.visibility = View.INVISIBLE
                                        decline_request_btn.isEnabled = false
                                    }
                            }
                    }
            }
    }

    private fun AcceptFriendRequest() {
        var calForDate: Calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd-MMMM-yyyy")
        saveCurrentDate = currentDate.format(calForDate.time)

        receiverUserId?.let {
            FriendsRef!!.child(senderUserId).child(it).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener{task ->
                    if (task.isSuccessful) {
                        FriendsRef!!.child(it).child(senderUserId).child("date")
                            .setValue(saveCurrentDate)
                            .addOnCompleteListener {task ->
                                if(task.isSuccessful){
                                    FriendRequestRef!!.child(senderUserId).child(userVisitId)
                                        .removeValue()
                                        .addOnCompleteListener{task ->
                                            if (task.isSuccessful)
                                            {
                                                FriendRequestRef!!.child(it).child(senderUserId)
                                                    .removeValue()
                                                    .addOnCompleteListener{task ->
                                                        if (task.isSuccessful)
                                                        {
                                                            send_friend_request_btn.setEnabled(true)
                                                            CURRENT_STATE = "friends"
                                                            send_friend_request_btn.setText("Unfriend this person")
                                                            decline_request_btn.visibility = View.INVISIBLE
                                                            decline_request_btn.isEnabled = false
                                                        }
                                                    }
                                            }
                                        }
                                }

                            }

                }
            }
        }
    }


    private fun CancelFriendRequest() {
        FriendRequestRef!!.child(senderUserId).child(userVisitId)
            .removeValue()
            .addOnCompleteListener{task ->
                    if (task.isSuccessful)
                    {
                        FriendRequestRef!!.child(userVisitId).child(senderUserId)
                            .removeValue()
                            .addOnCompleteListener{task ->
                                    if (task.isSuccessful)
                                    {
                                        send_friend_request_btn.setEnabled(true)
                                        CURRENT_STATE = "not_friends"
                                        send_friend_request_btn.setText("Send Friend Request")
                                        decline_request_btn.visibility = View.INVISIBLE
                                        decline_request_btn.isEnabled = false
                                    }
                            }
                    }
            }
    }

    private fun MaintananceofButtons() {
        FriendRequestRef!!.child(senderUserId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChild(userVisitId))
                {
                    val request_type = p0.child(userVisitId).child("request_type").getValue().toString()
                    if (request_type == "sent")
                    {
                        CURRENT_STATE = "request_sent"
                        send_friend_request_btn.setText("Cancel Friend Request")
                        decline_request_btn.visibility = View.INVISIBLE
                        decline_request_btn.isEnabled = false
                    }
                    else if(request_type == "received"){
                        CURRENT_STATE = "request_received"
                        send_friend_request_btn.setText("Accept friend Request")

                        decline_request_btn.visibility = View.VISIBLE
                        decline_request_btn.isEnabled =  true

                        decline_request_btn.setOnClickListener{
                            fun onClick(V:View) {
                                CancelFriendRequest()
                            }
                        }
                    }
                }
                else{
                    FriendsRef!!.child(senderUserId).addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot){
                            if(p0.hasChild(userVisitId)){
                                CURRENT_STATE = "friends";
                                send_friend_request_btn.setText("Unfriend this person");

                                decline_request_btn.visibility = View.INVISIBLE
                                decline_request_btn.isEnabled = false
                            }
                        }
                    });
                }
            }
        })
    }

}