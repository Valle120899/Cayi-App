package com.drma.mycayiapp.chat.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.drma.mycayiapp.R
import com.drma.mycayiapp.chat.MessageChatActivity
import com.drma.mycayiapp.chat.VisitUserProfileActivity
import com.drma.mycayiapp.chat.modelclasses.Chat
import com.drma.mycayiapp.chat.modelclasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class  UserAdapter(
    mContext : Context,
    mUsers:List<Users>,
    isChatCheck: Boolean)
    : RecyclerView.Adapter<UserAdapter.ViewHolder?>() {

    private val mContext:Context
    private val mUsers: List<Users>
    private var isChatCheck: Boolean
    var lastMsg: String = ""

    init{
        this.mUsers = mUsers
        this.isChatCheck = isChatCheck
        this.mContext = mContext
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

        if(isChatCheck){
            retrieveLastMessage(user.getuid(), holder.lastMessagetxt)
        }else{
            holder.lastMessagetxt.visibility = View.GONE
        }

        if(isChatCheck){
            if(user.getstatus() == "online"){
                holder.onlinetxt.visibility = View.VISIBLE
                holder.offlineTxt.visibility = View.GONE
            }else{
                holder.onlinetxt.visibility = View.GONE
                holder.offlineTxt.visibility = View.VISIBLE
            }
        }else{
            holder.onlinetxt.visibility = View.GONE
            holder.offlineTxt.visibility = View.GONE
        }

        holder.itemView.setOnClickListener{
            val options = arrayOf<CharSequence>(
                "Enviar mensaje",
                "Visitar perfil",
                "Eliminar chat"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("¿Qué desea hacer?")
            builder.setItems(options, DialogInterface.OnClickListener{dialog, position ->
                if(position == 0){
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", user.getuid())
                    mContext.startActivity(intent)
                }
                if(position == 1){
                    val intent = Intent(mContext, VisitUserProfileActivity::class.java)
                    intent.putExtra("visit_id", user.getuid())
                    mContext.startActivity(intent)

                }
                if(position ==2){
                    deleteChat(i, holder)

                }
            })
            builder.show()
        }
    }


    private fun deleteChat(position: Int, holder:UserAdapter.ViewHolder){
        val firebaseUser = FirebaseAuth.getInstance().currentUser

//        val ref = FirebaseDatabase.getInstance().reference.child("chatList")
//            .child(mUsers.get(position).getuid().toString()).child(firebaseUser!!.uid)
//            .removeValue()
//            .addOnCompleteListener { task ->
//                if(task.isSuccessful){
//                    //Toast.makeText(holder.itemView.context, "Eliminaro.", Toast.LENGTH_SHORT).show()
//                }
//                else{
//                    //Toast.makeText(holder.itemView.context, "Error, no se pudo eliminar.", Toast.LENGTH_SHORT).show()
//                }
//            }

        val ref2 = FirebaseDatabase.getInstance().reference.child("chatList")
            .child(firebaseUser!!.uid).child(mUsers.get(position).getuid().toString())
            .removeValue()
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(holder.itemView.context, "Eliminado.", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(holder.itemView.context, "Error, no se pudo eliminar.", Toast.LENGTH_SHORT).show()
                }
            }
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

    private fun retrieveLastMessage(chatUserId: String?, lastMessagetxt: TextView) {
        lastMsg = "defaultMsg"

        val firebaseUsers = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().reference.child("chats")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for(dataSnapshot in p0.children){
                    val chat: Chat? = dataSnapshot.getValue(Chat::class.java)

                    if(firebaseUsers!=null && chat!=null){
                        if(chat.getReceiver() == firebaseUsers!!.uid && chat.getSender() == chatUserId || chat.getReceiver() == chatUserId &&
                                chat.getSender() == firebaseUsers!!.uid)
                        {
                            lastMsg = chat.getMessage()!!
                        }
                    }
                }

                when(lastMsg){
                    "defaultMsg" -> lastMessagetxt.text = "Sin mensajes."
                    "sent you an image." -> lastMessagetxt.text = "Imagen enviada."
                    else -> lastMessagetxt.text = lastMsg
                }
                lastMsg = "defaultMsg"
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


}