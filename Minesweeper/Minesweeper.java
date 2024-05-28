import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Write a description of class Minesweeper here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Minesweeper
{
    private class MineTile extends JButton {
        int rows;
        int cols;
        
        public MineTile(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
        }
    }
    // Number variables
    int tileSize = 80; // The size of each tile is in pixels
    int rows = 8;
    int cols = 8;
    int boardWidth = cols * tileSize; // Dynamically changes the size of the screen to the correct amount using the tileSize
    int boardHeight = rows * tileSize;
    
    // Arrays
    MineTile[][] cells = new MineTile[rows][cols];
    ArrayList<MineTile> mines;
    
    // J variables
    JFrame frame = new JFrame("Joa's Minesweeper"); // Creating the frame
    JLabel title = new JLabel();
    JPanel titlePanel = new JPanel();
    JPanel cellsPanel = new JPanel();
    
    public Minesweeper()
    {
        setupBoard();
        createCells();
        placeMines();
        
        frame.setVisible(true); // Setting the frame to be visible only after everything has been drawn and created       
    }
    
    void setupBoard() { // This function runs all the code needed to setup the frame and boards
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null); // Resets location to the center of the screen
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Will shutdown the program when you close the window
        frame.setLayout(new BorderLayout());
        
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setText("Joa's Minesweeper");
        title.setOpaque(true);
        
        titlePanel.setLayout(new BorderLayout());
        titlePanel.add(title);

        frame.add(titlePanel, BorderLayout.NORTH); // Set the title to cling to the top of the frame
    }
    
    void createCells() {
        cellsPanel.setLayout(new GridLayout(rows, cols)); // This sets the size of the grid to however many cols and rows there are
                
        for (int r = 0; r < rows; r++) { // Adding tiles to the board depending on the size of the board
            for (int c = 0; c < cols; c++) {
                MineTile cell = new MineTile(r, c); // Actually creating the new tile
                cells[r][c] = cell; // Adding the newly created tile to the board array
                
                cell.setFocusable(false);
                cell.setMargin(new Insets(0, 0, 0, 0));
                cell.setFont(new Font("Arial", Font.PLAIN, 40));
                //cell.setText("Test");
                cellsPanel.add(cell);
            }
        }
        
        frame.add(cellsPanel);
    }
    
    void placeMines() {
        mines = new ArrayList<MineTile>(); // Making the mines array a new array of tiles
        
        debugMines();
    }
    
    void debugMines() { // Hard coded mines for debug perposes
        mines.add(cells[0][0]);
        mines.add(cells[1][1]);
        mines.add(cells[2][2]);
        mines.add(cells[3][3]);
        mines.add(cells[4][4]);
    }
}
