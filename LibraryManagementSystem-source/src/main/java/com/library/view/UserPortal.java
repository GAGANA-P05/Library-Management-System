package com.library.view;

import com.library.controller.*;
import com.library.model.*;
import com.library.repository.StaffDutyRepository;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserPortal extends JFrame {

    // ── Palette (identical to LibraryApp) ──────────────────────────────────
    private static final Color BG_DARK      = new Color(244, 246, 252);
    private static final Color BG_CARD      = new Color(255, 255, 255);
    private static final Color BG_SIDEBAR   = new Color(94, 84, 245);
    private static final Color BG_ELEVATED  = new Color(248, 249, 255);
    private static final Color ACCENT       = new Color(92, 81, 242);
    private static final Color ACCENT2      = new Color(71, 196, 126);
    private static final Color ACCENT3      = new Color(244, 106, 128);
    private static final Color ACCENT4      = new Color(245, 158, 11);
    private static final Color TEXT_PRIMARY = new Color(31, 41, 55);
    private static final Color TEXT_MUTED   = new Color(113, 122, 138);
    private static final Color BORDER_COL   = new Color(228, 232, 242);
    private static final Color ROW_ALT      = new Color(250, 251, 255);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    // ── Dependencies (unchanged) ───────────────────────────────────────────
    private final TransactionController txCtrl;
    private final UserController        userCtrl;
    private final String                userId;
    private final String                userName;
    private final BookController        bookCtrl;
    private final LibrarianController   libCtrl;
    private final AdminController       adminCtrl;
    private final MaintenanceController maintCtrl;
    private final StaffDutyRepository   dutyRepo;

    // ── Status bar ─────────────────────────────────────────────────────────
    private JLabel statusBar;

    // ── Card layout ────────────────────────────────────────────────────────
    private JPanel    contentPanel;
    private CardLayout cardLayout;

    public UserPortal(TransactionController txCtrl, UserController userCtrl,
                      String userId, String userName,
                      BookController bookCtrl, LibrarianController libCtrl,
                      AdminController adminCtrl, MaintenanceController maintCtrl,
                      StaffDutyRepository dutyRepo) {

        this.txCtrl    = txCtrl;
        this.userCtrl  = userCtrl;
        this.userId    = userId;
        this.userName  = userName;
        this.bookCtrl  = bookCtrl;
        this.libCtrl   = libCtrl;
        this.adminCtrl = adminCtrl;
        this.maintCtrl = maintCtrl;
        this.dutyRepo  = dutyRepo;

        buildUI();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Frame bootstrap
    // ══════════════════════════════════════════════════════════════════════
    private void buildUI() {
        configureGlobalTheme();

        setTitle("Member Portal — " + userName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);

        root.add(buildHeader(),    BorderLayout.NORTH);
        root.add(buildSidebar(),   BorderLayout.WEST);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_DARK);
        contentPanel.add(buildBorrowedTab(),      "borrowed");
        contentPanel.add(buildFinesTab(),         "fines");
        contentPanel.add(buildNotificationsTab(), "notifications");

        root.add(contentPanel,     BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        setContentPane(root);
        showCard("borrowed");

        checkDueDates();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Header
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(BG_CARD);
        h.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_COL),
                new EmptyBorder(8, 14, 8, 14)));
        h.setPreferredSize(new Dimension(0, 56));

        JLabel logo = new JLabel("  Library Management System");
        logo.setFont(FONT_TITLE);
        logo.setForeground(TEXT_PRIMARY);
        h.add(logo, BorderLayout.WEST);

        JLabel info = new JLabel("Welcome, " + userName + "   |   Member   ");
        info.setFont(FONT_SMALL);
        info.setForeground(TEXT_MUTED);
        h.add(info, BorderLayout.EAST);

        return h;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Sidebar
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(BG_SIDEBAR);
        sb.setBorder(new MatteBorder(0, 0, 0, 1, BORDER_COL));
        sb.setPreferredSize(new Dimension(210, 0));

        sb.add(Box.createVerticalStrut(12));
        sb.add(navLabel("MY PORTAL"));
        sb.add(navBtn("  Borrowed Books",   "borrowed"));
        sb.add(navBtn("  My Fines",         "fines"));
        sb.add(navBtn("  Notifications",    "notifications"));

        sb.add(Box.createVerticalStrut(8));
        sb.add(navLabel("ACCOUNT"));
        JButton logoutBtn = navBtn("  Logout", "logout");
        logoutBtn.addActionListener(e -> logout());
        sb.add(logoutBtn);

        sb.add(Box.createVerticalGlue());

        JLabel badge = new JLabel("  ID: " + userId);
        badge.setFont(FONT_SMALL);
        badge.setForeground(new Color(200, 196, 255));
        badge.setBorder(new EmptyBorder(10, 10, 14, 10));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        sb.add(badge);

        return sb;
    }

    private JLabel navLabel(String text) {
        JLabel l = new JLabel("  " + text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(new Color(222, 216, 255));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(10, 0, 4, 0));
        return l;
    }

    private JButton navBtn(String text, String card) {
        JButton btn = new JButton(text);
        btn.setUI(new BasicButtonUI());
        btn.setFont(FONT_BODY);
        btn.setForeground(Color.WHITE);
        btn.setBackground(BG_SIDEBAR);
        btn.setBorder(new EmptyBorder(10, 20, 10, 10));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(109, 98, 252)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(BG_SIDEBAR); }
        });
        btn.addActionListener(e -> showCard(card));
        return btn;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Status bar
    // ══════════════════════════════════════════════════════════════════════
    private JLabel buildStatusBar() {
        statusBar = new JLabel("  Ready  |  Member Portal — " + userName);
        statusBar.setFont(FONT_SMALL);
        statusBar.setForeground(TEXT_MUTED);
        statusBar.setOpaque(true);
        statusBar.setBackground(BG_CARD);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, BORDER_COL),
                new EmptyBorder(8, 18, 8, 18)));
        return statusBar;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  BORROWED BOOKS TAB  (logic unchanged)
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildBorrowedTab() {
        JPanel p = darkPanel(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(sectionTitle("Borrowed Books"), BorderLayout.NORTH);

        JPanel card = cardPanel("Currently Issued");

        String[] cols = {"Transaction ID", "Book ID", "Issue Date", "Due Date", "Status", "Days Left"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        txCtrl.getTransactionsByUser(userId).stream()
                .filter(t -> t.getStatus().name().equals("ISSUED") ||
                             t.getStatus().name().equals("OVERDUE"))
                .forEach(t -> {
                    long daysLeft = LocalDate.now().until(t.getDueDate()).getDays();
                    model.addRow(new Object[]{
                            t.getTransactionId(),
                            t.getBookId(),
                            t.getIssueDate().format(FMT),
                            t.getDueDate().format(FMT),
                            t.getStatus(),
                            daysLeft < 0
                                    ? "OVERDUE by " + Math.abs(daysLeft) + " days"
                                    : daysLeft + " days left"
                    });
                });

        JTable table = styledTable(model);

        // Days-left colour renderer (logic unchanged)
        table.getColumnModel().getColumn(5).setCellRenderer(
                new DefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent(
                            JTable t, Object val, boolean sel,
                            boolean foc, int r, int c) {
                        JLabel l = (JLabel) super.getTableCellRendererComponent(
                                t, val, sel, foc, r, c);
                        String s = String.valueOf(val);
                        if (s.contains("OVERDUE"))
                            l.setForeground(ACCENT3);
                        else if (s.contains("1") || s.contains("2") || s.contains("3"))
                            l.setForeground(ACCENT4);
                        else
                            l.setForeground(ACCENT2);
                        l.setBackground(sel ? new Color(238, 241, 255)
                                : (r % 2 == 0 ? BG_CARD : ROW_ALT));
                        l.setBorder(new EmptyBorder(0, 10, 0, 10));
                        l.setOpaque(true);
                        return l;
                    }
                });

        card.add(styledScrollPane(table), BorderLayout.CENTER);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  FINES TAB  (logic unchanged)
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildFinesTab() {
        JPanel p = darkPanel(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(sectionTitle("My Fines"), BorderLayout.NORTH);

        // Summary strip
        User user = userCtrl.getUserById(userId).orElse(null);
        double pendingAmt = user == null ? 0 : user.getPendingFineAmount();

        JPanel summary = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        summary.setBackground(BG_CARD);
        summary.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, BORDER_COL),
                new EmptyBorder(2, 0, 2, 0)));

        JLabel l1 = new JLabel("Total Pending Fine:");
        l1.setFont(FONT_BODY);
        l1.setForeground(TEXT_MUTED);

        JLabel l2 = new JLabel("₹" + pendingAmt);
        l2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        l2.setForeground(pendingAmt > 0 ? ACCENT3 : ACCENT2);

        summary.add(l1);
        summary.add(l2);

        // Fines table
        String[] cols = {"Fine ID", "Transaction ID", "Days Late", "Amount", "Status", "Payment Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        txCtrl.getFinesByUser(userId).forEach(f -> model.addRow(new Object[]{
                f.getFineId(),
                f.getTransactionId(),
                f.getDaysLate(),
                "₹" + f.getFineAmount(),
                f.getPaymentStatus(),
                f.getPaymentDate() == null ? "Pending" : f.getPaymentDate().format(FMT)
        }));

        JTable table = styledTable(model);
        colorStatusColumn(table, 4);

        JPanel card = cardPanel("Fine Details");
        card.add(styledScrollPane(table), BorderLayout.CENTER);

        p.add(card,    BorderLayout.CENTER);
        p.add(summary, BorderLayout.SOUTH);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  NOTIFICATIONS TAB  (logic unchanged)
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildNotificationsTab() {
        JPanel p = darkPanel(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(sectionTitle("Notifications"), BorderLayout.NORTH);

        String[] cols = {"Notification ID", "Message", "Date Sent"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        adminCtrl.getAllNotifications().stream()
                .filter(n -> n.getUserId().equals(userId))
                .forEach(n -> model.addRow(new Object[]{
                        n.getNotificationId(),
                        n.getMessage(),
                        n.getDateSent().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
                }));

        JPanel card = cardPanel("Recent Notifications");
        card.add(styledScrollPane(styledTable(model)), BorderLayout.CENTER);

        p.add(card, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Due-date alert (logic unchanged)
    // ══════════════════════════════════════════════════════════════════════
    private void checkDueDates() {
        txCtrl.getTransactionsByUser(userId).stream()
                .filter(t -> t.getStatus().name().equals("ISSUED"))
                .forEach(t -> {
                    long daysLeft = LocalDate.now().until(t.getDueDate()).getDays();
                    if (daysLeft >= 0 && daysLeft <= 3) {
                        JOptionPane.showMessageDialog(this,
                                "Book " + t.getBookId() +
                                " due in " + daysLeft + " day(s)",
                                "Reminder",
                                JOptionPane.WARNING_MESSAGE);
                    }
                });
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Logout (logic unchanged)
    // ══════════════════════════════════════════════════════════════════════
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() ->
                    new LoginFrame(bookCtrl, userCtrl, libCtrl, txCtrl,
                            adminCtrl, maintCtrl, dutyRepo).setVisible(true));
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  UI HELPERS  (1-to-1 with LibraryApp)
    // ══════════════════════════════════════════════════════════════════════

    private void configureGlobalTheme() {
        UIManager.put("Panel.background",              BG_CARD);
        UIManager.put("OptionPane.background",         BG_CARD);
        UIManager.put("OptionPane.messageForeground",  TEXT_PRIMARY);
        UIManager.put("Button.background",             BG_ELEVATED);
        UIManager.put("Button.foreground",             TEXT_PRIMARY);
        UIManager.put("Button.select",                 BG_ELEVATED);
        UIManager.put("Label.foreground",              TEXT_PRIMARY);
        UIManager.put("TextField.background",          BG_ELEVATED);
        UIManager.put("TextField.foreground",          TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",     ACCENT);
        UIManager.put("ComboBox.background",           BG_ELEVATED);
        UIManager.put("ComboBox.foreground",           TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground",  BG_ELEVATED);
        UIManager.put("ComboBox.selectionForeground",  TEXT_PRIMARY);
        UIManager.put("TabbedPane.selected",           BG_ELEVATED);
        UIManager.put("TabbedPane.contentAreaColor",   BG_CARD);
        UIManager.put("ScrollBar.thumb",               new Color(214, 219, 235));
    }

    private JPanel darkPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(BG_DARK);
        return p;
    }

    private JPanel cardPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_COL, 1, true),
                        new MatteBorder(0, 0, 3, 0, new Color(240, 242, 250))),
                new EmptyBorder(18, 18, 18, 18)));
        if (!title.isEmpty()) {
            JLabel lbl = new JLabel(title);
            lbl.setFont(FONT_HEADER);
            lbl.setForeground(TEXT_PRIMARY);
            lbl.setBorder(new EmptyBorder(0, 0, 12, 0));
            p.add(lbl, BorderLayout.NORTH);
        }
        return p;
    }

    private JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 24));
        l.setForeground(TEXT_PRIMARY);
        l.setBorder(new EmptyBorder(0, 2, 14, 0));
        return l;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(BG_CARD);
        t.setForeground(TEXT_PRIMARY);
        t.setFont(FONT_BODY);
        t.setGridColor(BORDER_COL);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setRowHeight(42);
        t.setSelectionBackground(new Color(238, 241, 255));
        t.setSelectionForeground(TEXT_PRIMARY);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setFillsViewportHeight(true);
        t.setBorder(new EmptyBorder(4, 0, 4, 0));

        t.getTableHeader().setBackground(BG_CARD);
        t.getTableHeader().setForeground(TEXT_MUTED);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COL));
        ((DefaultTableCellRenderer) t.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.LEFT);

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                lbl.setBorder(new EmptyBorder(0, 10, 0, 10));
                lbl.setBackground(isSelected
                        ? table.getSelectionBackground()
                        : (row % 2 == 0 ? BG_CARD : ROW_ALT));
                lbl.setForeground(isSelected ? table.getSelectionForeground() : TEXT_PRIMARY);
                return lbl;
            }
        });

        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        return t;
    }

    private void colorStatusColumn(JTable table, int col) {
        table.getColumnModel().getColumn(col).setCellRenderer(
                new DefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable t, Object val,
                            boolean sel, boolean foc, int r, int c) {
                        JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                                t, val, sel, foc, r, c);
                        String s = String.valueOf(val);
                        lbl.setForeground(switch (s) {
                            case "PAID"    -> ACCENT2;
                            case "UNPAID"  -> ACCENT3;
                            case "Pending" -> ACCENT4;
                            default        -> TEXT_MUTED;
                        });
                        lbl.setBackground(sel
                                ? new Color(238, 241, 255)
                                : (r % 2 == 0 ? BG_CARD : ROW_ALT));
                        lbl.setBorder(new EmptyBorder(0, 10, 0, 10));
                        lbl.setOpaque(true);
                        return lbl;
                    }
                });
    }

    private JScrollPane styledScrollPane(Component view) {
        JScrollPane pane = new JScrollPane(view);
        pane.setBorder(new LineBorder(BORDER_COL, 1, true));
        pane.getViewport().setBackground(BG_CARD);
        pane.setBackground(BG_CARD);
        pane.getVerticalScrollBar().setUnitIncrement(14);
        pane.getHorizontalScrollBar().setUnitIncrement(14);
        return pane;
    }

    private void showCard(String name) {
        cardLayout.show(contentPanel, name);
    }

}