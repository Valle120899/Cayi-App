package com.drma.mycayiapp.activities

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drma.mycayiapp.R
import com.drma.mycayiapp.db.QbUsersDbManager
import com.drma.mycayiapp.services.CallService
import com.drma.mycayiapp.utils.longToast
import com.drma.mycayiapp.utils.shortToast
import com.quickblox.chat.QBChatService
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.core.request.GenericQueryRule
import com.quickblox.core.request.QBPagedRequestBuilder
import com.quickblox.messages.services.SubscribeService
import com.drma.mycayiapp.adapters.UsersAdapter
import com.drma.mycayiapp.services.LoginService
import com.drma.mycayiapp.util.loadUsersByPagedRequestBuilder
import com.drma.mycayiapp.util.signOut
import com.drma.mycayiapp.utils.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.quickblox.users.model.QBUser
import com.quickblox.videochat.webrtc.QBRTCClient
import com.quickblox.videochat.webrtc.QBRTCTypes
import java.lang.Exception


private const val PER_PAGE_SIZE_100 = 100
private const val ORDER_RULE = "order"
private const val ORDER_DESC_UPDATED = "desc date updated_at"

class OpponentsActivity : BaseActivity() {
    private val TAG = OpponentsActivity::class.java.simpleName

    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var currentUser: QBUser
    private lateinit var navView: BottomNavigationView
    private lateinit var ajustes: Button
    var contador: Int = 0

    private var usersIds: ArrayList<Int> = ArrayList<Int>()
    private var usersIds2: ArrayList<Int> = ArrayList<Int>()
    //intent.extras!!.getIntegerArrayList("returnedList")

