package bg.sofia.uni.fmi.mjt.restaurant;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MJTDiningPlace implements Restaurant {

    private final Chef[] chefs;
    private final Set<Order> orderSet;
    public AtomicBoolean restaurantWorking;
    AtomicInteger totalOrders = new AtomicInteger(0);

    MJTDiningPlace(int chefNumber) {
        orderSet = new TreeSet<>();
        chefs = new Chef[chefNumber];
        restaurantWorking = new AtomicBoolean(true);

        for (int i = 0; i < chefNumber; i++) {
            Chef chef = new Chef(i, this);
            chef.start();
            chefs[i] = chef;
        }
    }

    @Override
    public synchronized void submitOrder(Order order) {
        orderSet.add(order);
        notify();
    }

    @Override
    public synchronized Order nextOrder() {

        while (orderSet.size() == 0) {
            try {
                if (restaurantWorking.get()) {
                    this.wait();
                } else {
                    return null;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Order nextOrder = orderSet.iterator().next();
        orderSet.remove(nextOrder);
        totalOrders.getAndIncrement();

        return nextOrder;
    }

    @Override
    public int getOrdersCount() {
        return totalOrders.get();
    }

    @Override
    public Chef[] getChefs() {
        return chefs;
    }

    @Override
    public synchronized void close() {

        for (int i = 0; i < chefs.length; i++) {
            chefs[i].finishWork();
        }

        notifyAll();

        restaurantWorking = new AtomicBoolean(false);
    }
}
