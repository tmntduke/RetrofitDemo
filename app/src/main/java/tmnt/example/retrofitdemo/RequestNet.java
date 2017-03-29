package tmnt.example.retrofitdemo;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by tmnt on 2017/3/27.
 */

public interface RequestNet {

    @POST("Login")
    @FormUrlEncoded
    Call<ResponseBody> getName(@Field("employee_name") String name, @Field("password") String pass);


    @Multipart
    @POST("UploadServlet")
    Call<ResponseBody> upload(@Part MultipartBody.Part file);

    @POST("PrintServlet")
    @FormUrlEncoded
    Observable<List<FileInfo>> getFile(@Field("name") String name, @Field("pass") String pass);

}