    private var usersAdapter: UsersAdapter? = null

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, OpponentsActivity::class.java)
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation2)
        currentUser = SharedPrefsHelper.getQbUser()
        initDefaultActionBar()
        initUI()
        startLoginService()


    }

    override fun onResume() {
        super.onResume()
        val isIncomingCall = SharedPrefsHelper.get(EXTRA_IS_INCOMING_CALL, false)
        if (isCallServiceRunning(CallService::class.java)) {
            Log.d(TAG, "CallService is running now")
            CallActivity.start(this, isIncomingCall)
        }
        clearAppNotifications()
        loadUsers()
    }

    private fun isCallServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = manager.getRunningServices(Integer.MAX_VALUE)
        for (service in services) {
            if (CallService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun clearAppNotifications() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    private fun startPermissionsActivity(checkOnlyAudio: Boolean) {
        PermissionsActivity.startForResult(this, checkOnlyAudio, PERMISSIONS)
    }

    private fun loadUsers() {
        showProgressDialog(R.string.dlg_loading_opponents)

        val rules = ArrayList<GenericQueryRule>()
        rules.add(GenericQueryRule(ORDER_RULE, ORDER_DESC_UPDATED))
        val requestBuilder = QBPagedRequestBuilder()
        requestBuilder.rules = rules
        requestBuilder.perPage = PER_PAGE_SIZE_100

        loadUsersByPagedRequestBuilder(object : QBEntityCallback<java.util.ArrayList<QBUser>> {
            override fun onSuccess(result: ArrayList<QBUser>, params: Bundle) {
                QbUsersDbManager.saveAllUsers(result, true)
                initUsersList()
                hideProgressDialog()
            }

            override fun onError(responseException: QBResponseException) {
                hideProgressDialog()
                showErrorSnackbar(
                    R.string.loading_users_error,
                    responseException,
                    View.OnClickListener { loadUsers() })
            }
        }, requestBuilder)
    }

    private fun initUI() {
        ajustes = findViewById(R.id.ajustes)
        usersRecyclerView = findViewById(R.id.list_select_users)
        navView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_videocalls -> {
                    true
                }
                R.id.navigation_profile -> {
                    var Intent: Intent = Intent(this@OpponentsActivity, ProfileActivity::class.java)
                    startActivity(Intent)
                    true
                }

                else -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }
        ajustes.setOnClickListener() {
            var intent: Intent = Intent(this, FindFriendActivity::class.java)
            intent.putExtra("Lista", usersIds)
            startActivity(intent)
        }
    }

    private fun initUsersList() {
        try {
            if (contador != 0) {
                var bundle: Bundle? = intent.extras
                 usersIds = bundle!!.getIntegerArrayList("Lista")
            }
        }catch (e:Exception){
            Toast.makeText(this, "No hay usuarios por actualizar", Toast.LENGTH_SHORT).show()
        }
        contador++
        var currentOpponentsList = QbUsersDbManager.getUsersByIds(usersIds)
        currentOpponentsList.remove(SharedPrefsHelper.getQbUser())
        if (usersAdapter == null) {
            usersAdapter = UsersAdapter(this, currentOpponentsList)
            usersAdapter!!.setSelectedItemsCountsChangedListener(object :
                UsersAdapter.SelectedItemsCountsChangedListener {
                override fun onCountSelectedItemsChanged(count: Int) {
                    updateActionBar(count)
                }
            })

            usersRecyclerView.layoutManager = LinearLayoutManager(this)
            usersRecyclerView.adapter = usersAdapter
        } else {
            usersAdapter!!.updateUsersList(currentOpponentsList)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (usersAdapter != null && usersAdapter!!.selectedUsers.isNotEmpty()) {
            menuInflater.inflate(R.menu.activity_selected_opponents, menu)
        } else {
            menuInflater.inflate(R.menu.activity_opponents, menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.update_opponents_list -> {
                loadUsers()
                return true
            }
            R.id.settings -> {
                SettingsActivity.start(this)
                return true
            }
            R.id.log_out -> {
                logout()
                return true
            }
            R.id.start_video_call -> {
                if (checkIsLoggedInChat()) {
                    startCall(true)
                }
                if (checkPermissions(PERMISSIONS)) {
                    startPermissionsActivity(false)
                }
                return true
            }
            R.id.start_audio_call -> {
                if (checkIsLoggedInChat()) {
                    startCall(false)
                }
                if (checkPermission(PERMISSIONS[1])) {
                    startPermissionsActivity(true)
                }
                return true
            }
            R.id.appinfo -> {
                AppInfoActivity.start(this)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun checkIsLoggedInChat(): Boolean {
        if (!QBChatService.getInstance().isLoggedIn) {
            startLoginService()
            shortToast(R.string.login_chat_retry)
            return false
        }
        return true
    }

    private fun startLoginService() {
        if (SharedPrefsHelper.hasQbUser()) {
            LoginService.start(this, SharedPrefsHelper.getQbUser())
        }
    }

    private fun startCall(isVideoCall: Boolean) {
        val usersCount = usersAdapter!!.selectedUsers.size
        if (usersCount > MAX_OPPONENTS_COUNT) {
            longToast(
                String.format(
                    getString(R.string.error_max_opponents_count),
                    MAX_OPPONENTS_COUNT
                )
            )
            return
        }

        val opponentsList = getIdsSelectedOpponents(usersAdapter!!.selectedUsers)
        val conferenceType = if (isVideoCall) {
            QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
        } else {
            QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO
        }
        val qbrtcClient = QBRTCClient.getInstance(applicationContext)

        val newQbRtcSession =
            qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType)

        WebRtcSessionManager.setCurrentSession(newQbRtcSession)

        sendPushMessage(opponentsList, currentUser.fullName)

        CallActivity.start(this, false)
    }

    private fun getIdsSelectedOpponents(selectedUsers: Collection<QBUser>): ArrayList<Int> {
        val opponentsIds = ArrayList<Int>()
        if (!selectedUsers.isEmpty()) {
            for (qbUser in selectedUsers) {
                opponentsIds.add(qbUser.id)
            }
        }
        return opponentsIds
    }

    private fun updateActionBar(countSelectedUsers: Int) {
        if (countSelectedUsers < 1) {
            initDefaultActionBar()
        } else {
            val title = if (countSelectedUsers > 1) {
                R.string.tile_many_users_selected
            } else {
                R.string.title_one_user_selected
            }
            supportActionBar?.title = getString(title, countSelectedUsers)
            supportActionBar?.subtitle = null
        }
        invalidateOptionsMenu()
    }

    private fun initDefaultActionBar() {
        val currentUserFullName = SharedPrefsHelper.getQbUser().login
        supportActionBar?.title = ""
        supportActionBar?.subtitle =
            getString(R.string.subtitle_text_logged_in_as, currentUserFullName)
    }

    private fun logout() {
        SubscribeService.unSubscribeFromPushes(this)
        LoginService.logout(this)
        removeAllUserData()
        startLoginActivity()
    }

    private fun removeAllUserData() {
        SharedPrefsHelper.clearAllData()
        QbUsersDbManager.clearDB()
        signOut()
    }

    private fun startLoginActivity() {
        LoginActivity.start(this)
        finish()
    }
}