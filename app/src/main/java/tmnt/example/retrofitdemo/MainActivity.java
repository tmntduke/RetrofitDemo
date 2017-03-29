package tmnt.example.retrofitdemo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private OkHttpClient mClient;


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClient = new OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        request.newBuilder().addHeader("Cotent-Type", "application/json");

                        return chain.proceed(request);
                    }
                })
                .build();

        //request();

        okRequest();

        TextView textView = (TextView) findViewById(R.id.tv_text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 010);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 010) {
            Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
            cursor.moveToNext();
            String path = cursor.getString(cursor.getColumnIndex("_data"));
            Log.i(TAG, "onActivityResult: " + path);
            //upload(path);
            okUpload(path);
        }
    }

    /**
     * Retrofit 请求http
     */
    private void request() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://114.215.202.209:89/WebAPI.asmx/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(mClient)
                .build();

        RequestNet net = retrofit.create(RequestNet.class);
        Call<ResponseBody> call = net.getName("张丽", "123456");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    Log.i(TAG, "onResponse: " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * Retrofit上传文件
     *
     * @param path
     */
    private void upload(String path) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.2:8080/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(mClient)
                .build();

        File file = new File(path);

        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part part = MultipartBody.Part.createFormData("upload", file.getName(), body);

        RequestNet net = retrofit.create(RequestNet.class);

        Call<ResponseBody> call = net.upload(part);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * okHttp请求http
     */
    private void okRequest() {

        User user = new User();
        user.setEmployee_name("张丽");
        user.setPassword("123456");
        String s = new Gson().toJson(user);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), s);

        RequestBody body1 = new FormBody.Builder()
                .add("employee_name", "张丽")
                .add("password", "123456")
                .build();

        Request request = new Request.Builder()
                .url("http://114.215.202.209:89/WebAPI.asmx/Login")
                .post(body1)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okhttp3.Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    /**
     * okHttp上传文件
     *
     * @param file
     */
    private void okUpload(String file) {
        File file1 = new File(file);
        Log.i(TAG, "okUpload: " + file);
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file1);

        RequestBody b = new MultipartBody.Builder()
                .addFormDataPart("upload", file1.getName(), body)
                .build();

        final OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://192.168.1.2:8080/UploadServlet")
                .post(b)
                .build();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = okHttpClient.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

}
