import java.util.Random;

public class BookingCounter implements Runnable {

    private String counterName;
    private TicketPool roomA;
    private TicketPool roomB;
    private int soldCount = 0;

    public BookingCounter(String counterName, TicketPool roomA, TicketPool roomB) {
        this.counterName = counterName;
        this.roomA = roomA;
        this.roomB = roomB;
    }

    public int getSoldCount() {
        return soldCount;
    }

    @Override
    public void run() {

        Random random = new Random();

        while (true) {

            if (!roomA.hasTickets() && !roomB.hasTickets()) {
                break;
            }

            int choice = random.nextInt(3);

            Ticket ticket = null;

            if (choice == 0) {

                ticket = roomA.sellTicket();

                if (ticket != null) {
                    soldCount++;
                    System.out.println(counterName + " bán vé phòng A");
                    System.out.println(counterName + " đã bán vé " + ticket.getTicketId());
                }

            } else if (choice == 1) {

                ticket = roomB.sellTicket();

                if (ticket != null) {
                    soldCount++;
                    System.out.println(counterName + " bán vé phòng B");
                    System.out.println(counterName + " đã bán vé " + ticket.getTicketId());
                }

            } else {

                sellCombo();

            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public boolean sellCombo() {

        Ticket ticketA = null;
        Ticket ticketB = null;

        synchronized (roomA) {

            synchronized (roomB) {

                ticketA = roomA.sellTicket();
                ticketB = roomB.sellTicket();

                if (ticketA != null && ticketB != null) {

                    soldCount++;

                    System.out.println(counterName +
                            " bán combo thành công: "
                            + ticketA.getTicketId()
                            + " &   "
                            + ticketB.getTicketId());

                    return true;
                }

                if (ticketA != null) {
                    ticketA.setSold(false);
                }

                if (ticketB != null) {
                    ticketB.setSold(false);
                }

                System.out.println(counterName + " bán combo thất bại");

                return false;
            }
        }
    }
}