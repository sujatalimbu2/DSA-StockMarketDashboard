package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import manager.StockManager;
import thread.DataProducer;
import thread.DataConsumer;

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

    // Speed Slider
    private JSlider speedSlider;

    // Live Quote Table
    private JTable quoteTable;
    private DefaultTableModel tableModel;

    // Indicator Display
    private JTextArea indicatorArea;

    // Status Bar
    private JLabel statusLabel;

    public MainFrame() {

        setTitle("Real-Time Financial Market Dashboard");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeComponents();
        stockManager = new StockManager();

        setVisible(true);
    }

    private void initializeComponents() {

        setLayout(new BorderLayout());

        // =======================
        // Top Panel
        // =======================

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        startButton = new JButton("Start Data Feed");
        stopButton = new JButton("Stop Data Feed");
        calculateButton = new JButton("Calculate Indicators");

        speedSlider = new JSlider(100, 2000, 1000);
        speedSlider.setMajorTickSpacing(500);
        speedSlider.setMinorTickSpacing(100);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);

        topPanel.add(startButton);
        topPanel.add(stopButton);
        topPanel.add(calculateButton);

        topPanel.add(new JLabel("Data Speed"));

        topPanel.add(speedSlider);

        add(topPanel, BorderLayout.NORTH);

        // =======================
        // Quote Table
        // =======================

        String[] columns = { "Timestamp", "Stock", "Price" };

        tableModel = new DefaultTableModel(columns, 0);

        quoteTable = new JTable(tableModel);

        JScrollPane tableScroll = new JScrollPane(quoteTable);

        // =======================
        // Indicator Area
        // =======================

        indicatorArea = new JTextArea();
        indicatorArea.setEditable(false);

        JScrollPane indicatorScroll = new JScrollPane(indicatorArea);

        indicatorArea.setBorder(
                BorderFactory.createTitledBorder("Indicator Results"));

        // =======================
        // Split Pane
        // =======================

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                tableScroll,
                indicatorScroll);

        splitPane.setDividerLocation(420);

        add(splitPane, BorderLayout.CENTER);

        // =======================
        // Status Bar
        // =======================

        JPanel statusPanel = new JPanel(new BorderLayout());

        statusLabel = new JLabel("Status : Idle");

        statusPanel.add(statusLabel, BorderLayout.WEST);

        add(statusPanel, BorderLayout.SOUTH);
        // Change producer speed when slider moves
        speedSlider.addChangeListener(e -> {

            if (producer != null) {

                producer.setDelay(speedSlider.getValue());

            }

        });
        // Start Button
        startButton.addActionListener(e -> startFeed());

        // Stop Button
        stopButton.addActionListener(e -> stopFeed());

    }

    public void addQuote(model.StockQuote quote) {

        tableModel.addRow(new Object[] {
                quote.getFormattedTime(),
                quote.getSymbol(),
                String.format("%.2f", quote.getPrice())
        });

        quoteTable.scrollRectToVisible(
                quoteTable.getCellRect(
                        quoteTable.getRowCount() - 1,
                        0,
                        true));

    }

    private void startFeed() {

        if (producerThread != null && producerThread.isAlive()) {
            return;
        }

        producer = new DataProducer(stockManager);

        // Set speed from slider
        producer.setDelay(speedSlider.getValue());

        consumer = new DataConsumer(stockManager, this);

        producerThread = new Thread(producer);
        consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();

        statusLabel.setText("Status : Data Feed Running");

    }

    private void stopFeed() {

        if (producer != null) {
            producer.stopProducer();
        }

        if (consumer != null) {
            consumer.stopConsumer();
        }

        statusLabel.setText("Status : Data Feed Stopped");

    }

}
