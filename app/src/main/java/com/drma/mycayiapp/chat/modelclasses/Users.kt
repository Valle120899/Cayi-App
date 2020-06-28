package com.drma.mycayiapp.chat.modelclasses

class Users {
    private var uid: String = ""
    private var username: String = ""
    private var profile: String = ""
    private var cover: String = ""
    private var status: String = ""
    private var search: String = ""
    private var image1: String = ""
    private var image2: String = ""
    private var image3: String = ""

    constructor()
    constructor(
        uid: String,
        username: String,
        profile: String,
        cover: String,
        status: String,
        search: String,
        image1: String,
        image2: String,
        image3: String
    ) {
        this.uid = uid
        this.username = username
        this.profile = profile
        this.cover = cover
        this.status = status
        this.search = search
        this.image1 = image1
        this.image2 = image2
        this.image3 = image3
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

    fun getimage1(): String?{
        return image1
    }

    fun setimage1(image1:String){
        this.image1 = image1
    }
    fun getimage2(): String?{
        return image2
    }

    fun setimage2(image2:String){
        this.image2 = image2
    }
    fun getimage3(): String?{
        return image3
    }

    fun setimage3(image3:String){
        this.image3 = image3
    }

}