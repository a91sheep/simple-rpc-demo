package com;

import com.impl.EchoServiceImpl;
import com.interf.EchoService;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author <a href="mailto:linxh@59store.com">linxiaohui</a>
 * @version 1.0 16/12/16
 * @since 1.0
 */
public class RpcTest {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Provider发布了一个服务
                    EchoService echoService = new EchoServiceImpl();
                    RpcExporter.exporter(echoService, "localhost", 8080);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //Consumer调用一个服务
        RpcImporter<EchoService> importer = new RpcImporter<EchoService>();
        EchoService echo = importer.importer(EchoService.class, new InetSocketAddress("localhost", 8080));
        System.out.println("客户端------代理类收到结果:" + echo.echo("Are you ok ?"));
    }
}
