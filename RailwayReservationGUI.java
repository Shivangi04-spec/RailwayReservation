import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class RailwayReservationGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReservationSystem());
    }
}

class ReservationSystem extends JFrame {
    private ArrayList<Train> trains = new ArrayList<>();
    private ArrayList<Booking> bookings = new ArrayList<>();
    private int nextBookingId = 1;

    // GUI Components
    private JTabbedPane tabbedPane;
    private JPanel viewTrainsPanel, bookTicketPanel, viewBookingsPanel, cancelBookingPanel;

    public ReservationSystem() {
        // Initialize sample data
        trains.add(new Train(101, "Rajdhani Express", "Delhi", "Mumbai", 50));
        trains.add(new Train(102, "Shatabdi Express", "Chennai", "Bangalore", 40));
        trains.add(new Train(103, "Duronto Express", "Kolkata", "Delhi", 60));

        // Setup main window
        setTitle("Railway Reservation System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create tabbed interface
        tabbedPane = new JTabbedPane();
        
        // Create panels for each tab
        createViewTrainsPanel();
        createBookTicketPanel();
        createViewBookingsPanel();
        createCancelBookingPanel();

        // Add panels to tabbed pane
        tabbedPane.addTab("View Trains", viewTrainsPanel);
        tabbedPane.addTab("Book Ticket", bookTicketPanel);
        tabbedPane.addTab("View Bookings", viewBookingsPanel);
        tabbedPane.addTab("Cancel Booking", cancelBookingPanel);

        add(tabbedPane);
        setVisible(true);
    }

    private void createViewTrainsPanel() {
        viewTrainsPanel = new JPanel(new BorderLayout());

        // Column names for the table
        String[] columnNames = {"Train No", "Name", "From", "To", "Available Seats"};

        // Create data for the table
        Object[][] data = new Object[trains.size()][5];
        for (int i = 0; i < trains.size(); i++) {
            Train train = trains.get(i);
            data[i][0] = train.getNumber();
            data[i][1] = train.getName();
            data[i][2] = train.getSource();
            data[i][3] = train.getDestination();
            data[i][4] = train.getAvailableSeats();
        }

        // Create table
        JTable trainTable = new JTable(data, columnNames);
        trainTable.setEnabled(false); // Make non-editable
        JScrollPane scrollPane = new JScrollPane(trainTable);

        viewTrainsPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void createBookTicketPanel() {
        bookTicketPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Train selection
        gbc.gridx = 0; gbc.gridy = 0;
        bookTicketPanel.add(new JLabel("Select Train:"), gbc);
        
        String[] trainNames = new String[trains.size()];
        for (int i = 0; i < trains.size(); i++) {
            trainNames[i] = trains.get(i).getNumber() + " - " + trains.get(i).getName();
        }
        JComboBox<String> trainCombo = new JComboBox<>(trainNames);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        bookTicketPanel.add(trainCombo, gbc);

        // Passenger name
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        bookTicketPanel.add(new JLabel("Passenger Name:"), gbc);
        JTextField nameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 2;
        bookTicketPanel.add(nameField, gbc);

        // Book button
        JButton bookButton = new JButton("Book Ticket");
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 1;
        bookTicketPanel.add(bookButton, gbc);

        // Status label
        JLabel statusLabel = new JLabel(" ");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        bookTicketPanel.add(statusLabel, gbc);

        // Book button action
        bookButton.addActionListener(e -> {
            int selectedIndex = trainCombo.getSelectedIndex();
            if (selectedIndex < 0) {
                statusLabel.setText("Please select a train!");
                return;
            }

            String passengerName = nameField.getText().trim();
            if (passengerName.isEmpty()) {
                statusLabel.setText("Please enter passenger name!");
                return;
            }

            Train selectedTrain = trains.get(selectedIndex);
            if (selectedTrain.getAvailableSeats() <= 0) {
                statusLabel.setText("No seats available on this train!");
                return;
            }

            Booking booking = new Booking(nextBookingId++, passengerName, selectedTrain);
            bookings.add(booking);
            selectedTrain.bookSeat();

            statusLabel.setText("<html>Booking Successful!<br>Booking ID: " + booking.getId() + 
                              "<br>Train: " + selectedTrain.getName() + 
                              "<br>From: " + selectedTrain.getSource() + 
                              " To: " + selectedTrain.getDestination() + "</html>");
            
            // Update other panels
            updateViewTrainsPanel();
            updateViewBookingsPanel();
        });
    }

    private void createViewBookingsPanel() {
        viewBookingsPanel = new JPanel(new BorderLayout());
        updateViewBookingsPanel();
    }

    private void updateViewBookingsPanel() {
        viewBookingsPanel.removeAll();
        
        if (bookings.isEmpty()) {
            viewBookingsPanel.add(new JLabel("No bookings found!", SwingConstants.CENTER), BorderLayout.CENTER);
        } else {
            // Column names for the table
            String[] columnNames = {"Booking ID", "Passenger", "Train No", "Train Name", "From", "To"};

            // Create data for the table
            Object[][] data = new Object[bookings.size()][6];
            for (int i = 0; i < bookings.size(); i++) {
                Booking booking = bookings.get(i);
                Train train = booking.getTrain();
                data[i][0] = booking.getId();
                data[i][1] = booking.getPassengerName();
                data[i][2] = train.getNumber();
                data[i][3] = train.getName();
                data[i][4] = train.getSource();
                data[i][5] = train.getDestination();
            }

            // Create table
            JTable bookingsTable = new JTable(data, columnNames);
            bookingsTable.setEnabled(false);
            JScrollPane scrollPane = new JScrollPane(bookingsTable);

            viewBookingsPanel.add(scrollPane, BorderLayout.CENTER);
        }
        
        viewBookingsPanel.revalidate();
        viewBookingsPanel.repaint();
    }

    private void createCancelBookingPanel() {
        cancelBookingPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new FlowLayout());
        
        JLabel idLabel = new JLabel("Enter Booking ID:");
        JTextField idField = new JTextField(10);
        JButton cancelButton = new JButton("Cancel Booking");
        JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
        
        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(cancelButton);
        
        cancelBookingPanel.add(inputPanel, BorderLayout.NORTH);
        cancelBookingPanel.add(statusLabel, BorderLayout.CENTER);
        
        cancelButton.addActionListener(e -> {
            try {
                int bookingId = Integer.parseInt(idField.getText());
                Booking bookingToCancel = null;
                
                for (Booking booking : bookings) {
                    if (booking.getId() == bookingId) {
                        bookingToCancel = booking;
                        break;
                    }
                }
                
                if (bookingToCancel == null) {
                    statusLabel.setText("Booking ID not found!");
                } else {
                    bookingToCancel.getTrain().cancelSeat();
                    bookings.remove(bookingToCancel);
                    statusLabel.setText("Booking " + bookingId + " cancelled successfully!");
                    
                    // Update other panels
                    updateViewTrainsPanel();
                    updateViewBookingsPanel();
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Please enter a valid Booking ID!");
            }
        });
    }

    private void updateViewTrainsPanel() {
        viewTrainsPanel.removeAll();
        createViewTrainsPanel();
        viewTrainsPanel.revalidate();
        viewTrainsPanel.repaint();
    }
}

class Train {
    private int number;
    private String name;
    private String source;
    private String destination;
    private int availableSeats;

    public Train(int number, String name, String source, String destination, int availableSeats) {
        this.number = number;
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.availableSeats = availableSeats;
    }

    public int getNumber() { return number; }
    public String getName() { return name; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public int getAvailableSeats() { return availableSeats; }
    
    public void bookSeat() { availableSeats--; }
    public void cancelSeat() { availableSeats++; }
}

class Booking {
    private int id;
    private String passengerName;
    private Train train;

    public Booking(int id, String passengerName, Train train) {
        this.id = id;
        this.passengerName = passengerName;
        this.train = train;
    }

    public int getId() { return id; }
    public String getPassengerName() { return passengerName; }
    public Train getTrain() { return train; }
}
