package thread;

import java.util.Random;

import manager.StockManager;
import model.StockQuote;
import util.Constants;
import gui.MainFrame;
import java.awt.Color;
import javax.swing.SwingUtilities;

public class DataProducer implements Runnable {

    private StockManager manager;
    private volatile boolean running = true;
    private int delay = Constants.DEFAULT_SPEED;
    private MainFrame frame;

    private Random random = new Random();

    public DataProducer(StockManager manager, MainFrame frame) {
        this.manager = manager;
        this.frame = frame;
    }

    private Thread thread;

    @Override
    public void run() {
        thread = Thread.currentThread();

        while (running) {

            try {

                String symbol = Constants.STOCKS[random.nextInt(Constants.STOCKS.length)];

                double price = 100 + random.nextDouble() * 400;

                StockQuote quote = new StockQuote(symbol, price);

                // Add to queue
                boolean added = manager.getQueue().offer(
                        quote,
                        500,
                        java.util.concurrent.TimeUnit.MILLISECONDS);

                if (added) {

                    manager.incrementProduced();

                    SwingUtilities.invokeLater(() -> {
                        frame.updateProduced(manager.getProducedCount());
                    });

                } else {

                    SwingUtilities.invokeLater(() -> {
                        frame.setStatus("Status : Queue Full!", Color.RED);
                    });

                }

                Thread.sleep(delay);

            } catch (InterruptedException e) {

                running = false;

            }

        }

    }

    // Stop the thread safely
    public void stopProducer() {
        running = false;
        if (thread != null) {
            thread.interrupt();
        }
    }

    // Change the speed dynamically
    public void setDelay(int delay) {
        this.delay = delay;
    }

}