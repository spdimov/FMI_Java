package bg.sofia.uni.fmi.mjt.shopping.item;

import java.util.Objects;

public class Apple implements Item, Comparable {

    private final String id;

    public Apple(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return Objects.equals(id, ((Item) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Object o) {

        if (o == null) {
            return -1;
        }

        if (!(o instanceof Item)) {
            return -1;
        }

        if (this.id.equals(((Item) o).getId())) {
            return 0;
        }

        return -1;
    }
}
