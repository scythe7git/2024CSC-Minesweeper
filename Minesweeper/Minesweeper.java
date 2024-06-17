import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;
import javafx.scene.layout.Border;
import java.io.IOException;
import java.io.File;
import java.util.Random;
import javax.sound.sampled.*;

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
    int tileSize = 80; // The size of each tile is in pixels
    int gridRows = 10;
    int gridCols = 10;
    int boardSize = gridRows * gridCols;
    int boardWidth = gridCols * tileSize; // Dynamically changes the size of the screen to the correct amount using the tileSize
    int boardHeight = gridRows * tileSize;
    int cellsClicked = 0;
    int flagsLeft = 0;
    int mineCount = boardSize / 8;
    
    
    // Booleans
    boolean gameHasEnded = false;       
    boolean firstClick = true;
    
    // Randoms
    Random rand = new Random();
    
    // Arrays
    GridTile[][] cells = new GridTile[gridRows][gridCols];
    ArrayList<GridTile> mines;
    
    // GridTile
    GridTile firstClickCell = null;
    
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
        cellsPanel.setLayout(new GridLayout(gridRows, gridCols)); // This sets the size of the grid to however many cols and rows there are
                
        for (int r = 0; r < gridRows; r++) { // Adding tiles to the board depending on the size of the board
            for (int c = 0; c < gridCols; c++) {
                GridTile cell = new GridTile(r, c); // Actually creating the new tile
                cells[r][c] = cell; // Adding the newly created tile to the board array
                
                cell.setFocusable(false);
                
                //Change the style of the buttons
                cell.setBorderPainted(true);
                cell.setContentAreaFilled(true);
                
                cell.setForeground(new Color(255, 255, 255));
                cell.setBackground(new Color(162,209,73));
                cell.setOpaque(true);

                // Button text
                cell.setMargin(new Insets(0, 0, 0, 0));
                cell.setFont(new Font("comfortaa", Font.PLAIN, 40));
                cell.setText("");
                
                cell.addMouseListener(new MouseAdapter() { // Adding a mouse listener
                   @Override // This overrides the default event
                   public void mousePressed(MouseEvent e) { // Created an event for when a mouse button is pressed
                       if (gameHasEnded) {
                           return;
                       }
                       GridTile cell = (GridTile) e.getSource(); // Gets the button that was pressed
                       
                        if (e.getButton() == MouseEvent.BUTTON1) { // Button 1 is left click, button 2 is middle click, and button 3 is right click
                            System.out.println("Left button clicked");
                            if (firstClickCell == null) {
                                firstClickCell = cell;
                                placeMines();
                            }
                            
                            if (cell.getText() == "") { // This will check if the cell clicked doesn't have anything displayed, and if it is a bomb
                                if (mines.contains(cell)) {
                                    playSound("sounds/explosion.wav");
                                    displayGrid(); // If it is a bomb, the game will end, this runs the function to reveal all the mines
                                }
                                else {
                                    playSound("sounds/click.wav");
                                    checkMine(cell.rows, cell.cols); // For a mine, and how many mines are nearby
                                }
                            }
                        }
                        else if (e.getButton() == MouseEvent.BUTTON3) { // Checking for the right click button
                            System.out.println("Right button clicked");
                            if (cell.getText() == "" && cell.isEnabled() && flagsLeft > 0) { // This if loop toggle changing an empty cell to a flagged cell and back
                                cell.setText("F");
                                flagsLeft--;
                                playSound("sounds/flag.wav");
                            } else if (cell.getText() == "F") {
                                cell.setText("");
                                flagsLeft++;
                                playSound("sounds/flag.wav");
                            }
                        }
                        
                        if (!gameHasEnded) {
                            displayInfo();
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
        
        //debugMines();
        int minesToPlace = mineCount;
        while (minesToPlace > 0) {
            int rowPlace = rand.nextInt(gridRows);
            int colPlace = rand.nextInt(gridCols);
            
            GridTile cell = cells[rowPlace][colPlace];
            
            if (firstClick && !cell.equals(firstClickCell)) {
                mines.add(cell);
                minesToPlace--;
            } else if (!firstClick) {
                if (!mines.contains(cell)) {
                    mines.add(cell);
                    
                    minesToPlace--;
                }
            }
        }
        
        for (int i = 0; i < mines.size(); i++) { // Looping through all the mines that don't have anything displayed, and revealing them
            GridTile cell = mines.get(i);
            System.out.println("Mine at" + cell.rows + ":" + cell.cols);
        }
        
        flagsLeft = mines.size();
        
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
        
        gameHasEnded = true;
        
        title.setFont(new Font("comfortaa", Font.BOLD, 30));
        title.setText("Game Over!");
    }
    
    void checkMine(int rows, int cols) {
        if (rows < 0 || rows >= gridRows || cols < 0 || cols >= gridCols) { // This will cancel checking for mines if the cell is out of bounds. This only matters for chain reactions and will be ignored on the initial click
            return;
        }
        
        GridTile cell = cells[rows][cols];
        if (!cell.isEnabled()) { // This will make sure it only chain reacts through cells that are enabled.
            return;
        }
        cell.setEnabled(false); // Disabled the button so you can't click on it again
        cellsClicked += 1;
        if (cell.getText() == "F") {
            flagsLeft++;
        }
        
        int minesFound = 0;
        
        for (int r = -1; r <= 1; r++) { // Runs the countMine function for all the cells except the cell that was clicekd on.
            for (int c = -1; c <= 1; c++) {
                if (r == 0 && c == 0) {
                    
                } else {
                    //System.out.println("Checking mine at: " + r + ", " + c);
                    minesFound += countMine(rows+r, cols+c);
                }
            }
        }

        if (minesFound > 0) { // If there were mines found then change the colour of the cell and set the cell text to the amount of mines nearby
            cell.setForeground(new Color(255,255,255));
            cell.setBackground(new Color(204, 204, 204));
            
            cell.setText(Integer.toString(minesFound));
        } else { // If there were no mines found then change the colour and have no text
            cell.setText("");
            cell.setForeground(new Color(255,255,255));
            cell.setBackground(new Color(204, 204, 204));
            
            for (int r = -1; r <= 1; r++) { // This will keep chain reacting checking mines.
                for (int c = -1; c <= 1; c++) {
                    if (r == 0 && c == 0) {
                        
                    } else {
                        checkMine(rows+r, cols+c);
                    }
                }
            }
            
        }
        
        if (cellsClicked == gridRows * gridCols - mines.size()) {
            gameHasEnded = true;
            
            title.setFont(new Font("comfortaa", Font.BOLD, 30));
            title.setText("You found all the mines!");
            playSound("sounds/win.wav");
        }
    }
    
    int countMine(int r, int c) { // This returns a 1 or 0 depending if there is a mine at that cell
        if (r < 0 || r >= gridRows || c < 0 || c >= gridCols) { // This will return 0 if the cell is out of bounds
            return 0;
        }
        if (mines.contains(cells[r][c])) { // This will return 1 if the mines array has a mine at the location of the cell
            return 1;
        }
        return 0;
    }
    
    void displayInfo() {
        int numberOfCells = gridCols * gridRows - mines.size();
        
        title.setFont(new Font("comfortaa", Font.BOLD, 20));
        title.setText("Cells cleared: " + cellsClicked + " / " + numberOfCells + " | " + "Flags left: " + flagsLeft);
        
        System.out.println("Cells cleared: " + cellsClicked + " / " + numberOfCells);
        System.out.println("# of mines: " + mines.size());
    }
    
    public void playSound(String filePath) {
        try {
            File soundFile = new File(filePath); // Get the file
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile); // Create audio stream
            
            Clip clip = AudioSystem.getClip(); // Create the clip
            clip.open(audioStream); // Add the audio to the clip
            
            clip.start(); // Play the clip
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) { // Check for IOExpections
            e.printStackTrace();
        }
    }
}
