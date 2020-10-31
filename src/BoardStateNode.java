import java.util.ArrayList;
import java.util.List;

public class BoardStateNode {
    Cell[][] val;
    List<BoardStateNode> children = new ArrayList<>();

    public BoardStateNode(Cell[][] cells){
        val = makeCellsCopy(cells);
    }

    static Cell[][] makeCellsCopy(Cell[][] cells){
        if(cells == null) return null;
        int boardSize = cells.length;
        Cell[][] copy = new Cell[boardSize][boardSize];
        for(int i = 0; i < boardSize; i++)
            for(int j = 0; j < boardSize; j++){
                if(cells[i][j].role == Cell.ROLE.CIRCLE)
                    copy[i][j] = new Cell('O');
                else if(cells[i][j].role == Cell.ROLE.CROSS)
                    copy[i][j] = new Cell('X');
                else
                    copy[i][j] = new Cell('E');
            }
        return copy;
    }

    public void setupChildren(boolean isAIsTurn) { // isAIsTurn == maximizeAI
        if(val == null) return;
        int boardSize = val.length;
        for(int i = 0; i < boardSize; i++)
            for(int j = 0; j < boardSize; j++){
                if(val[i][j].role == Cell.ROLE.EMPTY){
                    Cell[][] child = makeCellsCopy(val);
                    if(isAIsTurn)
                        child[i][j].role = Cell.ROLE.CROSS;
                    else
                        child[i][j].role = Cell.ROLE.CIRCLE;
                    children.add(new BoardStateNode(child));
                }
            }
    }
}