package com.will.service.register;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * 向远程注册中心注册服务
 *
 * @author clewill
 * @create 2022:04:11 19:25
 **/
@Service
public class RegisterServer {
  private static final Map<String,Class<?>> serviceNameCacheMap = new ConcurrentHashMap<>();
  private static final String registerCenterHost = "127.0.0.1";
  private static final int registerCenterPort = 9999;


  /**
   * 注册服务到远程注册中心
   * @param serviceName 服务名称
   * @param host 当前provider机器地址
   * @param port 当前服务端口号
   * @param implClass 实现类
   * @return 是否注册成功
   */
  public boolean register2RemoteCenter(String serviceName,String host,int port,Class<?> implClass)
      throws IOException {
    System.out.println("RPC 开始向远程注册中心注册服务,ServiceName="+serviceName+",registerCenterHost="+registerCenterHost+",registerCenterPort="+registerCenterPort);
    //基于BIO 开启一个client socket去像注册中心(server socket)发送数据
    Socket socket = new Socket();
    socket.connect(new InetSocketAddress(registerCenterHost,registerCenterPort));
    //向注册中心注册服务
    OutputStream outputStream = socket.getOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
    //通过第一个bool标识来区分是向注册中心注册服务还是获取服务(可以理解为一个注册中心serverSocket对应多个client rpc-client那里会去获取服务
    //这里的rpc-server这里是需要注册服务 所以需要通过一个标识来区分是获取还是注册)
    objectOutputStream.writeBoolean(false);
    objectOutputStream.writeUTF(serviceName);
    objectOutputStream.writeUTF(host);
    objectOutputStream.writeInt(port);
    objectOutputStream.flush();

    //接受注册中心返回的数据 如果注册成功 返回true
    InputStream inputStream = socket.getInputStream();
    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
    if(objectInputStream.readBoolean()){
      System.out.println(serviceName+"注册成功！");
    }

    //将服务实现映射关系放在本地缓存
    serviceNameCacheMap.put(serviceName,implClass);
    return true;
  }

  /**
   * 通过服务名称从本地缓存获取服务
   * @param serviceName 服务名称
   * @return 对应服务
   */
  public Class<?> getLocalService(String serviceName){
    return serviceNameCacheMap.get(serviceName);
  }
}
