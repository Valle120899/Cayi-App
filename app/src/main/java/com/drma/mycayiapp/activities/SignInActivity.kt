package com.drma.mycayiapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import com.drma.mycayiapp.R
import com.drma.mycayiapp.utils.longToast
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.drma.mycayiapp.services.LoginService
import com.drma.mycayiapp.util.signInUser
import com.drma.mycayiapp.utils.*
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBUser


class SignInActivity : BaseActivity() {

    private lateinit var userLoginEditText_SignIn: EditText
    private lateinit var Password_SignIn: EditText
    private lateinit var New_User_tv:TextView
    private lateinit var userfullnameEditText_SignIn:String

    private lateinit var user: QBUser

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, SignInActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        initUI()

    }

    private fun initUI() {
        supportActionBar?.title = getString(R.string.title_login_activity)

        userLoginEditText_SignIn = findViewById(R.id.usernameEditText_SignIn)
        userLoginEditText_SignIn.addTextChangedListener(LoginEditTextWatcher(userLoginEditText_SignIn))


        Password_SignIn = findViewById(R.id.Password_SignIn)
        Password_SignIn.addTextChangedListener(LoginEditTextWatcher(Password_SignIn))


        New_User_tv= findViewById(R.id.New_User_tv)

        New_User_tv.setOnClickListener(){
            var Intent:Intent = Intent(this@SignInActivity, LoginActivity::class.java)
            startActivity(Intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_login, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_login_user_done -> {
                if (isEnteredUserNameValid()) {
                    hideKeyboard()
                    val user = createUserWithEnteredData()
                    signInCreatedUser(user)
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun isEnteredUserNameValid(): Boolean {
        return isLoginValid(this, userLoginEditText_SignIn)
    }


    private fun isEnteredPasswordValid(): Boolean {
        return isPasswordValid(this, Password_SignIn)
    }

    private fun hideKeyboard() {
        hideKeyboard(userLoginEditText_SignIn)
        hideKeyboard(Password_SignIn)
    }

    private fun loginToChat(qbUser: QBUser) {
        qbUser.password = Password_SignIn.text.toString()
        user = qbUser
        startLoginService(qbUser)
        OpponentsActivity.start(this@SignInActivity)
        finish()

    }

    private fun createUserWithEnteredData(): QBUser {
        val qbUser = QBUser()
        val userLogin = userLoginEditText_SignIn.text.toString()
        val userFullLogin = userLogin
        qbUser.login = userLogin
        qbUser.fullName= userFullLogin
        qbUser.password = Password_SignIn.text.toString()
        return qbUser
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == EXTRA_LOGIN_RESULT_CODE) {
            hideProgressDialog()

            var isLoginSuccess = false
            data?.let {
                isLoginSuccess = it.getBooleanExtra(EXTRA_LOGIN_RESULT, false)
            }

            var errorMessage = getString(R.string.unknown_error)
            data?.let {
                errorMessage = it.getStringExtra(EXTRA_LOGIN_ERROR_MESSAGE)
            }

            if (isLoginSuccess) {
                SharedPrefsHelper.saveQbUser(user)
                signInCreatedUser(user)
            } else {
                longToast(getString(R.string.login_chat_login_error) + errorMessage)
                userLoginEditText_SignIn.setText(user.login)
                Password_SignIn.setText(user.password)
            }
        }
    }

    private fun signInCreatedUser(user: QBUser) {
        signInUser(user, object : QBEntityCallback<QBUser> {
            override fun onSuccess(result: QBUser, params: Bundle) {
                SharedPrefsHelper.saveQbUser(user)
                updateUserOnServer(user)
            }

            override fun onError(responseException: QBResponseException) {
                hideProgressDialog()
                longToast(R.string.sign_in_error)
            }
        })
    }

    private fun updateUserOnServer(user: QBUser) {
        user.password = null
        QBUsers.updateUser(user).performAsync(object : QBEntityCallback<QBUser> {
            override fun onSuccess(updUser: QBUser?, params: Bundle?) {
                hideProgressDialog()
                OpponentsActivity.start(this@SignInActivity)
                finish()
            }

            override fun onError(responseException: QBResponseException?) {
                hideProgressDialog()
                longToast(R.string.update_user_error)
            }
        })
    }

    override fun onBackPressed() {
        finish()
    }

    private fun startLoginService(qbUser: QBUser) {
        val tempIntent = Intent(this, LoginService::class.java)
        val pendingIntent = createPendingResult(EXTRA_LOGIN_RESULT_CODE, tempIntent, 0)
        LoginService.start(this, qbUser, pendingIntent)
    }

    private inner class LoginEditTextWatcher internal constructor(private val editText: EditText) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            editText.error = null
        }

        override fun afterTextChanged(s: Editable) {

        }
    }
}