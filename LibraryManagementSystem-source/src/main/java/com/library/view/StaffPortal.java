package com.library.view;

import com.library.controller.*;
import com.library.repository.StaffDutyRepository;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;

public class StaffPortal extends JFrame {

    // ── Palette (identical to LibraryApp) ──────────────────────────────────
    private static final Color BG_DARK      = new Color(244, 246, 252);
    private static final Color BG_CARD      = new Color(255, 255, 255);
    private static final Color BG_SIDEBAR   = new Color(94, 84, 245);
    private static final Color BG_ELEVATED  = new Color(248, 249, 255);
    private static final Color ACCENT       = new Color(92, 81, 242);
    private static final Color ACCENT2      = new Color(71, 196, 126);
    private static final Color ACCENT3      = new Color(244, 106, 128);
    private static final Color TEXT_PRIMARY = new Color(31, 41, 55);
    private static final Color TEXT_MUTED   = new Color(113, 122, 138);
    private static final Color BORDER_COL   = new Color(228, 232, 242);
    private static final Color ROW_ALT      = new Color(250, 251, 255);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    // ── Dependencies ───────────────────────────────────────────────────────
    private final StaffDutyRepository    dutyRepo;
    private final String                 staffId;
    private final String                 staffName;
    private final BookController         bookCtrl;
    private final UserController         userCtrl;
    private final LibrarianController    libCtrl;
    private final TransactionController  txCtrl;
    private final AdminController        adminCtrl;
    private final MaintenanceController  maintCtrl;

    // ── Status bar ─────────────────────────────────────────────────────────
    private JLabel statusBar;

    // ══════════════════════════════════════════════════════════════════════
    //  Constructor
    // ══════════════════════════════════════════════════════════════════════
    public StaffPortal(StaffDutyRepository dutyRepo,
                       String staffId, String staffName,
                       BookController bookCtrl, UserController userCtrl,
                       LibrarianController libCtrl, TransactionController txCtrl,
                       AdminController adminCtrl, MaintenanceController maintCtrl) {
        this.dutyRepo   = dutyRepo;
        this.staffId    = staffId;
        this.staffName  = staffName;
        this.bookCtrl   = bookCtrl;
        this.userCtrl   = userCtrl;
        this.libCtrl    = libCtrl;
        this.txCtrl     = txCtrl;
        this.adminCtrl  = adminCtrl;
        this.maintCtrl  = maintCtrl;
        buildUI();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Frame bootstrap
    // ══════════════════════════════════════════════════════════════════════
    private void buildUI() {
        configureGlobalTheme();

        setTitle("Staff Portal — " + staffName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);

        root.add(buildHeader(),    BorderLayout.NORTH);
        root.add(buildSidebar(),   BorderLayout.WEST);
        root.add(buildContent(),   BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Header  (matches LibraryApp exactly)
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(BG_CARD);
        h.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_COL),
                new EmptyBorder(8, 14, 8, 14)));
        h.setPreferredSize(new Dimension(0, 56));

        JLabel logo = new JLabel("  Library Management System — Staff Portal");
        logo.setFont(FONT_TITLE);
        logo.setForeground(TEXT_PRIMARY);
        h.add(logo, BorderLayout.WEST);

        JLabel info = new JLabel("Welcome, " + staffName + "   |   Maintenance Staff   ");
        info.setFont(FONT_SMALL);
        info.setForeground(TEXT_MUTED);
        h.add(info, BorderLayout.EAST);

        return h;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Sidebar  (matches LibraryApp exactly)
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(BG_SIDEBAR);
        sb.setBorder(new MatteBorder(0, 0, 0, 1, BORDER_COL));
        sb.setPreferredSize(new Dimension(210, 0));

        sb.add(Box.createVerticalStrut(12));
        sb.add(navLabel("MY PORTAL"));
        sb.add(navStaticBtn("  My Duties"));

        sb.add(Box.createVerticalStrut(8));
        sb.add(navLabel("ACCOUNT"));
        JButton logoutBtn = navStaticBtn("  Logout");
        logoutBtn.addActionListener(e -> logout());
        sb.add(logoutBtn);

        sb.add(Box.createVerticalGlue());

        // Staff ID badge at bottom
        JLabel badge = new JLabel("  ID: " + staffId);
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

