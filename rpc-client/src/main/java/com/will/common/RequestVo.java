package com.will.common;

import java.util.StringJoiner;

/**
 * 请求参数实体类封装
 *
 * @author clewill
 * @create 2021:03:17 14:56
 **/
public class RequestVo {
  private String name;
  private String phone;
  private String msg;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
