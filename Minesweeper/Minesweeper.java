import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * Write a description of class Minesweeper here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Minesweeper
{
    int tileSize = 80; // The size of each tile is in pixels
    int rows = 8;
    int cols = 8;
    int boardWidth = cols * tileSize; // Dynamically changes the size of the screen to the correct amount using the tileSize
    int boardHeight = rows * tileSize;
    
    JFrame frame = new JFrame("Joa's Minesweeper"); // Creating the frame
    JLabel title = new JLabel();
    JPanel titlePanel = new JPanel();
    
    public Minesweeper()
    {
        setupFrame();
    }
    
    void setupFrame() { // This function runs all the code needed to setup the frame and boards
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
        titlePanel.add(title, BorderLayout.NORTH); // Set the title to cling to the top of the frame
        
        frame.add(titlePanel);
        frame.setVisible(true);
    }
}
