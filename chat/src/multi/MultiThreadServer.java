package multi;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

public class MultiThreadServer {
    private static Map<String, Socket> clientLists = new ConcurrentHashMap<>();

    //处理每个客户端的输入输出请求，包装成一个线程，
    private static class ExecuteClientRequest implements Runnable {
        private Socket client;

        public ExecuteClientRequest(Socket client) {
            this.client = client;
        }

        public void run() {
            //获取用户输入流，读取用户发来的信息
            try {
                Scanner in =new Scanner(client.getInputStream());
                String strFromClient="";
                while(true){
                    if(in.hasNext()){
                        strFromClient=in.nextLine();
                        //windows换行\r\n,信息里有|r，需要处理消除
                        //\r替换为空字符串
                        Pattern pattern=Pattern.compile("\r");
                        Matcher matcher=pattern.matcher(strFromClient);
                        strFromClient=matcher.replaceAll("");
                    }
                   //注册流程
                    if(strFromClient.startsWith("username:")){
                        String username=strFromClient.split("\\:")[1];//拆出用户名
                        userRegister(username,client);

                    }
                    //群聊G：
                    if(strFromClient.startsWith("G:")){
                        String groupMsg=strFromClient.split("\\:")[1];
                        groupChat(groupMsg);

                    }
                    //私聊
                    //P：1-hello world
                    if(strFromClient.startsWith("P:")){
                        String username=strFromClient.split("\\:")[1]
                                .split("\\-")[0];
                        String privateMsg=strFromClient.split("\\:")[1]
                                .split("\\-")[1];
                        //私聊方法
                        privateChat(username,privateMsg);

                    }
                    //用户退出
                    if(strFromClient.startsWith("bye")){
                        String username=strFromClient.split("\\:")[0];
                        userOffline(username);
                        break;


                    }



                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //注册方法。要注册的用户名，用户名对应的socket
        private  void userRegister(String username,Socket socket){//注册的用户名放在map里
            clientLists.put(username,socket);
            //判断用户ing是否存在，socket是否退出
            out.println("用户"+username+"上线了");
            out.println("当前聊天室人数为："+clientLists.size());
            try {
                PrintStream out=new PrintStream(socket.getOutputStream(),true,"UTF-8");
                out.println("注册成功");
                out.println("当前聊天室人数为："+clientLists.size());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //群聊方法,遍历map向每个客户端输出一变
        private void groupChat(String groupMsg){
            Set<Map.Entry<String,Socket>>clientEntry=
                    clientLists.entrySet();
            Iterator<Map.Entry<String,Socket>>
                    iterator=clientEntry.iterator();
            while(iterator.hasNext()){
                //取出每一个客户端实体
                Map.Entry<String,Socket> client=iterator.next();
                //拿到客户端输出流输出群聊信息
                try {
                    PrintStream out=new PrintStream(client.getValue().getOutputStream(),true,"UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out.println("群聊信息为"+groupMsg);

            }
        }
        //私聊方法--私聊的用户名，和信息
        private  void privateChat(String username,String privateMsg){
            //todo  如果不存在，该用户已下线
            Socket client=clientLists.get(username);
            //获取输出流
            try {
                PrintStream out=new PrintStream(client.getOutputStream(),true,"UTF-8");
                out.println("私聊信息为"+privateMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //退出方法,把map中的用户实体删除，关闭输入输出流
        private  void userOffline(String username){
            clientLists.remove(username);
            System.out.println("用户"+username+"已下线");
            System.out.println("当前聊天室还有"+clientLists.size()+"人");

        }
    }

    public static void main(String[] args) throws IOException {
        //建立基站
        ServerSocket serverSocket = new ServerSocket(6666) ;
            //使用线程池同时处理多个客户端连接
            ExecutorService executorService = Executors.newFixedThreadPool(20);
        out.println("等待客户端连接");
        for(int i=0;i<20;i++){
            Socket client=serverSocket.accept();
            out.println("有新的客户端连接，端口号："+client.getPort());
            executorService.submit(new ExecuteClientRequest(client));//来一个访问一个

        }
        //关闭线程池与服务端
        executorService.shutdown();
        serverSocket.close();



    }
}
