package com.drma.mycayiapp.chat.AdapterClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drma.mycayiapp.R
import com.drma.mycayiapp.chat.modelclasses.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat2.*

class UserAdapter(
    mContext : Context,
    mUsers:List<Users>,
    isChatCheck: Boolean)
    : RecyclerView.Adapter<UserAdapter.ViewHolder?>() {

    private val mContext:Context
    private val mUsers: List<Users>
    private val isChatCheck: Boolean

    init{
        this.mUsers = mUsers
        this.isChatCheck = isChatCheck
        this.mContext = mContext
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var userNameText: TextView
        var profileImageView: CircleImageView
        var onlinetxt :CircleImageView
        var offlineTxt :CircleImageView
        var lastMessagetxt: TextView

        init{
            userNameText = itemView.findViewById(R.id.username)
            profileImageView = itemView.findViewById(R.id.profile_image)
            onlinetxt = itemView.findViewById(R.id.image_online)
            offlineTxt = itemView.findViewById(R.id.image_offline)
            lastMessagetxt = itemView.findViewById(R.id.message_last)

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout,viewGroup,false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val user : Users = mUsers[i]
        holder.userNameText.text= user!!.getusername()
        Picasso.get().load(user.getprofile()).placeholder(R.drawable.ic_person_big).into(holder.profileImageView)
    }

}