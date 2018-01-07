package com.example.starim.big_work;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AppUtil {

    //默认值
    private static final String JSON="json";
    private static final String XML="xml";

    /**
     *  工具类入口
     * @param code 状态码
     * @param msg  提示信息
     * @param obj  返回数据
     * @param type 数据类型   json / xml
     * @return  字符串
     */
    public static String toPrint(int code,String msg,Object obj,String type){

        //大小写不影响
        String str="";

        if(JSON.equalsIgnoreCase(type)&&JSON.equals(type)){
            //生成json数据
            str=toJsonString(code, msg, obj);

        }else if(XML.equalsIgnoreCase(type)&&XML.equals(type)){
            //生成xml数据
            str=toXml(code, msg, obj);

        }else{
            //默认使用 json ， 参数错误的话 ，默认 为json
            str=toJsonString(code, msg, obj);
        }

        return str;
    }


    /**
     * 对象转 json 数组
     * @param code 状态码
     * @param msg  状态信息
     * @param obj  数据
     * @return json数组 字符串
     */
    @SuppressWarnings("unused")
    private static String toJsonArray(int code,String msg,Object obj){

        Map<String, Object> map=toKeyVal(code, msg, obj);

        JSONArray jsonArray=JSONArray.fromObject(map);
        return jsonArray.toString();
    }

    /**
     *  这一个就够了！
     *
     * 转 json 对象
     * @param code 状态码
     * @param msg  提示信息
     * @param obj  数据
     * @return json对象 字符串
     */
    private static String toJsonString(int code,String msg,Object obj){

        if(obj==null) {
            obj="";
        }

        Map<String, Object> map=toKeyVal(code, msg, obj);

        JSONObject jsonObject=JSONObject.fromObject(map);
        return jsonObject.toString();
    }

    /**
     *  转xml 格式 ：字符串拼装的格式
     * @param code 状态码
     * @param msg  提示信息
     * @param obj  数据
     * @return xml数据
     */
    private static String toXml(int code,String msg,Object obj){

        if(obj==null) {
            obj="";
        }

        Map<String,Object> map=toKeyVal(code, msg, obj);
        JSONObject object=JSONObject.fromObject(map);

        StringBuilder builder=new StringBuilder("<?xml version='1.0' encoding='UTF-8'?>");
        builder.append("<root>");
        builder.append(mapToxml(object));
        builder.append("</root>");

        return builder.toString();
    }


    /**
     * 用来封装数据
     * @param code 状态码
     * @param msg  信息
     * @param obj 数据
     * @return
     */
    private static Map<String, Object> toKeyVal(int code,String msg,Object obj){

        Map<String,Object> map=new HashMap<String, Object>();
        map.put("code", code);
        map.put("msg", msg);
        map.put("data", obj);

        return map;
    }





    /**
     * 生成 xml
     * @param object
     * @return
     */
    private static String mapToxml(JSONObject object){


        StringBuilder builder=new StringBuilder();

        @SuppressWarnings("unchecked")
        Iterator<String> iterator=object.keys();

        while(iterator.hasNext()){

            String key=iterator.next();

            builder.append("<"+key+">");

            if(object.get(key) instanceof JSONObject){
                //如果是 JSONObject的话
                //递归 调用
                builder.append(mapToxml((JSONObject) object.get(key)));
            }else if(object.get(key) instanceof JSONArray){
                //如果是 JSONArray的话
                StringBuilder builder2=new StringBuilder();
                JSONArray array=(JSONArray) object.get(key);

                if(array.isArray()){
                    int i=0;
                    for(Object obj : array) {
                        JSONObject objitem=(JSONObject) obj;
                        String attr="num='"+i+"'";

                        //如果有 id 的话 ，可以使用这个
//                     if(objitem.containsKey("id")){
//                         attr="id='"+objitem.getString("id")+"'";
//                     }

                        builder2.append("<item "+attr+">");
                        builder2.append(mapToxml(objitem));
                        builder2.append("</item>");
                        i++;
                    }
                }
                builder.append(builder2.toString());


            }else{
                builder.append(object.get(key));
            }

            builder.append("</"+key+">");
        }

        return builder.toString();
    }



}