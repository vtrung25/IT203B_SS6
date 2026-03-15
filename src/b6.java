import java.util.*;
import java.util.concurrent.*;
import java.lang.management.*;

public class b6 {

    static Scanner sc = new Scanner(System.in);

    static List<Room> rooms = new ArrayList<>();
    static ExecutorService executor;
    static boolean running = false;
    static boolean paused = false;

    static final Object pauseLock = new Object();

    // ===== ROOM =====
    static class Room {
        String name;
        int totalSeats;
        int soldSeats = 0;

        public Room(String name, int seats) {
            this.name = name;
            this.totalSeats = seats;
        }

        public synchronized boolean sellTicket() {
            if (soldSeats < totalSeats) {
                soldSeats++;
                return true;
            }
            return false;
        }

        public synchronized void addTicket(int amount) {
            totalSeats += amount;
        }

        public synchronized int getSold() {
            return soldSeats;
        }

        public synchronized int getTotal() {
            return totalSeats;
        }
    }

    // ===== TICKET COUNTER THREAD =====
    static class TicketCounter implements Runnable {

        int id;
        Random random = new Random();

        public TicketCounter(int id) {
            this.id = id;
        }

        public void run() {
            System.out.println("Quầy " + id + " bắt đầu bán vé...");

            while (running) {

                try {

                    synchronized (pauseLock) {
                        while (paused) {
                            pauseLock.wait();
                        }
                    }

                    Room room = rooms.get(random.nextInt(rooms.size()));

                    boolean sold = room.sellTicket();

                    if (sold) {
                        System.out.println("Quầy " + id + " bán 1 vé phòng " + room.name);
                    }

                    Thread.sleep(500);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ===== DEADLOCK DETECTOR =====
    static class DeadlockDetector implements Runnable {

        public void run() {

            while (running) {

                try {

                    ThreadMXBean bean = ManagementFactory.getThreadMXBean();
                    long[] threadIds = bean.findDeadlockedThreads();

                    if (threadIds != null) {
                        System.out.println("⚠ PHÁT HIỆN DEADLOCK!");
                    }

                    Thread.sleep(5000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ===== START SIMULATION =====
    static void startSimulation() {

        System.out.print("Nhập số phòng: ");
        int roomCount = sc.nextInt();

        System.out.print("Nhập số vé mỗi phòng: ");
        int seats = sc.nextInt();

        System.out.print("Nhập số quầy bán vé: ");
        int counters = sc.nextInt();

        rooms.clear();

        for (int i = 1; i <= roomCount; i++) {
            rooms.add(new Room("Phòng " + (char)('A'+i-1), seats));
        }

        executor = Executors.newFixedThreadPool(counters + 1);

        running = true;
        paused = false;

        for (int i = 1; i <= counters; i++) {
            executor.submit(new TicketCounter(i));
        }

        executor.submit(new DeadlockDetector());

        System.out.println("Đã khởi tạo hệ thống với "
                + roomCount + " phòng, "
                + (roomCount*seats) + " vé, "
                + counters + " quầy");
    }

    // ===== PAUSE =====
    static void pauseSimulation() {

        paused = true;

        System.out.println("Đã tạm dừng tất cả quầy bán vé.");
    }

    // ===== RESUME =====
    static void resumeSimulation() {

        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }

        System.out.println("Đã tiếp tục hoạt động.");
    }

    // ===== ADD TICKET =====
    static void addTicket() {

        System.out.print("Chọn phòng (A,B,C...): ");
        String name = sc.next();

        for (Room r : rooms) {
            if (r.name.equalsIgnoreCase("Phòng " + name)) {

                System.out.print("Thêm bao nhiêu vé: ");
                int amount = sc.nextInt();

                r.addTicket(amount);

                System.out.println("Đã thêm vé vào phòng " + name);
                return;
            }
        }

        System.out.println("Không tìm thấy phòng.");
    }

    // ===== STATISTICS =====
    static void showStatistics() {

        System.out.println("\n=== THỐNG KÊ HIỆN TẠI ===");

        int price = 150000;
        int totalSold = 0;

        for (Room r : rooms) {

            System.out.println(
                    r.name + ": Đã bán "
                            + r.getSold() + "/"
                            + r.getTotal() + " vé"
            );

            totalSold += r.getSold();
        }

        System.out.println("Tổng doanh thu: " + (totalSold * price) + " VNĐ\n");
    }

    // ===== MANUAL DEADLOCK CHECK =====
    static void checkDeadlock() {

        System.out.println("Đang quét deadlock...");

        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long[] ids = bean.findDeadlockedThreads();

        if (ids == null) {
            System.out.println("Không phát hiện deadlock.");
        } else {
            System.out.println("⚠ Phát hiện deadlock!");
        }
    }

    // ===== STOP SYSTEM =====
    static void stopSystem() {

        running = false;

        if (executor != null) {
            executor.shutdownNow();
        }

        System.out.println("Đang dừng hệ thống...");
    }

    // ===== MAIN MENU =====
    public static void main(String[] args) {

        while (true) {

            System.out.println("""
                    
                    ===== RẠP CHIẾU PHIM =====
                    1. Bắt đầu mô phỏng
                    2. Tạm dừng mô phỏng
                    3. Tiếp tục mô phỏng
                    4. Thêm vé vào phòng
                    5. Xem thống kê
                    6. Phát hiện deadlock
                    7. Thoát
                    """);

            System.out.print("Chọn: ");
            int choice = sc.nextInt();

            switch (choice) {

                case 1 -> startSimulation();

                case 2 -> pauseSimulation();

                case 3 -> resumeSimulation();

                case 4 -> addTicket();

                case 5 -> showStatistics();

                case 6 -> checkDeadlock();

                case 7 -> {
                    stopSystem();
                    return;
                }

                default -> System.out.println("Lựa chọn không hợp lệ");
            }
        }
    }
}