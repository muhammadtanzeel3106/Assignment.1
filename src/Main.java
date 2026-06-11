import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Person {
    private int id;
    private String name;
    private String email;

    public Person(int id, String name, String email) {
        this.id = id;
        this.name = name;
        setEmail(email);
    }

    public int getId()         { return id; }
    public String getName()    { return name; }
    public String getEmail()   { return email; }
    public void setName(String name) { this.name = name; }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email must contain '@'");
        }
        this.email = email;
    }

    @Override
    public String toString() {
        return "Person[id=" + id + ", name=" + name + ", email=" + email + "]";
    }
}

class Student extends Person {
    private String program;
    private int semester;

    public Student(int id, String name, String email, String program, int semester) {
        super(id, name, email);
        this.program = program;
        this.semester = semester;
    }

    public String getProgram()  { return program; }
    public int    getSemester() { return semester; }

    @Override
    public String toString() {
        return "Student[id=" + getId() + ", name=" + getName()
                + ", program=" + program + ", semester=" + semester + "]";
    }
}

class Staff extends Person {
    private String department;

    public Staff(int id, String name, String email, String department) {
        super(id, name, email);
        this.department = department;
    }

    public String getDepartment() { return department; }

    @Override
    public String toString() {
        return "Staff[id=" + getId() + ", name=" + getName()
                + ", department=" + department + "]";
    }
}

abstract class Ticket {
    private int ticketId;
    private String title;
    private String description;
    private String location;
    private String status;
    private int assignedPersonId;

    public Ticket(int ticketId, String title, String description,
                  String location, int assignedPersonId) {
        this.ticketId         = ticketId;
        this.title            = title;
        this.description      = description;
        this.location         = location;
        this.status           = "New";
        this.assignedPersonId = assignedPersonId;
    }

    public abstract double priorityScore();

    public int    getTicketId()         { return ticketId; }
    public String getTitle()            { return title; }
    public String getDescription()      { return description; }
    public String getLocation()         { return location; }
    public String getStatus()           { return status; }
    public int    getAssignedPersonId() { return assignedPersonId; }

    public void advanceStatus() {
        switch (status) {
            case "New":      status = "Assigned"; break;
            case "Assigned": status = "Resolved"; break;
            default: System.out.println("Ticket is already Resolved."); break;
        }
    }

    @Override
    public String toString() {
        return String.format(
                "Ticket #%d | %-20s | Status: %-8s | Priority: %4.1f | %s",
                ticketId, getClass().getSimpleName(), status, priorityScore(), title
        );
    }
}

class MaintenanceTicket extends Ticket {
    private String type;

    public MaintenanceTicket(int ticketId, String title, String description,
                             String location, int assignedPersonId, String type) {
        super(ticketId, title, description, location, assignedPersonId);
        this.type = type;
    }

    @Override
    public double priorityScore() {
        double score = 5.0;
        if (getLocation().toLowerCase().contains("lab"))     score += 3.0;
        if (type.equalsIgnoreCase("Chair") ||
                type.equalsIgnoreCase("Desk"))                   score += 1.0;
        return score;
    }

    @Override
    public String toString() {
        return super.toString() + " | Type: " + type;
    }
}

class CleaningTicket extends Ticket {
    private String type;

    public CleaningTicket(int ticketId, String title, String description,
                          String location, int assignedPersonId, String type) {
        super(ticketId, title, description, location, assignedPersonId);
        this.type = type;
    }

    @Override
    public double priorityScore() {
        double score = 4.0;
        if (getDescription().toLowerCase().contains("trash pile")) score += 4.0;
        if      (type.equalsIgnoreCase("Trash"))      score += 2.0;
        else if (type.equalsIgnoreCase("Dirty Area")) score += 1.0;
        return score;
    }

    @Override
    public String toString() {
        return super.toString() + " | Type: " + type;
    }
}

public class Main {

    static List<Person> persons = new ArrayList<>();
    static List<Ticket> tickets = new ArrayList<>();
    static int personIdCounter  = 1;
    static int ticketIdCounter  = 1;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("     Welcome to CampusCare System        ");
        System.out.println("   Campus Maintenance Complaint Manager   ");
        System.out.println("=========================================");

