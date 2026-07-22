package thread;

import gui.MainFrame;
import manager.StockManager;
import model.StockQuote;

import javax.swing.SwingUtilities;

public class DataConsumer implements Runnable {

    private StockManager manager;
    private MainFrame frame;
    private volatile boolean running = true;

    public DataConsumer(StockManager manager, MainFrame frame) {
        this.manager = manager;
        this.frame = frame;
    }

    @Override
    public void run() {

        while (running) {

            try {

                // Take a stock quote from the queue
                StockQuote quote = manager.getQueue().take();

                // Update the GUI safely
                SwingUtilities.invokeLater(() -> {
                    frame.addQuote(quote);
                });

            } catch (InterruptedException e) {
                running = false;
            }

        }

    }

    // Stop the consumer safely
    public void stopConsumer() {
        running = false;
    }

}