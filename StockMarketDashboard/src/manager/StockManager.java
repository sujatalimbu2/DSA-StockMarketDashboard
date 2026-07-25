package manager;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import model.StockQuote;
import util.Constants;

public class StockManager {

    private final BlockingQueue<StockQuote> queue;
    private final List<StockQuote> history;

    private int producedCount = 0;
    private int consumedCount = 0;

    public synchronized void incrementProduced() {
        producedCount++;
    }

    public synchronized void incrementConsumed() {
        consumedCount++;
    }

    public synchronized int getProducedCount() {
        return producedCount;
    }

    public synchronized int getConsumedCount() {
        return consumedCount;
    }

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

    public int getQueueSize() {
        return queue.size();
    }

}