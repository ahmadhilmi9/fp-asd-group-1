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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;

/**
 * The Cell class models the cells of the Sudoku puzzle, by customizing (subclass)
 * the javax.swing.JTextField to include row/column, puzzle number and status.
 */
public class Cell extends JTextField {
    private static final long serialVersionUID = 1L;  // to prevent serial warning

    // Define named constants for JTextField's colors and fonts
    //  to be chosen based on CellStatus
    public static final Color BG_GIVEN_LIGHT = new Color(240, 240, 240); // RGB
    public static final Color FG_GIVEN_LIGHT = Color.BLACK;
    public static final Color FG_NOT_GIVEN_LIGHT = Color.GRAY;
    public static final Color BG_TO_GUESS_LIGHT  = Color.GRAY;
    public static final Color BG_CORRECT_GUESS_LIGHT = new Color(0, 0, 0);
    public static final Color BG_WRONG_GUESS_LIGHT   = new Color(216, 0, 0);

    public static final Color BG_GIVEN= new Color(40, 40, 40); // Dark gray for Dark mode
    public static final Color FG_GIVEN = Color.GREEN;
    public static final Color BG_TO_GUESS = new Color(80, 80, 80);
    public static final Color BG_CORRECT_GUESS = new Color(0, 0, 0);
    public static final Color BG_WRONG_GUESS = new Color(216, 0, 0);

    public static final Font FONT_NUMBERS = new Font("Chiller", Font.BOLD, 40);

    // Define properties (package-visible)
    /** The row and column number [0-8] of this cell */
    int row, col;
    /** The puzzle number [1-9] for this cell */
    int number;
    /** The status of this cell defined in enum CellStatus */
    CellStatus status;

    private GameBoardPanel gameBoardPanel;

    private boolean isDarkMode = true;
    /** Constructor */
    public Cell(int row, int col, GameBoardPanel gameBoardPanel) {
        super();
        this.row = row;
        this.col = col;
        this.gameBoardPanel = gameBoardPanel;
        super.setHorizontalAlignment(JTextField.CENTER);
        super.setFont(FONT_NUMBERS);

        // Add key listener for navigation
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        moveFocus(row - 1, col);
                        break;
                    case KeyEvent.VK_DOWN:
                        moveFocus(row + 1, col);
                        break;
                    case KeyEvent.VK_LEFT:
                        moveFocus(row, col - 1);
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveFocus(row, col + 1);
                        break;
                }
            }
        });
    }

    // Method to highlight the cell
    public void highlight() {
        setBackground(Color.YELLOW);
    }

    // Method to reset the highlight of the cell
    public void resetHighlight() {
        paint(); // Repaint the cell based on its status
    }

    public void setDarkMode(boolean isDarkMode) {
        this.isDarkMode = isDarkMode;
        paint(); // Repaint the cell based on the new mode
    }
    private void moveFocus(int newRow, int newCol) {
        if (newRow >= 0 && newRow < SudokuConstants.GRID_SIZE && newCol >= 0 && newCol < SudokuConstants.GRID_SIZE) {
            gameBoardPanel.getCell(newRow, newCol).requestFocusInWindow();
        }
    }

    /** Reset this cell for a new game, given the puzzle number and isGiven */
    public void newGame(int number, boolean isGiven) {
        this.number = number;
        status = isGiven ? CellStatus.GIVEN : CellStatus.TO_GUESS;
        paint();    // paint itself
    }

    /** This Cell (JTextField) paints itself based on its status */
    /** This Cell (JTextField) paints itself based on its status */
    /** This Cell (JTextField) paints itself based on its status */
    public void paint() {
        if (status == CellStatus.GIVEN) {
            super.setText(number + "");
            super.setEditable(false); // GIVEN cells are not editable
            if (!isDarkMode) {
                super.setBackground(BG_GIVEN_LIGHT); // Light mode: White background for GIVEN cells
                super.setForeground(FG_GIVEN_LIGHT); // Light mode: Black number
            } else {
                super.setBackground(BG_GIVEN);       // Dark mode background
                super.setForeground(FG_GIVEN);      // Dark mode number
            }
        } else if (status == CellStatus.TO_GUESS) {
            super.setText(""); // Editable cells initially blank
            super.setEditable(true);
            if (!isDarkMode) {
                super.setBackground(BG_TO_GUESS_LIGHT); // Light mode: Yellow background for guessing
                super.setForeground(FG_NOT_GIVEN_LIGHT); // Light mode: Gray number for guessing
            } else {
                super.setBackground(BG_TO_GUESS); // Dark mode background for guessing
                super.setForeground(FG_GIVEN);   // Dark mode number for guessing
            }
        } else if (status == CellStatus.CORRECT_GUESS) {
            if (!isDarkMode) {
                super.setBackground(Color.GREEN); // Light mode: Green background for correct guess
                super.setForeground(Color.BLACK); // Light mode: Black number for correct guess
            } else {
                super.setBackground(BG_CORRECT_GUESS); // Dark mode: Correct guess background
                super.setForeground(FG_GIVEN);        // Dark mode: Correct guess number
            }
        } else if (status == CellStatus.WRONG_GUESS) {
            if (!isDarkMode) {
                super.setBackground(BG_WRONG_GUESS_LIGHT); // Light mode: Red background for wrong guess
                super.setForeground(Color.BLACK);         // Light mode: Black number for wrong guess
            } else {
                super.setBackground(BG_WRONG_GUESS); // Dark mode: Wrong guess background
                super.setForeground(FG_GIVEN);      // Dark mode: Wrong guess number
            }
        }
    }


    /** Update the text of this cell when a number is input (keyboard interaction) */
    public void updateCellValue(int value) {
        if (status == CellStatus.TO_GUESS) {
            this.setText(String.valueOf(value));
            this.number = value; // Update internal value
            status = CellStatus.TO_GUESS; // Ensure it stays as "TO_GUESS"
            paint();  // Repaint to reflect the change
        }
    }
}
