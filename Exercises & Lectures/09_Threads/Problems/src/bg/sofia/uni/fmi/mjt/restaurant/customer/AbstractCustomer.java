package bg.sofia.uni.fmi.mjt.restaurant.customer;

import bg.sofia.uni.fmi.mjt.restaurant.Meal;
import bg.sofia.uni.fmi.mjt.restaurant.Order;
import bg.sofia.uni.fmi.mjt.restaurant.Restaurant;

public abstract class AbstractCustomer extends Thread {

    public static boolean isRestaurantOpen;
    Restaurant restaurant;

    public AbstractCustomer(Restaurant restaurant) {
        this.restaurant = restaurant;
        isRestaurantOpen = true;
    }

    @Override
    public void run() {
        try {
            sleep(2000);

            Meal meal = Meal.chooseFromMenu();
            restaurant.submitOrder(new Order(meal, this));

            System.out.println("Customer ordered " + meal.getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public abstract boolean hasVipCard();

}