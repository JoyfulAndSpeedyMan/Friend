package top.pin90.server;

public class TestAnother {
    public void t(){
        Runnable a=new Runnable(){
            @Override
            public void run() {
                System.out.println("匿名类");
            }
        };
        Runnable b=()-> System.out.println("lambda");
    }
}
