package com.drma.mycayiapp.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drma.mycayiapp.R

class FriendsActivity : AppCompatActivity() {

    private var myFriendlist: RecyclerView = TODO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        myFriendlist = findViewById<RecyclerView>(R.id.friend_list)
        myFriendlist.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        myFriendlist.layoutManager = linearLayoutManager
    }

    class FriendsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        internal var mView:View = itemView
    }
}