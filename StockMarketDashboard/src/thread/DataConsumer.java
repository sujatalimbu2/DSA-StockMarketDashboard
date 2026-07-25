package thread;

import gui.MainFrame;
import manager.StockManager;
import model.StockQuote;

import javax.swing.SwingUtilities;

public class DataConsumer implements Runnable {

    private StockManager manager;
    private MainFrame frame;
    private volatile boolean running = true;
    private Thread thread;

    public DataConsumer(StockManager manager, MainFrame frame) {
        this.manager = manager;
        this.frame = frame;
    }

    @Override
    public void run() {
        thread = Thread.currentThread();

        while (running) {

            try {

                // Take a stock quote from the queue
                StockQuote quote = manager.getQueue().take();

                manager.incrementConsumed();

                // Save to history
                manager.getHistory().add(quote);

                // Update the GUI safely
                SwingUtilities.invokeLater(() -> {
                    frame.addQuote(quote);
                    frame.updateConsumed(manager.getConsumedCount());
                });

            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
            }

        }

    }

    // Stop the consumer safely
    public void stopConsumer() {
        running = false;

        if (thread != null) {
            thread.interrupt();
        }
    }

}