package single;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SingleThreadServer {
    public static void main(String[] args) throws IOException {
        //建立基站
        ServerSocket server =new ServerSocket(6666);
        //等待客户端连接
        System.out.println("正在等待客户端连接......");
        Socket socket=server.accept();//两根线
        //3.建立连接后进行数据的输入输出
        //服务器给客户端输出流,输出用打印流，输入用scanner类
        PrintStream  printStream=new PrintStream(socket.getOutputStream(),true,"UTF-8");//自动// 刷新
        printStream.println("hello i am server");
        Scanner scanner=new Scanner(socket.getInputStream());
        if(scanner.hasNext()){
            System.out.println("客户端发送过来的信息为："+scanner.nextLine());
        }
        //关闭流
        scanner.close();
        printStream.close();
        server.close();

    }
}
