package com.incampus.graphql_testing;

import android.content.Context;

import com.ramkishorevs.graphqlconverter.converter.GraphQLConverter;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL="https://incampus-test.herokuapp.com/v1/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;
    private RetrofitClient(Context context)
    {
        retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GraphQLConverter.create(context))
                .build();
    }

    public static synchronized RetrofitClient getInstance(Context mcontext)
    {
        if(mInstance==null)
        {
            mInstance=new RetrofitClient(mcontext);
        }
        return mInstance;
    }

    public Api getApi()
    {
        return retrofit.create(Api.class);
    }

}