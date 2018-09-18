package com.ysk.turingv2.net;

import com.ysk.turingv2.bean.Ask;
import com.ysk.turingv2.bean.Take;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 描述: Retrofit接口
 */

public interface Api {
    //发送json数据形式的post请求，把网络请求接口的后半部分openapi/api/v写在里面
    //Get是请求数据实体类，Take接受数据实体类
    @POST("openapi/api/v2")
    Call<Take> request(@Body Ask ask);
}

