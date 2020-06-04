package com.drma.mycayiapp.chat.modelclasses

class Chatlist {
    private var id: String = ""

    constructor()

    constructor(id:String){
        this.id = id
    }

    fun getId(): String?{
        return id
    }

    fun setId(Id : String?){
        this.id = id!!
    }
}