package com.drma.mycayiapp.chat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.drma.mycayiapp.R
import com.drma.mycayiapp.activities.AppInfoActivity
import com.drma.mycayiapp.activities.LoginActivity
import com.drma.mycayiapp.activities.OpponentsActivity
import com.drma.mycayiapp.activities.SettingsActivity
import com.drma.mycayiapp.chat.AdapterClasses.ChatsAdapter
import com.drma.mycayiapp.chat.fragment.ChatFragment
import com.drma.mycayiapp.chat.fragment.SearchFragment
import com.drma.mycayiapp.chat.fragment.SettingFragment
import com.drma.mycayiapp.chat.modelclasses.Chat
import com.drma.mycayiapp.chat.modelclasses.Users
import com.drma.mycayiapp.db.QbUsersDbManager
import com.drma.mycayiapp.services.LoginService
import com.drma.mycayiapp.util.signOut
import com.drma.mycayiapp.utils.PERMISSIONS
import com.drma.mycayiapp.utils.SharedPrefsHelper
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.quickblox.messages.services.SubscribeService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat2.*

class ChatActivity : AppCompatActivity() {

    var refUsers : DatabaseReference? = null
    var firebaseUser : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat2)
        setSupportActionBar(toolbar_chat2)


        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val toolbar : Toolbar = findViewById(R.id.toolbar_chat2)
        setSupportActionBar(toolbar)

        supportActionBar!!.title=""

        //De aquí para abajo se modificó el ChatActivity, eliminando los PagerAdapter comentados y
        //colocándolos en el listener de abajo
        val tablayout : TabLayout = findViewById(R.id.nav_chat_menu)
        val viewpager : ViewPager = findViewById(R.id.view_id)

        val ref = FirebaseDatabase.getInstance().reference.child("chats")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

                var countUnreadMessages = 0

                for(dataSnapshot in p0.children){
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && !chat.isIsSeen())
                    {
                        countUnreadMessages += 1
                    }
                }

                if(countUnreadMessages == 0)
                {
                    viewPagerAdapter.addFragment(ChatFragment(), "Chats")
                }else
                {
                    viewPagerAdapter.addFragment(ChatFragment(), "($countUnreadMessages) Chats")
                }

                viewPagerAdapter.addFragment(SearchFragment(), "Búsqueda")
                viewPagerAdapter.addFragment(SettingFragment(), "Perfil")
                viewpager.adapter = viewPagerAdapter
                tablayout.setupWithViewPager(viewpager)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.settingsChat -> {
                SettingsActivity.start(this)
                return true
            }

            R.id.appinfoChat -> {
                AppInfoActivity.start(this)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

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

    internal class ViewPagerAdapter(fragmentManager : FragmentManager) :
        FragmentPagerAdapter(fragmentManager){
        private val fragments: ArrayList<Fragment>
        private val titles: ArrayList<String>

        init {
            fragments = ArrayList<Fragment>()
            titles = ArrayList<String>()
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title:String){
            fragments.add(fragment)
            titles.add(title)

        }

        override fun getPageTitle(i: Int): CharSequence? {
            return titles[i]
        }

    }

    private fun updateStatus(status: String){
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        ref!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()

        updateStatus("offline")
    }
}