        int choice = -1;
        while (choice != 5) {
            printMenu();
            System.out.print("Enter your choice: ");
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Please enter a number 1-5.");
                continue;
            }
            switch (choice) {
                case 1: addPerson();          break;
                case 2: createTicket();       break;
                case 3: viewAllTickets();     break;
                case 4: updateTicketStatus(); break;
                case 5: System.out.println("Goodbye! Exiting CampusCare..."); break;
                default: System.out.println("[ERROR] Invalid option. Choose 1-5.");
            }
        }
    }

    static void printMenu() {// validated input menu
        System.out.println("\n-------- MAIN MENU --------");
        System.out.println("1. Add Person (Student / Staff)");
        System.out.println("2. Create Ticket");
        System.out.println("3. View All Tickets");
        System.out.println("4. Update Ticket Status");
        System.out.println("5. Exit");
        System.out.println("6. Export Report (coming soon)");
        System.out.println("---------------------------");
    }

    static void addPerson() {
        System.out.println("\n--- Add Person ---");
        System.out.println("1. Student    2. Staff");
        System.out.print("Select type: ");
        String tc = sc.nextLine().trim();

        System.out.print("Enter Name: ");
        String name = sc.nextLine().trim();

        String email = "";
        while (true) {
            System.out.print("Enter Email (must contain @): ");
            email = sc.nextLine().trim();
            if (email.contains("@")) break;
            System.out.println("[ERROR] Email must contain '@'. Try again.");
        }

        int id = personIdCounter++;

        if (tc.equals("1")) {
            System.out.print("Enter Program (e.g. BSCS): ");
            String prog = sc.nextLine().trim();
            System.out.print("Enter Semester (1-8): ");
            int sem = Integer.parseInt(sc.nextLine().trim());
            Student s = new Student(id, name, email, prog, sem);
            persons.add(s);
            System.out.println("[SUCCESS] Added: " + s);

        } else if (tc.equals("2")) {
            System.out.print("Enter Department: ");
            String dept = sc.nextLine().trim();
            Staff st = new Staff(id, name, email, dept);
            persons.add(st);
            System.out.println("[SUCCESS] Added: " + st);

        } else {
            System.out.println("[ERROR] Invalid type. Try again.");
            personIdCounter--;
        }
    }

    static void createTicket() {
        if (persons.isEmpty()) {
            System.out.println("[ERROR] No persons found. Add a person first.");
            return;
        }
        System.out.println("\n--- Create Ticket ---");
        System.out.println("1. Maintenance Ticket    2. Cleaning Ticket");
        System.out.print("Select type: ");
        String tc = sc.nextLine().trim();

        System.out.print("Title: ");       String title = sc.nextLine().trim();
        System.out.print("Description: "); String desc  = sc.nextLine().trim();
        System.out.print("Location: ");    String loc   = sc.nextLine().trim();

        System.out.println("\nAvailable Persons:");
        for (Person p : persons)
            System.out.println("  ID " + p.getId() + " → " + p.getName()
                    + " (" + p.getClass().getSimpleName() + ")");
        System.out.print("Assign to Person ID: ");
        int pid = Integer.parseInt(sc.nextLine().trim());

        boolean found = persons.stream().anyMatch(p -> p.getId() == pid);
        if (!found) {
            System.out.println("[ERROR] Person ID not found.");
            return;
        }

        int tid = ticketIdCounter++;

        if (tc.equals("1")) {
            System.out.print("Maintenance Type (Chair / Desk / Window / Board): ");
            String mtype = sc.nextLine().trim();
            MaintenanceTicket mt = new MaintenanceTicket(tid, title, desc, loc, pid, mtype);
            tickets.add(mt);
            System.out.println("[SUCCESS] Created! Priority Score: " + mt.priorityScore());

        } else if (tc.equals("2")) {
            System.out.print("Cleaning Type (Trash / Dirty Area): ");
            String ctype = sc.nextLine().trim();
            CleaningTicket ct = new CleaningTicket(tid, title, desc, loc, pid, ctype);
            tickets.add(ct);
            System.out.println("[SUCCESS] Created! Priority Score: " + ct.priorityScore());

        } else {
            System.out.println("[ERROR] Invalid ticket type.");
            ticketIdCounter--;
        }
    }

    static void viewAllTickets() {
        if (tickets.isEmpty()) {
            System.out.println("[INFO] No tickets yet.");
            return;
        }
        System.out.println("\n========== ALL TICKETS ==========");
        for (Ticket t : tickets)
            System.out.println(t);   // polymorphism — calls each ticket's toString()
        System.out.println("=================================");
    }
    static void printTicketReport() {
        long newCount      = tickets.stream().filter(t -> t.getStatus().equals("New")).count();
        long assignedCount = tickets.stream().filter(t -> t.getStatus().equals("Assigned")).count();
        long resolvedCount = tickets.stream().filter(t -> t.getStatus().equals("Resolved")).count();
        System.out.println("--- Ticket Report ---");
        System.out.println("New: " + newCount + " | Assigned: " + assignedCount + " | Resolved: " + resolvedCount);
    }

    static void updateTicketStatus() {
        if (tickets.isEmpty()) {
            System.out.println("[INFO] No tickets to update.");
            return;
        }
        viewAllTickets();
        System.out.print("Enter Ticket ID to update: ");
        int id;
        try { id = Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("[ERROR] Invalid ID."); return; }

        for (Ticket t : tickets) {
            if (t.getTicketId() == id) {
                System.out.println("Current Status : " + t.getStatus());
                t.advanceStatus();
                System.out.println("Updated Status : " + t.getStatus());
                return;
            }
        }
        System.out.println("[ERROR] Ticket ID " + id + " not found.");
    }
}