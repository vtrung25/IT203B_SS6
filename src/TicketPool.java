import java.util.ArrayList;
import java.util.List;

public class TicketPool {

    private String roomName;
    private List<Ticket> tickets;
    private int nextId = 1;

    public TicketPool(String roomName, int totalTickets) {
        this.roomName = roomName;
        tickets = new ArrayList<>();

        for (int i = 1; i <= totalTickets; i++) {
            String id = roomName + "-" + String.format("%03d", i);
            tickets.add(new Ticket(id, roomName));
        }
    }

    public synchronized Ticket sellTicket() {

        while (true) {

            for (Ticket t : tickets) {
                if (!t.isSold()) {
                    t.setSold(true);
                    return t;
                }
            }

            try {
                System.out.println("Hết vé phòng " + roomName + ", đang chờ...");
                wait();   // chờ khi hết vé
            } catch (InterruptedException e) {
                return null;
            }
        }
    }

    public boolean hasTickets() {
        for (Ticket t : tickets) {
            if (!t.isSold()) {
                return true;
            }
        }
        return false;
    }

    public int remainingTickets() {
        int count = 0;
        for (Ticket t : tickets) {
            if (!t.isSold()) {
                count++;
            }
        }
        return count;
    }

    public synchronized void addTickets(int count) {

        for (int i = 0; i < count; i++) {

            String id = roomName + "-" + String.format("%03d", nextId++);
            tickets.add(new Ticket(id, roomName));
        }

        System.out.println("Nhà cung cấp: Đã thêm " + count + " vé vào phòng " + roomName);
    }

    public synchronized void releaseExpiredTickets() {

        for (Ticket t : tickets) {

            if (t.isExpired()) {

                System.out.println(
                        "TimeoutManager: Vé "
                                + t.getTicketId()
                                + " hết hạn giữ, trả lại kho");

                t.release();
            }
        }
    }

}