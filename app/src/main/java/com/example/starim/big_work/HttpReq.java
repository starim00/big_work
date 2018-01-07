package com.example.starim.big_work;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by starim on 2017/12/25.
 */

public class HttpReq {
    public static String toPostdata(List<BasicNameValuePair> parameters,int type) throws ClientProtocolException, IOException {

        String str="获取失败";

        //初始化
        HttpClient client=new DefaultHttpClient();
        //HttpPath.GetGamesPath() : 网络请求路径
        HttpPost httpPost=null;
        switch (type){
            case 1:httpPost = new HttpPost(HttpPath.getUserLoginPath());
            break;
            case 2:httpPost = new HttpPost(HttpPath.getUserRegister());
            break;
            default:;
                break;
        }
        //设置参数
        UrlEncodedFormEntity params=new UrlEncodedFormEntity(parameters,"UTF-8");
        httpPost.setEntity(params);
        //执行请求
        HttpResponse response= client.execute(httpPost);
        //取得返回值
        if(response.getStatusLine().getStatusCode()==200){
            //请求成功
            HttpEntity entity=response.getEntity();
            str= EntityUtils.toString(entity, "UTF-8");
        }

        return str;
    }
    public static String toGetData(int type){
        String str="获取数据失败";
        HttpGet get = null;
        switch (type){
            case 1:get = new HttpGet(HttpPath.getUserLoginPath());
            break;
            case 2:get = new HttpGet(HttpPath.getUserRegister());
            break;
            default:;
            break;
        }
        HttpClient client=new DefaultHttpClient();
        try {

            HttpResponse httpResponse=client.execute(get);

            if(httpResponse.getStatusLine().getStatusCode()==200){
                str=EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
            }

            return str;

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            return "toGetData 异常："+e.getMessage();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return "toGetData 异常："+e.getMessage();
        }
    }
}
