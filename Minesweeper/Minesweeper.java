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
 * Title: Minesweeper
 * Author: Joa Contreras
 * Date: 20/06/2024
 * Version: FINAL 1
 * Purpose: A Minesweeper game coded in Java and BlueJ as an assesment for the CSC223 class.
 **/

public class Minesweeper
{
    /* This class is the GridTile object which is used throughout the whole project.
     * It extends the JButton method which allows many different mechanics to be used
     * throughout the project such as customisation, setting event handlers, etc.
     */
    private class GridTile extends JButton {
        int rows;
        int cols;
        
        public GridTile(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
        }
    }
    
    // Numericals
    int tileSize = 80; // The size of each cell in pixels
    int gridRows = 10; // The number of row
    int gridCols = 10; // The number of columns
    int boardSize = gridRows * gridCols; // The total size of the board
    int boardWidth = gridCols * tileSize; // The width of the board in pixels
    int boardHeight = gridRows * tileSize; // The height of the board in pixels
    int cellsClicked = 0; // The number of cells cleared
    int flagsLeft = 0; // The number of flags the player has
    int mineCount = boardSize / 8; // The number of mines
    int elapsedTime = 0; // The time the player has been playing
    int fontSize = 16; // The main font size
    
    // Timers
    Timer timer; // The main timer
    
    // Booleans
    boolean gameHasEnded = false; // Whether or not the game has finished
    boolean firstClick = true; // Whether or not it is the initial click
    boolean minesPlaced = false; // Whether or not the mines have been placed
    
    // Randoms
    Random rand = new Random(); // A random which can be used many times
    
    // Arrays
    GridTile[][] cells; // The cells
    ArrayList<GridTile> mines; // The mines
    String[] difficulties = new String[] {"Easy", "Medium", "Hard"}; // The different difficulties
    ArrayList<GridTile> forbiddenCells; // The cells mines cannot be placed on
    
    // GridTiles
    GridTile firstClickCell = null; // The first clicked cell
    
    // GUI
    JFrame frame = new JFrame(); // The main window
    JLabel title = new JLabel(); // The title
    JPanel titlePanel = new JPanel(); // The title panel
    JPanel cellsPanel = new JPanel(); // The cells panel
    JComboBox<String> difficultiesDropdown = new JComboBox<>(difficulties); // The difficulty dropdown
    JButton restartButton = new JButton("Restart"); // The restart button
    
    // Strings
    String selectedDifficulties = (String) difficultiesDropdown.getSelectedItem(); // The different difficulties
    
