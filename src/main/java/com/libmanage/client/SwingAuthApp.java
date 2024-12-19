package com.libmanage.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.libmanage.dto.BookDTO;
import com.libmanage.dto.RegisterRequest;
import com.libmanage.dto.ReservationDTO;
import com.libmanage.dto.UserDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

public class SwingAuthApp {

    private JFrame frame;
    private JLabel userLabel;
    private JButton loginButton;
    private JButton registerButton;
    private JPanel buttonPanel;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private String currentUser;
    private String password;
    private String currentRole;

    public SwingAuthApp() {
        currentUser = "Unauthorized";
        currentRole = "";
        initialize();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingAuthApp::new);
    }

    private void initialize() {
        frame = new JFrame("Library Auth App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        userLabel = new JLabel(getUserStatus());
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        loginButton.addActionListener(e -> showLoginDialog());
        registerButton.addActionListener(e -> showRegisterDialog());

        topPanel.add(userLabel);
        topPanel.add(loginButton);
        topPanel.add(registerButton);

        frame.add(topPanel, BorderLayout.NORTH);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 10, 10));
        buttonPanel.setPreferredSize(new Dimension(200, 0));
        frame.add(buttonPanel, BorderLayout.WEST);

        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private String getUserStatus() {
        return currentUser.equals("Unauthorized") ? currentUser : currentUser + " (" + currentRole + ")";
    }

    private void showLoginDialog() {
        JDialog loginDialog = new JDialog(frame, "Login", true);
        loginDialog.setSize(300, 200);
        loginDialog.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton submitButton = new JButton("Login");

        submitButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (authenticate(username, password)) {
                userLabel.setText(getUserStatus());
                loginDialog.dispose();
                if ("Читатель".equals(currentRole)) {
                    setupReaderPanel();
                } else if ("Администратор".equals(currentRole)) {
                    setupAdminPanel();
                }

            } else {
                JOptionPane.showMessageDialog(loginDialog, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginDialog.add(usernameLabel);
        loginDialog.add(usernameField);
        loginDialog.add(passwordLabel);
        loginDialog.add(passwordField);
        loginDialog.add(new JLabel());
        loginDialog.add(submitButton);

        loginDialog.setVisible(true);
    }

    private void setupAdminPanel() {
        buttonPanel.removeAll();

        JButton allUsersButton = new JButton("View All Users");
        allUsersButton.addActionListener(e -> fetchAllUsers());

        JButton allReservationsButton = new JButton("View All Reservations");
        allReservationsButton.addActionListener(e -> fetchAllReservations());

        buttonPanel.add(allUsersButton);
        buttonPanel.add(allReservationsButton);

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void fetchAllUsers() {
        try {
            HttpURLConnection conn = createConnectionWithAuth("http://localhost:8080/admin/users", "GET");

            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                ObjectMapper mapper = new ObjectMapper();
                UserDTO[] users = mapper.readValue(response, UserDTO[].class);

                updateTable(users);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to fetch users. Error: " + conn.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTable(UserDTO[] users) {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{"ID", "Username", "Role", "Action"});

        for (UserDTO user : users) {
            tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getRole(),
                    "Delete"
            });
        }

        resultTable.getColumn("Action").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton button = new JButton(value.toString());
            button.addActionListener(e -> deleteUser((int) tableModel.getValueAt(row, 0)));
            return button;
        });

        resultTable.getColumn("Action").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JButton button = new JButton(value.toString());
                button.addActionListener(e -> deleteUser((int) tableModel.getValueAt(row, 0)));
                return button;
            }
        });
    }



    private void deleteUser(int userId) {
        try {
            HttpURLConnection conn = createConnectionWithAuth("http://localhost:8080/admin/users/" + userId, "DELETE");

            if (conn.getResponseCode() == 204) {
                JOptionPane.showMessageDialog(frame, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                fetchAllUsers(); // Refresh users table
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to delete user. Error: " + conn.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSearchDialog() {
        JDialog searchDialog = new JDialog(frame, "Search Books", true);
        searchDialog.setSize(400, 300);
        searchDialog.setLayout(new GridLayout(5, 2, 10, 10));

        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField genreField = new JTextField();
        JTextField isbnField = new JTextField();
        JButton searchButton = new JButton("Search");

        searchButton.addActionListener(e -> {
            String title = titleField.getText();
            String author = authorField.getText();
            String genre = genreField.getText();
            String isbn = isbnField.getText();
            searchBooks(title, author, genre, isbn);
            searchDialog.dispose();
        });

        searchDialog.add(new JLabel("Title:"));
        searchDialog.add(titleField);
        searchDialog.add(new JLabel("Author:"));
        searchDialog.add(authorField);
        searchDialog.add(new JLabel("Genre:"));
        searchDialog.add(genreField);
        searchDialog.add(new JLabel("ISBN:"));
        searchDialog.add(isbnField);
        searchDialog.add(new JLabel());
        searchDialog.add(searchButton);

        searchDialog.setVisible(true);
    }

    private void searchBooks(String title, String author, String genre, String isbn) {
        try {
            StringBuilder urlBuilder = new StringBuilder("http://localhost:8080/books/search?");
            if (!title.isEmpty()) urlBuilder.append("title=").append(URLEncoder.encode(title, StandardCharsets.UTF_8)).append("&");
            if (!author.isEmpty()) urlBuilder.append("author=").append(URLEncoder.encode(author, StandardCharsets.UTF_8)).append("&");
            if (!genre.isEmpty()) urlBuilder.append("genre=").append(URLEncoder.encode(genre, StandardCharsets.UTF_8)).append("&");
            if (!isbn.isEmpty()) urlBuilder.append("isbn=").append(URLEncoder.encode(isbn, StandardCharsets.UTF_8));

            String urlString = urlBuilder.toString().replaceAll("&$", ""); // Удалить последний "&"
            HttpURLConnection conn = createConnectionWithAuth(urlString, "GET");

            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                ObjectMapper mapper = new ObjectMapper();
                BookDTO[] books = mapper.readValue(response, BookDTO[].class);

                updateTable(books);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to search books. Error: " + conn.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    private void fetchAllReservations() {
        try {
            HttpURLConnection conn = createConnectionWithAuth("http://localhost:8080/admin/reservations", "GET");

            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                ReservationDTO[] reservations = mapper.readValue(response, ReservationDTO[].class);

                updateTable(reservations);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to fetch reservations. Error: " + conn.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRegisterDialog() {
        JDialog registerDialog = new JDialog(frame, "Register", true);
        registerDialog.setSize(300, 200);
        registerDialog.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton submitButton = new JButton("Register");

        submitButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (register(username, password)) {
                JOptionPane.showMessageDialog(registerDialog, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                registerDialog.dispose();
                showLoginDialog();
            } else {
                JOptionPane.showMessageDialog(registerDialog, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerDialog.add(usernameLabel);
        registerDialog.add(usernameField);
        registerDialog.add(passwordLabel);
        registerDialog.add(passwordField);
        registerDialog.add(new JLabel());
        registerDialog.add(submitButton);

        registerDialog.setVisible(true);
    }

    private boolean authenticate(String username, String password) {
        try {
            URL url = new URL("http://localhost:8080/auth/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(new AuthRequest(username, password));
            System.out.println(requestBody);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                JsonNode responseJson = mapper.readTree(conn.getInputStream());
                currentRole = responseJson.get("role").get("roleName").asText();
                currentUser = username;
                this.password = password;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean register(String username, String password) {
        try {
            URL url = new URL("http://localhost:8080/auth/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(new RegisterRequest(username, password, password));

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setupReaderPanel() {
        buttonPanel.removeAll();

        JButton availableBooksButton = new JButton("Available Books");
        availableBooksButton.addActionListener(e -> fetchAvailableBooks());

        JButton bookHistoryButton = new JButton("My Book History");
        bookHistoryButton.addActionListener(e -> fetchUserBookHistory());

        JButton recommendationsButton = new JButton("Recommendations");
        recommendationsButton.addActionListener(e -> fetchBookRecommendations());

        JButton popularBooksButton = new JButton("Popular Books");
        popularBooksButton.addActionListener(e -> fetchPopularBooks());

        JButton searchBooksButton = new JButton("Search Books");
        searchBooksButton.addActionListener(e -> showSearchDialog());
        buttonPanel.add(searchBooksButton);


        JButton myReservationsButton = new JButton("My Reservations");
        myReservationsButton.addActionListener(e -> fetchUserReservations());

        buttonPanel.add(availableBooksButton);
        buttonPanel.add(bookHistoryButton);
        buttonPanel.add(recommendationsButton);
        buttonPanel.add(popularBooksButton);
        buttonPanel.add(myReservationsButton);

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private HttpURLConnection createConnectionWithAuth(String urlString, String method) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((currentUser + ":" + password).getBytes(StandardCharsets.UTF_8)));
        conn.setRequestProperty("Content-Type", "application/json");
        return conn;
    }

    private void fetchAvailableBooks() {
        fetchBooks("http://localhost:8080/books/available");
    }

    private void fetchUserBookHistory() {
        fetchBooks("http://localhost:8080/books/my-history");
    }

    private void fetchBookRecommendations() {
        fetchBooks("http://localhost:8080/books/recommendations");
    }

    private void fetchPopularBooks() {
        fetchBooks("http://localhost:8080/books/popular");
    }

    private void fetchUserReservations() {
        try {
            HttpURLConnection conn = createConnectionWithAuth("http://localhost:8080/reservations/my", "GET");

            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                ReservationDTO[] reservations = mapper.readValue(response, ReservationDTO[].class);

                updateTable(reservations);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to fetch reservations. Error: " + conn.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchBooks(String urlString) {
        try {
            HttpURLConnection conn = createConnectionWithAuth(urlString, "GET");

            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                ObjectMapper mapper = new ObjectMapper();
                BookDTO[] books = mapper.readValue(response, BookDTO[].class);

                updateTable(books);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to fetch books. Error: " + conn.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTable(BookDTO[] books) {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{"ID", "Title", "Author", "Genre", "ISBN", "Available Copies", "Action"});

        for (BookDTO book : books) {
            tableModel.addRow(new Object[]{
                    book.getId(),
                    book.getTitle(),
                    book.getAuthorName(),
                    book.getGenreName(),
                    book.getIsbn(),
                    book.getAvailableCopies(),
                    "Reserve"
            });
        }

        resultTable.getColumn("Action").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton button = new JButton(value.toString());
            button.addActionListener(e -> createReservation((int) tableModel.getValueAt(row, 0)));
            return button;
        });

        resultTable.getColumn("Action").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JButton button = new JButton(value.toString());
                button.addActionListener(e -> createReservation((int) tableModel.getValueAt(row, 0)));
                return button;
            }
        });
    }

    private void updateTable(ReservationDTO[] reservations) {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{"ID", "Book Title", "Reservation Date", "Status", "Action"});

        for (ReservationDTO reservation : reservations) {
            tableModel.addRow(new Object[]{
                    reservation.getId(),
                    reservation.getBookTitle(),
                    reservation.getReservationDate(),
                    reservation.getStatus(),
                    "Cancel"
            });
        }

        resultTable.getColumn("Action").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton button = new JButton(value.toString());
            button.addActionListener(e -> cancelReservation((int) tableModel.getValueAt(row, 0)));
            return button;
        });

        resultTable.getColumn("Action").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JButton button = new JButton(value.toString());
                button.addActionListener(e -> cancelReservation((int) tableModel.getValueAt(row, 0)));
                return button;
            }
        });
    }


    private void createReservation(int bookId) {
        try {
            HttpURLConnection conn = createConnectionWithAuth("http://localhost:8080/reservations", "POST");
            conn.setDoOutput(true);

            String requestBody = "{\"bookId\":" + bookId + "}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 201) {
                JOptionPane.showMessageDialog(frame, "Reservation created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to create reservation. Error: " + conn.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelReservation(int reservationId) {
        try {
            HttpURLConnection conn = createConnectionWithAuth("http://localhost:8080/reservations/" + reservationId + "/cancel", "POST");

            if (conn.getResponseCode() == 204) {
                JOptionPane.showMessageDialog(frame, "Reservation canceled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                fetchUserReservations(); // Refresh reservations
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to cancel reservation. Error: " + conn.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class AuthRequest {
        private String username;
        private String password;

        public AuthRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
