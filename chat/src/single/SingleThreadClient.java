package single;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class SingleThreadClient {
    public static void main(String[] args) throws IOException {
        //建立连接
        Socket socket=new Socket("127.0.0.1",6666);

        //进行数据输入输出,先发送再接收
        PrintStream printStream=new PrintStream(socket.getOutputStream(),true,"UTF-8");
        Scanner in =new Scanner(System.in);
        String str="";
        System.out.println("请输入向服务器发送的信息......");
        if(in.hasNext()){
            str=in.nextLine();
        }
        Scanner scanner=new Scanner(socket.getInputStream());
        if(scanner.hasNext()){
            System.out.println("从服务器端发来的信息为："+scanner.nextLine());
        }

        printStream.println("hello.i am client");
        //关闭流
        scanner.close();
        printStream.close();
        socket.close();
    }

}
