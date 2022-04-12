package com.will.service.impl;

import com.will.base.User;
import com.will.service.SendMsg;

/**
 * SendMsg 实现类
 *
 * @author clewill
 * @create 2022:04:11 19:21
 **/
public class SendMsgImpl implements SendMsg {

  @Override
  public boolean sendMail(User user, String msg) {
    //一些校验逻辑 TODO
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("给手机号为："+user.getPhone()+"的用户发送短信成功!"+"短信内容为："+msg);
    return true;
  }
}
