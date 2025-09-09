import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Main extends JFrame {
    private static final String url = "jdbc:mysql://localhost:3306/";
    private static final String username = "";
    private static final String password = "";

    private Connection connection;

    public Main() {
        setTitle("Hotel Management System");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10));

        JButton reserveBtn = new JButton("Reserve Room");
        JButton viewBtn = new JButton("View Reservations");
        JButton updateBtn = new JButton("Update Reservation");
        JButton deleteBtn = new JButton("Delete Reservation");
        JButton getRoomBtn = new JButton("Get Room Number");
        JButton exitBtn = new JButton("Exit");

        add(reserveBtn);
        add(viewBtn);
        add(updateBtn);
        add(deleteBtn);
        add(getRoomBtn);
        add(exitBtn);

        connectDB();

        // Button actions
        reserveBtn.addActionListener(e -> reserveRoom());
        viewBtn.addActionListener(e -> viewReservations());
        updateBtn.addActionListener(e -> updateReservation());
        deleteBtn.addActionListener(e -> deleteReservation());
        getRoomBtn.addActionListener(e -> getRoomNumber());
        exitBtn.addActionListener(e -> System.exit(0));
    }

    private void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + e.getMessage());
        }
    }

    private void reserveRoom() {
        JTextField nameField = new JTextField();
        JTextField roomField = new JTextField();
        JTextField contactField = new JTextField();

        Object[] fields = {
                "Guest Name:", nameField,
                "Room Number:", roomField,
                "Contact Number:", contactField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Reserve Room", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String sql = "INSERT INTO reservations (guest_name, room_num, contact_num) VALUES (?, ?, ?)";
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setString(1, nameField.getText());
                pst.setInt(2, Integer.parseInt(roomField.getText()));
                pst.setString(3, contactField.getText());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Reservation Successful!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void viewReservations() {
        try {
            String sql = "SELECT reserv_id, guest_name, room_num, contact_num, reserv_date FROM reservations";
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Guest", "Room", "Contact", "Date"}, 0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("reserv_id"),
                        rs.getString("guest_name"),
                        rs.getInt("room_num"),
                        rs.getString("contact_num"),
                        rs.getTimestamp("reserv_date")
                });
            }

            JTable table = new JTable(model);
            JOptionPane.showMessageDialog(this, new JScrollPane(table), "Reservations", JOptionPane.PLAIN_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void getRoomNumber() {
        String id = JOptionPane.showInputDialog(this, "Enter Reservation ID:");
        String guest = JOptionPane.showInputDialog(this, "Enter Guest Name:");

        try {
            String sql = "SELECT room_num FROM reservations WHERE reserv_id=? AND guest_name=?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(id));
            pst.setString(2, guest);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Room Number: " + rs.getInt("room_num"));
            } else {
                JOptionPane.showMessageDialog(this, "Reservation Not Found!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateReservation() {
        String id = JOptionPane.showInputDialog(this, "Enter Reservation ID to Update:");

        JTextField nameField = new JTextField();
        JTextField roomField = new JTextField();
        JTextField contactField = new JTextField();

        Object[] fields = {
                "New Guest Name:", nameField,
                "New Room Number:", roomField,
                "New Contact Number:", contactField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Update Reservation", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String sql = "UPDATE reservations SET guest_name=?, room_num=?, contact_num=? WHERE reserv_id=?";
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setString(1, nameField.getText());
                pst.setInt(2, Integer.parseInt(roomField.getText()));
                pst.setString(3, contactField.getText());
                pst.setInt(4, Integer.parseInt(id));
                int rows = pst.executeUpdate();

                if (rows > 0) JOptionPane.showMessageDialog(this, "Reservation Updated!");
                else JOptionPane.showMessageDialog(this, "Reservation Not Found!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void deleteReservation() {
        String id = JOptionPane.showInputDialog(this, "Enter Reservation ID to Delete:");

        try {
            String sql = "DELETE FROM reservations WHERE reserv_id=?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(id));
            int rows = pst.executeUpdate();

            if (rows > 0) JOptionPane.showMessageDialog(this, "Reservation Deleted!");
            else JOptionPane.showMessageDialog(this, "Reservation Not Found!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new Main().setVisible(true);
    }
}
