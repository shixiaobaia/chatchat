package multi;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import  java.io.IOException;
//读取服务器发来的信息
class ReadFromServer implements  Runnable{


    private Socket client;
    public  ReadFromServer(Socket client){
        this.client=client;
    }
    public void run() {
        try {
            Scanner in= new Scanner(client.getInputStream());
            while(true){
                if(client.isClosed()){
                    System.out.println("客户端已关闭");
                    in.close();
                    break;
                }
                if(in.hasNext()){
                    String msgFromServer=in.nextLine();
                    System.out.println("服务器发来的信息为"+msgFromServer);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class SendMsgToServer  implements  Runnable{
    private  Socket client;

    public SendMsgToServer(Socket client){
        this.client=client;
    }

    public void run(){
        //向服务器发送信息，PrintStream
        try {
            PrintStream printStream=new PrintStream(client.getOutputStream(),true,"UTF-8");
            //获取用户输入
           Scanner in=new Scanner(System.in);
           while(true){
               //bye表示表聊了
               System.out.println("请输入要向服务器发送的信息");
               String strFromUser="";
               if(in.hasNext()){
                   strFromUser=in.nextLine();

               }
               //向服务器发送信息

               printStream.println(strFromUser);
               //判断退出，字符串有bye
               if(strFromUser.contains("bye")){
                   System.out.println("客户端退出聊天室");
                   printStream.close();
                   in.close();
                   client.close();
                   break;

               }
           }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
public class MultiThreadClient {
    public static void main(String[] args)throws Exception  {
        //根据指定的ip和端口号建立连接
        Socket client=new Socket("127.0.0.1",6666);
        //启动读写线程
        Thread readThread=new Thread(new ReadFromServer(client));
        Thread sendThread=new Thread(new SendMsgToServer(client));
        readThread.start();
        sendThread.start();
    }
}
