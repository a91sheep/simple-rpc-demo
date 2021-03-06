package com;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 发布注册
 *
 * @author <a href="mailto:linxh@59store.com">linxiaohui</a>
 * @version 1.0 16/12/16
 * @since 1.0
 */
public class RpcExporter {
    private static Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void exporter(Object service, String hostName, int port) throws IOException {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(hostName, port));
        try {
            while (true) {
                executor.execute(new ExporterTask(service, server.accept()));
            }
        } finally {
            server.close();
        }
    }

    public static class ExporterTask implements Runnable {
        Socket client  = null;
        Object service = null;

        public ExporterTask(Object service, Socket client) {
            this.client = client;
            this.service = service;
        }

        @Override
        public void run() {
            ObjectInputStream input = null;
            ObjectOutputStream output = null;
            try {
                input = new ObjectInputStream(client.getInputStream());
                String interfaceName = input.readUTF();
                System.out.println("服务端------收到客户端调用" + interfaceName + "的RPC请求");

                String methodName = input.readUTF();
                Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                Object[] arguments = (Object[]) input.readObject();
                if (interfaceName.equals(service.getClass().getInterfaces()[0].getName())) {
                    System.out.println("服务端------接口匹配成功...");
                }
                Method method = service.getClass().getMethod(methodName, parameterTypes);
                Object result = method.invoke(service, arguments);
                System.out.println("服务端------反射结果:" + result);
                output = new ObjectOutputStream(client.getOutputStream());
                output.writeObject(result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
