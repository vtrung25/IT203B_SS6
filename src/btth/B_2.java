package btth;

public class B_2 {

    public static void main(String[] args) {
    Luong_2 l = new Luong_2();
    Thread thread = new Thread(l);

    thread.start();

    }
}
