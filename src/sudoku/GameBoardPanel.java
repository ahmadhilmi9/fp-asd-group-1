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

    private String getPossibleNumbers(int row, int col) {
        StringBuilder possibleNumbers = new StringBuilder();
        for (int num = 1; num <= SudokuConstants.GRID_SIZE; num++) {
            if (!isConflict(row, col, num)) {
                possibleNumbers.append(num).append(" ");
            }
        }
        return possibleNumbers.length() > 0 ? possibleNumbers.toString() : "None";
    }


    /** Suggest a logical move based on the current game state */
    private Cell getSuggestedMove() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; row++) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; col++) {
                if (cells[row][col].status == CellStatus.TO_GUESS) {
                    int correctNumber = puzzle.numbers[row][col];
                    if (!isConflict(row, col, correctNumber)) {
                        return cells[row][col];
                    }
                }
            }
        }
        return null; // No suggested moves
    }

    /** Show a hint */
    private void showHint() {
        int selectedRow = -1, selectedCol = -1;
        // Locate the first editable cell that is still empty
        outer:
        for (int row = 0; row < SudokuConstants.GRID_SIZE; row++) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; col++) {
                if (cells[row][col].status == CellStatus.TO_GUESS) {
                    selectedRow = row;
                    selectedCol = col;
                    break outer;
                }
            }
        }

        if (selectedRow == -1 || selectedCol == -1) {
            strategyArea.setText("No hints available. Puzzle might be solved!");
            return;
        }

        // Generate hints
        StringBuilder hintMessage = new StringBuilder("Hints:\n");
        hintMessage.append("- Possible numbers for cell (")
                .append(selectedRow + 1).append(", ").append(selectedCol + 1).append("): ")
                .append(getPossibleNumbers(selectedRow, selectedCol)).append("\n");



        Cell suggestedMove = getSuggestedMove();
        if (suggestedMove != null) {
            hintMessage.append("- Suggested move: Place ")
                    .append(puzzle.numbers[suggestedMove.row][suggestedMove.col])
                    .append(" in cell (").append(suggestedMove.row + 1).append(", ")
                    .append(suggestedMove.col + 1).append(").\n");
        }

        strategyArea.setText(hintMessage.toString());
    }

    /** Update the time display */
    public void updateTime(int secondsElapsed) {
        timeLabel.setText("Time: " + secondsElapsed + "s");
    }

    /** Update the score display */
    public void updateScore(int score) {

        scoreLabel.setText("Score: " + score);
    }

    /**
     * Return true if the puzzle is solved
     * i.e., none of the cells have status of TO_GUESS or WRONG_GUESS
     */
    public boolean isSolved() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
                    return false;
                }
            }
        }

        // Notify the parent Sudoku frame that the game is won
        Sudoku parentFrame = (Sudoku) SwingUtilities.getWindowAncestor(this);
        parentFrame.gameWon();
        return true;
    }

    private void highlightSameValue(int number, int currentRow, int currentCol) {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; row++) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; col++) {
                // Skip the current cell
                if (row == currentRow && col == currentCol) {
                    continue;
                }

                // Highlight cells with the same value that are GIVEN or CORRECT_GUESS
                if (cells[row][col].number == number &&
                        (cells[row][col].status == CellStatus.GIVEN || cells[row][col].status == CellStatus.CORRECT_GUESS)) {
                    cells[row][col].setBackground(Color.LIGHT_GRAY); // Highlight matching cells
                } else {
                    cells[row][col].paint(); // Reset non-matching cells
                }
            }
        }
    }

    private void resetHighlights() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; row++) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; col++) {
                cells[row][col].paint(); // Reset cell background to its default
            }
        }
    }

    // Inside GameBoardPanel class

    // Get a specific cell for checking in Sudoku class
    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    // Modify your existing game logic to reflect the progress bar and time updates
    // Jika Anda memiliki metode updateTime sebelumnya, ubah namanya
    public void updateStatusBar(String message) {
        statusBar.setText(message);
    }


    private boolean isConflict(int row, int col, int number) {
        resetHighlights();

        boolean conflict = false;

        for (int i = 0; i < SudokuConstants.GRID_SIZE; i++) {
            if (i != col && cells[row][i].number == number &&
                    (cells[row][i].status == CellStatus.GIVEN || cells[row][i].status == CellStatus.CORRECT_GUESS)) {
                cells[row][i].setBackground(Color.RED);
                cells[row][col].setBackground(Color.RED);
                conflict = true;
            }
        }

        for (int i = 0; i < SudokuConstants.GRID_SIZE; i++) {
            if (i != row && cells[i][col].number == number &&
                    (cells[i][col].status == CellStatus.GIVEN || cells[i][col].status == CellStatus.CORRECT_GUESS)) {
                cells[i][col].setBackground(Color.RED);
                cells[row][col].setBackground(Color.RED);
                conflict = true;
            }
        }

        int subgridStartRow = (row / SudokuConstants.SUBGRID_SIZE) * SudokuConstants.SUBGRID_SIZE;
        int subgridStartCol = (col / SudokuConstants.SUBGRID_SIZE) * SudokuConstants.SUBGRID_SIZE;

        for (int r = subgridStartRow; r < subgridStartRow + SudokuConstants.SUBGRID_SIZE; r++) {
            for (int c = subgridStartCol; c < subgridStartCol + SudokuConstants.SUBGRID_SIZE; c++) {
                if ((r != row || c != col) && cells[r][c].number == number &&
                        (cells[r][c].status == CellStatus.GIVEN || cells[r][c].status == CellStatus.CORRECT_GUESS)) {
                    cells[r][c].setBackground(Color.RED);
                    cells[row][col].setBackground(Color.RED);
                    conflict = true;
                }
            }
        }

        return conflict;
    }
    private void toggleMode() {
        isDarkMode = !isDarkMode;
        // Switch mode for each cell
        for (int row = 0; row < SudokuConstants.GRID_SIZE; row++) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; col++) {
                cells[row][col].setDarkMode(isDarkMode);
            }
        }
        repaint(); // Repaint the entire panel
    }

    // Listener for cell input
    private class CellInputListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            Cell sourceCell = (Cell) e.getSource();

            char keyChar = e.getKeyChar();
            if (keyChar < '1' || keyChar > '9') {
                e.consume();
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number between 1 and 9.");
                return;
            }

            sourceCell.setText(""); // Clear current cell value

            int numberIn = Character.getNumericValue(keyChar);

            // Cek apakah ada konflik
            if (isConflict(sourceCell.row, sourceCell.col, numberIn)) {
                sourceCell.status = CellStatus.WRONG_GUESS;
                sourceCell.paint();
            } else {
                // Update angka di cell dan status jika benar
                sourceCell.number = numberIn;
                sourceCell.setText(String.valueOf(numberIn));

                if (sourceCell.number == puzzle.numbers[sourceCell.row][sourceCell.col]) {
                    sourceCell.status = CellStatus.CORRECT_GUESS;
                    sourceCell.paint();

                    // Perbarui status bar dengan mengurangi remaining cells jika angka benar
                    updateStatusBar();
                } else {
                    sourceCell.status = CellStatus.WRONG_GUESS;
                    sourceCell.paint();
                }

                // Highlight angka yang sama
                highlightSameValue(numberIn, sourceCell.row, sourceCell.col);

                // Cek apakah puzzle sudah selesai
                if (isSolved()) {
                    JOptionPane.showMessageDialog(null, "Congratulations again!");
                }
            }
            e.consume();  // Konsumsi event sehingga tidak diproses lebih lanjut
        }
        @Override
        public void keyPressed(KeyEvent e) {}
        @Override
        public void keyReleased(KeyEvent e) {}
    }
}
