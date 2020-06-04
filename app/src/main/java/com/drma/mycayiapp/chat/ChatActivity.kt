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
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_chat2.*

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat2)
        setSupportActionBar(toolbar_chat)

        val toolbar : Toolbar = findViewById(R.id.toolbar_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""

        val tablayout : TabLayout = findViewById(R.id.nav_chat_menu)
        val viewpager : ViewPager = findViewById(R.id.view_id)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(ChatFragment(), "Chats")
        viewPagerAdapter.addFragment(SearchFragment(), "Search")
        viewPagerAdapter.addFragment(SettingFragment(), "Settings")

        viewpager.adapter = viewPagerAdapter
        tablayout.setupWithViewPager(viewpager)

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


}