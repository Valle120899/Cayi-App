package com.drma.mycayiapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import com.drma.mycayiapp.R
import com.drma.mycayiapp.activities.SignInActivity.Companion.start
import com.drma.mycayiapp.chat.ChatActivity
import com.drma.mycayiapp.services.LoginService
import com.drma.mycayiapp.utils.SharedPrefsHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

private const val SPLASH_DELAY = 1500

class SplashActivity : BaseActivity() {

    var FirebaseUser:FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        if (FirebaseAuth.getInstance().currentUser == null) {
            var Intent: Intent = Intent(this@SplashActivity, OptionsLorSActivity::class.java)
            startActivity(Intent)
        }
        else {
            var Intent: Intent = Intent(this@SplashActivity, ChatActivity::class.java)
            startActivity(Intent)
        }
        finish()
    }


}