package com.drma.mycayiapp.chat.AdapterClasses

import android.content.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.drma.mycayiapp.R
import com.drma.mycayiapp.chat.ViewFullImageActivity
import com.drma.mycayiapp.chat.modelclasses.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class ChatsAdapter(mContext: Context,
                  mChatList: List<Chat>,
                   imageUrl: String) : RecyclerView.Adapter<ChatsAdapter.ViewHolder?>()
{
    private val mContext: Context
    private val mChatList: List<Chat>
    private val imageUrl: String
    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    init {
        this.mChatList = mChatList
        this.mContext = mContext
        this.imageUrl = imageUrl
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat: Chat = mChatList[position]

        Picasso.get().load(imageUrl).into(holder.profile_image)

        if (chat.getMessage().equals("sent you an image.") && !chat.getUrl().equals("")){
            //Mensaje del lado derecho
            if(chat.getSender().equals(firebaseUser!!.uid)){
                holder.show_text_message!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.right_image_view)

                holder.right_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "Mirar imagen completa",
                        "Eliminar imagen",
                        "Cancelar"
                    )

                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("¿Qué desea hacer?")

                    builder.setItems(options, DialogInterface.OnClickListener{
                        dialog, which ->
                        if(which == 0){
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.getUrl())
                            mContext.startActivity(intent)
                        }
                        else if(which == 1){
                            deleteSentMessage(position, holder)
                        }
                    })
                    builder.show()
                }


            }
            //Mensaje del lado izquierdo
            else if(!chat.getSender().equals(firebaseUser!!.uid)){
                holder.show_text_message!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.left_image_view)

                holder.left_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "Mirar imagen completa",
                        "Cancel"
                    )

                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("¿Qué desea hacer?")

                    builder.setItems(options, DialogInterface.OnClickListener{
                            dialog, which ->
                        if(which == 0){
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.getUrl())
                            mContext.startActivity(intent)
                        }

                    })
                    builder.show()
                }
            }
        }else{
            holder.show_text_message!!.text = chat.getMessage()

            if(firebaseUser!!.uid == chat.getSender()){
                holder.show_text_message!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "Eliminar mensaje",
                        "Cancelar"
                    )

                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("¿Qué desea hacer?")

                    builder.setItems(options, DialogInterface.OnClickListener{
                            dialog, which ->
                        if(which == 0){
                            deleteSentMessage(position, holder)
                        }
                    })
                    builder.show()
                }
            }else{
                holder.show_text_message!!.setOnClickListener{
                    val options = arrayOf<CharSequence>(
                        "Copiar mensaje",
                        "Cancel"
                    )

                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("¿Qué desea hacer?")

                    builder.setItems(options, DialogInterface.OnClickListener{
                            dialog, which ->
                        if(which == 0){
                            copyMessage(position, holder)
                            Toast.makeText(mContext, "Mensaje copiado", Toast.LENGTH_SHORT).show()
                        }

                    })
                    builder.show()
                }
            }
        }



        //Mensajes vistos
        if(position == mChatList.size-1){
            if(chat.isIsSeen()){
                holder.text_seen!!.text = "Visto"

                if(chat.getMessage().equals("sent you an image.") && !chat.getUrl().equals("")){
                        val lp: RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                        lp!!.setMargins(0,245,10,0)
                        holder.text_seen!!.layoutParams = lp
                    }
            }else{
                holder.text_seen!!.text = "Enviado"

                if(chat.getMessage().equals("sent you an image.") && !chat.getUrl().equals("")){
                    val lp: RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0,245,10,0)
                    holder.text_seen!!.layoutParams = lp
                }
            }
        }
        else {
            holder.text_seen!!.visibility = View.GONE
        }
    }
    private fun deleteSentMessage(position: Int, holder:ChatsAdapter.ViewHolder){
        val ref = FirebaseDatabase.getInstance().reference.child("chats")
            .child(mChatList.get(position).getMessageId()!!)
            .removeValue()
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(holder.itemView.context, "Eliminado.", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(holder.itemView.context, "No se pudo eliminar.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun copyMessage(position: Int, holder: ChatsAdapter.ViewHolder){
        val ref = FirebaseDatabase.getInstance().reference.child("chats")
            .child(mChatList.get(position).getMessageId()!!).addValueEventListener(object :ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    var text:String = p0.value.toString()
                    var copy:String=""
                    copy = text.substring(130).dropLast(7)


                    val clipboard = mContext?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    var clipData = ClipData.newPlainText("CopyMessage", copy)
                    clipboard.primaryClip = clipData
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })


    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return if (position == 1){
            val view: View = LayoutInflater.from(mContext).inflate(com.drma.mycayiapp.R.layout.message_item_right,parent,false)
            ViewHolder(view)
        }else{
            val view: View = LayoutInflater.from(mContext).inflate(com.drma.mycayiapp.R.layout.message_item_left,parent,false)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profile_image: CircleImageView? = null
        var show_text_message: TextView? = null
        var left_image_view: ImageView? = null
        var text_seen: TextView? = null
        var right_image_view: ImageView? = null

        init{
            profile_image = itemView.findViewById(R.id.profile_image)
            show_text_message = itemView.findViewById(R.id.show_text_message)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            right_image_view = itemView.findViewById(R.id.right_image_view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(mChatList[position].getSender().equals(firebaseUser!!.uid)){
            1
        }else{
            0
        }
    }


}