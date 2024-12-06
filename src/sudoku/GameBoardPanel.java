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
import java.awt.event.*;
import javax.swing.border.Border;


public class GameBoardPanel extends JPanel {
    private static final long serialVersionUID = 1L; // to prevent serial warning

    // Define named constants for UI sizes
    public static final int CELL_SIZE = 60; // Cell width/height in pixels
    public static final int BOARD_WIDTH = CELL_SIZE * SudokuConstants.GRID_SIZE;
    public static final int BOARD_HEIGHT = CELL_SIZE * SudokuConstants.GRID_SIZE;
    public static final int SIDE_PANEL_WIDTH = 200; // Width of the side panel

    // Define properties
    private Cell[][] cells = new Cell[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    private Puzzle puzzle = new Puzzle();

    // Side panel components
    private JLabel timeLabel;
    private JLabel scoreLabel;
    private JTextArea strategyArea;
    private JButton resetButton, hintButton, toggleSoundButton, toggleModeButton;

    private boolean isDarkMode = true;

    private final SoundManager soundManager;

    private JTextField statusBar;  // Status bar
    private JButton cheatButton;
    /** Constructor */
    public GameBoardPanel(SoundManager soundManager) {
        super.setLayout(new BorderLayout()); // Main layout

        // Create the main grid panel
        JPanel gridPanel = createGridPanel();

        // Create the side panel
        JPanel sidePanel = createSidePanel();
        //status
        statusBar = new JTextField("Game Started");
        statusBar.setEditable(false);// Set it to non-editable
        statusBar.setHorizontalAlignment(JTextField.CENTER);
        statusBar.setBackground(Color.BLACK);
        statusBar.setForeground(Color.WHITE);

        // Add the grid panel and the side panel to the main layout
        this.add(gridPanel, BorderLayout.CENTER);
        this.add(sidePanel, BorderLayout.EAST);
        this.add(statusBar, BorderLayout.SOUTH);

        // Set focus on the first editable cell
        setInitialFocus();

        // Hide mouse cursor only for the grid panel
        hideMouseCursor(gridPanel);

        this.soundManager = soundManager;
        SoundManager.playBackgroundMusic("day_stage.wav");
    }

    // Update the hideMouseCursor method to accept a specific panel
    private void hideMouseCursor(JPanel gridPanel) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.createImage("");
        Point hotSpot = new Point(0, 0);
        Cursor invisibleCursor = toolkit.createCustomCursor(image, hotSpot, "InvisibleCursor");

        // Apply the invisible cursor to all cells in the grid panel
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col].setCursor(invisibleCursor);
            }
        }

        // Set the cursor for the grid panel itself
        gridPanel.setCursor(invisibleCursor);
    }

    // Method to show the cheat dialog and highlight cells
    private void showCheatDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter a number (1-9) to highlight:", "Cheat", JOptionPane.PLAIN_MESSAGE);
        if (input != null && !input.isEmpty()) {
            try {
                int number = Integer.parseInt(input);
                if (number >= 1 && number <= 9) {
                    highlightCells(number);
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number between 1 and 9.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to highlight cells containing the specified number
    private void highlightCells(int number) {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; row++) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; col++) {
                if (cells[row][col].number == number) {
                    cells[row][col].highlight();
                } else {
                    cells[row][col].resetHighlight();
                }
            }
        }
    }

    private void hideMouseCursor() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.createImage("");
        Point hotSpot = new Point(0, 0);
        Cursor invisibleCursor = toolkit.createCustomCursor(image, hotSpot, "InvisibleCursor");
        setCursor(invisibleCursor);

        // Apply the invisible cursor to all cells
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col].setCursor(invisibleCursor);
            }
        }
    }

    private void setInitialFocus() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].isEditable()) {
                    cells[row][col].requestFocusInWindow();
                    return;
                }
            }
        }
    }

    /** Create the main Sudoku grid panel */
    private JPanel createGridPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(SudokuConstants.GRID_SIZE, SudokuConstants.GRID_SIZE));
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col] = new Cell(row, col, this);
                gridPanel.add(cells[row][col]);
            }
        }
        gridPanel.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        // Attach listeners to editable cells
        for (int row = 0; row < SudokuConstants.GRID_SIZE; row++) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; col++) {
                if (cells[row][col].isEditable()) {
                    cells[row][col].addKeyListener(new CellInputListener());
                }
            }
        }

        return gridPanel;
    }

    // Fungsi untuk memperbarui status bar dengan jumlah sel yang tersisa
    private void updateStatusBar() {
        int remainingCells = 0;
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                // Hanya menghitung sel yang masih perlu diisi
                if (cells[row][col].status == CellStatus.TO_GUESS) {
                    remainingCells++;
                }
            }
        }
        // Update status bar dengan jumlah sel yang tersisa
        statusBar.setText("Cells remaining: " + remainingCells);
    }

    /** Create the side panel with commands, display, and strategy */
    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(SIDE_PANEL_WIDTH, BOARD_HEIGHT));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createTitledBorder("Controls & Info"));

        // Time display
        timeLabel = new JLabel("Time: 0s");
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(timeLabel);

        // Score display
        scoreLabel = new JLabel("Score: 10000");
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(scoreLabel);

        toggleModeButton = new JButton("Theme");
        toggleModeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggleModeButton.addActionListener(e -> toggleMode());
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(toggleModeButton);

        // Add the cheat button
        cheatButton = new JButton("Cheat");
        cheatButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cheatButton.addActionListener(e -> showCheatDialog());
        sidePanel.add(Box.createVerticalStrut(10)); // Spacer
        sidePanel.add(cheatButton);

        // Hint button
        hintButton = new JButton("Get Hint");
        hintButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintButton.addActionListener(e -> showHint());
        sidePanel.add(Box.createVerticalStrut(10)); // Spacer
        sidePanel.add(hintButton);

        // Strategy display
        strategyArea = new JTextArea(8, 15);
        strategyArea.setEditable(false);
        strategyArea.setLineWrap(true);
        strategyArea.setWrapStyleWord(true);
        strategyArea.setText("Strategy: Focus on rows, columns, and subgrids.");
        JScrollPane scrollPane = new JScrollPane(strategyArea);
        sidePanel.add(Box.createVerticalStrut(20)); // Spacer
        sidePanel.add(scrollPane);


        return sidePanel;
    }

    /** Generate a new puzzle and reset the game board */
    public void newGame(int difficulty) {
        puzzle.newPuzzle(difficulty);

        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col].newGame(puzzle.numbers[row][col], puzzle.isGiven[row][col]);
            }
        }

        // Set focus on the first editable cell
        setInitialFocus();
    }

    /** Reset the game */
    private void resetGame() {
        JOptionPane.showMessageDialog(this, "Game reset! Starting fresh...");
        newGame(1); // Default to easy difficulty
    }
}
