package com.will.service;

import com.will.base.RegisterServiceVo;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * rpc register center
 *
 * 注册中心只需要提供两项服务：
 * 1.服务注册
 * 2.服务发现(服务获取)
 *
 * @author clewill
 * @create 2022:04:12 15:14
 **/
@Service
public class RegisterCenter {
  private final String registerCenterHost = "127.0.0.1";
  private final int registerCenterPort = 9999;
  private Map<String, Set<RegisterServiceVo>> cacheMap = new ConcurrentHashMap<>();

  ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
      .availableProcessors()*2);

  public void newRpcServerSocket(String host, int port)
      throws IOException {
    ServerSocket serverSocket = new ServerSocket();
    serverSocket.bind(new InetSocketAddress(host,port));
    System.out.println("RPC 注册中心已启动(host="+host+",port="+port+")");

    while (true){
      //accept方法 这里会阻塞
      executorService.execute(new MyTask(serverSocket.accept()));
    }
  }

  private class MyTask implements Runnable {
    private final Socket socket;

    public MyTask(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {
      ObjectInputStream objectInputStream = null;
      ObjectOutputStream objectOutputStream = null;
      try {
        //接受客户端的数据
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        boolean serviceFlag = objectInputStream.readBoolean();
        if (serviceFlag){
          //说明是服务发现
          String serviceName = objectInputStream.readUTF();
          //从缓存map中获取对应的服务实例集合
          Set<RegisterServiceVo> registerServiceVoSet = cacheMap.get(serviceName);
          //回传给客户端的数据
          OutputStream outputStream = socket.getOutputStream();
          objectOutputStream = new ObjectOutputStream(outputStream);
          objectOutputStream.writeObject(registerServiceVoSet);
          objectOutputStream.flush();
          System.out.println("服务发现,返回服务实例集合成功！ServiceName="+serviceName);
        }else{
          //说明是服务注册
          String serviceName = objectInputStream.readUTF();
          String host = objectInputStream.readUTF();
          int port = objectInputStream.readInt();
          registerService(serviceName,host,port);
          //回传给客户端的数据
          OutputStream outputStream = socket.getOutputStream();
          objectOutputStream = new ObjectOutputStream(outputStream);
          objectOutputStream.writeBoolean(true);
          objectOutputStream.flush();
          System.out.println("服务注册：serviceName="+serviceName+",host="+host+",port="+port+"实例注册成功！");
        }
      } catch (IOException e) {
        e.fillInStackTrace();
      }finally {
        if (null != socket){
          try {
            socket.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        if(null != objectInputStream){
          try {
            objectInputStream.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        if(null != objectOutputStream){
          try {
            objectOutputStream.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  /**
   * 这里可能会有并发 所以做一个同步代码块的操作
   * @param serviceName
   * @param host
   * @param port
   */
  public synchronized void registerService(String serviceName,String host,int port){
    Set<RegisterServiceVo> registerServiceVoSet = cacheMap.get(serviceName);
    if(null == registerServiceVoSet){
      registerServiceVoSet = new HashSet<>();
      registerServiceVoSet.add(new RegisterServiceVo(host,port));
    }else{
      registerServiceVoSet.add(new RegisterServiceVo(host,port));
    }
    cacheMap.put(serviceName,registerServiceVoSet);
  }

  @PostConstruct
  public void startRpcServer(){
    try {
      newRpcServerSocket(registerCenterHost,registerCenterPort);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
