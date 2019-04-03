import battleshipGame.BattleshipGame;
import interfaces.Reader;
import io.ConsoleReader;
import io.ConsoleWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import battleshipGame.ships.Battleship;
import battleshipGame.ships.Destroyer;
import battleshipGame.ships.Ship;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BattleshipGameTest {

    private BattleshipGame battleshipGame;

    @Before
    public void init() {
        List<Ship> shipList = this.createThreeShips();
        Reader reader = new ConsoleReader();
        ConsoleWriter writer = new ConsoleWriter();
        this.battleshipGame = new BattleshipGame(shipList, reader, writer);
        this.battleshipGame.setupGame();
    }

    @Test
    public void setupGame_ShouldGenerate13_DifferentShipLocations() {
        List<String> locations = this.getAllLocations();
        HashSet<String> set = new HashSet<>(locations);
        int expected = 13;    //different tests for both
        int actual = set.size();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setupGame_ShouldGenerate_ConsecutiveShipLocations() {
        List<Ship> ships = this.battleshipGame.getShips();

        for (Ship ship : ships) {
            String firstLocation = ship.getLocations().get(0);
            List<String> locations = ship.getLocations();
            String alpha = "-ABCDEFGHIJ";

            for (int i = 1; i < locations.size(); i++) {
                if(locations.get(i).contains(String.valueOf(firstLocation.charAt(0)))) {
                    String expected = String.valueOf(firstLocation.charAt(0)) +
                            (Integer.parseInt(firstLocation.substring(1)) + i);
                    String actual = locations.get(i);

                    Assert.assertEquals(expected, actual);

                } else if(locations.get(i).contains(String.valueOf(firstLocation.substring(1)))) {
                    String expected = alpha.charAt(alpha.indexOf(firstLocation.charAt(0)) + i) +
                            firstLocation.substring(1);
                    String actual = locations.get(i);

                    Assert.assertEquals(expected, actual);

                } else {
                    Assert.fail();
                }
            }
        }
    }

    @Test
    public void setupGame_ShouldGenerate_DefaultBoard() {
        String[][] expectedBoard = this.createDefaultBoard();

        String[][] actualBoard = this.battleshipGame.getGameBoard();
                                                                        //delete this one
        for (int row = 0; row < expectedBoard.length; row++) {
            for (int col = 0; col < expectedBoard[row].length; col++) {
                Assert.assertEquals(expectedBoard[row][col], actualBoard[row][col]);
            }
        }
    }

    private List<Ship> createThreeShips() {
        Ship firstDestroyer = new Destroyer();
        Ship secondDestroyer = new Destroyer();
        Ship battleship = new Battleship();

        return new ArrayList<Ship>(){{
            add(firstDestroyer);
            add(secondDestroyer);
            add(battleship);
        }};
    }

    private List<String> getAllLocations() {
        List<String> locations = new ArrayList<>();
        this.battleshipGame.getShips().forEach(s -> locations.addAll(s.getLocations()));
        return locations;
    }

    private String[][] createDefaultBoard() {
       String[][] gameBoard = new String[11][11];
        String alpha = "-ABCDEFGHIJ";

        for (int row = 0; row < gameBoard.length; row++) {
            for (int col = 0; col < gameBoard[row].length; col++) {

                if(row == 0 && col == 0) {
                    gameBoard[row][col] = "  ";
                }
                else if(row == 0) {
                        gameBoard[row][col] =
                            String.valueOf(col).length() < 2
                                    ? String.valueOf(col) : String.valueOf(row);
                }
                else if(col == 0){
                    gameBoard[row][col] = alpha.charAt(row) + " ";
                }else {
                    gameBoard[row][col] = ".";
                }
            }
        }
        return gameBoard;
    }
}