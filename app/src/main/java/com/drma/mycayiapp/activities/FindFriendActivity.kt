package com.drma.mycayiapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.drma.mycayiapp.R
import java.util.ArrayList
import com.drma.mycayiapp.activities.OpponentsActivity
import com.drma.mycayiapp.services.LoginService
import java.lang.Exception

class FindFriendActivity : AppCompatActivity() {

    private lateinit var id_user:EditText
    private lateinit var add:Button
    private lateinit var delete:Button
    private  var id:Int = 0
    private var usersIds:ArrayList<Int> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_friend)
        bind()

    }

    fun bind(){


        id_user = findViewById(R.id.id_user)
        add = findViewById(R.id.add)
        delete = findViewById(R.id.delete)
        var bundle: Bundle? = intent.extras
        usersIds = bundle!!.getIntegerArrayList("Lista")



        add.setOnClickListener(){
            try {

                id = id_user.text.toString().toInt()
                if (id == 0) {
                    Toast.makeText(this, "Ingrese un ID correcto", Toast.LENGTH_SHORT).show()
                } else {
                    usersIds.add(id)
                    Toast.makeText(this, "Usuario agregado correctamente", Toast.LENGTH_SHORT)
                        .show()
                    var intent: Intent = Intent(this, OpponentsActivity::class.java)
                    intent.putExtra("Lista", usersIds)
                    startActivity(intent)
                    finish()
                }
            }catch (e:Exception){
                Toast.makeText(this, "Ingrese un valor", Toast.LENGTH_SHORT).show()
            }
        }

        delete.setOnClickListener(){
            try {
                id = id_user.text.toString().toInt()
                if (id == 0) {
                    Toast.makeText(this, "Ingrese un ID correcto", Toast.LENGTH_SHORT).show()
                } else {
                    usersIds.remove(id)
                    Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT)
                        .show()
                    var intent: Intent = Intent(this, OpponentsActivity::class.java)
                    intent.putExtra("Lista", usersIds)
                    startActivity(intent)
                    finish()
                }
            }catch (e:Exception){
                Toast.makeText(this, "Ingrese un valor", Toast.LENGTH_SHORT).show()
            }
        }



    }
}