    /*
     * This is the Main method which is executed when the program first runs.
     * It handles all the logic with starting the game. Everytime the game resets
     * this method is run again and reinitialised many of the mechanics.
     */
    public Minesweeper()
    {
        gameHasEnded = false;
        
        // Running setup methods
        createFont();
        setupBoard();
        difficultyPanel();
        
        // Initialising the timer
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime++;
                displayInfo();
                System.out.println("Elapsed time: " + elapsedTime);
            }
        });

        frame.setVisible(true);
        
        System.out.println("Game setup");
    }
    
    
    /*
     * This method handles the logic for the restart button. It changes
     * the style of the button aswell as adding and event handler which
     * can detect when the button is clicked. The button then resets the
     * game by calling the main Minesweeper method again.
     */
    void restartButton() {
        // Changing the style of the button
        restartButton.setFocusable(false);
        
        restartButton.setBorderPainted(true);
        restartButton.setContentAreaFilled(true);
        
        restartButton.setForeground(new Color(255, 255, 255));
        restartButton.setBackground(new Color(162,209,73));
        restartButton.setOpaque(true);

        restartButton.setFont(new Font("comfortaa", Font.PLAIN, fontSize));
        
        // Adding a click event handler
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        titlePanel.add(restartButton, BorderLayout.EAST);
    }
    
    /*
     * This is the reset function which simply deletes the current GUI
     * and calls the Minesweeper function again which resets the game.
     */
    void reset() {
        frame.dispose();
        new Minesweeper();
    }
    
    /*
     * This method is called after the player chooses the difficulty and
     * creates the level the player will be interacting with.
     */
    void beginGame(){
        // Reseting window
        frame.setVisible(false);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        minesPlaced = false;
        titlePanel.remove(difficultiesDropdown);
        title.setText("Select a cell to start:");
        createCells();
        frame.setVisible(true);
    }
    
    /*
     * This method handles the logic for the difficulty swapper.
     * It allows the player to change the size of the map between
     * three different options. Once the difficulty is chosen the
     * game begins and the ability to change the difficulty is disabled.
     */
    void difficultyPanel() {
        // Styling the dropdown menu
        difficultiesDropdown.setEditable(false);
        difficultiesDropdown.setForeground(new Color(255,255,255));
        difficultiesDropdown.setBackground(new Color(162,209,73));
        difficultiesDropdown.setFont(new Font("comfortaa", Font.BOLD, 15));
        // The click handler logic
        difficultiesDropdown.addActionListener(new ActionListener() {
         
            @Override
            public void actionPerformed(ActionEvent event) {
                JComboBox<String> comboBox = (JComboBox<String>) event.getSource();
                String selected = (String) comboBox.getSelectedItem();
         
                if (selected.equals("Easy")) { // Changing the size of the map depending on the difficulty
                    // Easy difficulty
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
                    // Medium difficulty
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
                    // Hard difficulty
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
                
                beginGame(); // Initialising the game
            }
        });
        
        titlePanel.add(difficultiesDropdown, BorderLayout.EAST);
    }

    /*
     * This method imports a custom font from the computer and
     * enables it to be used by different parts of the code.
     */
    void createFont() {
        try {
            Font comfortaaFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/comfortaa.ttf")).deriveFont(16f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(comfortaaFont);
        } catch (IOException e) {
            e.printStackTrace();
        } catch(FontFormatException e) {
            e.printStackTrace();
        }
        System.out.println("Font created");
    }
    
    /*
     * This method handles all the logic with displaying the window of
     * the game.
     */
    void setupBoard() {
        // Window customisation
        frame.setSize(500, 80);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // Title customisation
        title.setFont(new Font("comfortaa", Font.BOLD, 30));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setText("Pick your difficulty:");
        title.setOpaque(true);
        
        titlePanel.setLayout(new BorderLayout());
        titlePanel.add(title);

        frame.add(titlePanel, BorderLayout.NORTH);
        
        System.out.println("Board setup");
    }
    
    /*
     * This method handles creating the cells and adding them to the
     * board. This has all the logic regarding interacting with
     * the cells and how they look.
     */
    void createCells() {
        cellsPanel.setLayout(new GridLayout(gridRows, gridCols));
                
        for (int r = 0; r < gridRows; r++) { // Looping through a 2D array
            for (int c = 0; c < gridCols; c++) {
                GridTile cell = new GridTile(r, c);
                cells[r][c] = cell;
                // Changing the button style
                cell.setFocusable(false);
                
                cell.setBorderPainted(true);
                cell.setContentAreaFilled(true);
                
                cell.setForeground(new Color(255, 255, 255));
                cell.setBackground(new Color(162,209,73));
                cell.setOpaque(true);

                cell.setMargin(new Insets(0, 0, 0, 0));
                cell.setFont(new Font("comfortaa", Font.PLAIN, fontSize));
                cell.setText("");
                
                // Creating click handlers
                cell.addMouseListener(new MouseAdapter() {
                   @Override
                   public void mousePressed(MouseEvent e) {
                       if (gameHasEnded) {
                           return;
                       }
                       titlePanel.remove(difficultiesDropdown);
                       GridTile cell = (GridTile) e.getSource();
                       
                        if (e.getButton() == MouseEvent.BUTTON1) { // Left click
                            System.out.println("Left button clicked");
                            timer.start();
                            if (firstClickCell == null) {
                                firstClickCell = cell;
                                placeMines();
                            }
                            
                            if (cell.getText() == "") {
                                if (mines.contains(cell)) {
                                    playSound("sounds/explosion.wav");
                                    displayGrid();
                                    timer.stop();
                                }
                                else {
                                    if (cell.isEnabled()) {
                                        playSound("sounds/click.wav");
                                    }
                                    checkMine(cell.rows, cell.cols);
                                }
                            }
                        }
                        else if (e.getButton() == MouseEvent.BUTTON3) { // Right click
                            System.out.println("Right button clicked");
                            if (cell.getText() == "" && cell.isEnabled() && flagsLeft > 0) { // Toggles flags
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
    
    /*
     * This is a simple method which returns the neighbors of any
     * given cell.
     */
    public ArrayList<GridTile> getNeighbors(int row, int col) {
        ArrayList<GridTile> neighbors = new ArrayList<>();
        for (int r = row-1; r <= row+1; r++) { // Looping through a 3x3 area
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
    
    /*
     * This method handles placing the mines in random locations and
     * handling the logic behind the first click, making sure the first
     * click always has a 3x3 empty grid around it.
     */
    void placeMines() {
        mines = new ArrayList<GridTile>();
        
        int minesToPlace = mineCount;
        forbiddenCells = getNeighbors(firstClickCell.rows, firstClickCell.cols);
        forbiddenCells.add(firstClickCell);
        
        while (minesToPlace > 0) { // Changes depending on the amount of mines thanks for the difficulty
            int rowPlace = rand.nextInt(gridRows);
            int colPlace = rand.nextInt(gridCols);
            
            GridTile cell = cells[rowPlace][colPlace];
            
            if (!forbiddenCells.contains(cell) &&!mines.contains(cell)) { // Disables placing mines in the 3x3 area of the initial click
                mines.add(cell);
                minesToPlace--;
            }
        }
        
        for (int i = 0; i < mines.size(); i++) { // Displaying mine locations in the console
            GridTile cell = mines.get(i);
            System.out.println("Mine at" + cell.rows + ":" + cell.cols);
        }
        
        flagsLeft = mines.size();
        
        minesPlaced = true;
        
        System.out.println("Mines added");
    }
    
    /*
     * And unused method which placed hard coded mines.
     * This was only using during the beginning of
     * development.
     */
    void debugMines() {
        mines.add(cells[0][0]);
        mines.add(cells[4][3]);
        mines.add(cells[2][6]);
        mines.add(cells[3][1]);
        mines.add(cells[7][6]);
        
        System.out.println("(Mines have been hard coded):\n0,0\n4,3\n2,6\n3,1\n7,6");
    }
    
    /*
     * This function handles ending the game. It disables the ability
     * to interact with the grid once the game has ended. It also reveals
     * all the mines on the board and creates the restart button.
     */
    void displayGrid() {
        for (int i = 0; i < mines.size(); i++) {
            GridTile cell = mines.get(i);
            cell.setText("B");
        }
        
        System.out.println("The game has ended");
        
        gameHasEnded = true;
        
        title.setFont(new Font("comfortaa", Font.BOLD, 30));
        title.setText("Game Over! (" + elapsedTime + "s)");
        
        restartButton();
    }
    
    /*
     * This method handles checking for mines. It will first check if
     * the click if the area is out of bounds, in which case it would cancel.
     * It will then loop through a 3x3 area and run the countMine function for
     * each cell. Depending on how many mines are found it will display the
     * number on the cell and call itself on the cells around it, causing a
     * chain reaction of checking for mines until it reaches no more empty
     * cells or the end of the board. This method also hanldes the win logic,
     * checking if there are no more empty cells.
     */
    void checkMine(int rows, int cols) {
        if (rows < 0 || rows >= gridRows || cols < 0 || cols >= gridCols) { // Checking if the area is out of bounds
            return;
        }
        
        GridTile cell = cells[rows][cols];
        if (!cell.isEnabled()) {
            return;
        }
        cell.setEnabled(false);
        cellsClicked += 1;
        if (cell.getText() == "F") { // Giving back the player a flag if it is on an empty cell
            flagsLeft++;
        }
        
        int minesFound = 0;
        
        for (int r = -1; r <= 1; r++) { // Looping through a 3x3 area
            for (int c = -1; c <= 1; c++) {
                if (r == 0 && c == 0) {
                    
                } else {
                    minesFound += countMine(rows+r, cols+c);
                }
            }
        }

        if (minesFound > 0) { // Changing style depending on how many mines were found
            cell.setForeground(new Color(255,255,255));
            cell.setBackground(new Color(204, 204, 204));
            
            cell.setText(Integer.toString(minesFound));
        } else {
            cell.setText("");
            cell.setForeground(new Color(255,255,255));
            cell.setBackground(new Color(204, 204, 204));
            
            for (int r = -1; r <= 1; r++) { // Calls the neighbors to run the method
                for (int c = -1; c <= 1; c++) {
                    if (r == 0 && c == 0) {
                        
                    } else {
                        checkMine(rows+r, cols+c);
                    }
                }
            }
            
        }
        
        // Handling the win logic
        if (cellsClicked == gridRows * gridCols - mines.size()) {
            gameHasEnded = true;
            
            title.setFont(new Font("comfortaa", Font.BOLD, 30));
            title.setText("You found all the mines! (" + elapsedTime + "s)");
            playSound("sounds/win.wav");
            timer.stop();
            restartButton();
        }
    }
    
    /*
     * This is a simple method which returns a 1 or 0 depending
     * on if there is a mine on a specific cell.
     */
    int countMine(int r, int c) {
        if (r < 0 || r >= gridRows || c < 0 || c >= gridCols) {
            return 0;
        }
        if (mines.contains(cells[r][c])) {
            return 1;
        }
        return 0;
    }
    
    /*
     * This method displays the information for the player including
     * the number of flags the player has left, the number of empty cells
     * remaining, and the time the player has been playing the game.
     */
    void displayInfo() {
        int numberOfCells = gridCols * gridRows - mines.size();
        
        title.setFont(new Font("comfortaa", Font.BOLD, 20));
        title.setText("Cells cleared: " + cellsClicked + " / " + numberOfCells + " | " + "Flags left: " + flagsLeft + " | " + "Elapsed Time: " + elapsedTime);
    }
    
    /*
     * This method can be called from anywhere and will play a .wav file
     * from the computer. It is made so nothing needs to be inputted
     * except the file path of the sound.
     */
    public void playSound(String filePath) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