    private JButton navStaticBtn(String text) {
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
        return btn;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Status bar  (matches LibraryApp exactly)
    // ══════════════════════════════════════════════════════════════════════
    private JLabel buildStatusBar() {
        statusBar = new JLabel("  Ready  |  Staff Portal — " + staffName);
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
    //  Main content — Duties panel
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildContent() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Section title
        JLabel title = new JLabel("My Assigned Duties");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 2, 14, 0));
        p.add(title, BorderLayout.NORTH);

        // Card wrapper (matches LibraryApp's cardPanel)
        JPanel card = cardPanel("Duty List");

        // Table model
        String[] cols = {"Duty ID", "Title", "Description", "Assigned By", "Assigned Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        Runnable load = () -> {
            model.setRowCount(0);
            dutyRepo.findByStaffId(staffId).forEach(d -> model.addRow(new Object[]{
                    d.getDutyId(),
                    d.getTaskType(),
                    d.getDescription(),
                    d.getAssignedBy(),
                    d.getAssignedDate().format(FMT),
                    d.getStatus()
            }));
        };
        load.run();

        JTable table = styledTable(model);
        colorStatusColumn(table, 5);

        card.add(styledScrollPane(table), BorderLayout.CENTER);

        // Action bar
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);

        JButton markInProgress = accentBtn("🔄 Mark In Progress", ACCENT);
        JButton markComplete   = accentBtn("✅ Mark Completed",   ACCENT2);
        JButton refreshBtn     = ghostBtn("↺ Refresh");
        JButton logoutBtn      = ghostBtn("Logout");

        actions.add(markInProgress);
        actions.add(markComplete);
        actions.add(refreshBtn);
        actions.add(Box.createHorizontalStrut(16));
        actions.add(logoutBtn);

        // Summary strip (matches LibraryApp's Reports summary)
        JPanel summary = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        summary.setBackground(BG_CARD);
        summary.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, BORDER_COL),
                new EmptyBorder(2, 0, 2, 0)));

        Runnable refreshSummary = () -> {
            summary.removeAll();
            java.util.List<com.library.model.StaffDuty> duties =
                    dutyRepo.findByStaffId(staffId);
            long total     = duties.size();
            long pending   = duties.stream().filter(d -> "PENDING".equals(d.getStatus())).count();
            long inProg    = duties.stream().filter(d -> "IN_PROGRESS".equals(d.getStatus())).count();
            long completed = duties.stream().filter(d -> "COMPLETED".equals(d.getStatus())).count();
            summary.add(summaryLabel("Total Duties: " + total));
            summary.add(summaryLabel("Pending: " + pending));
            summary.add(summaryLabel("In Progress: " + inProg));
            summary.add(summaryLabel("Completed: " + completed));
            summary.revalidate();
            summary.repaint();
        };
        refreshSummary.run();

        // Wire all buttons
        markInProgress.addActionListener(e -> { updateStatus(table, model, "IN_PROGRESS", load); refreshSummary.run(); });
        markComplete.addActionListener(e ->   { updateStatus(table, model, "COMPLETED",   load); refreshSummary.run(); });
        refreshBtn.addActionListener(e ->     { load.run(); refreshSummary.run(); status("Refreshed duty list."); });
        logoutBtn.addActionListener(e -> logout());

        JPanel centre = new JPanel(new BorderLayout(0, 10));
        centre.setOpaque(false);
        centre.add(card,    BorderLayout.CENTER);
        centre.add(actions, BorderLayout.SOUTH);

        p.add(centre,  BorderLayout.CENTER);
        p.add(summary, BorderLayout.SOUTH);

        return p;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Duty status update
    // ══════════════════════════════════════════════════════════════════════
    private void updateStatus(JTable table, DefaultTableModel model,
                              String newStatus, Runnable reload) {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Select a duty first."); return; }
        String dutyId = (String) model.getValueAt(row, 0);
        dutyRepo.findById(dutyId).ifPresent(d -> {
            d.setStatus(newStatus);
            dutyRepo.save(d);
        });
        reload.run();
        status("Duty " + dutyId + " marked as " + newStatus + ".");
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Logout  (matches LibraryApp — confirm dialog before exit)
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
    //  UI HELPERS  (all 1-to-1 with LibraryApp)
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
                            case "PENDING"     -> ACCENT3;
                            case "IN_PROGRESS" -> ACCENT;
                            case "COMPLETED"   -> ACCENT2;
                            default            -> TEXT_MUTED;
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
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(245, 247, 255));
                b.setForeground(ACCENT);
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(BG_CARD);
                b.setForeground(TEXT_PRIMARY);
            }
        });
        return b;
    }

    private JLabel summaryLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private void status(String msg) {
        statusBar.setText("  " + msg);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}