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
    private class GridTile extends JButton { // Creating the GridTile variables. It extends JButton which allows me to use everything within that group
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
    int elapsedTime = 0;
    int fontSize = 16;
    
    // Timer
    Timer timer;
    
    // Booleans
    boolean gameHasEnded = false;       
    boolean firstClick = true;
    boolean menuShowed = true;
    boolean minesPlaced = false;
    
    // Randoms
    Random rand = new Random();
    
    // Arrays
    GridTile[][] cells;
    ArrayList<GridTile> mines;
    String[] difficulties = new String[] {"Easy", "Medium", "Hard"};
    ArrayList<GridTile> forbiddenCells;
    
    // GridTiles
    GridTile firstClickCell = null;
    
    // J variables
    JFrame frame = new JFrame(); // Creating the frame
    JLabel title = new JLabel();
    JPanel titlePanel = new JPanel();
    JPanel cellsPanel = new JPanel();
    JComboBox<String> difficultiesDropdown = new JComboBox<>(difficulties);
    JButton restartButton = new JButton("Restart");
    
    // Strings
    String selectedDifficulties = (String) difficultiesDropdown.getSelectedItem();
    
    public Minesweeper() // The main method
    {
        gameHasEnded = false;
        
        createFont();
        setupBoard();
        difficultyPanel();
        
        timer = new Timer(1000, new ActionListener() { // Creating a timer that ticks every second and runs displays the info
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime++;
                displayInfo();
                System.out.println("Elapsed time: " + elapsedTime);
            }
        });
            
        frame.setVisible(true); // Setting the frame to be visible only after everything has been drawn and created
        
        System.out.println("Game setup");
    }
    
    void restartButton() {
        restartButton.setFocusable(false);
        
        restartButton.setBorderPainted(true);
        restartButton.setContentAreaFilled(true);
        
        restartButton.setForeground(new Color(255, 255, 255));
        restartButton.setBackground(new Color(162,209,73));
        restartButton.setOpaque(true);

        restartButton.setFont(new Font("comfortaa", Font.PLAIN, fontSize));
        
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        titlePanel.add(restartButton, BorderLayout.EAST);
    }
    
    void reset() {
        frame.dispose();
        new Minesweeper();
    }
    
    void beginGame(){ // The function that starts the game
        frame.setVisible(false);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        minesPlaced = false;
        titlePanel.remove(difficultiesDropdown); // Disabling the ability to change the difficulty after the game has started
        title.setText("Select a cell to start:");
        createCells();
        frame.setVisible(true);
    }
    
    void difficultyPanel() { // The code for the difficulty panel
        difficultiesDropdown.setEditable(false);
        difficultiesDropdown.setForeground(new Color(255,255,255));
        difficultiesDropdown.setBackground(new Color(162,209,73));
        difficultiesDropdown.setFont(new Font("comfortaa", Font.BOLD, 15));
        difficultiesDropdown.addActionListener(new ActionListener() {
         
            @Override
            public void actionPerformed(ActionEvent event) {
                JComboBox<String> comboBox = (JComboBox<String>) event.getSource();
                String selected = (String) comboBox.getSelectedItem();
         
                if (selected.equals("Easy")) { // Checking what difficulty it is and changing the size of the map depending on it
                    tileSize = 80;
                    gridRows = 10;
                    gridCols = 10;
                    boardSize = gridRows * gridCols;
                    boardWidth = gridCols * tileSize;
                    boardHeight = gridRows * tileSize;
                    cellsClicked = 0;
                    flagsLeft = 0;
                    mineCount = boardSize / 8;
                    cells = new GridTile[gridRows][gridCols];
                    fontSize = 40;
                    System.out.println("Set to easy!");
                } else if (selected.equals("Medium")) {
                    tileSize = 60;
                    gridRows = 15;
                    gridCols = 15;
                    boardSize = gridRows * gridCols;
                    boardWidth = gridCols * tileSize;
                    boardHeight = gridRows * tileSize;
                    cellsClicked = 0;
                    flagsLeft = 0;
                    mineCount = boardSize / 6;
                    cells = new GridTile[gridRows][gridCols];
                    fontSize = 30;
                    System.out.println("Set to medium!");
                } else {
                    tileSize = 50;
                    gridRows = 20;
                    gridCols = 20;
                    boardSize = gridRows * gridCols;
                    boardWidth = gridCols * tileSize;
                    boardHeight = gridRows * tileSize;
                    cellsClicked = 0;
                    flagsLeft = 0;
                    mineCount = boardSize / 4;
                    cells = new GridTile[gridRows][gridCols];
                    fontSize = 20;
                    System.out.println("Set to hard!"); 
                }
                
                beginGame();
            }
        });
        
        titlePanel.add(difficultiesDropdown, BorderLayout.EAST);
    }

    void createFont() {
        try {
            // Creates a new plain font using the file path given, the font size is changeable
            Font comfortaaFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/comfortaa.ttf")).deriveFont(16f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            // Registering the font to the graphics enviroment
            ge.registerFont(comfortaaFont);
        } catch (IOException e) {
            e.printStackTrace();
        } catch(FontFormatException e) {
            e.printStackTrace();
        }
        System.out.println("Font created");
    }
    
    void setupBoard() { // This function runs all the code needed to setup the frame and boards
        frame.setSize(500, 80);
        frame.setLocationRelativeTo(null); // Resets location to the center of the screen
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Will shutdown the program when you close the window
        frame.setLayout(new BorderLayout());
        
        title.setFont(new Font("comfortaa", Font.BOLD, 30));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setText("Pick your difficulty:");
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
                cell.setFont(new Font("comfortaa", Font.PLAIN, fontSize));
                cell.setText("");
                
                cell.addMouseListener(new MouseAdapter() { // Adding a mouse listener
                   @Override // This overrides the default event
                   public void mousePressed(MouseEvent e) { // Created an event for when a mouse button is pressed
                       if (gameHasEnded) {
                           return;
                       }
                       titlePanel.remove(difficultiesDropdown);
                       GridTile cell = (GridTile) e.getSource(); // Gets the button that was pressed
                       
                        if (e.getButton() == MouseEvent.BUTTON1) { // Button 1 is left click, button 2 is middle click, and button 3 is right click
                            System.out.println("Left button clicked");
                            timer.start();
                            if (firstClickCell == null) {
                                firstClickCell = cell;
                                placeMines();
                            }
                            
                            if (cell.getText() == "") { // This will check if the cell clicked doesn't have anything displayed, and if it is a bomb
                                if (mines.contains(cell)) {
                                    playSound("sounds/explosion.wav");
                                    displayGrid(); // If it is a bomb, the game will end, this runs the function to reveal all the mines
                                    timer.stop();
                                }
                                else {
                                    if (cell.isEnabled()) {
                                        playSound("sounds/click.wav");
                                    }
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
                        
                        if (!gameHasEnded && minesPlaced) {
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
    
    private ArrayList<GridTile> getNeighbors(int row, int col) { // Same logic as the checkMine function but only runs once and returns all the neighbors
        ArrayList<GridTile> neighbors = new ArrayList<>();
        for (int r = row-1; r <= row+1; r++) {
            for (int c = col-1; c <= col+1; c++) {
                if (r >= 0 && r < gridRows && c >= 0 && c < gridCols) {
                    if (!(r == row && c == col)) {
                        neighbors.add(cells[r][c]);
                    }
                }
            }
        }
        return neighbors;
    }
    
    void placeMines() {
        mines = new ArrayList<GridTile>(); // Making the mines array a new array of tiles
        
        //debugMines();
        int minesToPlace = mineCount;
        forbiddenCells = getNeighbors(firstClickCell.rows, firstClickCell.cols); // Make the neighbors forbidden
        forbiddenCells.add(firstClickCell); // Make sure the first clicked cell is also forbidden
        
        while (minesToPlace > 0) {
            int rowPlace = rand.nextInt(gridRows);
            int colPlace = rand.nextInt(gridCols);
            
            GridTile cell = cells[rowPlace][colPlace];
            
            if (!forbiddenCells.contains(cell) &&!mines.contains(cell)) { // Only places a mine if there isn't already a mine and it isn't a forbidden cell
                mines.add(cell);
                minesToPlace--;
            }
        }
        
        for (int i = 0; i < mines.size(); i++) { // Looping through all the mines that don't have anything displayed, and revealing them
            GridTile cell = mines.get(i);
            System.out.println("Mine at" + cell.rows + ":" + cell.cols); // Displays where all the bombs are
        }
        
        flagsLeft = mines.size();
        
        minesPlaced = true;
        
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
        title.setText("Game Over! (" + elapsedTime + "s)");
        
        restartButton();
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
        
        if (cellsClicked == gridRows * gridCols - mines.size()) { // Checks if all the cells have been cleared
            gameHasEnded = true;
            
            title.setFont(new Font("comfortaa", Font.BOLD, 30));
            title.setText("You found all the mines! (" + elapsedTime + "s)");
            playSound("sounds/win.wav");
            timer.stop();
            restartButton();
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
    
    void displayInfo() { // Displays information about the game such as the # of cleared cells and how many flags the player has left
        int numberOfCells = gridCols * gridRows - mines.size();
        
        title.setFont(new Font("comfortaa", Font.BOLD, 20));
        title.setText("Cells cleared: " + cellsClicked + " / " + numberOfCells + " | " + "Flags left: " + flagsLeft + " | " + "Elapsed Time: " + elapsedTime);
        
        //System.out.println("Cells cleared: " + cellsClicked + " / " + numberOfCells);
        //System.out.println("# of mines: " + mines.size());
    }
    
    public void playSound(String filePath) { // The playSound function, this can be run from anywhere with any file path
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
