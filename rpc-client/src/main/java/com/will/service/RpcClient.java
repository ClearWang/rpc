package com.will.service;

import com.will.base.RegisterServiceVo;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

/**
 * client
 *
 * @author clewill
 * @create 2022:04:12 12:53
 **/
@Service
public class RpcClient {
  private static final String registerCenterHost = "127.0.0.1";
  private static final int registerCenterPort = 9999;

  @SuppressWarnings("unchecked")
  public <T> T getRemoteProxyObject(final Class<?> serviceInterface) throws Exception {
    //通过serviceName获取
    InetSocketAddress addr = getService(serviceInterface.getName());
    if(null == addr){
      return null;
    }
    //通过动态代理模式 进行接口的远程调用
    return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(),
        new Class<?>[]{serviceInterface},
        new InvocationHandler() {
          @Override
          public Object invoke(Object proxy, Method method, Object[] args)
              throws IOException, ClassNotFoundException {
            //其实就是相当于new一个客户端 socket把反射方法调用需要的参数传给RpcServer端的socket
            Socket socket = null;
            ObjectOutputStream objectOutputStream = null;
            ObjectInputStream objectInputStream = null;
            try{
              System.out.println("RPC 客户端开启一个socket 开始远程服务调用-"+serviceInterface.getName()+"/"+method.getName());
              socket = new Socket();
              socket.connect(addr);
              //向服务端socket发送发射接口调用所需要的条件
              objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
              //类名
              objectOutputStream.writeUTF(serviceInterface.getName());
              //方法名
              objectOutputStream.writeUTF(method.getName());
              //参数类型
              objectOutputStream.writeObject(method.getParameterTypes());
              //参数值
              objectOutputStream.writeObject(args);
              //返回值类型
              objectOutputStream.writeObject(method.getReturnType());
              objectOutputStream.flush();

              //获取服务端返回的调用结果
              objectInputStream = new ObjectInputStream(socket.getInputStream());
              return objectInputStream.readObject();
            }finally {
              if(null != socket){
                socket.close();
              }
              if(null != objectOutputStream){
                objectOutputStream.close();
              }
              if(null != objectInputStream){
                objectInputStream.close();
              }
            }
          }
        });
  }



  private InetSocketAddress getService(String serviceName)
      throws Exception {
    List<InetSocketAddress> serviceList = getServiceList(serviceName);
    if (null == serviceList){
      return null;
    }
    //获得服务提供者的地址列表
    return getServiceAddress(serviceList);
  }

  /**
   * 负载均衡策略：随机选择一个可用的服务实例
   * @param serviceVoList  可以提供服务实例的机器地址集合
   * @return 选择的一台服务实例
   */
  private InetSocketAddress getServiceAddress(List<InetSocketAddress> serviceVoList){
    Random random = new Random();
    InetSocketAddress addr
        = serviceVoList.get(random.nextInt(serviceVoList.size()));
    System.out.println("本次接口调用选择了服务器："+addr);
    return addr;
  }

  @SuppressWarnings("unchecked")
  private List<InetSocketAddress> getServiceList(String serviceName)
      throws IOException, ClassNotFoundException {

    Socket socket = null;
    ObjectOutputStream objectOutputStream = null;
    ObjectInputStream objectInputStream = null;

    try {
      System.out.println("RPC 客户端开启一个socket 开始获取注册中心 可用服务实例列表【服务发现机制】");
      //本质上就是一个socket client 从注册中心获取对应的服务
      socket = new Socket();
      socket.connect(new InetSocketAddress(registerCenterHost, registerCenterPort));
      //向注册中心socket发送服务名称
      objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
      //标识是从注册中心获取服务
      objectOutputStream.writeBoolean(true);
      objectOutputStream.writeUTF(serviceName);
      objectOutputStream.flush();

      //接受注册中心socket返回的数据
      objectInputStream = new ObjectInputStream(socket.getInputStream());
      Set<RegisterServiceVo> registerServiceVoSet = (Set<RegisterServiceVo>) objectInputStream
          .readObject();
      if (null == registerServiceVoSet){
        System.out.println("注册中心当前没有提供【"+serviceName+"】可用的服务实例!");
        return null;
      }
      List<InetSocketAddress> addressList = new ArrayList<>();
      registerServiceVoSet.forEach((registerServiceVo) -> {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(registerServiceVo.getHost(),
            registerServiceVo.getPort());
        addressList.add(inetSocketAddress);
      });
      return addressList;
    }finally {
      if(null != socket){
        socket.close();
      }
      if(null != objectOutputStream){
        objectOutputStream.close();
      }
      if(null != objectInputStream){
        objectInputStream.close();
      }
    }
  }
}
