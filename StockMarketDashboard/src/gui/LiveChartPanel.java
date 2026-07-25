package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.BasicStroke;
import java.awt.RenderingHints;

public class LiveChartPanel extends JPanel {

    private final List<Double> prices = new ArrayList<>();

    public LiveChartPanel() {
        setPreferredSize(new Dimension(300, 180));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Live Price Chart"));
    }

    public void addPrice(double price) {

        prices.add(price);

        // Keep only the latest 30 prices
        if (prices.size() > 30) {
            prices.remove(0);
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int left = 50;
        int right = getWidth() - 20;
        int top = 20;
        int bottom = getHeight() - 40;

        // Background
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Grid
        g2.setColor(new Color(230, 230, 230));

        for (int i = 0; i <= 5; i++) {
            int y = top + i * (bottom - top) / 5;
            g2.drawLine(left, y, right, y);
        }

        for (int i = 0; i <= 5; i++) {
            int x = left + i * (right - left) / 5;
            g2.drawLine(x, top, x, bottom);
        }

        // Axes
        g2.setColor(Color.GRAY);
        g2.drawLine(left, bottom, right, bottom);
        g2.drawLine(left, top, left, bottom);

        if (prices.size() < 2)
            return;

        double max = Collections.max(prices);
        double min = Collections.min(prices);

        if (max == min)
            max += 1;

        int previousX = left;
        int previousY = bottom;

        g2.setStroke(new BasicStroke(3f));
        g2.setColor(new Color(41, 128, 185));

        for (int i = 0; i < prices.size(); i++) {

            double value = prices.get(i);

            int x = left + i * (right - left) / (prices.size() - 1);

            int y = bottom - (int) ((value - min) / (max - min) * (bottom - top));

            if (i > 0)
                g2.drawLine(previousX, previousY, x, y);

            previousX = x;
            previousY = y;
        }

        // Last point
        g2.setColor(Color.RED);
        g2.fillOval(previousX - 5, previousY - 5, 10, 10);

        // Price labels
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        g2.drawString(String.format("%.2f", max), 5, top + 5);
        g2.drawString(String.format("%.2f", min), 5, bottom);
    }
}