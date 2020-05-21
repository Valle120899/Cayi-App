package com.drma.mycayiapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.drma.mycayiapp.R
import com.drma.mycayiapp.utils.longToast
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.drma.mycayiapp.DEFAULT_USER_PASSWORD
import com.drma.mycayiapp.services.LoginService
import com.drma.mycayiapp.util.signInUser
import com.drma.mycayiapp.util.signUp
import com.drma.mycayiapp.utils.*
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBUser

const val ERROR_LOGIN_ALREADY_TAKEN_HTTP_STATUS = 422

class LoginActivity : BaseActivity() {

    private lateinit var userLoginEditText: EditText
    private lateinit var userFullNameEditText: EditText
    private lateinit var Password: EditText

    private lateinit var user: QBUser

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, LoginActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initUI()
    }

    private fun initUI() {
        supportActionBar?.title = getString(R.string.title_login_activity)

        userLoginEditText = findViewById(R.id.userLoginEditText)
        userLoginEditText.addTextChangedListener(LoginEditTextWatcher(userLoginEditText))

        userFullNameEditText = findViewById(R.id.userFullNameEditText)
        userFullNameEditText.addTextChangedListener(LoginEditTextWatcher(userFullNameEditText))

        Password = findViewById(R.id.Password)
        Password.addTextChangedListener(LoginEditTextWatcher(Password))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_login, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_login_user_done -> {
                if (isEnteredUserNameValid() && isEnteredRoomNameValid()) {
                    hideKeyboard()
                    val user = createUserWithEnteredData()
                    signUpNewUser(user)
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun isEnteredUserNameValid(): Boolean {
        return isLoginValid(this, userLoginEditText)
    }

    private fun isEnteredRoomNameValid(): Boolean {
        return isFoolNameValid(this, userFullNameEditText)
    }

    private fun isEnteredPasswordValid(): Boolean {
        return isPasswordValid(this, Password)
    }

    private fun hideKeyboard() {
        hideKeyboard(userLoginEditText)
        hideKeyboard(userFullNameEditText)
        hideKeyboard(Password)
    }

    private fun signUpNewUser(newUser: QBUser) {
        showProgressDialog(R.string.dlg_creating_new_user)
        signUp(newUser, object : QBEntityCallback<QBUser> {
            override fun onSuccess(result: QBUser, params: Bundle) {
                SharedPrefsHelper.saveQbUser(newUser)
                loginToChat(result)
                SignInActivity.start(this@LoginActivity)
                Toast.makeText(this@LoginActivity, "Usuario creado correctamente", Toast.LENGTH_SHORT).show()
            }

            override fun onError(e: QBResponseException) {
                if (e.httpStatusCode == ERROR_LOGIN_ALREADY_TAKEN_HTTP_STATUS) {
                    hideProgressDialog()
                    longToast(R.string.sign_up_error)
                }
            }
        })
    }

    private fun loginToChat(qbUser: QBUser) {
        qbUser.password = Password.text.toString()
        user = qbUser
        startLoginService(qbUser)
    }

    private fun createUserWithEnteredData(): QBUser {
        val qbUser = QBUser()
        val userLogin = userLoginEditText.text.toString()
        val userFullName = userFullNameEditText.text.toString()
        qbUser.login = userLogin
        qbUser.fullName = userFullName
        qbUser.password = Password.text.toString()
        return qbUser
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