package com.drma.mycayiapp.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.drma.mycayiapp.R

import com.drma.mycayiapp.chat.modelclasses.IDFriends

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class FindFriendActivity : AppCompatActivity() {

    private lateinit var id_user:EditText
    private lateinit var add:Button
    private lateinit var delete:Button
    private var firebaseUser: FirebaseUser? = null
    var refUsers : DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_friend)
        bind()
    }

    fun bind(){

        id_user = findViewById(R.id.id_user)
        add = findViewById(R.id.add)
        delete = findViewById(R.id.delete)

        add.setOnClickListener(){
            GuardarAmigo()
        }

        delete.setOnClickListener(){
            EliminaAmigo()
        }
        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Amigos").child(firebaseUser!!.uid)
    }

    fun GuardarAmigo(){
        var name = id_user.text.toString()
        if(name.isEmpty()){
            Toast.makeText(this, "Escriba un ID", Toast.LENGTH_SHORT).show()
        }else{
            val user= IDFriends(name)
            refUsers?.child(name)?.setValue(user)
            Toast.makeText(this, "Usuario agregado correctamente", Toast.LENGTH_SHORT).show()
            id_user.setText("")
        }
    }

    fun EliminaAmigo() {
        var name = id_user.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(this, "Escriba un ID", Toast.LENGTH_SHORT).show()
        } else {
            val user = IDFriends(name)
            refUsers?.child(name)?.removeValue()
            Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()
            id_user.setText("")
        }
    }

}
