package battleshipGame;

import interfaces.Game;
import interfaces.Reader;
import interfaces.Writer;
import battleshipGame.ships.Ship;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public class BattleshipGame implements Game {

    private final String NO_SHOT_SYMBOL = ".";
    private final String HIT_SYMBOL = "X";
    private final String MISS_SYMBOL = "-";

    private final int GAME_BOARD_LENGTH = 11;

    private final String MISS_SHIP_OUTPUT = "Miss";
    private final String SUNK_SHIP_OUTPUT = "Sunk";
    private final String HIT_SHIP_OUTPUT = "Hit";

    private final String BOARD_ALPHABET_LETTERS = "-ABCDEFGHIJ";

    private int shots;

    private Reader consoleReader;
    private Writer consoleWriter;
    private String[][] gameBoard;
    private List<Ship> ships;
    private List<String> allCoordinates;

    public BattleshipGame(List<Ship> ships, Reader consoleReader, Writer consoleWriter) {
        this.ships = ships;
        this.consoleReader = consoleReader;
        this.consoleWriter = consoleWriter;
        this.allCoordinates = new ArrayList<>();
        this.shots = 0;
    }

    public String[][] getGameBoard() {
        return this.gameBoard;
    }

    public List<Ship> getShips() {
        return this.ships;
    }

    public void setupGame() {
        this.setupGameBoard();  //create default game board
        this.ships.forEach(this::setupLocations);   //setup locations for every ship
        this.allCoordinates = this.getAllCoordinates();
    }

    public void play() throws IOException {
        this.showCurrentStateOfTheBoard();

        while (this.getAllLocations().size() > 0) {
            List<String> locationList = this.getAllLocations();

            this.consoleWriter.write("Enter coordinates (row, col), e.g. A5 = ");
            String command = this.consoleReader.readLine().toUpperCase();

            if (command.equals("SHOW")) {
                this.show();
            } else {
                if (command.matches("[a-jA-J][1-9]|[a-jA-J]10")) {  //check if command contains valid locations

                    String coordinates = this.formatLocationsIntoCoordinates(command);

                    int row = Integer.parseInt(coordinates.split(",")[0]);
                    int col = Integer.parseInt(coordinates.split(",")[1]);

                    if (this.gameBoard[row][col].equals(this.NO_SHOT_SYMBOL)) {   //check if current location is not already on the board

                        this.shots++;

                        if (locationList.contains(command)) {  //check if the player have hit a ship
                            Ship ship = getCurrentShip(command)                                                           //get ship which contains current location
                                    .orElseThrow(() -> new NoSuchElementException("There is no ship with " + command + " location"));
                            ship.getLocations().remove(command);

                            this.gameBoard[row][col] = this.HIT_SYMBOL;

                            if (ship.getLocations().size() == 0) {  //check if ship is destroyed
                                this.consoleWriter.writeLine(this.SUNK_SHIP_OUTPUT);
                            } else {
                                this.consoleWriter.writeLine(this.HIT_SHIP_OUTPUT);
                            }
                        } else {
                            this.gameBoard[row][col] = this.MISS_SYMBOL;
                            this.consoleWriter.writeLine(this.MISS_SHIP_OUTPUT);
                        }
                        this.showCurrentStateOfTheBoard();
                    }
                } else {
                    this.consoleWriter.writeLine("Incorrect input, try again!");
                }
            }
        }
        this.consoleWriter.writeLine("Well done! You completed the game in " + this.shots + " shots");
    }

    private void showCurrentStateOfTheBoard() {
        for (int row = 0; row < this.gameBoard.length; row++) {
            for (int col = 0; col < this.gameBoard[row].length; col++) {
                this.consoleWriter.write(this.gameBoard[row][col]);
            }
            this.consoleWriter.writeEmptyLine();
        }
    }

    private Optional<Ship> getCurrentShip(String command) {
        return this.ships
                .stream()
                .filter(s -> s.getLocations()  //get the ship who contains current command
                        .contains(command))
                .findFirst();
    }

    private void show() {
        String[][] showBoard = new String[this.GAME_BOARD_LENGTH][this.GAME_BOARD_LENGTH];
        this.fillBoard(showBoard); //fill cheat board

        this.consoleWriter.writeEmptyLine();
        for (int row = 0; row < showBoard.length; row++) {
            for (int col = 0; col < showBoard[row].length; col++) {
                this.consoleWriter.write(showBoard[row][col]);
            }
            this.consoleWriter.writeEmptyLine();
        }
    }

    private void fillBoard(String[][] showBoard) {

        for (int row = 0; row < this.gameBoard.length; row++) {
            for (int col = 0; col < this.gameBoard[row].length; col++) {
                String currentCoordinates = String.valueOf(row) + "," + String.valueOf(col);    //get current coordinates as string representation

                if (this.allCoordinates.contains(currentCoordinates)) {
                    showBoard[row][col] = this.HIT_SYMBOL;
                } else if (row == 0 || col == 0) {
                    showBoard[row][col] = this.gameBoard[row][col];
                } else {
                    showBoard[row][col] = " ";
                }
            }
        }
    }

    private List<String> getAllCoordinates() {  //coordinates e.g. row, col
        List<String> locations = new ArrayList<>();
        this.ships.forEach(s -> locations.addAll(s.getLocations()));
        return locations
                .stream()
                .map(this::formatLocationsIntoCoordinates)
                .collect(Collectors.toList());
    }

    private List<String> getAllLocations() {  //locations e.g. A5
        List<String> locations = new ArrayList<>();
        this.ships.forEach(s -> locations.addAll(s.getLocations()));
        return locations;
    }

    private void setupLocations(Ship ship) {
        List<String> locationsList = new ArrayList<>();
        int shipSize = ship.getShipSize();

        if (Math.random() < 0.5) {
            //horizontal ship
            //generate row, col
            int col = (int) (1 + ((Math.random()) * (this.gameBoard.length - 1 - shipSize)));
            int row = (int) (1 + ((Math.random() * (this.gameBoard.length - 1))));

            for (int i = 0; i < shipSize; i++) { //add coordinates into the list
                locationsList.add(this.formatCoordinatesIntoLocation(row, col + i));
            }

            if (correctLocations(locationsList)) { // check if any of locations are not already in the lists
                ship.setLocations(locationsList);  // add locations into the ship list
            } else {
                this.setupLocations(ship);    //if locations are not correct, call method again
            }
        } else {
            //vertical ship
            int col = (int) (1 + ((Math.random() * (this.gameBoard.length - 1))));
            int row = (int) (1 + ((Math.random()) * (this.gameBoard.length - 1 - shipSize)));

            for (int i = 0; i < shipSize; i++) {
                locationsList.add(this.formatCoordinatesIntoLocation(row + i, col));
            }
            if (correctLocations(locationsList)) {
                ship.setLocations(locationsList);
            } else {
                this.setupLocations(ship);
            }
        }
    }

    private boolean correctLocations(List<String> formattedCoordinates) {
        return this
                .ships
                .stream()
                .noneMatch(s -> s.getLocations()            //check if locations in the list are different from those in the ships
                        .stream()                           // if none match, they are correct
                        .anyMatch(formattedCoordinates::contains));
    }

    private String formatCoordinatesIntoLocation(int row, int col) {   //convert coordinates [row, col] into e.g. A5
        return String.valueOf(this.BOARD_ALPHABET_LETTERS.charAt(row)) + col;
    }

    private String formatLocationsIntoCoordinates(String location) {  //convert locations e.g A5 into [row, col]
        int row = this.BOARD_ALPHABET_LETTERS.indexOf(location.charAt(0));
        String col;
        if (location.length() < 3) {
            col = String.valueOf(location.charAt(1));
        } else {
            col = location.substring(1);
        }
        return row + "," + col;
    }

    private void setupGameBoard() {
        this.gameBoard = new String[this.GAME_BOARD_LENGTH][this.GAME_BOARD_LENGTH];

        for (int row = 0; row < this.gameBoard.length; row++) {
            for (int col = 0; col < this.gameBoard[row].length; col++) {

                if (row == 0 && col == 0) {
                    this.gameBoard[row][col] = "  ";
                } else if (row == 0) {
                    this.gameBoard[row][col] =
                            String.valueOf(col).length() < 2
                                    ? String.valueOf(col) : String.valueOf(row);    //if our col=10, we set it with 0
                } else if (col == 0) {
                    this.gameBoard[row][col] = this.BOARD_ALPHABET_LETTERS.charAt(row) + " ";
                } else {
                    this.gameBoard[row][col] = this.NO_SHOT_SYMBOL;
                }
            }
        }
    }
}
