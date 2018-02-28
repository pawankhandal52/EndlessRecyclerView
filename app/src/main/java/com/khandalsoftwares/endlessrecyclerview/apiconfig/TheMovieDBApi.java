package com.khandalsoftwares.endlessrecyclerview.apiconfig;

import android.content.Context;

import com.khandalsoftwares.endlessrecyclerview.appConstants.TheMovieDBConstants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Khandal Software on 2/28/18,57
 * This will be responsible for configuring Retrofit client
 */

public class TheMovieDBApi {
    private static Retrofit mRetrofit = null;
    
    
    // builds OkHttpClient with logging Interceptor
    private static OkHttpClient buildClient() {
        return new OkHttpClient
                .Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }
    
    public static Retrofit getClient(Context context) {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .client(buildClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(TheMovieDBConstants.BASE_URL)
                    .build();
        }
        return mRetrofit;
    }
}
