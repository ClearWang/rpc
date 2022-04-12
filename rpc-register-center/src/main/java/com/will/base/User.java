package com.will.base;

import java.io.Serializable;

/**
 * 用户信息实体类
 *
 * @author clewill
 * @create 2022:04:11 19:12
 **/
public class User implements Serializable {
  private String name;
  private Integer sex;
  private String phone;

  public Integer getSex() {
    return sex;
  }
  public void setSex(Integer sex) {
    this.sex = sex;
  }
  public void setName(String name) {
    this.name = name;
  }
  public void setPhone(String phone) {
    this.phone = phone;
  }
  public String getName() {
    return name;
  }
  public String getPhone() {
    return phone;
  }
}
