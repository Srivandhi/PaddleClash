package com.sri;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class PingPongGameFrame extends JPanel implements ActionListener, KeyListener {

    public static final int WINDOW_WIDTH = 500;
    public static final int WINDOW_HEIGHT = 300;

    public int ballX = 100, ballY = 100, ballVelX = 2, ballVelY = 2;
    public int paddle1Y = 100, paddle2Y = 100;
    public final int PADDLE_WIDTH = 50, PADDLE_HEIGHT = 60;
    public Timer timer;

    public Image backgroundImage;
    public Image paddle1Image, paddle2Image;

    public boolean gameOver = false;
    
    // Score variables
    public int player1Score = 0, player2Score = 0;
    public final int MAX_SCORE = 5;  // Set the maximum score to win the game

    // Constructor
    public PingPongGameFrame() {
        // Load background image
        ImageIcon backgroundIcon = new ImageIcon("src/AWT_practice/bg.jpg");
        backgroundImage = backgroundIcon.getImage();

        // Load paddle images with ImageIO for better error handling
        try {
            paddle1Image = ImageIO.read(new File("src/AWT_practice/redpaddle.png"));
            paddle2Image = ImageIO.read(new File("src/AWT_practice/black paddle.png"));
        } catch (IOException e) {
            System.out.println("Error loading paddle images: " + e.getMessage());
        }

        // Set preferred size for the panel
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        // Add key listener to the panel
        addKeyListener(this);
        setFocusable(true);

        // Initialize timer for animation (calls actionPerformed every 10ms)
        timer = new Timer(10, this);
        timer.start();
    }

    // Method to start the game by making the JFrame visible
    public void startGame(JFrame gameFrame) {
        gameFrame.setVisible(true);
    }

    // Action event handler for game logic (ball movement, collision, etc.)
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;  // Don't update game logic if the game is over

        ballX += ballVelX;
        ballY += ballVelY;

        // Ball collision with Player 1 paddle (left side)
        if (ballX <= 40 && ballY >= paddle1Y && ballY <= paddle1Y + PADDLE_HEIGHT) {
            ballVelX = -ballVelX; // Reflect ball horizontally
            // Adjust vertical speed based on where the ball hits the paddle
            int paddleCenter = paddle1Y + PADDLE_HEIGHT / 2;
            int ballCenter = ballY + 5; // Ball radius = 5 (half of 10)
            ballVelY = (ballCenter - paddleCenter) / 5;  // Modify vertical velocity based on impact position
        }

        // Ball collision with Player 2 paddle (right side)
        if (ballX + 30 >= 440 && ballY >= paddle2Y && ballY <= paddle2Y + PADDLE_HEIGHT) {
            ballVelX = -ballVelX; // Reflect ball horizontally
            // Adjust vertical speed based on where the ball hits the paddle
            int paddleCenter = paddle2Y + PADDLE_HEIGHT / 2;
            int ballCenter = ballY + 15; // Ball's center (half of its height)
            ballVelY = (ballCenter - paddleCenter) / 5;  // Modify vertical velocity based on impact position
        }

        // Ball collision with top and bottom walls
        if (ballY <= 0 || ballY >= getHeight() - 10) {
            ballVelY = -ballVelY;
        }

        // Ball out of bounds (Player 1 missed on the left side, Player 2 missed on the right side)
        if (ballX <= 0) {
            player2Score++; // Player 2 scores a point
            resetBall(); // Reset the ball after a point is scored
        }
        if (ballX >= getWidth()) {
            player1Score++; // Player 1 scores a point
            resetBall(); // Reset the ball after a point is scored
        }

        // Check if any player reached the maximum score
        if (player1Score >= MAX_SCORE) {
            gameOver = true;
            repaint(); // Trigger a repaint to show the "Game Over" message
        }
        if (player2Score >= MAX_SCORE) {
            gameOver = true;
            repaint(); // Trigger a repaint to show the "Game Over" message
        }

        // Redraw the game
        repaint();
    }

    // Paint the game components (ball, paddles, score, etc.)
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw background image
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        if (gameOver) {
            // Draw "Game Over" message
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            if (player1Score >= MAX_SCORE) {
                g.drawString("Player 1 Wins!", 150, 100);
            } else {
                g.drawString("Player 2 Wins!", 150, 100);
            }

            // Draw restart instruction
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press 'R' to Restart", 170, 150);
        } else {
            // Draw the ball and paddles
            drawGame(g);

            // Draw the scores
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Player 1: " + player1Score, 20, 30); // Display Player 1 score
            g.drawString("Player 2: " + player2Score, getWidth() - 120, 30); // Display Player 2 score
        }
    }

    // Draw the ball and paddles
    public void drawGame(Graphics g) {
        // Draw ball
        g.setColor(Color.RED);
        g.fillOval(ballX, ballY, 30, 30); // Ball is 30x30px

        // Draw paddles using images
        g.drawImage(paddle1Image, 50, paddle1Y, PADDLE_WIDTH, PADDLE_HEIGHT, this); // Player 1 Paddle
        g.drawImage(paddle2Image, 440, paddle2Y, PADDLE_WIDTH, PADDLE_HEIGHT, this); // Player 2 Paddle
    }

    // Handle key events for paddle movement
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver && e.getKeyCode() == KeyEvent.VK_R) {
            restartGame(); // Restart the game when 'R' is pressed
        }

        // Player 1 controls (A and Z keys)
        if (e.getKeyCode() == KeyEvent.VK_A && paddle1Y > 0) {
            paddle1Y -= 30; // Move player 1 up
        } else if (e.getKeyCode() == KeyEvent.VK_Z && paddle1Y < getHeight() - PADDLE_HEIGHT) {
            paddle1Y += 30; // Move player 1 down
        }

        // Player 2 controls (Up and Down arrow keys)
        if (e.getKeyCode() == KeyEvent.VK_UP && paddle2Y > 0) {
            paddle2Y -= 30; // Move player 2 up
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && paddle2Y < getHeight() - PADDLE_HEIGHT) {
            paddle2Y += 30; // Move player 2 down
        }
    }

    // Method to reset the ball after scoring
    public void resetBall() {
        ballX = 250; // Reset to center
        ballY = 150;
        ballVelX = -ballVelX; // Change direction
        ballVelY = 2;
    }

    // Method to restart the game
    public void restartGame() {
        player1Score = 0;
        player2Score = 0;
        ballX = 250;
        ballY = 150;
        ballVelX = 2;
        ballVelY = 2;
        paddle1Y = 100;
        paddle2Y = 100;
        gameOver = false;
        repaint();  // Refresh the screen
    }

    public static void main(String[] args) {
        // Create the game frame (JFrame)
        JFrame gameFrame = new JFrame("Ping Pong Game");
        PingPongGameFrame gamePanel = new PingPongGameFrame();

        // Set frame properties
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        gameFrame.setLocationRelativeTo(null); // Center the window
        gameFrame.setResizable(false);

        // Add the game panel (which handles the game drawing) to the frame
        gameFrame.add(gamePanel);

        // Start the game
        gamePanel.startGame(gameFrame);
    }
}
