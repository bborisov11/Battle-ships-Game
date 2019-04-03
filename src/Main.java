import battleshipGame.BattleshipGame;
import interfaces.Game;
import interfaces.Reader;
import interfaces.Writer;
import io.ConsoleReader;
import io.ConsoleWriter;
import battleshipGame.ships.Battleship;
import battleshipGame.ships.Ship;
import battleshipGame.ships.Destroyer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Ship firstDestroyer = new Destroyer();
        Ship secondDestroyer = new Destroyer();
        Ship battleship = new Battleship();

        List<Ship> ships = new ArrayList<Ship>(){{
            add(firstDestroyer);
            add(secondDestroyer);
            add(battleship);
        }};

        Reader reader = new ConsoleReader();
        Writer writer = new ConsoleWriter();

        Game game = new BattleshipGame(ships, reader, writer);
        game.setupGame();
        game.play();
    }
}
