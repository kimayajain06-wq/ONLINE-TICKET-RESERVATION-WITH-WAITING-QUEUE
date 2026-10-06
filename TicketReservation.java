import java.util.*;

public class TicketReservation {

    // ---------- Passenger ----------
    static class Passenger {
        int id;          // ticket / request id
        String name;
        int priority;    // 1 = Regular, 2 = VIP, 3 = Senior Citizen
        int seq;         // request order, used to break ties

        Passenger(int id, String name, int priority, int seq) {
            this.id = id;
            this.name = name;
            this.priority = priority;
            this.seq = seq;
        }

        String categoryName() {
            if (priority == 3) return "Senior Citizen";
            if (priority == 2) return "VIP";
            return "Regular";
        }

        public String toString() {
            return "ID: " + id + " | " + name + " | " + categoryName();
        }
    }

    // ---------- Custom Linked List for confirmed bookings ----------
    static class Node {
        Passenger data;
        Node next;
        Node(Passenger data) { this.data = data; }
    }

    static class BookingList {
        Node head, tail;
        int size = 0;

        // insert at the end - O(1) using tail pointer
        void add(Passenger p) {
            Node n = new Node(p);
            if (head == null) head = tail = n;
            else { tail.next = n; tail = n; }
            size++;
        }

        // delete by ticket id - O(n)
        Passenger remove(int id) {
            if (head == null) return null;
            if (head.data.id == id) {
                Passenger p = head.data;
                head = head.next;
                if (head == null) tail = null;
                size--;
                return p;
            }
            Node cur = head;
            while (cur.next != null && cur.next.data.id != id) cur = cur.next;
            if (cur.next == null) return null;
            Passenger p = cur.next.data;
            if (cur.next == tail) tail = cur;
            cur.next = cur.next.next;
            size--;
            return p;
        }

        void display() {
            if (head == null) { System.out.println("  (no confirmed bookings)"); return; }
            for (Node cur = head; cur != null; cur = cur.next)
                System.out.println("  " + cur.data);
        }
    }

    // ---------- Reservation System ----------
    static class ReservationSystem {
        int totalSeats;
        int idCounter = 0;
        BookingList bookings = new BookingList();
        Queue<Passenger> normalQueue = new LinkedList<>();
        PriorityQueue<Passenger> priorityQueue = new PriorityQueue<>(
            (a, b) -> a.priority != b.priority
                ? b.priority - a.priority      // higher priority first
                : a.seq - b.seq);              // same priority -> earlier request first

        ReservationSystem(int totalSeats) { this.totalSeats = totalSeats; }

        void book(String name, int category) {
            idCounter++;
            Passenger p = new Passenger(idCounter, name, category, idCounter);

            if (bookings.size < totalSeats) {
                bookings.add(p);
                System.out.println("Booking CONFIRMED -> " + p);
            } else if (category > 1) {
                priorityQueue.offer(p);
                System.out.println("Seats full. Added to PRIORITY waiting list -> " + p);
            } else {
                normalQueue.offer(p);
                System.out.println("Seats full. Added to NORMAL waiting list -> " + p);
            }
        }

        void cancel(int id) {
            Passenger removed = bookings.remove(id);
            if (removed == null) {
                System.out.println("Ticket ID " + id + " not found in confirmed bookings.");
                return;
            }
            System.out.println("Cancelled -> " + removed);

            // promote from waiting list: priority queue first, then normal queue
            Passenger next = !priorityQueue.isEmpty() ? priorityQueue.poll() : normalQueue.poll();
            if (next != null) {
                bookings.add(next);
                System.out.println("Promoted from waiting list -> " + next + " (now CONFIRMED)");
            }
        }

        void showBookings() {
            System.out.println("Confirmed bookings (" + bookings.size + "/" + totalSeats + "):");
            bookings.display();
        }

        void showWaiting() {
            System.out.println("Priority waiting list (highest first):");
            if (priorityQueue.isEmpty()) System.out.println("  (empty)");
            else {
                // copy so the original queue is not disturbed
                PriorityQueue<Passenger> copy = new PriorityQueue<>(priorityQueue);
                while (!copy.isEmpty()) System.out.println("  " + copy.poll());
            }
            System.out.println("Normal waiting list (FIFO):");
            if (normalQueue.isEmpty()) System.out.println("  (empty)");
            else for (Passenger p : normalQueue) System.out.println("  " + p);
        }
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter total number of seats: ");
        int seats = readInt(sc);
        ReservationSystem system = new ReservationSystem(seats);

        while (true) {
            System.out.println("\n===== TICKET RESERVATION MENU =====");
            System.out.println("1. Book ticket");
            System.out.println("2. Cancel ticket");
            System.out.println("3. View confirmed bookings");
            System.out.println("4. View waiting lists");
            System.out.println("5. Exit");
            System.out.print("Choice: ");
            int choice = readInt(sc);

            switch (choice) {
                case 1:
                    System.out.print("Passenger name: ");
                    String name = sc.nextLine().trim();
                    if (name.isEmpty()) { System.out.println("Name cannot be empty."); break; }
                    System.out.print("Category (1=Regular, 2=VIP, 3=Senior Citizen): ");
                    int cat = readInt(sc);
                    if (cat < 1 || cat > 3) { System.out.println("Invalid category."); break; }
                    system.book(name, cat);
                    break;
                case 2:
                    System.out.print("Enter ticket ID to cancel: ");
                    system.cancel(readInt(sc));
                    break;
                case 3:
                    system.showBookings();
                    break;
                case 4:
                    system.showWaiting();
                    break;
                case 5:
                    System.out.println("Thank you!");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    static int readInt(Scanner sc) {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
