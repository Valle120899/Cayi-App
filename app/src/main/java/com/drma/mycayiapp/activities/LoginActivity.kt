package com.drma.mycayiapp.activities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.drma.mycayiapp.R
import com.drma.mycayiapp.utils.longToast
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.drma.mycayiapp.DEFAULT_USER_PASSWORD
import com.drma.mycayiapp.chat.ChatActivity
import com.drma.mycayiapp.services.LoginService
import com.drma.mycayiapp.util.signInUser
import com.drma.mycayiapp.util.signUp
import com.drma.mycayiapp.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.list_item_user.view.*

const val ERROR_LOGIN_ALREADY_TAKEN_HTTP_STATUS = 422

class LoginActivity : BaseActivity() {

    private lateinit var userLoginEditText: EditText
    private lateinit var userFullNameEditText: String
    private lateinit var Password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var returned: TextView
    private lateinit var Email:EditText

    private lateinit var mAuth:FirebaseAuth
    private lateinit var refUsers:DatabaseReference
    private var firebaseUserID:String = ""


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

        mAuth = FirebaseAuth.getInstance()

        userLoginEditText = findViewById(R.id.userLoginEditText)
        userLoginEditText.addTextChangedListener(LoginEditTextWatcher(userLoginEditText))

        Password = findViewById(R.id.Password)
        Password.addTextChangedListener(LoginEditTextWatcher(Password))

        confirmPassword = findViewById(R.id.confirmPassword)
        confirmPassword.addTextChangedListener(LoginEditTextWatcher(confirmPassword))

        Email = findViewById(R.id.userEmail)
        Email.addTextChangedListener(LoginEditTextWatcher(Email))

        returned = findViewById(R.id.returned)

        returned.setOnClickListener(){
            var Intent:Intent = Intent(this@LoginActivity, SignInActivity::class.java)
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
                var firstpass = Password.text.toString()
                var secondpass = confirmPassword.text.toString()
                if (isEnteredUserNameValid() && firstpass == secondpass) {
                    hideKeyboard()

                    val user = createUserWithEnteredData()
                    signUpNewUser(user)

                    RegisterUserFirebase()
                }else{
                    Toast.makeText(this,"Password do not match!", Toast.LENGTH_SHORT).show();
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun isEnteredUserNameValid(): Boolean {
        return isLoginValid(this, userLoginEditText)
    }

    fun RegisterUserFirebase(){
        val username:String = userLoginEditText.text.toString()
        val email:String = userEmail.text.toString()
        val password:String = Password.text.toString()

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            if(it.isSuccessful){
                firebaseUserID = mAuth.currentUser!!.uid
                refUsers= FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                val userHasMap= HashMap<String, Any>()
                userHasMap["uid"] = firebaseUserID
                userHasMap["username"] = username
                userHasMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/cayi-app-2e512.appspot.com/o/Users%20Image%2Fprofile_pic.png?alt=media&token=b5e76d46-eb0c-431c-a5ac-4b028387d4f0"
                userHasMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/cayi-app-2e512.appspot.com/o/Users%20Image%2Flogin_background.png?alt=media&token=37506d14-90f8-4e6b-b82b-b9634f6930c1"
                userHasMap["status"] = "offline"
                userHasMap["search"] = username.toLowerCase()


                refUsers.updateChildren(userHasMap).addOnCompleteListener {
                    task ->  if(task.isSuccessful){
                    var intent: Intent = Intent(this@LoginActivity, ChatActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    Toast.makeText(this,"Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
                    finish()
                }
                }

            }
        }
    }


    private fun isEnteredPasswordValid(): Boolean {
        return isPasswordValid(this, Password)
    }

    private fun hideKeyboard() {
        hideKeyboard(userLoginEditText)
        hideKeyboard(Password)
        hideKeyboard(confirmPassword)
        hideKeyboard(Email)
    }

    private fun signUpNewUser(newUser: QBUser) {
        showProgressDialog(R.string.dlg_creating_new_user)
        signUp(newUser, object : QBEntityCallback<QBUser> {
            override fun onSuccess(result: QBUser, params: Bundle) {
                SharedPrefsHelper.saveQbUser(newUser)
                loginToChat(result)
                //SignInActivity.start(this@LoginActivity)
                //Toast.makeText(this@LoginActivity, "Usuario creado correctamente", Toast.LENGTH_SHORT).show()
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
        val userFullName = userLogin
        val userEmail = Email.text.toString()
        qbUser.login = userLogin
        qbUser.fullName = userFullName
        qbUser.email = userEmail
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