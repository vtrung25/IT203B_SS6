package btth;

import java.util.Random;

public class Luong_1 implements Runnable {

    String[] students = {
            "Nguyen Son Minh",
            "Nguyen Van A",
            "Tran Thi B",
            "Le Van C",
            "Pham Thi D",
            "Hoang Van E"
    };

    String[] homeTown = {
            "Hà Nội",
            "Bắc Ninh",
            "Lạng Sơn",
            "Hải Phòng"
    };

    Random random = new Random();
    @Override
    public void run() {
        while (true){
            int index = random.nextInt(students.length);

            System.out.println("Bạn được gọi: "+students[index]);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
