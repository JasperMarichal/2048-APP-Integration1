package be.kdg.integration1.team01.game2048;

import be.kdg.integration1.team01.game2048.manager.WireframesManager;
import be.kdg.integration1.team01.game2048.model.*;

import java.util.Scanner;

public class WFPrototype {
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);

        WireframesManager.displayWireframe(Wireframe.WELCOME, null);
        keyboard.nextLine(); //Press enter to begin

        Player player = new Player("Player1"); // default player
        int endMode = 1; //0 = quit, 1 = ask for player name again, 2 = same player, start new game
        do {
            if(endMode == 1) {
                System.out.print("Enter your name: ");
                player = new Player(keyboard.nextLine());
            }

            //GameSession object
            Game gameSession = new Game(4, player);

            //Start the new game
            endMode = gameSession.play(null);
        }while (endMode != 0);
    }
}
