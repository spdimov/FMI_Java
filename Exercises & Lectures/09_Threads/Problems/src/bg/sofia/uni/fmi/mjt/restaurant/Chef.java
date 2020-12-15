package bg.sofia.uni.fmi.mjt.restaurant;

public class Chef extends Thread {

    private final int id;
    private final Restaurant restaurant;
    public boolean isRestaurantOpen;
    private int totalCookedMeals = 0;

    public Chef(int id, Restaurant restaurant) {
        isRestaurantOpen = true;
        this.id = id;
        this.restaurant = restaurant;
    }

    public void finishWork() {
        isRestaurantOpen = false;
    }

    @Override
    public void run() {
        while (isRestaurantOpen) {
            Order order = restaurant.nextOrder();
            if (order != null) {
                System.out.println("Chef with id: " + id + " started preparing " + order.meal());
                try {
                    sleep(order.meal().getCookingTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Chef with id: " + id + " finished preparing " + order.meal());

                totalCookedMeals++;
            }
        }
    }

    /**
     * Returns the total number of meals that this chef has cooked.
     **/
    public int getTotalCookedMeals() {
        return totalCookedMeals;
    }

}