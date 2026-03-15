package btth;

public class B1 {
    public static void main(String[] args) {
        Luong_1 l = new Luong_1();
        Thread obj = new Thread(l);

        obj.start();

    }
}
