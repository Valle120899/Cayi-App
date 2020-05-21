package com.drma.mycayiapp.activities

import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import com.drma.mycayiapp.R
import com.drma.mycayiapp.services.LoginService
import com.drma.mycayiapp.utils.SharedPrefsHelper

private const val SPLASH_DELAY = 1500

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        fillVersion()
        Handler().postDelayed({
            if (SharedPrefsHelper.hasQbUser()) {
                LoginService.start(this, SharedPrefsHelper.getQbUser())
                OpponentsActivity.start(this)
            } else {
                SignInActivity.start(this)
            }
            finish()
        }, SPLASH_DELAY.toLong())
    }

    private fun fillVersion() {
        val appName = getString(R.string.app_name)
       // findViewById<TextView>(R.id.text_splash_app_title).text = appName
        //val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        //findViewById<TextView>(R.id.text_splash_app_version).text = getString(R.string.splash_app_version, versionName)
    }
}