package com.incampus.graphql_testing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ramkishorevs.graphqlconverter.converter.GraphQLConverter;
import com.ramkishorevs.graphqlconverter.converter.QueryContainerBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.util.JsonToken.BEGIN_OBJECT;

public class MainActivity extends AppCompatActivity {

    private TextView mtext;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mtext = (TextView) findViewById(R.id.text_view);

        final ArrayList<ExampleItem> list=new ArrayList<>();

        mRecyclerView=findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager=new LinearLayoutManager(this);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("content-type", "application/json")
                        .header("x-hasura-admin-secret", "incampus")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://incampus-test.herokuapp.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        Api apiInterface =retrofit.create(Api.class);
        QueryContainerBuilder queryContainer = new QueryContainerBuilder();
        // Call<Response> hello = RetrofitClient.getInstance(this).getApi().login();

        // String string ="query MyQuery { User { course}}";

        JSONObject jsonObject =new JSONObject();
        try {
            jsonObject.put("query","query MyQuery { User { course}}");
            Log.i("Query","creating JSON query");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("query","query MyQuery { User { course}}");


        Call<JsonObject> register =apiInterface.login(jsonObject1);

        register.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if(response.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, "Hooray!!!!!", Toast.LENGTH_SHORT).show();

                    mtext.setText(response.body().toString());

                    //CALLING JSON PROCESSOR FUNCTION
                    fetch_json_function(response.body().toString(),list);
                    mAdapter=new ExampleAdapter(list);

                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);
                }

                //SimpleEntity entity = gson.fromJson(response.body().toString(), SimpleEntity.class);
               // mtext.setText(response.body().toString());
                //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Failure:\n"+ t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


    }


    public  void fetch_json_function(String s,final ArrayList<ExampleItem> list)
    {
        try
        {
            //create JSON object or converting the read content to a JSON format.
            JSONObject jsonObjectMaster=new JSONObject(s);

            //Reading value stored in a particular key and store it as a string
            String Data=jsonObjectMaster.getString("data");

            Log.i("Content Depth 0: ",Data);

            //converting that to again a child json object
            JSONObject jsonObjectchild_1=new JSONObject(Data);
            String User=jsonObjectchild_1.getString("User");
            Log.i("Content Depth 1: ",User);

            /*
                Now that string can be array of sub JSON objects..
                So we assign each element as an array element
            */
            JSONArray array=new JSONArray(User);

            //Parsing that JSON array
            for(int i=0;i<array.length();i++)
            {
                JSONObject jsonObjectWorker=array.getJSONObject(i);
                String course_enrolled=jsonObjectWorker.getString("course");
                Log.i("Course:",jsonObjectWorker.getString("course"));
                list.add(new ExampleItem(R.drawable.user_img,course_enrolled));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
