package battleshipGame.ships;

import java.util.ArrayList;
import java.util.List;

public abstract class Ship {

    private final int shipSize;
    private List<String> locations;

    protected Ship(int shipSize) {
        this.shipSize = shipSize;
        this.locations = new ArrayList<>();
    }

    public int getShipSize() {
        return shipSize;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
}
