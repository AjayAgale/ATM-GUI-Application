import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class SmartATM extends JFrame implements ActionListener {

    private double balance = 0;
    private String userPIN;
    private JTextField amountField;
    private JButton depositBtn, withdrawBtn, checkBalanceBtn, resetPinBtn, exitBtn; 
    private JLabel messageLabel;
    private JTextArea historyArea;
    private ArrayList<String> transactionHistory = new ArrayList<>();

    public SmartATM() {
        setTitle("Smart ATM Machine");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setupPIN(); // Ask user to set PIN at start

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(0x2980b9);
                Color color2 = new Color(0x6dd5fa);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);

        JLabel title = new JLabel("Wellcome To My ATM ", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(150, 10, 300, 40);
        title.setForeground(Color.WHITE);
        mainPanel.add(title);

        JLabel amountLabel = new JLabel("Enter Amount:");
        amountLabel.setBounds(100, 70, 120, 30);
        amountLabel.setForeground(Color.WHITE);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(220, 70, 200, 30);
        mainPanel.add(amountField);

        amountField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                amountField.setEnabled(true);
            }
        });

        depositBtn = createButton("Deposit", 50, 120);
        withdrawBtn = createButton("Withdraw", 230, 120);
        checkBalanceBtn = createButton("Check Balance", 410, 120);
        resetPinBtn = createButton("Reset PIN", 230, 165); 

        mainPanel.add(depositBtn);
        mainPanel.add(withdrawBtn);
        mainPanel.add(checkBalanceBtn);
        mainPanel.add(resetPinBtn);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setBounds(100, 200, 400, 30);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(messageLabel);

        JLabel historyLabel = new JLabel("Transaction History:");
        historyLabel.setBounds(100, 230, 200, 25);
        historyLabel.setForeground(Color.WHITE);
        historyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(historyLabel);

        historyArea = new JTextArea();
        historyArea.setBounds(100, 260, 400, 100);
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        mainPanel.add(historyArea);

        exitBtn = new JButton("Exit");
        exitBtn.setBounds(225, 380, 150, 35);
        exitBtn.setBackground(Color.WHITE);
        exitBtn.setFont(new Font("Arial", Font.BOLD, 14));
        exitBtn.addActionListener(this);
        mainPanel.add(exitBtn);

        add(mainPanel);
        setVisible(true);
    }

    private void setupPIN() {
        while (true) {
            JPasswordField pinField = new JPasswordField();
            int option = JOptionPane.showConfirmDialog(this, pinField, "Set your 4-digit PIN", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String inputPIN = new String(pinField.getPassword());
                if (inputPIN.matches("\\d{4}")) {
                    userPIN = inputPIN;
                    JOptionPane.showMessageDialog(this, "PIN set successfully!");
                    break;
                } else {
                    JOptionPane.showMessageDialog(this, "PIN must be exactly 4 digits.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "PIN setup is required to use the ATM.");
                System.exit(0);
            }
        }
    }

    private boolean verifyPIN() {
        JPasswordField pinField = new JPasswordField();
        int option = JOptionPane.showConfirmDialog(this, pinField, "Enter your 4-digit PIN", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String enteredPIN = new String(pinField.getPassword());
            if (enteredPIN.equals(userPIN)) {
                return true;
            } else {
                showMessage("Incorrect PIN!");
            }
        }
        return false;
    }

    private JButton createButton(String text, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 150, 30);
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.addActionListener(this);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        String input = amountField.getText().trim();

        switch (command) {
            case "Deposit":
                if (!verifyPIN()) return;
                try {
                    double amount = Double.parseDouble(input);
                    if (amount <= 0) {
                        showMessage("Amount must be positive!");
                        return;
                    }
                    balance += amount;
                    transactionHistory.add("Deposited: ₹" + amount);
                    showMessage("₹" + amount + " deposited successfully.");
                } catch (NumberFormatException ex) {
                    showMessage("Invalid amount!");
                }
                break;

            case "Withdraw":
                if (!verifyPIN()) return;
                try {
                    double amount = Double.parseDouble(input);
                    if (amount <= 0) {
                        showMessage("Amount must be positive!");
                        return;
                    }
                    if (amount > balance) {
                        showMessage("Insufficient balance!");
                        return;
                    }
                    balance -= amount;
                    transactionHistory.add("Withdrawn: ₹" + amount);
                    showMessage("₹" + amount + " withdrawn successfully.");
                } catch (NumberFormatException ex) {
                    showMessage("Invalid amount!");
                }
                break;

            case "Check Balance":
                if (!verifyPIN()) return;
                transactionHistory.add("Checked Balance: ₹" + balance);
                showMessage("Current Balance: ₹" + balance);
                break;

            case "Reset PIN":
                if (!verifyPIN()) return;
                setupPIN(); // Re-setup the PIN
                showMessage("PIN has been reset successfully.");
                break;

            case "Exit":
                System.exit(0);
                break;
        }

        amountField.setText("");
        amountField.setEnabled(false);
        displayHistory();
    }

    private void showMessage(String msg) {
        messageLabel.setText(msg);
    }

    private void displayHistory() {
        if (transactionHistory.isEmpty()) {
            historyArea.setText("No transactions yet.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String record : transactionHistory) {
                sb.append(record).append("\n");
            }
            historyArea.setText(sb.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SmartATM());
    }
}
