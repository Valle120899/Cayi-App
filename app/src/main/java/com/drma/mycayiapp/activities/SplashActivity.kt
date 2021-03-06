package com.drma.mycayiapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.drma.mycayiapp.R
import com.drma.mycayiapp.chat.ChatActivity
import com.google.firebase.auth.FirebaseAuth

private const val SPLASH_DELAY = 1500

class SplashActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        Handler().postDelayed({
        if (FirebaseAuth.getInstance().currentUser == null) {
            var Intent: Intent = Intent(this@SplashActivity, OptionsLorSActivity::class.java)
            startActivity(Intent)
        }
        else {
            var Intent: Intent = Intent(this@SplashActivity, ChatActivity::class.java)
            startActivity(Intent)
        }
        finish()
    }, SPLASH_DELAY.toLong())
    }


}