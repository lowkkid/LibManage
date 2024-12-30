package com.libmanage.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.libmanage.dto.DepartmentDto;
import com.libmanage.dto.EmployeeDetailsDto;
import com.libmanage.dto.EmployeeDto;
import com.libmanage.dto.RegisterRequest;
import com.libmanage.dto.SalaryAdjustmentRequest;
import com.libmanage.dto.SalaryDto;
import com.libmanage.dto.UserDTO;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Properties;
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
                if ("Сотрудник".equals(currentRole)) {
                    setupEmployeePanel();
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

    private void setupEmployeePanel() {
        buttonPanel.removeAll();

        JButton pastSalariesButton = new JButton("История выплат");
        pastSalariesButton.addActionListener(e -> fetchPastSalaries());

        JButton nextSalaryButton = new JButton("Следующая выплата");
        nextSalaryButton.addActionListener(e -> fetchNextSalary());

        buttonPanel.add(pastSalariesButton);
        buttonPanel.add(nextSalaryButton);

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void fetchPastSalaries() {
        try {
            HttpURLConnection conn = createConnectionWithAuth("http://localhost:8080/employees/salaries/past", "GET");

            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                SalaryDto[] pastSalaries = mapper.readValue(response, SalaryDto[].class);

                updatePastSalariesTable(pastSalaries);
            } else {
                JOptionPane.showMessageDialog(frame, "Не удалось получить историю выплат. Код ошибки: " + conn.getResponseCode(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePastSalariesTable(SalaryDto[] pastSalaries) {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{"Дата", "Сумма", "Бонус"});

        for (SalaryDto salary : pastSalaries) {
            tableModel.addRow(new Object[]{
                    salary.getPaymentDate(),
                    salary.getAmount(),
                    salary.getBonus()
            });
        }
    }

    private void fetchNextSalary() {
        try {
            HttpURLConnection conn = createConnectionWithAuth("http://localhost:8080/employees/salaries/next", "GET");

            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                SalaryDto nextSalary = mapper.readValue(response, SalaryDto.class);

                showNextSalaryDialog(nextSalary);
            } else {
                JOptionPane.showMessageDialog(frame, "Не удалось получить следующую выплату. Код ошибки: " + conn.getResponseCode(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Нет ближайших выплат");
        }
    }

    private void showNextSalaryDialog(SalaryDto nextSalary) {
        JDialog nextSalaryDialog = new JDialog(frame, "Следующая выплата", true);
        nextSalaryDialog.setSize(400, 200);
        nextSalaryDialog.setLayout(new GridLayout(3, 2));

        nextSalaryDialog.add(new JLabel("Дата:"));
        nextSalaryDialog.add(new JLabel(nextSalary.getPaymentDate().toString()));

        nextSalaryDialog.add(new JLabel("Сумма:"));
        nextSalaryDialog.add(new JLabel(String.format("%.2f", nextSalary.getAmount())));

        nextSalaryDialog.add(new JLabel("Бонус:"));
        nextSalaryDialog.add(new JLabel(String.format("%.2f", nextSalary.getBonus())));

        nextSalaryDialog.setVisible(true);
    }





    private void setupAdminPanel() {
        buttonPanel.removeAll();

        JButton createEmployeeButton = new JButton("Create Employee");
        createEmployeeButton.addActionListener(e -> showCreateEmployeeDialog());

        JButton viewEmployeesButton = new JButton("View Employees");
        viewEmployeesButton.addActionListener(e -> showEmployeeFilterDialog());

        JButton viewDepartmentsButton = new JButton("View Departments");
        viewDepartmentsButton.addActionListener(e -> fetchDepartments());

        buttonPanel.add(createEmployeeButton);
        buttonPanel.add(viewEmployeesButton);
        buttonPanel.add(viewDepartmentsButton);

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void fetchDepartments() {
        try {
            String urlString = "http://localhost:8080/departments";
            HttpURLConnection conn = createConnectionWithAuth(urlString, "GET");

            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                ObjectMapper mapper = new ObjectMapper();
                DepartmentDto[] departments = mapper.readValue(response, DepartmentDto[].class);

                updateDepartmentTable(departments);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to fetch departments. Error: " + conn.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDepartmentTable(DepartmentDto[] departments) {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{"ID", "Name", "Employee Count", "Average Salary", "Adjust Salaries", "Set Next Salary"});

        for (DepartmentDto department : departments) {
            tableModel.addRow(new Object[]{
                    department.getId(),
                    department.getName(),
                    department.getEmployeeCount(),
                    String.format("%.2f", department.getAverageSalary()),
                    "Adjust Salaries",
                    "Set Next Salary"
            });
        }

        // Колонка для изменения зарплат
        resultTable.getColumn("Adjust Salaries").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton button = new JButton(value.toString());
            button.addActionListener(e -> showAdjustSalaryDialog((int) tableModel.getValueAt(row, 0)));
            return button;
        });

        resultTable.getColumn("Adjust Salaries").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JButton button = new JButton(value.toString());
                button.addActionListener(e -> showAdjustSalaryDialog((int) tableModel.getValueAt(row, 0)));
                return button;
            }
        });

        // Колонка для назначения следующей зарплаты
        resultTable.getColumn("Set Next Salary").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton button = new JButton(value.toString());
            button.addActionListener(e -> showSalaryDateDialog((int) tableModel.getValueAt(row, 0)));
            return button;
        });

        resultTable.getColumn("Set Next Salary").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JButton button = new JButton(value.toString());
                button.addActionListener(e -> showSalaryDateDialog((int) tableModel.getValueAt(row, 0)));
                return button;
            }
        });
    }

    private void showSalaryDateDialog(int departmentId) {
        JDialog salaryDateDialog = new JDialog(frame, "Set Next Salary Date", true);
        salaryDateDialog.setSize(300, 200);
        salaryDateDialog.setLayout(new GridLayout(2, 1));

        JPanel datePickerPanel = new JPanel();
        JLabel label = new JLabel("Select Date:");
        JDatePickerImpl datePicker = createDatePicker(); // Метод для создания календаря
        datePickerPanel.add(label);
        datePickerPanel.add(datePicker);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            LocalDate selectedDate = LocalDate.of(
                    datePicker.getModel().getYear(),
                    datePicker.getModel().getMonth() + 1,
                    datePicker.getModel().getDay()
            );
            assignSalaryDate(departmentId, selectedDate);
            salaryDateDialog.dispose();
        });

        salaryDateDialog.add(datePickerPanel);
        salaryDateDialog.add(submitButton);

        salaryDateDialog.setVisible(true);
    }

    private JDatePickerImpl createDatePicker() {
        Properties i18nStrings = new Properties();
        i18nStrings.put("text.today", "Today");
        i18nStrings.put("text.month", "Month");
        i18nStrings.put("text.year", "Year");

        UtilDateModel model = new UtilDateModel();
        JDatePanelImpl datePanel = new JDatePanelImpl(model, i18nStrings);

        JFormattedTextField.AbstractFormatter formatter = new JFormattedTextField.AbstractFormatter() {
            @Override
            public Object stringToValue(String text) throws java.text.ParseException {
                return java.time.LocalDate.parse(text);
            }

            @Override
            public String valueToString(Object value) throws java.text.ParseException {
                if (value != null) {
                    return value.toString();
                }
                return "";
            }
        };

        return new JDatePickerImpl(datePanel, formatter);
    }


    private void assignSalaryDate(int departmentId, LocalDate paymentDate) {
        try {
            URL url = new URL("http://localhost:8080/departments/" + departmentId + "/assign-salary-date?paymentDate=" + paymentDate);
            HttpURLConnection conn = createConnectionWithAuth(url.toString(), "POST");

            if (conn.getResponseCode() == 200) {
                JOptionPane.showMessageDialog(frame, "Salary date assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to assign salary date. Error: " + conn.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void showAdjustSalaryDialog(int departmentId) {
        JDialog adjustSalaryDialog = new JDialog(frame, "Adjust Salaries", true);
        adjustSalaryDialog.setSize(300, 200);
        adjustSalaryDialog.setLayout(new GridLayout(3, 2));

        JLabel factorLabel = new JLabel("Adjustment Factor:");
        JTextField factorField = new JTextField();
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e -> {
            double adjustmentFactor = Double.parseDouble(factorField.getText());
            adjustSalaries(departmentId, adjustmentFactor);
            adjustSalaryDialog.dispose();
        });

        adjustSalaryDialog.add(factorLabel);
        adjustSalaryDialog.add(factorField);
        adjustSalaryDialog.add(new JLabel());
        adjustSalaryDialog.add(submitButton);

        adjustSalaryDialog.setVisible(true);
    }

    private void adjustSalaries(int departmentId, double adjustmentFactor) {
        try {
            URL url = new URL("http://localhost:8080/departments/" + departmentId + "/adjust-salaries");
            HttpURLConnection conn = createConnectionWithAuth(url.toString(), "PUT");
            conn.setDoOutput(true);

            // Создаем объект для тела запроса
            SalaryAdjustmentRequest request = new SalaryAdjustmentRequest();
            request.setAdjustmentFactor(adjustmentFactor);

            // Используем ObjectMapper для преобразования в JSON
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(request);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                JOptionPane.showMessageDialog(frame, "Salaries adjusted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                fetchDepartments(); // Обновляем таблицу
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to adjust salaries. Error: " + conn.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showEmployeeFilterDialog() {
        JDialog filterDialog = new JDialog(frame, "Filter Employees", true);
        filterDialog.setSize(400, 200);
        filterDialog.setLayout(new GridLayout(2, 2));

        JLabel departmentLabel = new JLabel("Department ID (Optional):");
        JTextField departmentField = new JTextField();
        JButton fetchButton = new JButton("Fetch Employees");

        fetchButton.addActionListener(e -> {
            String departmentId = departmentField.getText();
            fetchEmployees(departmentId.isEmpty() ? null : Integer.parseInt(departmentId));
            filterDialog.dispose();
        });

        filterDialog.add(departmentLabel);
        filterDialog.add(departmentField);
        filterDialog.add(new JLabel());
        filterDialog.add(fetchButton);

        filterDialog.setVisible(true);
    }

    private void fetchEmployees(Integer departmentId) {
        try {
            String urlString = "http://localhost:8080/employees";
            if (departmentId != null) {
                urlString += "?departmentId=" + departmentId;
            }
            HttpURLConnection conn = createConnectionWithAuth(urlString, "GET");

            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                ObjectMapper mapper = new ObjectMapper();
                EmployeeDto[] employees = mapper.readValue(response, EmployeeDto[].class);

                updateEmployeeTable(employees);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to fetch employees. Error: " + conn.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateEmployeeTable(EmployeeDto[] employees) {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{"ID", "Username", "Department", "Salary", "Active", "Transfer", "Details"});

        for (EmployeeDto employee : employees) {
            tableModel.addRow(new Object[]{
                    employee.getId(),
                    employee.getUsername(),
                    employee.getDepartmentName(),
                    employee.getSalary(),
                    employee.getActive(),
                    "Transfer",
                    "Details"
            });
        }

        // Кнопка "Transfer"
        resultTable.getColumn("Transfer").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton button = new JButton(value.toString());
            button.addActionListener(e -> showTransferDialog((int) tableModel.getValueAt(row, 0)));
            return button;
        });

        resultTable.getColumn("Transfer").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JButton button = new JButton(value.toString());
                button.addActionListener(e -> showTransferDialog((int) tableModel.getValueAt(row, 0)));
                return button;
            }
        });

        // Кнопка "Details"
        resultTable.getColumn("Details").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton button = new JButton(value.toString());
            button.addActionListener(e -> showEmployeeDetailsDialog((int) tableModel.getValueAt(row, 0)));
            return button;
        });

        resultTable.getColumn("Details").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JButton button = new JButton(value.toString());
                button.addActionListener(e -> showEmployeeDetailsDialog((int) tableModel.getValueAt(row, 0)));
                return button;
            }
        });
    }

    private void showEmployeeDetailsDialog(int employeeId) {
        try {
            URL url = new URL("http://localhost:8080/employees/" + employeeId + "/details");
            HttpURLConnection conn = createConnectionWithAuth(url.toString(), "GET");

            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                EmployeeDetailsDto details = mapper.readValue(response, EmployeeDetailsDto.class);

                JDialog detailsDialog = new JDialog(frame, "Employee Details", true);
                detailsDialog.setSize(800, 600);
                detailsDialog.setLayout(new BorderLayout());

                JPanel infoPanel = new JPanel(new GridLayout(2, 1));
                infoPanel.add(new JLabel("Username: " + details.getUsername()));
                infoPanel.add(new JLabel("Department: " + details.getDepartmentName()));

                JTable pastSalariesTable = createSalariesTable(details.getPastSalaries());
                JTable upcomingSalariesTable = createUpcomingSalariesTable(details.getUpcomingSalaries());

                JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                        new JScrollPane(pastSalariesTable),
                        new JScrollPane(upcomingSalariesTable));

                splitPane.setDividerLocation(300);

                detailsDialog.add(infoPanel, BorderLayout.NORTH);
                detailsDialog.add(splitPane, BorderLayout.CENTER);

                detailsDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to fetch employee details. Error: " + conn.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JTable createSalariesTable(List<SalaryDto> salaries) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Date", "Amount", "Bonus"}, 0);
        salaries.forEach(s -> model.addRow(new Object[]{
                s.getPaymentDate(), s.getAmount(), s.getBonus()
        }));
        return new JTable(model);
    }

    private JTable createUpcomingSalariesTable(List<SalaryDto> salaries) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Salary ID", "Date", "Amount", "Bonus", "Action"}, 0);
        salaries.forEach(s -> model.addRow(new Object[]{
                s.getId(), // Добавляем идентификатор зарплаты как скрытую колонку
                s.getPaymentDate(),
                s.getAmount(),
                s.getBonus(),
                "Edit Bonus"
        }));

        JTable table = new JTable(model);

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Настраиваем кнопку "Edit Bonus"
        table.getColumn("Action").setCellRenderer((tbl, value, isSelected, hasFocus, row, column) -> {
            JButton button = new JButton(value.toString());
            button.addActionListener(e -> {
                int salaryId = (int) model.getValueAt(row, 0); // Берем идентификатор зарплаты из скрытой колонки
                showEditBonusDialog(salaryId);
            });
            return button;
        });

        table.getColumn("Action").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JButton button = new JButton(value.toString());
                button.addActionListener(e -> {
                    int salaryId = (int) model.getValueAt(row, 0); // Берем идентификатор зарплаты из скрытой колонки
                    showEditBonusDialog(salaryId);
                });
                return button;
            }
        });

        return table;
    }


    private void showEditBonusDialog(int salaryId) {
        JDialog editBonusDialog = new JDialog(frame, "Edit Bonus", true);
        editBonusDialog.setSize(300, 200);
        editBonusDialog.setLayout(new GridLayout(2, 2));

        JLabel bonusLabel = new JLabel("New Bonus:");
        JTextField bonusField = new JTextField();
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e -> {
            double newBonus = Double.parseDouble(bonusField.getText());
            updateBonus(salaryId, newBonus);
            editBonusDialog.dispose();
        });

        editBonusDialog.add(bonusLabel);
        editBonusDialog.add(bonusField);
        editBonusDialog.add(new JLabel());
        editBonusDialog.add(submitButton);

        editBonusDialog.setVisible(true);
    }

    private void updateBonus(int salaryId, double bonus) {
        try {
            URL url = new URL("http://localhost:8080/employees/salaries/" + salaryId + "/update-bonus?bonus=" + bonus);
            HttpURLConnection conn = createConnectionWithAuth(url.toString(), "PUT");

            if (conn.getResponseCode() == 200) {
                JOptionPane.showMessageDialog(frame, "Bonus updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to update bonus. Error: " + conn.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void showTransferDialog(int employeeId) {
        JDialog transferDialog = new JDialog(frame, "Transfer Employee", true);
        transferDialog.setSize(300, 200);
        transferDialog.setLayout(new GridLayout(3, 2));

        JLabel departmentLabel = new JLabel("New Department ID:");
        JTextField departmentField = new JTextField();
        JButton submitButton = new JButton("Transfer");

        submitButton.addActionListener(e -> {
            int newDepartmentId = Integer.parseInt(departmentField.getText());
            transferEmployee(employeeId, newDepartmentId);
            transferDialog.dispose();
        });

        transferDialog.add(departmentLabel);
        transferDialog.add(departmentField);
        transferDialog.add(new JLabel());
        transferDialog.add(submitButton);

        transferDialog.setVisible(true);
    }

    private void transferEmployee(int employeeId, int newDepartmentId) {
        try {
            URL url = new URL("http://localhost:8080/employees/" + employeeId + "/transfer");
            HttpURLConnection conn = createConnectionWithAuth(url.toString(), "PUT");
            conn.setDoOutput(true);

            String requestBody = String.format("{\"newDepartmentId\":%d}", newDepartmentId);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                JOptionPane.showMessageDialog(frame, "Employee transferred successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                fetchEmployees(null); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to transfer employee. Error: " + conn.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showCreateEmployeeDialog() {
        JDialog createEmployeeDialog = new JDialog(frame, "Create Employee", true);
        createEmployeeDialog.setSize(400, 300);
        createEmployeeDialog.setLayout(new GridLayout(4, 2, 10, 10));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField departmentIdField = new JTextField();
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            int departmentId = Integer.parseInt(departmentIdField.getText());
            createEmployee(username, password, departmentId);
            createEmployeeDialog.dispose();
        });

        createEmployeeDialog.add(new JLabel("Username:"));
        createEmployeeDialog.add(usernameField);
        createEmployeeDialog.add(new JLabel("Password:"));
        createEmployeeDialog.add(passwordField);
        createEmployeeDialog.add(new JLabel("Department ID:"));
        createEmployeeDialog.add(departmentIdField);
        createEmployeeDialog.add(new JLabel());
        createEmployeeDialog.add(submitButton);

        createEmployeeDialog.setVisible(true);
    }

    private void createEmployee(String username, String password, int departmentId) {
        try {
            URL url = new URL("http://localhost:8080/employees/onboard");
            HttpURLConnection conn = createConnectionWithAuth(url.toString(), "POST");
            conn.setDoOutput(true);

            String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\",\"departmentId\":%d}",
                    username, password, departmentId);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                JOptionPane.showMessageDialog(frame, "Employee created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to create employee. Error: " + conn.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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



    private HttpURLConnection createConnectionWithAuth(String urlString, String method) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((currentUser + ":" + password).getBytes(StandardCharsets.UTF_8)));
        conn.setRequestProperty("Content-Type", "application/json");
        return conn;
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