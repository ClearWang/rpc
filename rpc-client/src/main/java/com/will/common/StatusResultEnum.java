package com.will.common;


/**
 * 状态结果枚举定义
 * @author clewill
 */
public enum StatusResultEnum {

    /**
     * 成功
     */
    OK("0000", "ok"),
    SYS_ERROR("1001", "system err");
    private String result;
    private String message;

    /**
     * @param result  状态码<br>
     * @param message 状态消息<br>
     */
    private StatusResultEnum(String result, String message) {
        this.result = result;
        this.message = message;
    }


    /**
     * 获取返回编码<br>
     *
     * @return 返回编码<br>
     */
    public String getResult() {
        return result;
    }

    /**
     * 获取返回信息<br>
     *
     * @return 返回信息<br>
     */
    public String getMessage() {
        return message;
    }

    /**
     * 直接把枚举转换成ResponseVO对象<br>
     * 用法:StatusResultEnum.OK.toResponseVO()<br>
     *
     * @return responseVO对象
     */
    public ResponseVO toResponseVO() {
        return new ResponseVO(this);
    }
}
