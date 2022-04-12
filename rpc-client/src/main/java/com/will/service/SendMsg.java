package com.will.service;

import com.will.base.User;

/**
 * 发送短信业务
 * @author clewill
 */
public interface SendMsg {
  /**
   * 发送短信接口
   * @param user 要发送的客户
   * @param msg 要发送的短信信息
   * @return 短信发送是否成功
   */
  boolean sendMail(User user,String msg);
}
