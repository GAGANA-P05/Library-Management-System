package com.library.view;

import com.library.controller.*;
import com.library.enums.StaffRole;
import com.library.model.*;
import com.library.repository.StaffDutyRepository;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * MVC View layer – the entire Swing UI.
 * Communicates only through Controllers; knows nothing about Services or Repositories.
 */
public class LibraryApp extends JFrame {

    // ── Palette ────────────────────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(244, 246, 252);
    private static final Color BG_CARD      = new Color(255, 255, 255);
    private static final Color BG_SIDEBAR   = new Color(94, 84, 245);
    private static final Color BG_ELEVATED  = new Color(248, 249, 255);
    private static final Color ACCENT       = new Color(92, 81, 242);
    private static final Color ACCENT2      = new Color(71, 196, 126);
    private static final Color ACCENT3      = new Color(244, 106, 128);
    private static final Color ACCENT4      = new Color(129, 144, 168);
    private static final Color TEXT_PRIMARY = new Color(31, 41, 55);
    private static final Color TEXT_MUTED   = new Color(113, 122, 138);
    private static final Color BORDER_COL   = new Color(228, 232, 242);
    private static final Color ROW_ALT      = new Color(250, 251, 255);

    private static final Font  FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font  FONT_HEADER  = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font  FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font  FONT_MONO    = new Font("Consolas",  Font.PLAIN, 12);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    // ── Controllers ────────────────────────────────────────────────────────
    private final BookController        bookCtrl;
    private final UserController        userCtrl;
    private final LibrarianController   libCtrl;
    private final TransactionController txCtrl;
    private final AdminController       adminCtrl;
    private final MaintenanceController maintCtrl;

    // ── Layout ─────────────────────────────────────────────────────────────
    private JPanel    contentPanel;
    private CardLayout cardLayout;
    private JLabel    statusBar;

    private final StaffDutyRepository dutyRepo;
    private final String loggedInRole;
    private final String loggedInId;

    public LibraryApp(BookController bookCtrl, UserController userCtrl,
                    LibrarianController libCtrl, TransactionController txCtrl,
                    AdminController adminCtrl, MaintenanceController maintCtrl,
                    StaffDutyRepository dutyRepo,
                    String role, String id, String name) {
        this.bookCtrl     = bookCtrl;
        this.userCtrl     = userCtrl;
        this.libCtrl      = libCtrl;
        this.txCtrl       = txCtrl;
        this.adminCtrl    = adminCtrl;
        this.maintCtrl    = maintCtrl;
        this.dutyRepo     = dutyRepo;
        this.loggedInRole = role;
        this.loggedInId   = id;
        initFrame();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Frame bootstrap
    // ══════════════════════════════════════════════════════════════════════
    private void initFrame() {
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 780);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);
        configureGlobalTheme();
        getContentPane().setBackground(BG_DARK);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);
        root.setBorder(new EmptyBorder(0, 0, 0, 0));

        root.add(buildHeader(),  BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_DARK);
        contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        contentPanel.add(buildDashboard(),     "dashboard");
        contentPanel.add(buildBooksPanel(),    "books");
        contentPanel.add(buildUsersPanel(),    "users");
        contentPanel.add(buildIssuePanel(),    "issue");
        contentPanel.add(buildReturnPanel(),   "return");
        contentPanel.add(buildFinesPanel(),    "fines");
        contentPanel.add(buildLibrariansPanel(),"librarians");
        contentPanel.add(buildStaffPanel(),    "staff");
        contentPanel.add(buildReportsPanel(),  "reports");
        contentPanel.add(buildAssignDutyPanel(), "assignduty");

        root.add(contentPanel, BorderLayout.CENTER);

        statusBar = new JLabel("  Ready  |  Library Management System v1.0");
        statusBar.setFont(FONT_SMALL);
        statusBar.setForeground(TEXT_MUTED);
        statusBar.setOpaque(true);
        statusBar.setBackground(BG_CARD);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, BORDER_COL),
                new EmptyBorder(8, 18, 8, 18)));
        root.add(statusBar, BorderLayout.SOUTH);

        setContentPane(root);
        showCard("dashboard");
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

        JLabel logo = new JLabel(" Library Management System");
        logo.setFont(FONT_TITLE);
        logo.setForeground(TEXT_PRIMARY);
        h.add(logo, BorderLayout.WEST);

        JLabel info = new JLabel("Central Library   ");
        info.setFont(FONT_SMALL);
        info.setForeground(TEXT_MUTED);
        info.setBorder(new EmptyBorder(0, 0, 0, 8));
        h.add(info, BorderLayout.EAST);

        return h;
    }

