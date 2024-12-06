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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Sudoku number puzzle to be solved.
 */
public class Puzzle {
    int[][] numbers = new int[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    boolean[][] isGiven = new boolean[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];

    public Puzzle() {}

    // Generate a new puzzle with randomized numbers
    public void newPuzzle(int difficulty) {
        // Step 1: Generate a fully solved Sudoku grid
        generateSolvedGrid();

        // Step 2: Remove cells based on the difficulty level
        removeCellsForPuzzle(difficulty);
    }

    // Step 1: Generate a fully solved Sudoku grid using backtracking
    private void generateSolvedGrid() {
        // Create a list of numbers to populate cells (1-9)
        List<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= SudokuConstants.GRID_SIZE; i++) {
            nums.add(i);
        }

        // Fill the grid using backtracking
        fillGrid(0, 0, nums);
    }

    // Backtracking algorithm to fill the Sudoku grid
    private boolean fillGrid(int row, int col, List<Integer> nums) {
        if (row == SudokuConstants.GRID_SIZE) {
            return true; // Completed the grid
        }

        int nextRow = (col == SudokuConstants.GRID_SIZE - 1) ? row + 1 : row;
        int nextCol = (col == SudokuConstants.GRID_SIZE - 1) ? 0 : col + 1;

        // Shuffle numbers to ensure randomness
        Collections.shuffle(nums);

        for (int num : nums) {
            if (isValidPlacement(row, col, num)) {
                numbers[row][col] = num;
                if (fillGrid(nextRow, nextCol, nums)) {
                    return true;
                }
                numbers[row][col] = 0; // Backtrack
            }
        }

        return false;
    }

    // Check if placing a number is valid according to Sudoku rules
    private boolean isValidPlacement(int row, int col, int num) {
        // Check the row
        for (int c = 0; c < SudokuConstants.GRID_SIZE; c++) {
            if (numbers[row][c] == num) {
                return false;
            }
        }

        // Check the column
        for (int r = 0; r < SudokuConstants.GRID_SIZE; r++) {
            if (numbers[r][col] == num) {
                return false;
            }
        }

        // Check the 3x3 subgrid
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if (numbers[r][c] == num) {
                    return false;
                }
            }
        }

        return true; // Valid placement
    }
    // Step 2: Remove cells for the puzzle based on difficulty
    private void removeCellsForPuzzle(int difficulty) {
        // Calculate the number of cells to leave as "Given"
        int cellsToLeave;
        switch (difficulty) {
            case 1: // Easy
                cellsToLeave = 79; // 40 cells remain
                break;
            case 2: // Medium
                cellsToLeave = 30; // 30 cells remain
                break;
            case 3: // Hard
                cellsToLeave = 20; // 20 cells remain
                break;
            default: // Default to Easy
                cellsToLeave = 40;
        }

        // Create a list of all cell positions
        List<int[]> allCells = new ArrayList<>();
        for (int row = 0; row < SudokuConstants.GRID_SIZE; row++) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; col++) {
                allCells.add(new int[]{row, col});
            }
        }

        // Shuffle the cell positions
        Collections.shuffle(allCells);

        // Mark cells as "Given" or to be guessed
        boolean[][] tempIsGiven = new boolean[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
        for (int i = 0; i < cellsToLeave; i++) {
            int[] cell = allCells.get(i);
            tempIsGiven[cell[0]][cell[1]] = true; // Mark the cell as given
        }

        // Update the isGiven array to reflect which cells are shown
        isGiven = tempIsGiven;
    }
}
