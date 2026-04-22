package com.library.view;

import com.library.controller.*;
import com.library.repository.StaffDutyRepository;
import com.library.service.LoginService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Optional;

public class LoginFrame extends JFrame {

    private static final Color BG_MAIN = new Color(20, 24, 33);
    private static final Color BG_CARD = new Color(34, 40, 52);
    private static final Color BG_INPUT = new Color(45, 52, 66);

    private static final Color PRIMARY = new Color(96, 103, 255);
    private static final Color PRIMARY_HOVER = new Color(118, 124, 255);
    private static final Color PRIMARY_PRESS = new Color(80, 86, 235);

    private static final Color TEXT_MAIN = new Color(245, 247, 250);
    private static final Color TEXT_LIGHT = new Color(170, 178, 190);
    private static final Color ERROR = new Color(255, 110, 130);
    private static final Color BORDER = new Color(72, 80, 96);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 34);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);

    private final BookController bookCtrl;
    private final UserController userCtrl;
    private final LibrarianController libCtrl;
    private final TransactionController txCtrl;
    private final AdminController adminCtrl;
    private final MaintenanceController maintCtrl;
    private final StaffDutyRepository dutyRepo;
    private final LoginService loginService;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private JButton loginBtn;

    public LoginFrame(BookController bookCtrl, UserController userCtrl,
                      LibrarianController libCtrl, TransactionController txCtrl,
                      AdminController adminCtrl, MaintenanceController maintCtrl,
                      StaffDutyRepository dutyRepo) {

        this.bookCtrl = bookCtrl;
        this.userCtrl = userCtrl;
        this.libCtrl = libCtrl;
        this.txCtrl = txCtrl;
        this.adminCtrl = adminCtrl;
        this.maintCtrl = maintCtrl;
        this.dutyRepo = dutyRepo;
        this.loginService = new LoginService();

        buildUI();
    }

    private void buildUI() {
        setTitle("Library Management System — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getContentPane().setBackground(BG_MAIN);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_MAIN);
        root.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setPreferredSize(new Dimension(460, 540));
        card.setMaximumSize(new Dimension(460, 540));
        card.setBorder(new EmptyBorder(42, 52, 42, 52));

        JLabel title = new JLabel("Library System", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_MAIN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to access your account", SwingConstants.CENTER);
        subtitle.setFont(FONT_BODY);
        subtitle.setForeground(TEXT_LIGHT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel icon = new JLabel("◫", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 56));
        icon.setForeground(PRIMARY);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = makeLabel("Email address");
        emailField = makeField();

        JLabel passLabel = makeLabel("Password");
        passwordField = new JPasswordField();
        styleTextField(passwordField);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(FONT_BODY);
        errorLabel.setForeground(ERROR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn = new JButton("Sign In");
        loginBtn.setFont(FONT_HEADER);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(PRIMARY);
        loginBtn.setOpaque(true);
        loginBtn.setContentAreaFilled(true);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setMargin(new Insets(0, 0, 0, 0));
        loginBtn.setMaximumSize(new Dimension(320, 48));
        loginBtn.setPreferredSize(new Dimension(320, 48));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        loginBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginBtn.setBackground(PRIMARY_HOVER);
            }

            public void mouseExited(MouseEvent e) {
                loginBtn.setBackground(PRIMARY);
            }

            public void mousePressed(MouseEvent e) {
                loginBtn.setBackground(PRIMARY_PRESS);
            }

            public void mouseReleased(MouseEvent e) {
                loginBtn.setBackground(PRIMARY_HOVER);
            }
        });

        loginBtn.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());

        card.add(icon);
        card.add(Box.createVerticalStrut(18));
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(34));

        card.add(emailLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(emailField);
        card.add(Box.createVerticalStrut(18));

        card.add(passLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(14));

        card.add(errorLabel);
        card.add(Box.createVerticalStrut(18));
        card.add(loginBtn);

        root.add(card, new GridBagConstraints());
        setContentPane(root);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter email and password.");
            return;
        }

        Optional<String[]> result = loginService.login(email, password);

        if (result.isEmpty()) {
            showError("Invalid email or password.");
            return;
        }

        String[] info = result.get();
        String role = info[0];
        String id = info[1];
        String name = info[2];

        dispose();

        SwingUtilities.invokeLater(() -> {
            switch (role) {
                case LoginService.ROLE_ADMIN:
                case LoginService.ROLE_LIBRARIAN:
                    new LibraryApp(bookCtrl, userCtrl, libCtrl, txCtrl,
                            adminCtrl, maintCtrl, dutyRepo,
                            role, id, name).setVisible(true);
                    break;

                case LoginService.ROLE_STAFF:
                    new StaffPortal(dutyRepo, id, name,
                            bookCtrl, userCtrl, libCtrl, txCtrl,
                            adminCtrl, maintCtrl).setVisible(true);
                    break;

                case LoginService.ROLE_USER:
                    new UserPortal(txCtrl, userCtrl, id, name,
                            bookCtrl, libCtrl, adminCtrl,
                            maintCtrl, dutyRepo).setVisible(true);
                    break;
            }
        });
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(FONT_BODY);
        l.setForeground(TEXT_LIGHT);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setMaximumSize(new Dimension(320, 20));
        return l;
    }

    private JTextField makeField() {
        JTextField f = new JTextField();
        styleTextField(f);
        return f;
    }

    private void styleTextField(JTextField f) {
        f.setFont(FONT_BODY);
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        f.setMaximumSize(new Dimension(320, 42));
        f.setPreferredSize(new Dimension(320, 42));
        f.setAlignmentX(Component.CENTER_ALIGNMENT);
        f.setHorizontalAlignment(JTextField.CENTER);
    }
}