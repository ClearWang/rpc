package com.will.service.start;

import com.will.service.SendMsg;
import com.will.service.impl.SendMsgImpl;
import com.will.service.register.RegisterServer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 启动服务
 *
 * @author clewill
 * @create 2022:04:11 20:05
 **/
@Service
public class StartRpcServer {

  ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
      .availableProcessors() * 2);

  @Autowired
  private RegisterServer registerServer;

  public void newRpcServerSocket(String serviceName, String host, int port, Class<?> impl)
      throws IOException {
    ServerSocket serverSocket = new ServerSocket();
    serverSocket.bind(new InetSocketAddress(host,port));
    System.out.println("RPC 服务端已启动(host"+host+",port="+port+")");
    registerServer.register2RemoteCenter(serviceName, host, port, impl);

    while (true) {
      //accept方法 这里会阻塞
      executorService.execute(new MyTask(serverSocket.accept(), registerServer));
    }
  }

  /**
   * 这个注解表示加载类完成后调用这个方法
   */
  @PostConstruct
  public void startRpcServer() {
    Random random = new Random();
    //每次启动随机一个端口 这样保证端口不被占用
    int port = 10001 + random.nextInt(10);
    try {
      newRpcServerSocket(SendMsg.class.getName(), "127.0.0.1", port, SendMsgImpl.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static class MyTask implements Runnable {

    private final Socket socket;
    private final RegisterServer registerServer;

    public MyTask(Socket socket, RegisterServer registerServer) {
      this.socket = socket;
      this.registerServer = registerServer;
    }

    @Override
    public void run() {
      ObjectInputStream objectInputStream = null;
      ObjectOutputStream objectOutputStream = null;
      try {
        //接受客户端的数据
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        //读取类名
        String serviceName = objectInputStream.readUTF();
        //读取方法名称
        String methodName = objectInputStream.readUTF();
        //读取传入参数类型
        Class<?>[] paramType = (Class<?>[]) objectInputStream.readObject();
        //读取传入参数值
        Object[] paramValue = (Object[]) objectInputStream.readObject();
        //读取返回值类型  注意：这一部可能有的rpc框架不会实现 直接获取的是服务端实现接口的方法的返回值
        //换句话说 如果这一步不实现 那么当接口定义中有返回值的时候 接口实现的返回值是可以不存在的
        Class<?> defineReturnType = (Class<?>)objectInputStream.readObject();
        //从容器获取对应serviceName实现类对象
        Class<?> localService = registerServer.getLocalService(serviceName);
        if (null == localService) {
          throw new ClassNotFoundException(serviceName + "本地缓存不存在！");
        }
        //通过反射执行对应接口服务
        Method method = localService.getMethod(methodName, paramType);
        //java方法调用不支持多结果返回 所以这里只有一个Object对象 如果返回的是一个数组这里需要做转型
        Object result = method.invoke(localService.newInstance(), paramValue);
        //做一些返回值类型校验 TODO
        Type realReturnType = method.getGenericReturnType();
        System.out.println(serviceName + "实现实际返回值类型为：" + realReturnType.getTypeName() + ","
            + "接口定义返回值类型为：" + (defineReturnType == null ? "" :
            defineReturnType.getTypeName()));

        //回传给客户端的数据
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(result);
        objectOutputStream.flush();
        System.out.println("收到客户端消息: result=" + result);

      } catch (IOException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
        e.fillInStackTrace();
      } finally {
        if (null != socket) {
          try {
            socket.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        if (null != objectInputStream) {
          try {
            objectInputStream.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        if (null != objectOutputStream) {
          try {
            objectOutputStream.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

}
