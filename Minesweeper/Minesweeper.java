import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;
import javafx.scene.layout.Border;
import java.io.IOException;
import java.io.File;

/**
 * A version of Minesweeper coded in Java using BlueJ.
 *
 * Joa
 * v1.8
 */
public class Minesweeper
{
    private class GridTile extends JButton {
        int rows;
        int cols;
        
        public GridTile(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
        }
    }
    // Number variables
    int tileSize = 50; // The size of each tile is in pixels
    int rows = 15;
    int cols = 15;
    int boardWidth = cols * tileSize; // Dynamically changes the size of the screen to the correct amount using the tileSize
    int boardHeight = rows * tileSize;
    
    // Arrays
    GridTile[][] cells = new GridTile[rows][cols];
    ArrayList<GridTile> mines;
    
    // J variables
    JFrame frame = new JFrame(); // Creating the frame
    JLabel title = new JLabel();
    JPanel titlePanel = new JPanel();
    JPanel cellsPanel = new JPanel();
    
    public Minesweeper()
    {
        createFont();
        setupBoard();
        createCells();
        placeMines();
        
        frame.setVisible(true); // Setting the frame to be visible only after everything has been drawn and created
        
        System.out.println("Game setup");
    }
    
    void createFont() {
        try {
            //create the font to use. Specify the size!
            Font comfortaaFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/comfortaa.ttf")).deriveFont(16f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            //register the font
            ge.registerFont(comfortaaFont);
        } catch (IOException e) {
            e.printStackTrace();
        } catch(FontFormatException e) {
            e.printStackTrace();
        }
        System.out.println("Font created");
    }
    
    void setupBoard() { // This function runs all the code needed to setup the frame and boards
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null); // Resets location to the center of the screen
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Will shutdown the program when you close the window
        frame.setLayout(new BorderLayout());
        
        title.setFont(new Font("comfortaa", Font.BOLD, 30));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setText("Joa's Minesweeper");
        title.setOpaque(true);
        
        titlePanel.setLayout(new BorderLayout());
        titlePanel.add(title);

        frame.add(titlePanel, BorderLayout.NORTH); // Set the title to cling to the top of the frame
        
        System.out.println("Board setup");
    }
    
    void createCells() {
        cellsPanel.setLayout(new GridLayout(rows, cols)); // This sets the size of the grid to however many cols and rows there are
                
        for (int r = 0; r < rows; r++) { // Adding tiles to the board depending on the size of the board
            for (int c = 0; c < cols; c++) {
                GridTile cell = new GridTile(r, c); // Actually creating the new tile
                cells[r][c] = cell; // Adding the newly created tile to the board array
                
                cell.setFocusable(false);
                
                //Change the style of the buttons
                cell.setBorderPainted(true);
                cell.setContentAreaFilled(true);
                
                cell.setForeground(new Color(237, 237, 237));
                cell.setBackground(new Color(162,209,73));
                cell.setOpaque(true);

                // Button text
                cell.setMargin(new Insets(0, 0, 0, 0));
                cell.setFont(new Font("comfortaa", Font.PLAIN, 40));
                cell.setText("");
                
                cell.addMouseListener(new MouseAdapter() { // Adding a mouse listener
                   @Override // This overrides the default event
                   public void mousePressed(MouseEvent e) { // Created an event for when a mouse button is pressed
                       GridTile cell = (GridTile) e.getSource(); // Gets the button that was pressed
                       
                       if (e.getButton() == MouseEvent.BUTTON1) { // Button 1 is left click, button 2 is middle click, and button 3 is right click
                           if (cell.getText() == "") { // This will check if the cell clicked doesn't have anything displayed, and if it is a bomb
                               if (mines.contains(cell)) {
                                   displayGrid(); // If it is a bomb, the game will end, this runs the function to reveal all the mines
                               }
                           }
                       }
                   }
                });
                cellsPanel.add(cell);
            }
        }
        
        frame.add(cellsPanel);
        
        System.out.println("Grid created");
    }
    
    void placeMines() {
        mines = new ArrayList<GridTile>(); // Making the mines array a new array of tiles
        
        debugMines();
        
        System.out.println("Mines added");
    }
    
    void debugMines() { // Hard coded mines for debug perposes
        mines.add(cells[0][0]);
        mines.add(cells[4][3]);
        mines.add(cells[2][6]);
        mines.add(cells[3][1]);
        mines.add(cells[7][6]);
        
        System.out.println("(Mines have been hard coded):\n0,0\n4,3\n2,6\n3,1\n7,6");
    }
    
    void displayGrid() {
        for (int i = 0; i < mines.size(); i++) { // Looping through all the mines that don't have anything displayed, and revealing them
            GridTile cell = mines.get(i);
            cell.setText("B");
        }
        
        System.out.println("The game has ended");
    }
}
