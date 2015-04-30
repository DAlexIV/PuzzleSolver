import java.util.Comparator;

/**
 * Created by Alex on 3/4/2015.
 */
public class Solver {
    private class SearchNode
    {
        private Board b;
        private int moves;
        private SearchNode prev;
        public SearchNode(Board b, int moves, SearchNode prev)
        {
            this.b = b;
            this.moves = moves;
            this.prev = prev;
        }
        @Override
        public boolean equals(Object y)
        {
            if (y == null)
                throw new NullPointerException();
            SearchNode tmp = (SearchNode) y;
            return (this.b == tmp.b);
        }
        @Override
        public String toString()
        {
            return b.toString();
        }
    }
    private class Comp implements Comparator<SearchNode>
    {
        /**
         * Compares two boards
         * @param a First board
         * @param b Secong board
         * @return Value of compare
         */
        @Override
        public int compare(SearchNode a, SearchNode b) {
            if (a.b.manhattan() + a.moves > b.b.manhattan() + b.moves)
                return 1;
            if (a.b.manhattan() + a.moves == b.b.manhattan() + b.moves) {
                if (a.b.manhattan() > b.b.manhattan())
                    return 1;
                if (a.b.manhattan() == b.b.manhattan()) {
                    if (a.b.hamming() > b.b.hamming())
                        return 1;
                    return -1;
                }
                return -1;
            }
            return -1;
        }
    }
    private Stack<SearchNode> sol = new Stack<SearchNode>(); // Way to solution
    private SearchNode startBoard; // Current search node for solving

    /**
     * Find a solution to the initial board (using the A* algorithm)
     * @param initial Board to start
     */
    public Solver(Board initial)
    {
        int mx = Integer.MIN_VALUE;
        if (initial == null)
            throw new NullPointerException("Null board");

        startBoard = new SearchNode(initial, 0, null);
        Comp comparator = new Comp();

        MinPQ myPQ = new MinPQ(comparator);
        myPQ.insert(startBoard);
        SearchNode cur;

        if (!isSolvable())
            return;

        do {
            cur = (SearchNode) myPQ.delMin();
            for (Board b : cur.b.neighbors()) {
                if (cur.prev == null || cur.prev.b != b)
                    myPQ.insert(new SearchNode(b, cur.moves + 1, cur));
            }
            if (mx < myPQ.size())
                mx = myPQ.size();
        } while (cur.b.hamming() != 0);
        System.out.println("Max pq size - " + mx);

        sol.push(cur);
        while (cur.prev != null)
        {
            sol.push(cur.prev);
            cur = cur.prev;
        }

    }
    // is the initial board solvable?
    public boolean isSolvable()
    {
        Board secbr = startBoard.b.twin();
        SearchNode secnd = new SearchNode(secbr, 0, null);
        Comp comparator = new Comp();

        MinPQ myPQ1 = new MinPQ(comparator);
        MinPQ myPQ2 = new MinPQ(comparator);
        myPQ1.insert(startBoard);
        myPQ2.insert(secnd);
        SearchNode cur1, cur2;

        do {
            cur1 = (SearchNode) myPQ1.delMin();
            cur2 = (SearchNode) myPQ2.delMin();
            for (Board b : cur1.b.neighbors()) {
                if (cur1.prev == null || cur1.prev.b != b)
                    myPQ1.insert(new SearchNode(b, cur1.moves + 1, cur1));
            }
            for (Board b : cur2.b.neighbors()) {
                if (cur2.prev == null || cur2.prev.b != b)
                    myPQ2.insert(new SearchNode(b, cur2.moves + 1, cur2));
            }
        } while (cur1.b.hamming() != 0 && cur2.b.hamming() != 0);
        return (cur1.b.hamming() == 0);
    }

    /**
     *
     * @return min number of moves to solve initial board; -1 if unsolvable
     */
    public int moves()
    {
        return sol.size() - 1;
    }

    /**
     * @return sequence of boards in a shortest solution; null if unsolvable
     */
    public Iterable<Board> solution()
    {
        Stack<Board> newsol = new Stack<Board>();
        Stack<Board> goodOrd = new Stack<Board>();
        while (!sol.isEmpty())
            newsol.push(sol.pop().b);
        while (!newsol.isEmpty())
            goodOrd.push(newsol.pop());
        return goodOrd;
    }

    // solve a slider puzzle (given below)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
