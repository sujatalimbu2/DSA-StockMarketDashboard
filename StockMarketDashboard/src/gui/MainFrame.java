package gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import manager.StockManager;
import thread.DataProducer;
import thread.DataConsumer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainFrame extends JFrame {

    // Buttons
    private JButton startButton;
    private JButton stopButton;
    private JButton calculateButton;

    private StockManager stockManager;

    private DataProducer producer;
    private DataConsumer consumer;

    private Thread producerThread;
    private Thread consumerThread;

    private ExecutorService executorService;

    private JLabel averageLabel;
    private JLabel highestLabel;
    private JLabel lowestLabel;
    private JLabel movingAverageLabel;
    private JLabel totalQuotesLabel;
    private JLabel queueLabel;
    private JLabel delayLabel;
    private JLabel queueStatusLabel;
    private JLabel producedLabel;
    private JLabel consumedLabel;
    private JSpinner threadSpinner;
    private LiveChartPanel chartPanel;

    // Speed Slider
    private JSlider speedSlider;

    // Live Quote Table
    private JTable quoteTable;
    private DefaultTableModel tableModel;

    // Indicator Display
    private JTextArea indicatorArea;

    // Status Bar
    private JLabel statusLabel;

    private JProgressBar progressBar;

    public MainFrame() {

        setTitle("Real-Time Financial Market Dashboard");
        setSize(1200, 1000);
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        initializeComponents();

        stockManager = new StockManager();

        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {

                stopFeed();
                dispose();
                System.exit(0);

            }

        });

        updateButtonState(false);

        setVisible(true);
    }

    private void initializeComponents() {

        setLayout(new BorderLayout());

        // ===================================================
        // HEADER
        // ===================================================

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.BLACK);
        headerPanel.setPreferredSize(new Dimension(0, 70));

        JLabel title = new JLabel("REAL-TIME FINANCIAL MARKET DASHBOARD");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        headerPanel.add(title, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        JLabel timeLabel = new JLabel();
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        headerPanel.add(timeLabel, BorderLayout.EAST);

        new Timer(1000, e -> {
            timeLabel.setText(
                    java.time.LocalTime.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
        }).start();

        // ===================================================
        // CONTROL PANEL
        // ===================================================
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        topPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        topPanel.setBackground(Color.WHITE);
        // =========================
        // BUTTONS
        // =========================

        startButton = new JButton(" ▶ Start Data Feed");
        stopButton = new JButton("■ Stop Data Feed");
        calculateButton = new JButton(" 📊 Calculate Indicators");

        Dimension buttonSize = new Dimension(150, 35);

        startButton.setPreferredSize(buttonSize);
        stopButton.setPreferredSize(buttonSize);
        calculateButton.setPreferredSize(buttonSize);

        // Start Button
        startButton.setBackground(new Color(46, 204, 113));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);

        // Stop Button
        stopButton.setBackground(new Color(231, 76, 60));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);

        // Calculate Button
        calculateButton.setBackground(new Color(52, 152, 219));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFocusPainted(false);

        // =========================
        // SPEED SLIDER
        // =========================
        delayLabel = new JLabel("Delay : 1000 ms");
        speedSlider = new JSlider(100, 2000, 1000);
        speedSlider.setMajorTickSpacing(500);
        speedSlider.setMinorTickSpacing(100);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        delayLabel.setText(
                "Delay : " + speedSlider.getValue() + " ms");
        threadSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 8, 1));
        topPanel.add(new JLabel("Indicator Threads"));
        topPanel.add(threadSpinner);
        // =========================
        // ADD TO CONTROL PANEL
        // =========================

        topPanel.add(startButton);
        topPanel.add(stopButton);
        topPanel.add(calculateButton);
        topPanel.add(new JLabel("Data Speed"));
        topPanel.add(speedSlider);
        topPanel.add(delayLabel);
        // ===================================================
        // TABLE
        // ===================================================

        // ===================================================
        // TABLE
        // ===================================================
        // =========================
        // LIVE QUOTE TABLE
        // =========================

        String[] columns = { "Timestamp", "Stock", "Price" };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

        };

        quoteTable = new JTable(tableModel);

        // Table font
        quoteTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quoteTable.setRowHeight(28);
        quoteTable.setFillsViewportHeight(true);
        quoteTable.setGridColor(new Color(220, 220, 220));
        quoteTable.setSelectionBackground(new Color(220, 235, 255));

        // Header style
        quoteTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        quoteTable.getTableHeader().setBackground(Color.BLACK);
        quoteTable.getTableHeader().setForeground(Color.WHITE);
        quoteTable.getTableHeader().setPreferredSize(new Dimension(0, 35));

        quoteTable.setShowVerticalLines(false);
        quoteTable.setShowHorizontalLines(true);

        // Center text + zebra rows
        quoteTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(SwingConstants.CENTER);

                if (!isSelected) {
                    if (row % 2 == 0)
                        c.setBackground(Color.WHITE);
                    else
                        c.setBackground(new Color(245, 245, 245));
                }

                return c;
            }
        });
        JScrollPane tableScroll = new JScrollPane(quoteTable);

        JLabel liveTitle = new JLabel("LIVE STOCK QUOTES");
        liveTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        liveTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(liveTitle, BorderLayout.NORTH);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        // =========================
        // MARKET STATISTICS PANEL
        // =========================

        JPanel indicatorPanel = new JPanel();
        indicatorPanel.setLayout(new GridLayout(5, 1, 5, 5));

        JLabel indicatorTitle = new JLabel("MARKET INDICATORS");
        indicatorTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 8, 5));
        indicatorTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        indicatorTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        indicatorPanel.setBackground(Color.WHITE);

        indicatorPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        averageLabel = new JLabel("▲ Average : -");
        highestLabel = new JLabel("▲ Highest : -");
        lowestLabel = new JLabel("▼ Lowest : -");
        movingAverageLabel = new JLabel("◆ Moving Avg : -");
        totalQuotesLabel = new JLabel("■ Quotes : 0");

        Font statsFont = new Font("Segoe UI", Font.BOLD, 15);

        averageLabel.setFont(statsFont);
        highestLabel.setFont(statsFont);
        lowestLabel.setFont(statsFont);
        movingAverageLabel.setFont(statsFont);
        totalQuotesLabel.setFont(statsFont);

        averageLabel.setForeground(new Color(41, 128, 185));
        highestLabel.setForeground(new Color(39, 174, 96));
        lowestLabel.setForeground(Color.RED);
        movingAverageLabel.setForeground(new Color(142, 68, 173));

        indicatorPanel.add(averageLabel);
        indicatorPanel.add(highestLabel);
        indicatorPanel.add(lowestLabel);
        indicatorPanel.add(movingAverageLabel);
        indicatorPanel.add(totalQuotesLabel);

        chartPanel = new LiveChartPanel();
        chartPanel.setPreferredSize(new Dimension(250, 150));

        JLabel chartTitle = new JLabel("LIVE PRICE CHART");
        indicatorTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 8, 5));
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        chartTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        chartPanel.setBackground(Color.WHITE);

        chartPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JPanel queuePanel = new JPanel();
        queuePanel.setLayout(new GridLayout(4, 1, 5, 5));

        queuePanel.setBackground(Color.WHITE);

        queuePanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        producedLabel = new JLabel("Produced : 0");
        consumedLabel = new JLabel("Consumed : 0");
        queueLabel = new JLabel("Queue Size : 0");
        queueStatusLabel = new JLabel("Status : NORMAL");

        producedLabel.setFont(statsFont);
        consumedLabel.setFont(statsFont);
        queueLabel.setFont(statsFont);
        queueStatusLabel.setFont(statsFont);

        queuePanel.add(producedLabel);
        queuePanel.add(consumedLabel);
        queuePanel.add(queueLabel);
        queuePanel.add(queueStatusLabel);
        indicatorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        chartPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        queuePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JPanel rightContent = new JPanel();
        rightContent.setLayout(new BoxLayout(rightContent, BoxLayout.Y_AXIS));
        rightContent.setBackground(new Color(245, 245, 245));
        rightContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        rightContent.add(indicatorTitle);
        rightContent.add(indicatorPanel);

        rightContent.add(Box.createVerticalStrut(15));

        rightContent.add(chartTitle);
        rightContent.add(chartPanel);

        rightContent.add(Box.createVerticalStrut(15));

        JLabel queueTitle = new JLabel("QUEUE");
        indicatorTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 8, 5));
        queueTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        rightContent.add(queueTitle);
        rightContent.add(queuePanel);

        // ===================================================
        // HORIZONTAL SPLIT
        // ===================================================
        JScrollPane rightPanel = new JScrollPane(rightContent);
        rightPanel.setBorder(null);

        JSplitPane horizontalSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                tablePanel,
                rightPanel);

        horizontalSplit.setDividerLocation(720);
        horizontalSplit.setDividerSize(3);
        horizontalSplit.setContinuousLayout(true);

        // ===================================================
        // INDICATOR AREA
        // ===================================================

        indicatorArea = new JTextArea(12, 50);
        indicatorArea.setEditable(false);
        indicatorArea.setFont(new Font("Consolas", Font.PLAIN, 17));
        indicatorArea.setBackground(Color.WHITE);
        indicatorArea.setForeground(new Color(30, 30, 30));
        indicatorArea.setLineWrap(false);
        indicatorArea.setWrapStyleWord(false);
        indicatorArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane indicatorScroll = new JScrollPane(indicatorArea);
        indicatorScroll.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        indicatorScroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // ===================================================
        // VERTICAL SPLIT
        // ===================================================

        JSplitPane verticalSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                horizontalSplit,
                indicatorScroll);
        verticalSplit.setResizeWeight(0.87);
        verticalSplit.setDividerSize(3);

        // ===================================================
        // CENTER PANEL
        // ===================================================

        JPanel centerPanel = new JPanel(new BorderLayout());

        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(verticalSplit, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // ===================================================
        // STATUS BAR
        // ===================================================

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(Color.BLACK);

        statusLabel = new JLabel("Status : Idle");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(180, 18));
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        progressBar.setForeground(new Color(52, 152, 219));

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(progressBar, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);

        // ===================================================
        // EVENTS
        // ===================================================

        speedSlider.addChangeListener(e -> {

            delayLabel.setText("Delay : " + speedSlider.getValue() + " ms");

            if (producer != null) {
                producer.setDelay(speedSlider.getValue());
            }

        });

        startButton.addActionListener(e -> startFeed());

        stopButton.addActionListener(e -> stopFeed());

        calculateButton.addActionListener(e -> calculateIndicators());
    }

    public void addQuote(model.StockQuote quote) {

        tableModel.addRow(new Object[] {
                quote.getFormattedTime(),
                quote.getSymbol(),
                String.format("%.2f", quote.getPrice())
        });
        chartPanel.addPrice(quote.getPrice());

        updateMarketStatistics();

        quoteTable.scrollRectToVisible(
                quoteTable.getCellRect(
                        quoteTable.getRowCount() - 1,
                        0,
                        true));
    }

    public void updateProduced(int produced) {
        producedLabel.setText("↑ Produced : " + produced);
    }

    public void updateConsumed(int consumed) {
        consumedLabel.setText("↓ Consumed : " + consumed);
    }

    private void updateMarketStatistics() {

        java.util.List<model.StockQuote> history = stockManager.getHistory();

        if (history.isEmpty()) {
            return;
        }

        double total = 0;
        double highest = history.get(0).getPrice();
        double lowest = history.get(0).getPrice();

        for (model.StockQuote quote : history) {

            double price = quote.getPrice();

            total += price;

            if (price > highest)
                highest = price;

            if (price < lowest)
                lowest = price;
        }

        double average = total / history.size();

        updateStatistics(
                average,
                highest,
                lowest,
                average,
                history.size(),
                stockManager.getQueueSize());
    }

    public void showIndicator(
            double average,
            double highest,
            double lowest,
            double movingAverage,
            int totalQuotes) {

        indicatorArea.setFont(new Font("Consolas", Font.BOLD, 16));
        indicatorArea.setMargin(new Insets(10, 10, 10, 10));

        indicatorArea.setText(
                "====================================\n" +
                        "      MARKET ANALYSIS REPORT\n" +
                        "====================================\n\n" +

                        "Total Quotes      : " + totalQuotes + "\n" +
                        "Average Price     : " + String.format("%.2f", average) + "\n" +
                        "Highest Price     : " + String.format("%.2f", highest) + "\n" +
                        "Lowest Price      : " + String.format("%.2f", lowest) + "\n" +
                        "Moving Average    : " + String.format("%.2f", movingAverage) + "\n\n" +

                        "====================================");
        indicatorArea.setCaretPosition(0);
    }

    public void updateStatistics(double average,
            double highest,
            double lowest,
            double movingAverage,
            int totalQuotes,
            int queueSize) {
        if (queueSize == 0) {

            queueStatusLabel.setText("○ Queue Status : EMPTY");
            queueStatusLabel.setForeground(new Color(0, 102, 204));

        } else if (queueSize >= 8) {

            queueStatusLabel.setText("▲ Queue Status : FULL");
            queueStatusLabel.setForeground(new Color(220, 53, 69)); // FULL

        } else {

            queueStatusLabel.setText("✔ Queue Status : NORMAL");
            queueStatusLabel.setForeground(new Color(40, 167, 69)); // NORMAL

        }

        averageLabel.setText("▲ Average Price : " + String.format("%.2f", average));

        highestLabel.setText("▲ Highest Price : " + String.format("%.2f", highest));

        lowestLabel.setText("▼ Lowest Price : " + String.format("%.2f", lowest));

        movingAverageLabel.setText("◆ Moving Average : " + String.format("%.2f", movingAverage));

        totalQuotesLabel.setText("■ Total Quotes : " + totalQuotes);

        queueLabel.setText("⌛ Queue Size : " + queueSize);
    }

    private void startFeed() {

        if (producerThread != null && producerThread.isAlive()) {
            return;
        }

        producer = new DataProducer(stockManager, this);

        // Set speed from slider
        producer.setDelay(speedSlider.getValue());

        consumer = new DataConsumer(stockManager, this);

        producerThread = new Thread(producer);
        consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();
        setStatus("Status : Data Feed Running", new Color(46, 204, 113));
        updateButtonState(true);

    }

    public void setStatus(String text, Color color) {

        statusLabel.setText(text);
        statusLabel.setForeground(color);

    }

    private void stopFeed() {

        if (producer != null) {
            producer.stopProducer();
        }

        if (consumer != null) {
            consumer.stopConsumer();
        }
        shutdownExecutor();

        statusLabel.setText("Status : Data Feed Stopped");
        statusLabel.setForeground(new Color(220, 53, 69));
        updateButtonState(false);

    }

    private void calculateIndicators() {

        setStatus("Status : Calculating Indicators...", Color.ORANGE);
        showProgressBar();

        int threadCount = (Integer) threadSpinner.getValue();

        executorService = Executors.newFixedThreadPool(threadCount);

        executorService.execute(

                new thread.IndicatorCalculator(
                        stockManager.getHistory(),
                        this)

        );
    }

    public void setStatus(String text) {

        statusLabel.setText(text);

    }

    private void updateButtonState(boolean running) {

        startButton.setEnabled(!running);
        stopButton.setEnabled(running);
        calculateButton.setEnabled(running);

        startButton.setBackground(!running
                ? new Color(46, 204, 113)
                : Color.GRAY);

        stopButton.setBackground(running
                ? new Color(231, 76, 60)
                : Color.GRAY);

        calculateButton.setBackground(running
                ? new Color(52, 152, 219)
                : Color.GRAY);
    }

    public void showProgressBar() {

        progressBar.setValue(0);
        progressBar.setVisible(true);

    }

    public void hideProgressBar() {

        progressBar.setVisible(false);

    }

    public void updateProgress(int value) {

        progressBar.setValue(value);

    }
    
    

    public void shutdownExecutor() {

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }

    }

}
