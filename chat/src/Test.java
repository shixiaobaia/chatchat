import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        int shuru=0;
        int sum=0;


        Scanner in =new Scanner(System.in);
        while(in.hasNext()){
            shuru=in.nextInt();
            sum=sum+shuru;
            System.out.println("输出结果为："+sum);
        }

    }
}
