package com.incampus.graphql_testing;

import com.google.gson.JsonObject;
import com.ramkishorevs.graphqlconverter.converter.GraphQuery;
import com.ramkishorevs.graphqlconverter.converter.QueryContainerBuilder;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    @POST("graphql/")
   // @GraphQuery("filename")
    @Headers({
            "Content-Type: application/json",
            "x-hasura-admin-secret:incampus"
    })
    Call<JsonObject> login(
           @Body JsonObject jsonObject
            );

}
