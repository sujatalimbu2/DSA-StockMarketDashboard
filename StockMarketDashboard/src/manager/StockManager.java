package manager;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import model.StockQuote;
import util.Constants;

public class StockManager {

    // Thread-safe queue (Producer -> Consumer)
    private BlockingQueue<StockQuote> queue;

    // Store all historical data
    private List<StockQuote> history;

    public StockManager() {

        queue = new LinkedBlockingQueue<>(Constants.QUEUE_SIZE);

        history = new CopyOnWriteArrayList<>();

    }

    public BlockingQueue<StockQuote> getQueue() {
        return queue;
    }

    public List<StockQuote> getHistory() {
        return history;
    }

}