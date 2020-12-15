package bg.sofia.uni.fmi.mjt.restaurant;

import bg.sofia.uni.fmi.mjt.restaurant.customer.AbstractCustomer;

public record Order(Meal meal, AbstractCustomer customer) implements Comparable<Order> {
    @Override
    public int compareTo(Order order) {
        if (this == order) {
            return 0;
        }

        if (this.customer.hasVipCard() && !order.customer.hasVipCard()) {
            return 1;
        } else if (!this.customer.hasVipCard() && order.customer.hasVipCard()) {
            return -1;
        } else {
            return this.meal.getCookingTime() - order.meal.getCookingTime() >= 0 ? 1 : -1;
        }
    }

}
