package com.example.eggspert_mobile;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Eggspert extends Application {
    private static Eggspert instance;
    private RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }

    public static synchronized Eggspert getInstance() {
        return  instance;

    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return requestQueue;

    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);

    }



}
