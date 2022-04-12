package com.will.common;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
* @Author: clewill
* @Description 响应对象
*/
public class ResponseVO implements Serializable {

    private static final long serialVersionUID = -2587841145983842867L;

    private String result;

    private String message;

    private Object data;


/*    //是否过滤反斜杠
    @Getter
    @Setter
    @JSONField(serialize = false)
    private boolean trimBackslash = true;

    //是否需要过滤数值结尾的 .0  ,比如5.0格式化为5  ,默认不处理
    @Getter
    @Setter
    @JSONField(serialize = false)
    private boolean writeNullNumberAsZero = false;*/

//    private StatusResultEnum statusResult;

    Map<String, Object> map = new LinkedHashMap<>();

    public ResponseVO() {
        this.result = "0000";
        this.message = "ok";
        map.put("result", "0000");
        map.put("message", "ok");
    }

    public ResponseVO(StatusResultEnum result) {
        this.result = result.getResult();
        this.message = result.getMessage();
        map.put("result", this.result);
        map.put("message", this.message);
    }

    public ResponseVO(String result, String message,Object data) {
        this.result = result;
        this.message = message;
        this.data = data;
        map.put("result", result);
        map.put("message", message);
        map.put("data",data);
    }

    public void setStatusResult(StatusResultEnum result) {
//        this.statusResult = result;
        this.result = result.getResult();
        this.message = result.getMessage();
        map.put("result", result);
        map.put("message", message);
    }

//    @JSONField(serialize = false)
//    public StatusResultEnum getStatusResult() {
//        return statusResult;
//    }

    public void put(String key, Object data) {
        map.put(key, data);
    }

    public void put(Map<String, Object> map) {
        if (map == null || map.size() < 1) {
            return;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            this.map.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 获取请求返回编号
     *
     * @return result 请求返回编号
     */
    public String getResult() {
        return result;
    }

    /**
     * 设置请求返回编号
     *
     * @param result 请求返回编号
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * 获取请求返回信息
     *
     * @return message 请求返回信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置请求返回信息
     *
     * @param message 请求返回信息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /*    @Override
    public String toString() {
        return "ResponseVO{" +
                "result='" + this.result + '\'' +
                ", message='" + this.message + '\'' +
                ", trimBackslash=" + this.trimBackslash +
                ", writeNullNumberAsZero=" + this.writeNullNumberAsZero +
                '}';
    }*/

}
