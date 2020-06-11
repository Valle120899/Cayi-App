package com.drma.mycayiapp.chat.modelclasses

class Friends {

    private var date:String = ""

    constructor()

    constructor(date: String) {
        this.date = date
    }

    fun getDate(): String?{
        return date
    }

    fun setDate(date: String?){
        this.date = date!!
    }
}