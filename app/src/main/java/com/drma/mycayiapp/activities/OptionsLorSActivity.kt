package com.drma.mycayiapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.drma.mycayiapp.R
import com.drma.mycayiapp.chat.ChatActivity

class OptionsLorSActivity : AppCompatActivity() {

    private lateinit var SignInB: Button
    private lateinit var LoginB: Button

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, OptionsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_ingreso)

        bind()
    }

    fun bind(){
        SignInB = findViewById(R.id.Entrar)
        LoginB = findViewById(R.id.Crear)

        LoginB.setOnClickListener {
            LoginActivity.start(this)
        }

        SignInB.setOnClickListener {
            var Intent: Intent = Intent(this@OptionsLorSActivity, SignInActivity::class.java)
            startActivity(Intent)
        }

    }
}