private JPanel buildAssignDutyPanel() {
    JPanel p = darkPanel(new BorderLayout(0, 16));
    p.setBorder(new EmptyBorder(24, 24, 24, 24));
    p.add(sectionTitle("Assign Duty to Staff"), BorderLayout.NORTH);

    JPanel form = cardPanel("New Duty Assignment");
    form.setLayout(new GridBagLayout());
    GridBagConstraints gbc = gbc();

    // Fetch staff list for dropdown
    java.util.List<com.library.model.MaintenanceStaff> staffList =
            maintCtrl.getAllStaff();
    String[] staffOptions = staffList.stream()
            .map(s -> s.getStaffId() + " - " + s.getName() + " (" + s.getRole() + ")")
            .toArray(String[]::new);

    // ── STEP 1: declare ALL fields BEFORE the button listener ──
    JComboBox<String> staffBox    = darkCombo(staffOptions.length > 0
            ? staffOptions : new String[]{"No staff available"});
    JComboBox<String> taskTypeBox = darkCombo(
            new String[]{"CLEANING", "REPAIR", "SECURITY"});
    JTextField        descField   = darkField(20);

    // ── STEP 2: add rows to form ──
    addRow(form, gbc, 0, "Select Staff:", staffBox);
    addRow(form, gbc, 1, "Task Type:",    taskTypeBox);
    addRow(form, gbc, 2, "Description:",  descField);

    // ── STEP 3: create button ──
    JButton assignBtn = accentBtn("Assign Duty", ACCENT2);
    gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
    form.add(assignBtn, gbc);

    // ── STEP 4: duties table ──
    String[] cols = {"Duty ID", "Staff", "Task Type",
                     "Description", "Assigned Date", "Status"};
    DefaultTableModel model = new DefaultTableModel(cols, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };

    Runnable load = () -> {
        model.setRowCount(0);
        dutyRepo.findAll().forEach(d -> model.addRow(new Object[]{
            d.getDutyId(),
            d.getStaffId(),
            d.getTaskType(),
            d.getDescription(),
            d.getAssignedDate().format(
                java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")),
            d.getStatus()
        }));
    };
    load.run();

    JTable table = styledTable(model);

    // Status column color
    table.getColumnModel().getColumn(5).setCellRenderer(
        new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel,
                    boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(
                        t, val, sel, foc, r, c);
                l.setForeground(switch (String.valueOf(val)) {
                    case "PENDING"     -> ACCENT4;
                    case "IN_PROGRESS" -> ACCENT;
                    case "COMPLETED"   -> ACCENT2;
                    default            -> TEXT_MUTED;
                });
                l.setBackground(sel ? new Color(35, 50, 75) : BG_CARD);
                l.setOpaque(true);
                return l;
            }
        }
    );

    // Task type column color
    table.getColumnModel().getColumn(2).setCellRenderer(
        new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel,
                    boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(
                        t, val, sel, foc, r, c);
                l.setForeground(switch (String.valueOf(val)) {
                    case "CLEANING" -> ACCENT2;
                    case "REPAIR"   -> ACCENT4;
                    case "SECURITY" -> ACCENT3;
                    default         -> TEXT_MUTED;
                });
                l.setBackground(sel ? new Color(35, 50, 75) : BG_CARD);
                l.setOpaque(true);
                return l;
            }
        }
    );

    // ── STEP 5: button listener — all variables visible here ──
    assignBtn.addActionListener(e -> {
        if (staffOptions.length == 0) {
            warn("No staff available."); return;
        }
        String selected = (String) staffBox.getSelectedItem();
        if (selected == null || selected.equals("No staff available")) {
            warn("No staff available."); return;
        }
        String sId      = selected.split(" - ")[0];
        String taskType = (String) taskTypeBox.getSelectedItem();
        String desc     = descField.getText().trim();
        if (desc.isEmpty()) {
            warn("Please enter a description."); return;
        }

        String dutyId = "DUTY-" + java.util.UUID.randomUUID()
                .toString().substring(0, 8).toUpperCase();

        com.library.model.StaffDuty duty = new com.library.model.StaffDuty(
                dutyId, sId, loggedInId, taskType, desc,
                java.time.LocalDateTime.now(), "PENDING");

        dutyRepo.save(duty);
        load.run();
        descField.setText("");
        status("Duty '" + taskType + "' assigned to " + sId);
    });

    // ── STEP 6: table panel with refresh ──
    JPanel tablePanel = cardPanel("All Assigned Duties");
    JButton refreshBtn = ghostBtn("Refresh");
    refreshBtn.addActionListener(e -> load.run());
    JPanel tableTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    tableTop.setOpaque(false);
    tableTop.add(refreshBtn);
    tablePanel.add(tableTop,            BorderLayout.NORTH);
    tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

    p.add(form,       BorderLayout.NORTH);
    p.add(tablePanel, BorderLayout.CENTER);
    return p;
}



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
    //  Sidebar
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
    JPanel sb = new JPanel();
    sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
    sb.setBackground(BG_SIDEBAR);
    sb.setBorder(new MatteBorder(0,0,0,1,BORDER_COL));
    sb.setPreferredSize(new Dimension(210,0));

    sb.add(Box.createVerticalStrut(12));
    sb.add(navLabel("NAVIGATION"));
    sb.add(navBtn("  Dashboard",   "dashboard"));
    sb.add(navBtn("  Books",       "books"));
    sb.add(navBtn("  Members",     "users"));
    sb.add(navBtn("  Issue Book",  "issue"));
    sb.add(navBtn("  Return Book", "return"));
    sb.add(navBtn("  Fines",       "fines"));

    if ("LIBRARIAN".equals(loggedInRole) || "ADMIN".equals(loggedInRole)) {
        sb.add(Box.createVerticalStrut(8));
        sb.add(navLabel("STAFF MANAGEMENT"));
        sb.add(navBtn("  Assign Duty",  "assignduty"));
    }

    if ("ADMIN".equals(loggedInRole)) {
        sb.add(Box.createVerticalStrut(8));
        sb.add(navLabel("ADMIN ONLY"));
        sb.add(navBtn("  Librarians",  "librarians"));
        sb.add(navBtn("  Maintenance", "staff"));
        sb.add(navBtn("  Reports",     "reports"));
    }

        sb.add(Box.createVerticalStrut(8));
        sb.add(navLabel("ACCOUNT"));
        JButton logoutBtn = navBtn("  Logout", "logout");
        logoutBtn.addActionListener(e -> logout());
        sb.add(logoutBtn);

        sb.add(Box.createVerticalGlue());
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
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(109, 98, 252)); btn.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(BG_SIDEBAR); btn.setForeground(Color.WHITE); }
        });
        btn.addActionListener(e -> showCard(card));
        return btn;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  DASHBOARD
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildDashboard() {
        JPanel p = darkPanel(new BorderLayout(16, 16));
        p.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = sectionTitle("Dashboard Overview");
        p.add(title, BorderLayout.NORTH);

        // Stats row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.add(statCard("Total Books",      String.valueOf(bookCtrl.getAllBooks().size()),                 ACCENT));
        statsRow.add(statCard("Registered Members", String.valueOf(userCtrl.getAllUsers().size()),              ACCENT2));
        statsRow.add(statCard("Currently Issued",  String.valueOf(adminCtrl.getActiveIssuedCount()),           ACCENT4));
        statsRow.add(statCard("Fines Collected",   "₹" + String.format("%.0f", adminCtrl.getTotalFinesCollected()), ACCENT3));

        // Recent transactions table
        JPanel centre = new JPanel(new BorderLayout(0, 12));
        centre.setOpaque(false);
        centre.add(statsRow, BorderLayout.NORTH);

        JPanel recentPanel = cardPanel("Recent Transactions");
        String[] txCols = {"Transaction ID","User ID","Book ID","Issue Date","Due Date","Status"};
        DefaultTableModel txModel = new DefaultTableModel(txCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        List<Transaction> txList = adminCtrl.getTransactionHistory();
        int start = Math.max(0, txList.size() - 8);
        for (int i = txList.size()-1; i >= start; i--) {
            Transaction tx = txList.get(i);
            txModel.addRow(new Object[]{
                tx.getTransactionId(), tx.getUserId(), tx.getBookId(),
                tx.getIssueDate().format(DATE_FMT),
                tx.getDueDate().format(DATE_FMT),
                tx.getStatus()
            });
        }
        JTable txTable = styledTable(txModel);
        colorStatusColumn(txTable, 5);
        recentPanel.add(styledScrollPane(txTable), BorderLayout.CENTER);
        centre.add(recentPanel, BorderLayout.CENTER);

        p.add(centre, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  BOOKS
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildBooksPanel() {
        JPanel p = darkPanel(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(sectionTitle("Book Catalogue"), BorderLayout.NORTH);

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBar.setOpaque(false);
        JComboBox<String> searchType = darkCombo(new String[]{"Title","Author","Category","ISBN"});
        JTextField searchField = darkField(24);
        JButton searchBtn = accentBtn("🔍 Search", ACCENT);
        JButton clearBtn  = ghostBtn("Clear");
        searchBar.add(new JLabel("Search by: "));
        ((JLabel)searchBar.getComponent(0)).setForeground(TEXT_MUTED);
        ((JLabel)searchBar.getComponent(0)).setFont(FONT_BODY);
        searchBar.add(searchType); searchBar.add(searchField);
        searchBar.add(searchBtn);  searchBar.add(clearBtn);

        String[] cols = {"Book ID","Title","Author","Category","ISBN","Year","Shelf","Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        Runnable loadAll = () -> {
            model.setRowCount(0);
            bookCtrl.getAllBooks().forEach(b -> model.addRow(new Object[]{
                b.getBookId(), b.getTitle(), b.getAuthor(), b.getCategory(),
                b.getIsbn(), b.getPublicationYear(), b.getShelfLocation(), b.getStatus()
            }));
        };
        loadAll.run();

        JTable table = styledTable(model);
        colorStatusColumn(table, 7);

        searchBtn.addActionListener(e -> {
            String q = searchField.getText().trim();
            if (q.isEmpty()) { loadAll.run(); return; }
            List<Book> results = switch (searchType.getSelectedItem().toString()) {
                case "Author"   -> bookCtrl.searchByAuthor(q);
                case "Category" -> bookCtrl.searchByCategory(q);
                case "ISBN"     -> bookCtrl.searchByIsbn(q);
                default         -> bookCtrl.searchByTitle(q);
            };
            model.setRowCount(0);
            results.forEach(b -> model.addRow(new Object[]{
                b.getBookId(), b.getTitle(), b.getAuthor(), b.getCategory(),
                b.getIsbn(), b.getPublicationYear(), b.getShelfLocation(), b.getStatus()
            }));
            status("Found " + results.size() + " book(s).");
        });
        clearBtn.addActionListener(e -> { searchField.setText(""); loadAll.run(); });

        // Action row
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        JButton addBtn    = accentBtn("➕ Add Book",    ACCENT2);
        JButton editBtn   = accentBtn("✏️ Edit",         ACCENT4);
        JButton removeBtn = accentBtn("🗑️ Remove",      ACCENT3);
        JButton damageBtn = accentBtn("⚠️ Mark Damaged", ACCENT4);
        JButton refreshBtn = ghostBtn("↺ Refresh");
        actions.add(addBtn); actions.add(editBtn); actions.add(removeBtn);
        actions.add(damageBtn); actions.add(refreshBtn);

        addBtn.addActionListener(e -> showAddBookDialog(model));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Select a book to edit."); return; }
            showEditBookDialog((String) model.getValueAt(row, 0), model);
        });
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Select a book to remove."); return; }
            String id = (String) model.getValueAt(row, 0);
            if (confirm("Remove book " + id + "?")) {
                bookCtrl.removeBook(id);
                loadAll.run();
                status("Book removed: " + id);
            }
        });
        damageBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Select a book to mark as damaged."); return; }
            String id = (String) model.getValueAt(row, 0);
            bookCtrl.markAsDamaged(id);
            loadAll.run();
            status("Book marked as damaged: " + id + ". Maintenance staff notified.");
        });
        refreshBtn.addActionListener(e -> loadAll.run());

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(searchBar, BorderLayout.NORTH);
        top.add(actions,   BorderLayout.SOUTH);

        p.add(top, BorderLayout.NORTH);
        p.add(styledScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  MEMBERS (Users)
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildUsersPanel() {
        JPanel p = darkPanel(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(sectionTitle("Library Members"), BorderLayout.NORTH);

        String[] cols = {"User ID","Name","Email","Phone","Address","Member Since","Borrowed","Pending Fine"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        Runnable load = () -> {
            model.setRowCount(0);
            userCtrl.getAllUsers().forEach(u -> model.addRow(new Object[]{
                u.getUserId(), u.getName(), u.getEmail(), u.getPhoneNumber(),
                u.getAddress(),
                u.getMembershipDate().format(DATE_FMT),
                u.getBorrowedBookIds().size(),
                "₹" + u.getPendingFineAmount()
            }));
        };
        load.run();
        JTable table = styledTable(model);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        JButton addBtn  = accentBtn("➕ Register Member", ACCENT2);
        JButton delBtn  = accentBtn("🗑️ Remove",         ACCENT3);
        JButton refBtn  = ghostBtn("↺ Refresh");
        actions.add(addBtn); actions.add(delBtn); actions.add(refBtn);

        addBtn.addActionListener(e -> showAddUserDialog(model));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Select a member."); return; }
            String id = (String) model.getValueAt(row, 0);
            if (confirm("Remove member " + id + "?")) {
                userCtrl.removeUser(id); load.run();
                status("Member removed: " + id);
            }
        });
        refBtn.addActionListener(e -> load.run());

        p.add(actions, BorderLayout.NORTH);
        p.add(styledScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  ISSUE BOOK
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildIssuePanel() {
        JPanel p = darkPanel(new BorderLayout(0, 16));
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(sectionTitle("Issue Book"), BorderLayout.NORTH);

        JPanel form = cardPanel("Issue Details");
        JPanel formBody = new JPanel(new GridBagLayout());
        formBody.setOpaque(false);
        form.add(formBody, BorderLayout.CENTER);
        GridBagConstraints gbc = gbc();

        JTextField userIdField   = darkField(20);
        JTextField bookIdField   = darkField(20);
        JTextField libIdField    = darkField(20);
        JTextArea  resultArea    = new JTextArea(6, 40);
        styleTextArea(resultArea);

        addRow(formBody, gbc, 0, "Member ID:",     userIdField);
        addRow(formBody, gbc, 1, "Book ID:",        bookIdField);
        addRow(formBody, gbc, 2, "Librarian ID:",   libIdField);

        JButton issueBtn = accentBtn("✅ Issue Book", ACCENT2);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formBody.add(issueBtn, gbc);

        issueBtn.addActionListener(e -> {
            try {
                Transaction tx = txCtrl.issueBook(
                        userIdField.getText().trim(),
                        bookIdField.getText().trim(),
                        libIdField.getText().trim());
                resultArea.setText(
                    "✅ Book Issued Successfully!\n\n" +
                    "Transaction ID : " + tx.getTransactionId() + "\n" +
                    "User ID        : " + tx.getUserId()        + "\n" +
                    "Book ID        : " + tx.getBookId()        + "\n" +
                    "Issue Date     : " + tx.getIssueDate().format(DATE_FMT) + "\n" +
                    "Due Date       : " + tx.getDueDate().format(DATE_FMT)   + "\n" +
                    "Status         : " + tx.getStatus()
                );
                status("Book issued. Transaction: " + tx.getTransactionId());
            } catch (Exception ex) {
                resultArea.setText("❌ Error: " + ex.getMessage());
                status("Issue failed: " + ex.getMessage());
            }
        });

        JPanel resultPanel = cardPanel("Result");
        resultPanel.add(styledScrollPane(resultArea), BorderLayout.CENTER);

        p.add(form,        BorderLayout.NORTH);
        p.add(resultPanel, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  RETURN BOOK
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildReturnPanel() {
        JPanel p = darkPanel(new BorderLayout(0, 16));
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(sectionTitle("Return Book"), BorderLayout.NORTH);

        JPanel form = cardPanel("Return Details");
        JPanel formBody = new JPanel(new GridBagLayout());
        formBody.setOpaque(false);
        form.add(formBody, BorderLayout.CENTER);
        GridBagConstraints gbc = gbc();

        JTextField txIdField  = darkField(20);
        JTextField libIdField = darkField(20);
        JTextArea  resultArea = new JTextArea(8, 40);
        styleTextArea(resultArea);

        addRow(formBody, gbc, 0, "Transaction ID:", txIdField);
        addRow(formBody, gbc, 1, "Librarian ID:",   libIdField);

        JButton returnBtn = accentBtn("📥 Process Return", ACCENT4);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formBody.add(returnBtn, gbc);

        returnBtn.addActionListener(e -> {
            try {
                Optional<Fine> fineOpt = txCtrl.returnBook(
                        txIdField.getText().trim(),
                        libIdField.getText().trim());
                if (fineOpt.isPresent()) {
                    Fine f = fineOpt.get();
                    resultArea.setText(
                        "⚠️  Book Returned (Overdue!)\n\n" +
                        "Fine ID        : " + f.getFineId()        + "\n" +
                        "Days Late      : " + f.getDaysLate()      + "\n" +
                        "Fine Amount    : ₹" + f.getFineAmount()   + "\n" +
                        "Payment Status : " + f.getPaymentStatus() + "\n\n" +
                        "Please pay the fine using Fine ID above."
                    );
                    status("Book returned with fine: ₹" + f.getFineAmount());
                } else {
                    resultArea.setText("✅ Book returned successfully!\nNo fine incurred.");
                    status("Book returned – no fine.");
                }
            } catch (Exception ex) {
                resultArea.setText("❌ Error: " + ex.getMessage());
                status("Return failed: " + ex.getMessage());
            }
        });

        JPanel resultPanel = cardPanel("Result");
        resultPanel.add(styledScrollPane(resultArea), BorderLayout.CENTER);

        p.add(form,        BorderLayout.NORTH);
        p.add(resultPanel, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  FINES
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildFinesPanel() {
        JPanel p = darkPanel(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(sectionTitle("Fines Management"), BorderLayout.NORTH);

        String[] cols = {"Fine ID","Transaction ID","User ID","Days Late","Amount","Status","Payment Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        Runnable load = () -> {
            model.setRowCount(0);
            txCtrl.getAllFines().forEach(f -> model.addRow(new Object[]{
                f.getFineId(), f.getTransactionId(), f.getUserId(),
                f.getDaysLate(), "₹" + f.getFineAmount(),
                f.getPaymentStatus(),
                f.getPaymentDate() == null ? "-" : f.getPaymentDate().format(DATE_FMT)
            }));
        };
        load.run();
        JTable table = styledTable(model);
        colorStatusColumn(table, 5);

        JPanel payRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        payRow.setOpaque(false);
        JTextField fineIdField = darkField(16);
        JButton payBtn = accentBtn("💳 Pay Fine", ACCENT2);
        JButton refBtn = ghostBtn("↺ Refresh");
        payRow.add(new JLabel("Fine ID to Pay: "));
        ((JLabel)payRow.getComponent(0)).setForeground(TEXT_MUTED);
        ((JLabel)payRow.getComponent(0)).setFont(FONT_BODY);
        payRow.add(fineIdField); payRow.add(payBtn); payRow.add(refBtn);

        payBtn.addActionListener(e -> {
            String fid = fineIdField.getText().trim();
            if (fid.isEmpty()) { warn("Enter a Fine ID."); return; }
            if (txCtrl.payFine(fid)) {
                load.run();
                info("Fine " + fid + " paid successfully!");
                status("Fine paid: " + fid);
            } else {
                warn("Could not pay fine – may already be paid or ID invalid.");
            }
        });
        refBtn.addActionListener(e -> load.run());

        p.add(payRow, BorderLayout.NORTH);
        p.add(styledScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  LIBRARIANS
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildLibrariansPanel() {
        JPanel p = darkPanel(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(sectionTitle("Librarian Management"), BorderLayout.NORTH);

        String[] cols = {"Librarian ID","Employee ID","Name","Email","Phone","Working Hours"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        Runnable load = () -> {
            model.setRowCount(0);
            libCtrl.getAllLibrarians().forEach(l -> model.addRow(new Object[]{
                l.getLibrarianId(), l.getEmployeeId(), l.getName(),
                l.getEmail(), l.getPhoneNumber(), l.getWorkingHours()
            }));
        };
        load.run();
        JTable table = styledTable(model);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        JButton addBtn = accentBtn("➕ Add Librarian", ACCENT2);
        JButton delBtn = accentBtn("🗑️ Remove",        ACCENT3);
        JButton refBtn = ghostBtn("↺ Refresh");
        actions.add(addBtn); actions.add(delBtn); actions.add(refBtn);

        addBtn.addActionListener(e -> showAddLibrarianDialog(model));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Select a librarian."); return; }
            String id = (String) model.getValueAt(row, 0);
            if (confirm("Remove librarian " + id + "?")) {
                libCtrl.removeLibrarian(id); load.run();
                status("Librarian removed: " + id);
            }
        });
        refBtn.addActionListener(e -> load.run());

        p.add(actions, BorderLayout.NORTH);
        p.add(styledScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  MAINTENANCE STAFF
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildStaffPanel() {
        JPanel p = darkPanel(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(sectionTitle("Maintenance Staff"), BorderLayout.NORTH);

        String[] cols = {"Staff ID","Name","Role","Email","Phone"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        Runnable load = () -> {
            model.setRowCount(0);
            maintCtrl.getAllStaff().forEach(s -> model.addRow(new Object[]{
                s.getStaffId(), s.getName(), s.getRole(), s.getEmail(), s.getPhoneNumber()
            }));
        };
        load.run();
        JTable table = styledTable(model);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        JButton addBtn = accentBtn("➕ Add Staff", ACCENT2);
        JButton delBtn = accentBtn("🗑️ Remove",    ACCENT3);
        JButton refBtn = ghostBtn("↺ Refresh");
        actions.add(addBtn); actions.add(delBtn); actions.add(refBtn);

        addBtn.addActionListener(e -> showAddStaffDialog(model));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Select a staff member."); return; }
            String id = (String) model.getValueAt(row, 0);
            if (confirm("Remove staff " + id + "?")) {
                maintCtrl.removeStaff(id); load.run();
            }
        });
        refBtn.addActionListener(e -> load.run());

        p.add(actions, BorderLayout.NORTH);
        p.add(styledScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  REPORTS
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildReportsPanel() {
        JPanel p = darkPanel(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(sectionTitle("Reports & Analytics"), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        styleTabs(tabs);

        // ── Transaction History tab ────────────────────────────────────────
        String[] txCols = {"Tx ID","User ID","Book ID","Librarian","Issue Date","Due Date","Return Date","Status"};
        DefaultTableModel txModel = new DefaultTableModel(txCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        adminCtrl.getTransactionHistory().forEach(tx -> txModel.addRow(new Object[]{
            tx.getTransactionId(), tx.getUserId(), tx.getBookId(), tx.getLibrarianId(),
            tx.getIssueDate().format(DATE_FMT),
            tx.getDueDate().format(DATE_FMT),
            tx.getReturnDate() == null ? "-" : tx.getReturnDate().format(DATE_FMT),
            tx.getStatus()
        }));
        JTable txTable = styledTable(txModel);
        colorStatusColumn(txTable, 7);
        tabs.addTab("📋 Transactions", styledScrollPane(txTable));

        // ── Overdue tab ────────────────────────────────────────────────────
        DefaultTableModel overdueModel = new DefaultTableModel(txCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        adminCtrl.getOverdueTransactions().forEach(tx -> overdueModel.addRow(new Object[]{
            tx.getTransactionId(), tx.getUserId(), tx.getBookId(), tx.getLibrarianId(),
            tx.getIssueDate().format(DATE_FMT),
            tx.getDueDate().format(DATE_FMT),
            tx.getReturnDate() == null ? "Not Returned" : tx.getReturnDate().format(DATE_FMT),
            tx.getStatus()
        }));
        JTable overdueTable = styledTable(overdueModel);
        tabs.addTab("⚠️ Overdue", styledScrollPane(overdueTable));

        // ── Fine Collection tab ────────────────────────────────────────────
        String[] fineCols = {"Fine ID","Tx ID","User ID","Days Late","Amount","Status","Payment Date"};
        DefaultTableModel fineModel = new DefaultTableModel(fineCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        adminCtrl.getFineReport().forEach(f -> fineModel.addRow(new Object[]{
            f.getFineId(), f.getTransactionId(), f.getUserId(), f.getDaysLate(),
            "₹" + f.getFineAmount(), f.getPaymentStatus(),
            f.getPaymentDate() == null ? "-" : f.getPaymentDate().format(DATE_FMT)
        }));
        JTable fineTable = styledTable(fineModel);
        colorStatusColumn(fineTable, 5);
        tabs.addTab("💰 Fine Collection", styledScrollPane(fineTable));

        // ── Notifications tab ──────────────────────────────────────────────
        String[] notifCols = {"Notification ID","User ID","Message","Date Sent"};
        DefaultTableModel notifModel = new DefaultTableModel(notifCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        adminCtrl.getAllNotifications().forEach(n -> notifModel.addRow(new Object[]{
            n.getNotificationId(), n.getUserId(), n.getMessage(),
            n.getDateSent().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
        }));
        tabs.addTab("🔔 Notifications", styledScrollPane(styledTable(notifModel)));

        // Summary strip
        JPanel summary = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        summary.setBackground(BG_CARD);
        summary.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_COL));
        summary.add(summaryLabel("Total Transactions: " + adminCtrl.getTransactionHistory().size()));
        summary.add(summaryLabel("Overdue: "           + adminCtrl.getOverdueTransactions().size()));
        summary.add(summaryLabel("Total Fines Collected: ₹" + String.format("%.0f", adminCtrl.getTotalFinesCollected())));
        summary.add(summaryLabel("Active Issues: "     + adminCtrl.getActiveIssuedCount()));

        p.add(tabs,    BorderLayout.CENTER);
        p.add(summary, BorderLayout.SOUTH);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  DIALOGS
    // ══════════════════════════════════════════════════════════════════════
    private void showAddBookDialog(DefaultTableModel model) {
        JDialog d = dialog("Add New Book", 480, 460);
        JPanel form = new JPanel(new GridLayout(9, 2, 8, 8));
        form.setBackground(BG_CARD);
        form.setBorder(new EmptyBorder(16, 16, 16, 16));

        JTextField[] fields = new JTextField[8];
        String[] labels = {"Book ID:","Title:","Author:","Publisher:","Year:","ISBN:","Category:","Shelf Location:"};
        for (int i = 0; i < 8; i++) {
            form.add(formLabel(labels[i]));
            fields[i] = darkField(18);
            form.add(fields[i]);
        }

        JButton save = accentBtn("💾 Save", ACCENT2);
        JButton cancel = ghostBtn("Cancel");
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setBackground(BG_CARD);
        btns.add(cancel); btns.add(save);

        save.addActionListener(e -> {
            try {
                int year = Integer.parseInt(fields[4].getText().trim());
                Book b = bookCtrl.addBook(
                    fields[0].getText().trim(), fields[1].getText().trim(),
                    fields[2].getText().trim(), fields[3].getText().trim(), year,
                    fields[5].getText().trim(), fields[6].getText().trim(), fields[7].getText().trim());
                model.addRow(new Object[]{b.getBookId(), b.getTitle(), b.getAuthor(),
                    b.getCategory(), b.getIsbn(), b.getPublicationYear(), b.getShelfLocation(), b.getStatus()});
                status("Book added: " + b.getBookId());
                d.dispose();
            } catch (Exception ex) { warn("Error: " + ex.getMessage()); }
        });
        cancel.addActionListener(e -> d.dispose());

        d.add(form, BorderLayout.CENTER);
        d.add(btns, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void showEditBookDialog(String bookId, DefaultTableModel model) {
        bookCtrl.getBookById(bookId).ifPresentOrElse(book -> {
            JDialog d = dialog("Edit Book – " + bookId, 480, 400);
            JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));
            form.setBackground(BG_CARD);
            form.setBorder(new EmptyBorder(16, 16, 16, 16));

            JTextField titleF  = darkField(18); titleF.setText(book.getTitle());
            JTextField authorF = darkField(18); authorF.setText(book.getAuthor());
            JTextField pubF    = darkField(18); pubF.setText(book.getPublisher());
            JTextField yearF   = darkField(18); yearF.setText(String.valueOf(book.getPublicationYear()));
            JTextField catF    = darkField(18); catF.setText(book.getCategory());
            JTextField shelfF  = darkField(18); shelfF.setText(book.getShelfLocation());

            form.add(formLabel("Title:")); form.add(titleF);
            form.add(formLabel("Author:")); form.add(authorF);
            form.add(formLabel("Publisher:")); form.add(pubF);
            form.add(formLabel("Year:")); form.add(yearF);
            form.add(formLabel("Category:")); form.add(catF);
            form.add(formLabel("Shelf:")); form.add(shelfF);

            JButton save = accentBtn("💾 Save Changes", ACCENT2);
            JButton cancel = ghostBtn("Cancel");
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btns.setBackground(BG_CARD);
            btns.add(cancel); btns.add(save);

            save.addActionListener(e -> {
                try {
                    bookCtrl.updateBook(bookId, titleF.getText(), authorF.getText(),
                            pubF.getText(), Integer.parseInt(yearF.getText()),
                            catF.getText(), shelfF.getText());
                    // refresh table row
                    bookCtrl.getBookById(bookId).ifPresent(b -> {
                        int row = findRow(model, bookId, 0);
                        if (row >= 0) {
                            model.setValueAt(b.getTitle(),    row, 1);
                            model.setValueAt(b.getAuthor(),   row, 2);
                            model.setValueAt(b.getCategory(), row, 3);
                        }
                    });
                    status("Book updated: " + bookId);
                    d.dispose();
                } catch (Exception ex) { warn("Error: " + ex.getMessage()); }
            });
            cancel.addActionListener(e -> d.dispose());

            d.add(form, BorderLayout.CENTER);
            d.add(btns, BorderLayout.SOUTH);
            d.setVisible(true);
        }, () -> warn("Book not found: " + bookId));
    }

    private void showAddUserDialog(DefaultTableModel model) {
        JDialog d = dialog("Register New Member", 440, 360);
        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBackground(BG_CARD);
        form.setBorder(new EmptyBorder(16, 16, 16, 16));

        JTextField idF    = darkField(18);
        JTextField nameF  = darkField(18);
        JTextField emailF = darkField(18);
        JTextField phoneF = darkField(18);
        JTextField addrF  = darkField(18);

        form.add(formLabel("User ID:"));    form.add(idF);
        form.add(formLabel("Name:"));       form.add(nameF);
        form.add(formLabel("Email:"));      form.add(emailF);
        form.add(formLabel("Phone:"));      form.add(phoneF);
        form.add(formLabel("Address:"));    form.add(addrF);

        JButton save = accentBtn("💾 Register", ACCENT2);
        JButton cancel = ghostBtn("Cancel");
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setBackground(BG_CARD);
        btns.add(cancel); btns.add(save);

        save.addActionListener(e -> {
            try {
                User u = userCtrl.registerUser(idF.getText().trim(), nameF.getText().trim(),
                        emailF.getText().trim(), phoneF.getText().trim(), addrF.getText().trim());
                model.addRow(new Object[]{u.getUserId(), u.getName(), u.getEmail(),
                        u.getPhoneNumber(), u.getAddress(),
                        u.getMembershipDate().format(DATE_FMT), 0, "₹0.0"});
                status("Member registered: " + u.getUserId());
                d.dispose();
            } catch (Exception ex) { warn("Error: " + ex.getMessage()); }
        });
        cancel.addActionListener(e -> d.dispose());

        d.add(form, BorderLayout.CENTER);
        d.add(btns, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void showAddLibrarianDialog(DefaultTableModel model) {
        JDialog d = dialog("Add Librarian", 420, 340);
        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBackground(BG_CARD);
        form.setBorder(new EmptyBorder(16, 16, 16, 16));

        JTextField empIdF = darkField(18);
        JTextField nameF  = darkField(18);
        JTextField emailF = darkField(18);
        JTextField phoneF = darkField(18);
        JTextField hoursF = darkField(18);

        form.add(formLabel("Employee ID:")); form.add(empIdF);
        form.add(formLabel("Name:"));        form.add(nameF);
        form.add(formLabel("Email:"));       form.add(emailF);
        form.add(formLabel("Phone:"));       form.add(phoneF);
        form.add(formLabel("Hours:"));       form.add(hoursF);

        JButton save   = accentBtn("💾 Save", ACCENT2);
        JButton cancel = ghostBtn("Cancel");
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setBackground(BG_CARD);
        btns.add(cancel); btns.add(save);

        save.addActionListener(e -> {
            Librarian l = libCtrl.addLibrarian(empIdF.getText(), nameF.getText(),
                    emailF.getText(), phoneF.getText(), hoursF.getText());
            model.addRow(new Object[]{l.getLibrarianId(), l.getEmployeeId(), l.getName(),
                    l.getEmail(), l.getPhoneNumber(), l.getWorkingHours()});
            status("Librarian added: " + l.getLibrarianId());
            d.dispose();
        });
        cancel.addActionListener(e -> d.dispose());

        d.add(form, BorderLayout.CENTER);
        d.add(btns, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void showAddStaffDialog(DefaultTableModel model) {
        JDialog d = dialog("Add Maintenance Staff", 420, 300);
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBackground(BG_CARD);
        form.setBorder(new EmptyBorder(16, 16, 16, 16));

        JTextField nameF  = darkField(18);
        JComboBox<StaffRole> roleBox = new JComboBox<>(StaffRole.values());
        styleCombo(roleBox);
        JTextField emailF = darkField(18);
        JTextField phoneF = darkField(18);

        form.add(formLabel("Name:"));  form.add(nameF);
        form.add(formLabel("Role:"));  form.add(roleBox);
        form.add(formLabel("Email:")); form.add(emailF);
        form.add(formLabel("Phone:")); form.add(phoneF);

        JButton save   = accentBtn("💾 Save", ACCENT2);
        JButton cancel = ghostBtn("Cancel");
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setBackground(BG_CARD);
        btns.add(cancel); btns.add(save);

        save.addActionListener(e -> {
            MaintenanceStaff s = maintCtrl.addStaff(nameF.getText(),
                    (StaffRole) roleBox.getSelectedItem(), emailF.getText(), phoneF.getText());
            model.addRow(new Object[]{s.getStaffId(), s.getName(), s.getRole(),
                    s.getEmail(), s.getPhoneNumber()});
            status("Staff added: " + s.getStaffId());
            d.dispose();
        });
        cancel.addActionListener(e -> d.dispose());

        d.add(form, BorderLayout.CENTER);
        d.add(btns, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  UI HELPERS
    // ══════════════════════════════════════════════════════════════════════
    private JPanel darkPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(BG_DARK);
        return p;
    }

    private void configureGlobalTheme() {
        UIManager.put("Panel.background", BG_CARD);
        UIManager.put("OptionPane.background", BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("Button.background", BG_ELEVATED);
        UIManager.put("Button.foreground", TEXT_PRIMARY);
        UIManager.put("Button.select", BG_ELEVATED);
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.background", BG_ELEVATED);
        UIManager.put("TextField.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground", ACCENT);
        UIManager.put("ComboBox.background", BG_ELEVATED);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", BG_ELEVATED);
        UIManager.put("ComboBox.selectionForeground", TEXT_PRIMARY);
        UIManager.put("TabbedPane.selected", BG_ELEVATED);
        UIManager.put("TabbedPane.contentAreaColor", BG_CARD);
        UIManager.put("ScrollBar.thumb", new Color(214, 219, 235));
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

    private JPanel statCard(String label, String value, Color accent) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COL, 1, true),
                new EmptyBorder(18, 22, 18, 22)));

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valLbl.setForeground(accent);

        JLabel nameLbl = new JLabel(label);
        nameLbl.setFont(FONT_BODY);
        nameLbl.setForeground(TEXT_MUTED);

        p.add(valLbl,  BorderLayout.CENTER);
        p.add(nameLbl, BorderLayout.SOUTH);
        return p;
    }

    private JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 24));
        l.setForeground(TEXT_PRIMARY);
        l.setBorder(new EmptyBorder(0, 2, 14, 0));
        return l;
    }

    private JLabel summaryLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(TEXT_MUTED);
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
        ((DefaultTableCellRenderer) t.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                lbl.setBorder(new EmptyBorder(0, 10, 0, 10));
                lbl.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? BG_CARD : ROW_ALT));
                lbl.setForeground(isSelected ? table.getSelectionForeground() : TEXT_PRIMARY);
                return lbl;
            }
        });
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        return t;
    }

    private void colorStatusColumn(JTable table, int col) {
        table.getColumnModel().getColumn(col).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                String s = String.valueOf(val);
                lbl.setForeground(switch (s) {
                    case "AVAILABLE", "PAID",    "CLOSED"   -> ACCENT2;
                    case "ISSUED"                           -> ACCENT;
                    case "OVERDUE",  "UNPAID",   "DAMAGED"  -> ACCENT3;
                    case "RETURNED"                         -> ACCENT4;
                    default -> TEXT_MUTED;
                });
                lbl.setBackground(sel ? new Color(238, 241, 255) : (r % 2 == 0 ? BG_CARD : ROW_ALT));
                lbl.setBorder(new EmptyBorder(0, 10, 0, 10));
                lbl.setOpaque(true);
                return lbl;
            }
        });
    }

    private JTextField darkField(int cols) {
        JTextField f = new JTextField(cols);
        f.setBackground(BG_ELEVATED);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COL, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        return f;
    }

    private JComboBox<String> darkCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        styleCombo(cb);
        return cb;
    }

    private <T> void styleCombo(JComboBox<T> cb) {
        cb.setBackground(BG_ELEVATED);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_BODY);
        cb.setOpaque(true);
        cb.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COL, 1, true),
                new EmptyBorder(4, 8, 4, 8)));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                lbl.setBackground(isSelected ? new Color(238, 241, 255) : BG_CARD);
                lbl.setForeground(TEXT_PRIMARY);
                lbl.setBorder(new EmptyBorder(6, 10, 6, 10));
                return lbl;
            }
        });
    }

    private JButton accentBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setUI(new BasicButtonUI());
        b.setFont(FONT_BODY);
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(color, 1, true),
                new EmptyBorder(8, 18, 8, 18)));
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(color.darker()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(color); b.setForeground(Color.WHITE); }
        });
        return b;
    }

    private JButton ghostBtn(String text) {
        JButton b = new JButton(text);
        b.setUI(new BasicButtonUI());
        b.setFont(FONT_BODY);
        b.setForeground(TEXT_PRIMARY);
        b.setBackground(BG_CARD);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COL, 1, true),
                new EmptyBorder(8, 16, 8, 16)));
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(245, 247, 255)); b.setForeground(ACCENT); }
            public void mouseExited(MouseEvent e)  { b.setBackground(BG_CARD); b.setForeground(TEXT_PRIMARY); }
        });
        return b;
    }


    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private void styleTextArea(JTextArea a) {
        a.setBackground(BG_ELEVATED);
        a.setForeground(TEXT_PRIMARY);
        a.setFont(FONT_MONO);
        a.setEditable(false);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    private JDialog dialog(String title, int width, int height) {
        JDialog d = new JDialog(this, title, true);
        d.setSize(width, height);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(BG_CARD);
        d.setLayout(new BorderLayout());
        d.getRootPane().setBorder(new LineBorder(BORDER_COL, 1, true));
        return d;
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

    private void styleTabs(JTabbedPane tabs) {
        tabs.setBackground(BG_CARD);
        tabs.setForeground(TEXT_PRIMARY);
        tabs.setFont(FONT_BODY);
        tabs.setBorder(new EmptyBorder(0, 0, 0, 0));
        tabs.setOpaque(true);
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        return g;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0;
        p.add(formLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        p.add(field, gbc);
    }

    private void showCard(String name) {
        cardLayout.show(contentPanel, name);
    }

    private void status(String msg) {
        statusBar.setText("  " + msg);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private int findRow(DefaultTableModel model, String id, int col) {
        for (int i = 0; i < model.getRowCount(); i++)
            if (id.equals(model.getValueAt(i, col))) return i;
        return -1;
    }
}