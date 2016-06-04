/**
 * Created by Bouse PC on 4/06/2016.
 */
import java.util.*;

class Point {

    int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

public class Board {

    //Copy constructor
    public Board(Board b) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.board[i][j] = b.board[i][j];
            }
        }
    }

    public Board() {
    }

    int[][] board = new int[3][3];

    List<Point> availablePoints;

    //Get the playable locations available on the board
    public List<Point> getAvailableStates() {
        availablePoints = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (board[i][j] == 0) {
                    availablePoints.add(new Point(i, j));
                }
            }
        }
        return availablePoints;
    }

    public boolean isGameOver() {
        //Game is over is someone has won, or board is full (draw)
        return (hasXWon() || hasOWon() || getAvailableStates().isEmpty());
    }

    public boolean hasXWon() {
        if ((board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] == 1)
                || (board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] == 1)) {
            //System.out.println("X Diagonal Win");
            return true;
        }
        for (int i = 0; i < 3; ++i) {
            if (((board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] == 1)
                    || (board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] == 1))) {
                //System.out.println("X Row or Column win");
                return true;
            }
        }
        return false;
    }

    public boolean hasOWon() {
        if ((board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] == 2)
                || (board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] == 2)) {
            //System.out.println("O Diagonal Win");
            return true;
        }
        for (int i = 0; i < 3; ++i) {
            if ((board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] == 2)
                    || (board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] == 2)) {
                //System.out.println("O Row or Column win");
                return true;
            }
        }
        return false;
    }

    public void placeAMove(Point point, int player) {
        board[point.x][point.y] = player;   //player = 1 for X, 2 for O
    }

    class PointsAndScores {

        int score;
        Point point;

        PointsAndScores(int score, Point point) {
            this.score = score;
            this.point = point;
        }
    }

    List<PointsAndScores> pointsAndScores = new ArrayList<>();

    //The actual MinMax Logic is in this function
    public int evaluateBoardPositions(int RecursionTurn) {

        List<Point> availablePoints = getAvailableStates();

        if (hasXWon()) {
            return +1;
        } else if (hasOWon()) {
            return -1;
        } else if (availablePoints.isEmpty()) {
            return 0;
        } else {
            int minOrMax = -999;

            List<Integer> scores = new ArrayList<>();

            for (Point point : availablePoints) {

                Board newBoard = new Board(this);
                if (RecursionTurn == 1) {
                    newBoard.placeAMove(point, 1);
                    scores.add(newBoard.evaluateBoardPositions(2));
                    Collections.sort(scores, Collections.reverseOrder());
                    minOrMax = scores.get(0);
                }
                if (RecursionTurn == 2) {
                    newBoard.placeAMove(point, 2);
                    scores.add(newBoard.evaluateBoardPositions(1));
                    Collections.sort(scores);
                    minOrMax = scores.get(0);
                }
                pointsAndScores.add(new PointsAndScores(minOrMax, point));
            }
            return minOrMax;
        }
    }

    //Return the move with the highest score i.e. +1 for our Bot.
    public Point returnBestMove() {
        int MAX = -100000;
        int best = -1;

        for (int i = 0; i < pointsAndScores.size(); ++i) {
            if (MAX < pointsAndScores.get(i).score) {
                MAX = pointsAndScores.get(i).score;
                best = i;
            }
        }

        return pointsAndScores.get(best).point;
    }
    Scanner scan = new Scanner(System.in);

    void takeHumanInput() {
        System.out.println("Your move: ");
        int x = scan.nextInt();
        int y = scan.nextInt();
        Point point = new Point(x, y);
        placeAMove(point, 2);
    }

    public void displayBoard() {
        System.out.println();

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();

        }
    }

    void placeFirstMove() {
        Random rand = new Random();
        Point p = new Point(rand.nextInt(3), rand.nextInt(3));
        placeAMove(p, 1);
    }

    public Point randomComputerAI()
    {
        List<Point> avaliableMoves = getAvailableStates();
        Random random = new Random();
        int randNum = random.nextInt(getAvailableStates().size());
        return avaliableMoves.get(randNum);


    }

    public static void main(String[] args) {

        Board b = new Board();
        b.displayBoard();

        System.out.println("Who's gonna move first? (1) You : (2) Me?");
        Scanner scan = new Scanner(System.in);
        int choice = scan.nextInt();

        if (choice == 2) {
            b.placeFirstMove();
            b.displayBoard();
        }
        while (!b.isGameOver()) {
            b.takeHumanInput();
            if (b.isGameOver()) {
                break;
            }
            b.displayBoard();

            b.pointsAndScores.clear();
            b.evaluateBoardPositions(1);
            Point p = b.returnBestMove();

            for (PointsAndScores pas : b.pointsAndScores) {
                System.out.println("Score: " + pas.score + " Point: " + pas.point.x + " " + pas.point.y);
            }
            b.placeAMove(p, 1);
            b.displayBoard();
        }
        if (b.hasXWon()) {
            System.out.println("Unfortunately, you lost!");
        } else if (b.hasOWon()) {
            System.out.println("Never gets displayed. Computer Always wins or draws");
        } else {
            System.out.println("It's a draw!");
        }
    }
}