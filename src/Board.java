/**
 * Board class for 8Puzzle solver
 * Made by Ivanov Alex, HSE,
 * Software Engineering 1 year student
 */

import java.util.*;
import java.util.Stack;


/**
 * Represents a boardclass
 */
public class Board {
    private char[][] b; // Array presentation of board
    private int N; // Size of board
    private int manh; // Result of manhattan function of this board
    private int ham; // Result of hamming function of this board

    private class Comp implements Comparator<Board>
    {
        /**
         * Compares two boards
         * @param a First board
         * @param b Secong board
         * @return Value of compare
         */
        @Override
        public int compare(Board a, Board b) {
            if (a.manhattan() > b.manhattan())
                return 1;
            if (a.manhattan() == b.manhattan() ) {
                if (a.hamming() > b.hamming())
                    return 1;
                return -1;
            }
            return -1;
        }
    }

    /**
     * construct a board from an N-by-N array of blocks
     * (where blocks[i][j] = block in row i, column j)
     * @param blocks Initial board
     */
    public Board(int[][] blocks)
    {
        if (blocks == null)
            throw new NullPointerException();
        b = copyArrToChar(blocks);
        N = blocks[0].length;
        manh = countManh();
        ham = countHamming();
    }

    /**
     * construct a board from an N-by-N array of blocks
     * (where blocks[i][j] = block in row i, column j)
     * and known numbers of manhattan value
     * @param br Initial board
     * @param mch Manhattan value of parent
     */
    private Board(Board br, int mch)
    {
        if (br == null)
            throw new NullPointerException();
        b = copyArr(br.b);
        N = br.N;
        ham = countHamming();
        manh = br.manh + mch;
    }

    /**
     * Copies char array to other place
     * @param c Target char array
     * @return Copied char array
     */
    private char[][] copyArr(char[][] c)
    {
        int n = c[0].length; // Size
        char[][] ret = new char[n][n];
        for (int i = 0; i < n; ++i)
            for (int k = 0; k < n; ++k)
                ret[i][k] = c[i][k];
        return ret;
    }

    /**
     * Copies int array to char
     * @param c Target int array
     * @return Copied char array
     */
    private char[][] copyArrToChar(int[][] c)
    {
        int n = c[0].length;
        char[][] ret = new char[n][n];
        for (int i = 0; i < n; ++i)
            for (int k = 0; k < n; ++k)
                ret[i][k] = (char) c[i][k];
        return ret;
    }



    /**
     * @return Board dimension N
     */
    public int dimension() {
        return N;
    }

    /**
     * Searches for given point in board
     * @param a Point
     * @return Array of coordinates
     */
    private int[] search(int a)
    {
        for (int i = 0; i < N; ++i)
            for (int k = 0; k < N; ++k)
                if (b[i][k] == a)
                {
                    int[] ret = {i, k};
                    return ret;
                }
        return null;
    }

    /**
     * Returns right place of element on board
     * @param a Element
     * @return Array of coordinates
     */
    private int[] getRightPl(int a)
    {
        int[] ret = new int[2];
        if (a == 0)
        {
            ret[0] = N - 1;
            ret[1] = N - 1;
        }
        ret[0] = (a - 1) / N;
        ret[1] = (a - 1) % N;
        return ret;
    }

    /**
     * Counts value for manhattan index for specific value
     * @param i Y coordinate
     * @param k X coordinate
     * @return Manhattan value
     */
    private int val(int i, int k)
    {
        int[] sr = getRightPl(b[i][k]);
        return Math.abs(sr[0] - i) + Math.abs(sr[1] - k);
    }

    /**
     * Exchanges two values on board
     * @param oldx Old X coordinate
     * @param oldy Old Y coordinate
     * @param newx New X coordinate
     * @param newy New Y coordinate
     */
    private void exchange(int oldx, int oldy, int newx, int newy)
    {
        char tmp = b[oldx][oldy]; // Temp variable for copying
        b[oldx][oldy] = b[newx][newy];
        b[newx][newy] = tmp;
    }

