import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Board extends JPanel {
    private static Random r = new Random();
    public static final int PREFERRED_GRID_SIZE_PIXELS = 100;
    public static final int MINIMAX_INIT_DEPTH = 6;
    private int boardSize = 3;

    private Cell[][] cells;
    private boolean isPlayersTurn;
    private boolean winnerFound;
    private final Image[] TERRAINS = new Image[3];

    public Board(){
        //init cell images
        try{
            TERRAINS[0] = ImageIO.read(new File("circle.png"));
            TERRAINS[1] = ImageIO.read(new File("cross.png"));
            TERRAINS[2] = ImageIO.read(new File("empty.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
    }

    private void init() {
        isPlayersTurn = true;
        winnerFound = false;
        cells = new Cell[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
                cells[i][j] = new Cell('E');

        int preferredWidth = boardSize * PREFERRED_GRID_SIZE_PIXELS;
        int preferredHeight = boardSize * PREFERRED_GRID_SIZE_PIXELS;
        setPreferredSize(new Dimension(preferredWidth, preferredHeight));
    }


    @Override
    public void paintComponent(Graphics g) {
        // Important to call super class method
        super.paintComponent(g);
        // Clear the board
        g.clearRect(0, 0, getWidth(), getHeight());
        // Draw the grid
        int rectWidth = getWidth() / boardSize;
        int rectHeight = getHeight() / boardSize;

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                int y = i * rectWidth;
                int x = j * rectHeight;
                g.drawImage(TERRAINS[findTerrainIndex(cells[i][j])], j * PREFERRED_GRID_SIZE_PIXELS, i * PREFERRED_GRID_SIZE_PIXELS, PREFERRED_GRID_SIZE_PIXELS, PREFERRED_GRID_SIZE_PIXELS, this);
            }
        }
    }

    public static void main(String[] args) {
        // http://docs.oracle.com/javase/tutorial/uiswing/concurrency/initial.html
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Tic-tac-toe");
                Board gameBoard = new Board();
                frame.add(gameBoard);

                // turn alert label
                JLabel jlWhosTurn = new JLabel("Player's turn");
                jlWhosTurn.setForeground(Color.RED);
                jlWhosTurn.setFont(new Font("Serif", Font.BOLD, 24));
                gameBoard.add(jlWhosTurn);

                // set up menubar
                JMenuBar menuBar = new JMenuBar();
                JMenu menu = new JMenu("New Game");

                JMenuItem menuItem = new JMenuItem();
                menuItem.setAction(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        gameBoard.boardSize = 3;
                        gameBoard.init();
                        gameBoard.repaintBoard();
                    }
                });
                menuItem.setText("New 3x3 Game");
                menu.add(menuItem);

                menuBar.add(menu);
                frame.setJMenuBar(menuBar);

                // set up mouse listener when clicking on a tile
                gameBoard.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        int x = e.getX() / PREFERRED_GRID_SIZE_PIXELS;
                        int y = e.getY() / PREFERRED_GRID_SIZE_PIXELS;
                        // e.getButton() == 1, 2, 3 aka MouseEvent.BUTTON1, MouseEvent.BUTTON2, MouseEvent.BUTTON3 for left/middle/right button
                        System.out.println(x + ", " + y);
                        if(e.getButton() == MouseEvent.BUTTON1 // left click on the board
                                && x >= 0 && x < gameBoard.boardSize && y >= 0 && y < gameBoard.boardSize // click in the board
                                && gameBoard.cells[y][x].role == Cell.ROLE.EMPTY // can only click on empty cell
                                && !gameBoard.winnerFound) { // can only click when winner hasn't been found
                            gameBoard.cells[y][x].role = Cell.ROLE.CIRCLE;
                            gameBoard.repaintBoard();
                            // AI move
                            gameBoard.aiMoveOneStep();
                            gameBoard.isPlayersTurn = true;
                            gameBoard.repaintBoard();
                        }
                    }
                });

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }


    public void repaintBoard(){
        JLabel jlTurn = (JLabel) getComponent(0);
        if(isPlayersTurn) jlTurn.setText("Player's turn");
        else jlTurn.setText("AI's turn");

        repaint();
        JFrame jfParent = ((JFrame) SwingUtilities.getWindowAncestor(this));
        if(jfParent != null)
            jfParent.pack();
        checkWinning();
    }


    private int checkWinning() {
        JLabel jlTurn = (JLabel) getComponent(0);
        for(int i = 0; i < boardSize; i++)
            for(int j = 0; j < boardSize; j++){
            if(i - 1 >= 0 && i + 1 < boardSize){
                if(cells[i-1][j].role == Cell.ROLE.CIRCLE && cells[i][j].role == Cell.ROLE.CIRCLE && cells[i+1][j].role == Cell.ROLE.CIRCLE){
                    jlTurn.setText("Player wins");
                    winnerFound = true;
                    return -1;
                }
                if(cells[i-1][j].role == Cell.ROLE.CROSS && cells[i][j].role == Cell.ROLE.CROSS && cells[i+1][j].role == Cell.ROLE.CROSS){
                    jlTurn.setText("AI wins");
                    winnerFound = true;
                    return 1;
                }
            }
            if(j - 1 >= 0 && j + 1 < boardSize){
                if(cells[i][j-1].role == Cell.ROLE.CIRCLE && cells[i][j].role == Cell.ROLE.CIRCLE && cells[i][j+1].role == Cell.ROLE.CIRCLE){
                    jlTurn.setText("Player wins");
                    winnerFound = true;
                    return -1;
                }
                if(cells[i][j-1].role == Cell.ROLE.CROSS && cells[i][j].role == Cell.ROLE.CROSS && cells[i][j+1].role == Cell.ROLE.CROSS){
                    jlTurn.setText("AI wins");
                    winnerFound = true;
                    return 1;
                }
            }
            if(i - 1 >= 0 && i + 1 < boardSize && j - 1 >= 0 && j + 1 < boardSize){
                if(cells[i-1][j-1].role == Cell.ROLE.CIRCLE && cells[i][j].role == Cell.ROLE.CIRCLE && cells[i+1][j+1].role == Cell.ROLE.CIRCLE){
                    jlTurn.setText("Player wins");
                    winnerFound = true;
                    return -1;
                }
                if(cells[i-1][j+1].role == Cell.ROLE.CIRCLE && cells[i][j].role == Cell.ROLE.CIRCLE && cells[i+1][j-1].role == Cell.ROLE.CIRCLE){
                    jlTurn.setText("Player wins");
                    winnerFound = true;
                    return -1;
                }
                if(cells[i-1][j-1].role == Cell.ROLE.CROSS && cells[i][j].role == Cell.ROLE.CROSS && cells[i+1][j+1].role == Cell.ROLE.CROSS){
                    jlTurn.setText("AI wins");
                    winnerFound = true;
                    return 1;
                }
                if(cells[i-1][j+1].role == Cell.ROLE.CROSS && cells[i][j].role == Cell.ROLE.CROSS && cells[i+1][j-1].role == Cell.ROLE.CROSS){
                    jlTurn.setText("AI wins");
                    winnerFound = true;
                    return 1;
                }
            }
        }

        for(int i = 0; i < boardSize; i++)
            for(int j = 0; j < boardSize; j++)
                if(cells[i][j].role == Cell.ROLE.EMPTY)
                    // not finished and undetermined, continue;
                    return 0;
        // all cells filled and nobody wins, draw
        jlTurn.setText("It's a draw");
        return 0;
    }

    private int findTerrainIndex(Cell cell){
        if(cell.role == Cell.ROLE.CIRCLE) return 0;
        if(cell.role == Cell.ROLE.CROSS) return 1;
        return 2;
    }

    private void aiMoveOneStep() {
        minimax(cells,true);
    }

    // return 1 for AI win, -1 for player win, 0 for draw or undeterministic state
    private int terminalState(Cell[][] cells){
        for(int i = 0; i < boardSize; i++)
            for(int j = 0; j < boardSize; j++) {
                if (i - 1 >= 0 && i + 1 < boardSize) {
                    if (cells[i - 1][j].role == Cell.ROLE.CIRCLE && cells[i][j].role == Cell.ROLE.CIRCLE && cells[i + 1][j].role == Cell.ROLE.CIRCLE)
                        return -1;
                    if (cells[i - 1][j].role == Cell.ROLE.CROSS && cells[i][j].role == Cell.ROLE.CROSS && cells[i + 1][j].role == Cell.ROLE.CROSS)
                        return 1;
                }
                if (j - 1 >= 0 && j + 1 < boardSize) {
                    if (cells[i][j - 1].role == Cell.ROLE.CIRCLE && cells[i][j].role == Cell.ROLE.CIRCLE && cells[i][j + 1].role == Cell.ROLE.CIRCLE)
                        return -1;
                    if (cells[i][j - 1].role == Cell.ROLE.CROSS && cells[i][j].role == Cell.ROLE.CROSS && cells[i][j + 1].role == Cell.ROLE.CROSS)
                        return 1;
                }
                if (i - 1 >= 0 && i + 1 < boardSize && j - 1 >= 0 && j + 1 < boardSize) {
                    if (cells[i - 1][j - 1].role == Cell.ROLE.CIRCLE && cells[i][j].role == Cell.ROLE.CIRCLE && cells[i + 1][j + 1].role == Cell.ROLE.CIRCLE)
                        return -1;
                    if (cells[i - 1][j + 1].role == Cell.ROLE.CIRCLE && cells[i][j].role == Cell.ROLE.CIRCLE && cells[i + 1][j - 1].role == Cell.ROLE.CIRCLE)
                        return -1;
                    if (cells[i - 1][j - 1].role == Cell.ROLE.CROSS && cells[i][j].role == Cell.ROLE.CROSS && cells[i + 1][j + 1].role == Cell.ROLE.CROSS)
                        return 1;
                    if (cells[i - 1][j + 1].role == Cell.ROLE.CROSS && cells[i][j].role == Cell.ROLE.CROSS && cells[i + 1][j - 1].role == Cell.ROLE.CROSS)
                        return 1;
                }
            }
        return 0;
    }



    private int minimax(Cell[][] cells, boolean maximizeAI) {
        int terminalState = terminalState(cells);
        if(Math.abs(terminalState) == 1) return terminalState;

        BoardStateNode currentRoot = new BoardStateNode(cells);
        currentRoot.setupChildren(maximizeAI);
        if(currentRoot.children.size() == 0)
            // cannot move anymore, is a leaf node
            return terminalState;

        if(maximizeAI){// AI's turn
            int childVal = -2;
            int maxIndex = -1;
            for(int i = 0; i < currentRoot.children.size(); i++){
                int minimaxVal = minimax(currentRoot.children.get(i).val, false);
                if(minimaxVal > childVal){
                    maxIndex = i;
                    childVal = minimaxVal;
                }
                if(minimaxVal == 1){
                    this.cells = currentRoot.children.get(i).val;
                    return 1;
                }
            }
            this.cells = currentRoot.children.get(maxIndex).val;
            return childVal;
        }else{// player's turn
            int childVal = 2;
            int minIndex = -1;
            for(int i = 0; i < currentRoot.children.size(); i++){
                int minimaxVal = minimax(currentRoot.children.get(i).val, true);
                if(minimaxVal < childVal){
                    minIndex = i;
                    childVal = minimaxVal;
                }
                if(minimaxVal == -1){
                    this.cells = currentRoot.children.get(i).val;
                    return -1;
                }
            }
            this.cells = currentRoot.children.get(minIndex).val;
            return childVal;
        }
    }
}
