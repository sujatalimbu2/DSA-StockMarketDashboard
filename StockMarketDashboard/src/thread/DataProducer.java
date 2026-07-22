package thread;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

import manager.StockManager;
import model.StockQuote;
import util.Constants;

public class DataProducer implements Runnable {

    private StockManager manager;
    private volatile boolean running = true;
    private int delay = Constants.DEFAULT_SPEED;

    private Random random = new Random();

    public DataProducer(StockManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {

        while (running) {

            try {

                // Pick a random stock symbol
                String symbol = Constants.STOCKS[
                        random.nextInt(Constants.STOCKS.length)];

                // Generate a random price
                double price = 100 + random.nextDouble() * 400;

                StockQuote quote = new StockQuote(symbol, price);

                // Add to queue
                manager.getQueue().put(quote);

                // Save to history
                manager.getHistory().add(quote);

                // Wait according to selected speed
                Thread.sleep(delay);

            } catch (InterruptedException e) {
                running = false;
            }

        }

    }

    // Stop the thread safely
    public void stopProducer() {
        running = false;
    }

    // Change the speed dynamically
    public void setDelay(int delay) {
        this.delay = delay;
    }

}