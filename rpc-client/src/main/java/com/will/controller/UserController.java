package com.will.controller;

import com.will.base.User;
import com.will.common.RequestVo;
import com.will.common.ResponseVO;
import com.will.common.StatusResultEnum;
import com.will.service.AgentService;
import com.will.service.RpcClient;
import com.will.service.SendMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * user
 *
 * @author clewill
 * @create 2022:04:12 16:48
 **/
@Controller
@RequestMapping(value = "/user")
public class UserController {
  @Autowired
  private AgentService agentService;

  @RequestMapping(value="/sendMsg",consumes = "application/json;charset=UTF-8",produces =
      "application/json;charset=UTF-8")
  @ResponseBody
  public ResponseVO sendMsg(@RequestBody RequestVo requestVo) {
    ResponseVO responseVO = new ResponseVO(StatusResultEnum.OK);
    SendMsg smsService = agentService.getSmsService();
    User user = new User();
    user.setName(requestVo.getName());
    user.setPhone(requestVo.getPhone());
    boolean result = smsService.sendMail(user,requestVo.getMsg());
    if (result){
      System.out.println("远程调用成功!");
    }else{
      System.out.println("远程调用失败!");
    }
    return responseVO;
  }
}
