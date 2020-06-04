package com.drma.mycayiapp.chat.modelclasses

class Users {
    private var uid: String = ""
    private var username: String = ""
    private var profile: String = ""
    private var cover: String = ""
    private var status: String = ""
    private var search: String = ""

    constructor()
    constructor(
        uid: String,
        username: String,
        profile: String,
        cover: String,
        status: String,
        search: String
    ) {
        this.uid = uid
        this.username = username
        this.profile = profile
        this.cover = cover
        this.status = status
        this.search = search
    }

    fun getuid(): String?{
        return uid
    }

    fun setuid(uid:String){
        this.uid = uid
    }

    fun getusername(): String?{
        return username
    }

    fun setusername(username:String){
        this.username = username
    }

    fun getprofile(): String?{
        return profile
    }

    fun setprofile(profile: String){
        this.profile = profile
    }

    fun getcover(): String?{
        return cover
    }

    fun setcover(cover:String){
        this.cover = cover
    }

    fun getstatus(): String?{
        return status
    }

    fun setstatus(status: String){
        this.status = status
    }

    fun getsearch(): String?{
        return search
    }

    fun setsearch(search:String){
        this.search = search
    }

}