    /**
     * Counts number of blocks out of place
     * @return Returns Hamming function number
     */
    private int countHamming()
    {
        int count = 0;
        for (int i = 0; i < N; ++i)
            for (int k = 0; k < N; ++k)
                if (b[i][k] != i * N + k + 1 && b[i][k] != 0)
                    ++count;
        return count;
    }

    /**
     * @return Hamming value of the board
     */
    public int hamming()
    {
        if (ham == -1)
            throw new NullPointerException();
        return ham;
    }
`
    /**
     * Counts sum of Manhattan distances between blocks and goal
     * @return Manhattan value of the board
     */
    private int countManh() {
        int count = 0;
        for (int i = 0; i < N; ++i)
            for (int k = 0; k < N; ++k) {
                count += val(i, k);
            }
        return count;
    }

    /**
     * @return Manhattan value of the board
     */
    public int manhattan()
    {
        return manh;
    }

    /**
     * Checks if current board is goal
     * @return True if it's true
     */
    public boolean isGoal()
    {
        if (hamming() == 0)
            return true;
        return false;
    }

    /**
     * @return
     * a board that is obtained by exchanging
     * two adjacent blocks in the same row
     */
    public Board twin() {
        Board ret = new Board(this, 0);
        int[] coords = search(0);
        if (coords[0] == 0)
            ret.exchange(1, 0, 1, 1);
        else
            ret.exchange(0, 0, 0, 1);
        return ret;
    }

    /**
     * does this board equal y?
     * @param y Comparable board
     * @return True if it does
     */
    public boolean equals(Object y) {
        Board cmp = (Board) y;
        for (int i = 0; i < N; ++i)
            for (int k = 0; k < N; ++k)
                if (b[i][k] != cmp.b[i][k])
                    return false;
        return true;
    }

    /**
     * @return all neighboring boards
     */
    public Iterable<Board> neighbors()
    {
        int[] coords = search(0); // Coordinates of null
        List<Board> ret = new ArrayList<Board>();
        if (coords[0] != 0) {
            int oldval = this.val(coords[0] - 1, coords[1]);
            this.exchange(coords[0], coords[1], coords[0] - 1, coords[1]);
            int newval = this.val(coords[0], coords[1]);
            Board tmpb = new Board(this, newval - oldval);
            ret.add(tmpb);
            this.exchange(coords[0] - 1, coords[1], coords[0], coords[1]);
        }
        if (coords[1] != 0) {
            int oldval = this.val(coords[0], coords[1] - 1);
            this.exchange(coords[0], coords[1], coords[0], coords[1] - 1);
            int newval = this.val(coords[0], coords[1]);
            Board tmpb = new Board(this, newval - oldval);
            ret.add(tmpb);
            this.exchange(coords[0], coords[1] - 1, coords[0], coords[1]);
        }
        if (coords[0] != N - 1) {
            int oldval = this.val(coords[0] + 1, coords[1]);
            this.exchange(coords[0], coords[1], coords[0] + 1, coords[1]);
            int newval = this.val(coords[0], coords[1]);
            Board tmpb = new Board(this, newval - oldval);
            ret.add(tmpb);
            this.exchange(coords[0] + 1, coords[1], coords[0], coords[1]);
        }
        if (coords[1] != N - 1) {
            int oldval = this.val(coords[0], coords[1] + 1);
            this.exchange(coords[0], coords[1], coords[0], coords[1] + 1);
            int newval = this.val(coords[0], coords[1]);
            Board tmpb = new Board(this, newval - oldval);
            ret.add(tmpb);
            this.exchange(coords[0], coords[1] + 1, coords[0], coords[1]);
        }
        Comp cmp = new Comp();
        Board[] outar = ret.toArray(new Board[ret.size()]);
        Arrays.sort(outar, cmp);
        ret = Arrays.asList(outar);
        Stack<Board> outst = new Stack<Board>();
        outst.addAll(ret);
        return outst;
    }

    /**
     * @return string representation of this board
     * (in the output format specified below)
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", (int) b[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    /**
     * Main function
     * @param args Main args
     */
    public static void main(String[] args)
    {
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        System.out.println(initial.manhattan());
    }
}