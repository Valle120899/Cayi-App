package com.example.Cayi

import android.app.Application
import com.example.Cayi.utils.SAMPLE_CONFIG_FILE_NAME
import com.example.Cayi.utils.getAllUsersFromFile
import com.quickblox.auth.session.QBSettings
import com.quickblox.users.model.QBUser

class App : Application() {
    private val applicationID = "82195"
    private val authKey = "gyFWOQTVxLyPyWs"
    private val authSecret = "D2T3nUXRrt7zZDH"
    private val accountKey = "U4w-3v3B447BCxbFBvPT"

    override fun onCreate() {
        super.onCreate()
        checkQBConfigJson()
        checkUserJson()
        initCredentials()
    }

    private fun checkQBConfigJson() {
        if (applicationID.isEmpty() || authKey.isEmpty() || authSecret.isEmpty() || accountKey.isEmpty()) {
            throw AssertionError(getString(R.string.error_qb_credentials_empty))
        }
    }

    private fun checkUserJson() {
        val users = getAllUsersFromFile(
            SAMPLE_CONFIG_FILE_NAME,
            this
        )
        if (users.size !in 2..4 || isUsersEmpty(users))
            throw AssertionError(getString(R.string.error_users_empty))
    }

    private fun isUsersEmpty(users: ArrayList<QBUser>): Boolean {
        users.forEach { user -> if (user.login.isBlank() || user.password.isBlank()) return true }
        return false
    }

    private fun initCredentials() {
        QBSettings.getInstance().init(applicationContext, applicationID, authKey, authSecret)
        QBSettings.getInstance().accountKey = accountKey

        // Uncomment and put your Api and Chat servers endpoints if you want to point the sample
        // against your own server.
        //
        // Please note. If you plan to migrate from the shared instance to enterprise,
        // you shouldn't set the custom endpoints
        //
        // QBSettings.getInstance().setEndpoints("https://your_api_endpoint.com", "your_chat_endpoint", ServiceZone.PRODUCTION);
        // QBSettings.getInstance().zone = ServiceZone.PRODUCTION
    }


}