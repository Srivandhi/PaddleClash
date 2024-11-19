package com.sri;

import org.springframework.web.bind.annotation.*;
import java.awt.event.KeyEvent;  // For KeyEvent
 


@RestController
@RequestMapping("/api/pingpong")
public class PingPongController {

    private PingPongGameFrame gameFrame;  // This will hold the game state

    // Constructor to initialize the game frame
   
    public PingPongController() {
        gameFrame = new PingPongGameFrame();  // Initialize the game frame
    }

    // End point to get the current game state (ball position, paddle position, scores, etc.)
    @GetMapping("/gameState")
    public GameState getGameState() {
        // Return the current game state (ball position, paddle position, scores, game over state)
        return new GameState(gameFrame);
    }

    // End point to move a paddle (Player 1 or Player 2)
    @PostMapping("/movePaddle")
    public GameState movePaddle(@RequestParam int player, @RequestParam String direction) {
        // Simulate key press for paddle movement
        if (player == 1) {
            if (direction.equals("up")) {
                gameFrame.keyPressed(new KeyEvent(gameFrame, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_A, 'A'));
            } else if (direction.equals("down")) {
                gameFrame.keyPressed(new KeyEvent(gameFrame, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_Z, 'Z'));
            }
        } else if (player == 2) {
            if (direction.equals("up")) {
                // Use '\u0000' as the keyChar for UP key
                gameFrame.keyPressed(new KeyEvent(gameFrame, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, '\u0000'));
            } else if (direction.equals("down")) {
                // Use '\u0000' as the keyChar for DOWN key
                gameFrame.keyPressed(new KeyEvent(gameFrame, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, '\u0000'));
            }
        }

        // Update the game state after paddle movement
        gameFrame.actionPerformed(null);

        return new GameState(gameFrame);  // Return the updated game state
    }


    @PostMapping("/resetGame")
    public GameState resetGame() {
        gameFrame.restartGame();  // Restart the game
        return new GameState(gameFrame);  // Return the game state after reset
    }

    // End point to get the score
    @GetMapping("/score")
    public String getScore() {
        GameState state = new GameState(gameFrame);
        return "Player 1: " + state.player1Score + " | Player 2: " + state.player2Score;
    }

    // GameState class to represent the game state
    public static class GameState {
        public int ballX, ballY;
        public int paddle1Y, paddle2Y;
        public int player1Score, player2Score;
        public boolean gameOver;

        public GameState(PingPongGameFrame gameFrame) {
            this.ballX = gameFrame.ballX;
            this.ballY = gameFrame.ballY;
            this.paddle1Y = gameFrame.paddle1Y;
            this.paddle2Y = gameFrame.paddle2Y;
            this.player1Score = gameFrame.player1Score;
            this.player2Score = gameFrame.player2Score;
            this.gameOver = gameFrame.gameOver;
        }
    }
}
