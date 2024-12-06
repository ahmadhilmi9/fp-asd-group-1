/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #1
 * 1 - 5026221016 - Arjuna Ahmad Dewangga Aljabbar
 * 2 - 5026221126 -  Nur Ghulam Musthafa Al Kautsar
 * 3 - 5026221147 -  Ahmad Hilmi Dwi Setiawan
 */

package sudoku;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class Sudoku extends JFrame {
    private static final long serialVersionUID = 1L;
    // Private variables
    private GameBoardPanel board;
    private JButton btnNewGame, btnPauseResume;
    private JLabel timerLabel, scoreLabel, highScoreLabel;
    private Timer gameTimer;
    private int score = 10000;
    private int secondsElapsed = 0;
    private String username;
    private SoundManager soundManager;

    private static final String HIGHSCORE_FILE = "highscore.txt";
    private int highScore = 0;
    private String highScoreUser = "None";

    // Progress bar and timer control
    private JProgressBar progressBar;
    private boolean isPaused = false;

    // Constructor with username and difficulty
    public Sudoku(String username, int difficulty) {
        this.username = username;

        soundManager = new SoundManager(); // Initialize SoundManager
        board = new GameBoardPanel(soundManager);

        // Load high score
        loadHighScore();

        // Initialize New Game and Pause/Resume buttons
        btnNewGame = new JButton("New Game");
        btnPauseResume = new JButton("Pause");

        // Initialize menu bar
        JMenuBar menuBar = new JMenuBar();

        // Menu "File"
        JMenu menuFile = new JMenu("File");
        JMenuItem newGame = new JMenuItem("New Game");
        JMenuItem resetGame = new JMenuItem("Reset Game");
        JMenuItem exit = new JMenuItem("Exit");

        // Action listeners for "File" menu items
        newGame.addActionListener(e -> newGameToWelcomeScreen()); // Start new game
        resetGame.addActionListener(e -> resetGame(difficulty)); // Reset game (default level)
        exit.addActionListener(e -> System.exit(0)); // Exit application

        // Add items to "File" menu
        menuFile.add(newGame);
        menuFile.add(resetGame);
        menuFile.addSeparator(); // Separator line
        menuFile.add(exit);

        // Menu "Options"
        JMenu menuOptions = new JMenu("Options");

        // Create the toggle sound menu item with an initial state
        String initialSoundState = soundManager.isSoundEnabled() ? "Disable Sound" : "Enable Sound";
        JMenuItem toggleSound = new JMenuItem(initialSoundState);

        // Add action listener to toggle sound and update the menu item text
        toggleSound.addActionListener(e -> {
            soundManager.toggleSound();
            String soundState = soundManager.isSoundEnabled() ? "Disable Sound" : "Enable Sound";
            toggleSound.setText(soundState);
            JOptionPane.showMessageDialog(this,
                    "Sound is now " + (soundManager.isSoundEnabled() ? "enabled" : "disabled") + ".",
                    "Sound Settings",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Add toggle sound to the options menu
        menuOptions.add(toggleSound);

        // Menu "Help"
        JMenu menuHelp = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");

        // Action listener for "About" menu item
        about.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Sudoku Game\nDeveloped using Java Swing\nEnjoy playing!",
                "About", JOptionPane.INFORMATION_MESSAGE));

        // Add item to "Help" menu
        menuHelp.add(about);

        // Add menus to menu bar
        menuBar.add(menuFile);
        menuBar.add(menuOptions);
        menuBar.add(menuHelp);

        // Set menu bar to the frame
        setJMenuBar(menuBar);

        // Set up the game UI
        timerLabel = new JLabel("Time: 0s");
        scoreLabel = new JLabel("Score: " + score);
        highScoreLabel = new JLabel("High Score: " + highScoreUser + " - " + highScore);

        // Initialize progress bar
        progressBar = new JProgressBar(0, SudokuConstants.GRID_SIZE * SudokuConstants.GRID_SIZE);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        // Info panel (timer, score, progress bar)
        JPanel infoPanel = new JPanel(new GridLayout(1, 4));
        infoPanel.add(timerLabel);
        infoPanel.add(scoreLabel);
        infoPanel.add(highScoreLabel);
        infoPanel.add(progressBar);
        cp.add(infoPanel, BorderLayout.NORTH);

        // Game board
        cp.add(board, BorderLayout.CENTER);

        // Pause/resume buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnPauseResume);
        cp.add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners for buttons
        btnNewGame.addActionListener(e -> newGameToWelcomeScreen());
        btnPauseResume.addActionListener(e -> togglePauseResume());

        // Set up the game
        board.newGame(difficulty);
        startTimer();

        setTitle("Sudoku - Player: " + username);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }


    // Reset game
    private void resetGame(int difficulty) {
        stopTimer();
        score = 10000;
        secondsElapsed = 0;
        board.newGame(difficulty);
        progressBar.setValue(0);
        startTimer();
    }

    // Start the game timer
    private void startTimer() {
        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isPaused) {
                    secondsElapsed++;
                    score = Math.max(0, 10000 - secondsElapsed);
                    board.updateTime(secondsElapsed);
                    board.updateScore(score);
                    progressBar.setValue(countSolvedCells());
                }
            }
        }, 0, 1000);
    }

    // Stop the game timer
    private void stopTimer() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }
    }

    // Pause or resume the timer
    private void togglePauseResume() {
        isPaused = !isPaused;
        btnPauseResume.setText(isPaused ? "Resume" : "Pause");
    }

    // Save high score to file
    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE))) {
            writer.write(username + "," + score);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving high score: " + e.getMessage());
        }
    }

    // Load high score from file
    private void loadHighScore() {
        File file = new File(HIGHSCORE_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String[] data = reader.readLine().split(",");
                highScoreUser = data[0];
                highScore = Integer.parseInt(data[1]);
            } catch (IOException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error loading high score: " + e.getMessage());
            }
        }
    }

    // Check if the current score is a new high score
    private void checkHighScore() {
        if (score > highScore) {
            highScore = score;
            highScoreUser = username;
            highScoreLabel.setText("High Score: " + highScoreUser + " - " + highScore);
            saveHighScore();
            JOptionPane.showMessageDialog(this, "New High Score!");
        }
    }

    private int countSolvedCells() {
        int solved = 0;
        for (int row = 0; row < SudokuConstants.GRID_SIZE; row++) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; col++) {
                if (board.getCell(row, col).status == CellStatus.CORRECT_GUESS || board.getCell(row, col).status==CellStatus.GIVEN ) {
                    solved++;
                }
            }
        }
        return solved;
    }

    private void newGameToWelcomeScreen() {
        // Stop the timer and dispose of the current game window
        stopTimer();
        dispose(); // Close the Sudoku game window

        // Launch the welcome screen
        SwingUtilities.invokeLater(WelcomeScreen::new);
    }

    // Call this when the puzzle is solved
    public void gameWon() {
        stopTimer();
        checkHighScore();
        JOptionPane.showMessageDialog(this, "Congratulations! You solved the puzzle in " + secondsElapsed + " seconds with a score of " + score + "!");
    }

}
