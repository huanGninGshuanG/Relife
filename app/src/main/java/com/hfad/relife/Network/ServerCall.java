package com.hfad.relife.Network;


import android.content.Context;
import android.net.ConnectivityManager;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 18359 on 2017/12/25.
 */

public class ServerCall {

    public static String getJsonFromUrl(String url, HashMap<String, String> hashMap){
        try{
            OkHttpClient client = new OkHttpClient();
            client.connectTimeoutMillis();
            client.readTimeoutMillis();

            Request.Builder builder = new Request.Builder().url(url);
            if(hashMap!=null){
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                //Add params to builder
                for(Map.Entry<String,String> entry:hashMap.entrySet()){
                    formBodyBuilder.add(entry.getKey(),entry.getValue());
                }
                RequestBody formBody = formBodyBuilder.build();
                builder.post(formBody);
            }
            Request request = builder.build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch(Exception ex){
            return ex.toString();
        }
    }

    public static boolean isNetworkConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo()!=null;
    }
}
