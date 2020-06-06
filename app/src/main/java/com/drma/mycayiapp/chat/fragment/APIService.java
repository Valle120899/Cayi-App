package com.drma.mycayiapp.chat.fragment;

import com.drma.mycayiapp.chat.Notifications.MyResponse;
import com.drma.mycayiapp.chat.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAYAc9S7k:APA91bGn1X_zR7AekfTz5pH39aw5sR6ICJcECN6dOCiYsvWjfGtPfxZlSJSh0Qec3KSB_H-WWz5XmeB6EXB_XdXIt-8c0QsJmnvB3TRQm271k7dwue8-NEtn7jSzXJedaoLdGlkk41hC"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
