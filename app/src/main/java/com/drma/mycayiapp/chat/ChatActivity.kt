package com.drma.mycayiapp.chat

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
import com.drma.mycayiapp.chat.fragment.ChatFragment
import com.drma.mycayiapp.chat.fragment.SearchFragment
import com.drma.mycayiapp.chat.fragment.SettingFragment
import com.drma.mycayiapp.chat.modelclasses.Chat
import com.drma.mycayiapp.chat.modelclasses.Users
import com.drma.mycayiapp.utils.SharedPrefsHelper
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat2.*

class ChatActivity : AppCompatActivity() {

    var refUsers : DatabaseReference? = null
    var firebaseUser : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat2)
        setSupportActionBar(toolbar_chat)


        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val toolbar : Toolbar = findViewById(R.id.toolbar_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""

        //De aquí para abajo se modificó el ChatActivity, eliminando los PagerAdapter comentados y
        //colocándolos en el listener de abajo
        val tablayout : TabLayout = findViewById(R.id.nav_chat_menu)
        val viewpager : ViewPager = findViewById(R.id.view_id)

        val ref = FirebaseDatabase.getInstance().reference.child("chats")
        ref!!.addValueEventListener(object : ValueEventListener{
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

                viewPagerAdapter.addFragment(SearchFragment(), "Search")
                viewPagerAdapter.addFragment(SettingFragment(), "Settings")
                viewpager.adapter = viewPagerAdapter
                tablayout.setupWithViewPager(viewpager)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


        refUsers!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user : Users? = p0.getValue(Users::class.java)
                    user_name.text= user!!.getusername()
                    Picasso.get().load(user.getprofile()).placeholder(R.drawable.ic_person_big).into(profile_image)
                }
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
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