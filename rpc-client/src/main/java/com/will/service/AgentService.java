package com.will.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * agent
 * 提供一个代理类服务
 * @author clewill
 * @create 2022:04:12 17:00
 **/
@Service
public class AgentService {
  @Autowired
  private RpcClient rpcClient;

  public SendMsg getSmsService(){
    SendMsg remoteProxyObject = null;
    try {
      remoteProxyObject = rpcClient.getRemoteProxyObject(SendMsg.class);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return remoteProxyObject;
  }
}
