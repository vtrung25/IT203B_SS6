import java.util.*;

public class b5 {

    static class Ticket {

        private String ticketId;
        private boolean isHeld = false;
        private long holdExpiryTime = 0;
        private boolean isVIP = false;

        public Ticket(String ticketId) {
            this.ticketId = ticketId;
        }

        public String getTicketId() {
            return ticketId;
        }

        public boolean isHeld() {
            return isHeld;
        }

        public void hold(boolean vip) {
            isHeld = true;
            isVIP = vip;
            holdExpiryTime = System.currentTimeMillis() + 5000;
        }

        public void release() {
            isHeld = false;
            isVIP = false;
        }

        public boolean isExpired() {
            return isHeld && System.currentTimeMillis() > holdExpiryTime;
        }

        public boolean isVIP() {
            return isVIP;
        }
    }

    // ===================== TICKET POOL =====================
    static class TicketPool {

        private String roomName;
        private List<Ticket> tickets = new ArrayList<>();

        public TicketPool(String roomName, int capacity) {

            this.roomName = roomName;

            for (int i = 1; i <= capacity; i++) {
                tickets.add(new Ticket(roomName + "-" + String.format("%03d", i)));
            }
        }

        public synchronized Ticket holdTicket(boolean vip, String counterName) {

            for (Ticket t : tickets) {

                if (!t.isHeld()) {

                    t.hold(vip);

                    System.out.println(counterName +
                            ": Đã giữ vé " + t.getTicketId() +
                            (vip ? " (VIP)" : "") +
                            ". Vui lòng thanh toán trong 5s");

                    return t;
                }
            }

            System.out.println(counterName + ": Không còn vé phòng " + roomName);
            return null;
        }

        public synchronized void sellHeldTicket(Ticket t, String counterName) {

            if (t != null && t.isHeld()) {

                System.out.println(counterName +
                        ": Thanh toán thành công vé " +
                        t.getTicketId());

                tickets.remove(t);
            }
        }

        public synchronized void releaseExpiredTickets() {

            for (Ticket t : tickets) {

                if (t.isExpired()) {

                    System.out.println(
                            "TimeoutManager: Vé "
                                    + t.getTicketId()
                                    + " hết hạn giữ, đã trả lại kho");

                    t.release();
                }
            }
        }
    }

    // ===================== BOOKING COUNTER =====================
    static class BookingCounter implements Runnable {

        private String name;
        private List<TicketPool> pools;

        public BookingCounter(String name, List<TicketPool> pools) {
            this.name = name;
            this.pools = pools;
        }

        @Override
        public void run() {

            Random random = new Random();

            while (true) {

                TicketPool pool = pools.get(random.nextInt(pools.size()));
                boolean vip = random.nextBoolean();

                Ticket t = pool.holdTicket(vip, name);

                if (t != null) {

                    try {
                        Thread.sleep(3000); // khách suy nghĩ
                    } catch (InterruptedException e) {}

                    pool.sellHeldTicket(t, name);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
        }
    }

    // ===================== TIMEOUT MANAGER =====================
    static class TimeoutManager implements Runnable {

        private List<TicketPool> pools;

        public TimeoutManager(List<TicketPool> pools) {
            this.pools = pools;
        }

        @Override
        public void run() {

            while (true) {

                for (TicketPool pool : pools) {
                    pool.releaseExpiredTickets();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
        }
    }


    public static void main(String[] args) {

        TicketPool roomA = new TicketPool("A", 5);
        TicketPool roomB = new TicketPool("B", 5);
        TicketPool roomC = new TicketPool("C", 5);

        List<TicketPool> pools = Arrays.asList(roomA, roomB, roomC);

        // 5 quầy bán vé
        for (int i = 1; i <= 5; i++) {

            Thread counter = new Thread(
                    new BookingCounter("Quầy " + i, pools));

            counter.start();
        }

        // Thread quản lý timeout
        Thread timeout = new Thread(
                new TimeoutManager(pools));

        timeout.start();
    }
}
