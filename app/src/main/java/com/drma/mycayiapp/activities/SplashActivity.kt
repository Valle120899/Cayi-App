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

private const val SPLASH_DELAY = 1500

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        Handler().postDelayed({
            if (SharedPrefsHelper.hasQbUser()) {
                LoginService.start(this, SharedPrefsHelper.getQbUser())
                var Intent: Intent = Intent(this@SplashActivity, ChatActivity::class.java)
                startActivity(Intent)
            } else {
                OptionsLorSActivity.start(this)
            }
            finish()
        }, SPLASH_DELAY.toLong())
    }


}