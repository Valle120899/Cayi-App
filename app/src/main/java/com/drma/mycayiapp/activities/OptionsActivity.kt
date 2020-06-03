package com.drma.mycayiapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.drma.mycayiapp.R

class OptionsActivity : AppCompatActivity() {

    private lateinit var videocallopt: Button
    private lateinit var chatopt: Button

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, OptionsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        bind()
    }

    fun bind(){
        videocallopt = findViewById(R.id.btn_videocall)
        chatopt = findViewById(R.id.btn_chat)

        videocallopt.setOnClickListener {
            OpponentsActivity.start(this)
        }

        chatopt.setOnClickListener {
            var Intent: Intent = Intent(this@OptionsActivity, ChatActivity::class.java)
            startActivity(Intent)
        }

    }
}
