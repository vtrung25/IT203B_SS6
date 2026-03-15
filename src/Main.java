public class Main {

    public static void main(String[] args) throws InterruptedException {

        TicketPool roomA = new TicketPool("A", 10);
        TicketPool roomB = new TicketPool("B", 10);

        BookingCounter counter1 = new BookingCounter("Quầy 1", roomA, roomB);
        BookingCounter counter2 = new BookingCounter("Quầy 2", roomA, roomB);

        TicketSupplier supplier =
                new TicketSupplier(roomA, roomB, 3, 3000, 1);

        Thread t1 = new Thread(counter1);
        Thread t2 = new Thread(counter2);
        Thread supplierThread = new Thread(supplier);

        t1.start();
        t2.start();
        supplierThread.start();

        // đợi supplier chạy xong
        supplierThread.join();

        // cho quầy bán thêm một chút
        Thread.sleep(5000);

        // dừng quầy bán
        t1.interrupt();
        t2.interrupt();

        System.out.println("\nKết thúc chương trình");

        System.out.println("Quầy 1 bán được: " + counter1.getSoldCount() + " vé");
        System.out.println("Quầy 2 bán được: " + counter2.getSoldCount() + " vé");

        System.out.println("Vé còn lại phòng A: " + roomA.remainingTickets());
        System.out.println("Vé còn lại phòng B: " + roomB.remainingTickets());
    }
}