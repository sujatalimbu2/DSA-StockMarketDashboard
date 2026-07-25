package thread;

import gui.MainFrame;
import model.StockQuote;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

public class IndicatorCalculator implements Runnable {

    private List<StockQuote> history;
    private MainFrame frame;

    public IndicatorCalculator(List<StockQuote> history, MainFrame frame) {
        this.history = history;
        this.frame = frame;
    }

    @Override
    public void run() {

        try {

            for (int i = 0; i <= 100; i += 20) {

                Thread.sleep(600);

                final int progress = i;

                SwingUtilities.invokeLater(() -> frame.updateProgress(progress));
            }

            List<StockQuote> snapshot = new ArrayList<>(history);

            if (snapshot.isEmpty()) {

                SwingUtilities.invokeLater(() -> {

                    frame.hideProgressBar();
                    frame.setStatus("Status : No Data");
                    frame.shutdownExecutor();

                });

                return;
            }

            double total = 0;
            double highest = snapshot.get(0).getPrice();
            double lowest = snapshot.get(0).getPrice();

            for (StockQuote quote : snapshot) {

                double price = quote.getPrice();

                total += price;

                highest = Math.max(highest, price);
                lowest = Math.min(lowest, price);
            }

            double average = total / snapshot.size();

            // Calculate moving average
            int windowSize = Math.min(5, snapshot.size());

            double movingTotal = 0;

            for (int i = snapshot.size() - windowSize; i < snapshot.size(); i++) {
                movingTotal += snapshot.get(i).getPrice();
            }

            double movingAverage = movingTotal / windowSize;

            // Create final copies for the lambda
            final double finalAverage = average;
            final double finalHighest = highest;
            final double finalLowest = lowest;
            final double finalMovingAverage = movingAverage;
            final int finalTotalQuotes = snapshot.size();

            SwingUtilities.invokeLater(() -> {

                frame.showIndicator(
                        finalAverage,
                        finalHighest,
                        finalLowest,
                        finalMovingAverage,
                        finalTotalQuotes);

                frame.hideProgressBar();
                frame.setStatus("Status : Calculation Complete");
                frame.shutdownExecutor();
            });

        } catch (InterruptedException e) {

            SwingUtilities.invokeLater(() -> {

                frame.hideProgressBar();
                frame.setStatus("Status : Calculation Interrupted");
                frame.shutdownExecutor();

            });

        }

    }
}