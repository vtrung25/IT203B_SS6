package btth;

import java.util.Random;

public class Luong_2 implements Runnable {

    String[] students = {
            "Nguyen Son Minh",
            "Nguyen Van A",
            "Tran Thi B",
            "Le Van C",
            "Pham Thi D",
            "Hoang Van E"
    };

    String[] hometowns = {
            "Hà Nội",
            "Bắc Ninh",
            "Lạng Sơn",
            "Hải Phòng",
            "Đà Nẵng",
            "Quảng Ninh"
    };

    Random random = new Random();
    @Override
    public void run() {
        while (true){
            int index = random.nextInt(students.length);

            System.out.println(
                    "Bạn: " + students[index] +
                            " | Quê quán: " + hometowns[index]
            );

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
