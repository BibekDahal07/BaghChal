import java.util.Scanner;

class BaghChalBoard {
    private char[][] board = new char[5][5]; // 'T' for Tiger, 'G' for Goat, '.' for empty
    private int goatsPlaced = 0;
    private int goatsCaptured = 0;
    private boolean isGoatTurn = true;
    private boolean placementPhase = true;

    public BaghChalBoard() {
        // Initialize empty board
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                board[i][j] = '.';
            }
        }
        // Place tigers at corners (row 0 = line 5, row 4 = line 1)
        board[0][0] = 'T'; // a5
        board[0][4] = 'T'; // e5
        board[4][0] = 'T'; // a1
        board[4][4] = 'T'; // e1
    }

    public void display() {
        System.out.println("  a b c d e");
        for (int i = 0; i < 5; i++) {
            System.out.print((5 - i) + " ");
            for (int j = 0; j < 5; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("Goats placed: " + goatsPlaced + "/20, Goats captured: " + goatsCaptured);
    }

    public boolean placeGoat(String pos) {
        if (!placementPhase || goatsPlaced >= 20) {
            System.out.println("Cannot place more goats!");
            return false;
        }
        int[] coords = parsePosition(pos);
        if (coords == null) {
            System.out.println("Invalid position format! Use e.g., 'a1'.");
            return false;
        }
        if (board[coords[0]][coords[1]] != '.') {
            System.out.println("Position already occupied!");
            return false;
        }
        board[coords[0]][coords[1]] = 'G';
        goatsPlaced++;
        if (goatsPlaced >= 20) placementPhase = false;
        return true;
    }

    public boolean movePiece(String from, String to) {
        int[] fromCoords = parsePosition(from);
        int[] toCoords = parsePosition(to);
        if (fromCoords == null || toCoords == null) {
            System.out.println("Invalid position format! Use e.g., 'a1 a2'.");
            return false;
        }

        int fr = fromCoords[0], fc = fromCoords[1];
        int tr = toCoords[0], tc = toCoords[1];

        // Validate piece selection
        if (isGoatTurn && board[fr][fc] != 'G') {
            System.out.println("Goat player must select a goat!");
            return false;
        }
        if (!isGoatTurn && board[fr][fc] != 'T') {
            System.out.println("Tiger player must select a tiger!");
            return false;
        }
        if (board[tr][tc] != '.') {
            System.out.println("Destination is not empty!");
            return false;
        }

        // Handle moves
        if (isGoatTurn) {
            if (!isValidMove(fr, fc, tr, tc)) {
                System.out.println("Invalid goat move!");
                return false;
            }
            board[tr][tc] = 'G';
            board[fr][fc] = '.';
        } else {
            // Tiger move or jump
            if (isValidMove(fr, fc, tr, tc)) {
                board[tr][tc] = 'T';
                board[fr][fc] = '.';
            } else if (isValidJump(fr, fc, tr, tc)) {
                board[tr][tc] = 'T';
                board[fr][fc] = '.';
                int mr = (fr + tr) / 2, mc = (fc + tc) / 2;
                board[mr][mc] = '.';
                goatsCaptured++;
                System.out.println("Goat captured!");
            } else {
                System.out.println("Invalid tiger move or jump!");
                return false;
            }
        }
        return true;
    }

    private boolean isValidMove(int fr, int fc, int tr, int tc) {
        if (tr < 0 || tr >= 5 || tc < 0 || tc >= 5) return false;
        int dr = Math.abs(fr - tr), dc = Math.abs(fc - tc);
        if (dr == 0 && dc == 1) return true; // Horizontal
        if (dr == 1 && dc == 0) return true; // Vertical
        if (dr == 1 && dc == 1) return isDiagonalLine(fr, fc, tr, tc); // Diagonal
        return false;
    }

    private boolean isValidJump(int fr, int fc, int tr, int tc) {
        if (tr < 0 || tr >= 5 || tc < 0 || tc >= 5) return false;
        int dr = tr - fr, dc = tc - fc;
        if ((Math.abs(dr) == 2 && dc == 0) || (dr == 0 && Math.abs(dc) == 2)) {
            int mr = (fr + tr) / 2, mc = (fc + tc) / 2;
            if (mr >= 0 && mr < 5 && mc >= 0 && mc < 5 && board[mr][mc] == 'G' && board[tr][tc] == '.') {
                return true;
            }
        } else if (Math.abs(dr) == 2 && Math.abs(dc) == 2) {
            int mr = (fr + tr) / 2, mc = (fc + tc) / 2;
            if (mr >= 0 && mr < 5 && mc >= 0 && mc < 5 && board[mr][mc] == 'G' && board[tr][tc] == '.' && isDiagonalLine(fr, fc, tr, tc)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDiagonalLine(int fr, int fc, int tr, int tc) {
        // Normalize coordinates
        int r1 = fr, c1 = fc, r2 = tr, c2 = tc;
        if (r1 > r2) {
            int temp = r1; r1 = r2; r2 = temp;
            temp = c1; c1 = c2; c2 = temp;
        }
        // Bagh Chal diagonal lines (based on standard 5x5 grid)
        // Corners to center (c3 = row 2, col 2)
        if ((r1 == 0 && c1 == 0 && r2 == 2 && c2 == 2) || // a5 to c3
            (r1 == 0 && c1 == 4 && r2 == 2 && c2 == 2) || // e5 to c3
            (r1 == 4 && c1 == 0 && r2 == 2 && c2 == 2) || // a1 to c3
            (r1 == 4 && c1 == 4 && r2 == 2 && c2 == 2)) { // e1 to c3
            return true;
        }
        // Additional diagonals (midpoints to edges)
        if ((r1 == 0 && c1 == 2 && r2 == 2 && c2 == 0) || // c5 to a3
            (r1 == 0 && c1 == 2 && r2 == 2 && c2 == 4) || // c5 to e3
            (r1 == 4 && c1 == 2 && r2 == 2 && c2 == 0) || // c1 to a3
            (r1 == 4 && c1 == 2 && r2 == 2 && c2 == 4) || // c1 to e3
            (r1 == 2 && c1 == 0 && r2 == 0 && c2 == 2) || // a3 to c5
            (r1 == 2 && c1 == 0 && r2 == 4 && c2 == 2) || // a3 to c1
            (r1 == 2 && c1 == 4 && r2 == 0 && c2 == 2) || // e3 to c5
            (r1 == 2 && c1 == 4 && r2 == 4 && c2 == 2)) { // e3 to c1
            return true;
        }
        return false;
    }

    private int[] parsePosition(String pos) {
        if (pos == null || pos.length() != 2) return null;
        int col = pos.charAt(0) - 'a';
        int row = 5 - (pos.charAt(1) - '0');
        if (row < 0 || row >= 5 || col < 0 || col >= 5) return null;
        return new int[]{row, col};
    }

    public boolean checkWin() {
        if (goatsCaptured >= 5) {
            System.out.println("Tigers win! 5 goats captured.");
            return true;
        }
        int tigersBlocked = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (board[i][j] == 'T') {
                    if (!hasValidMoveOrJump(i, j)) tigersBlocked++;
                }
            }
        }
        if (tigersBlocked == 4) {
            System.out.println("Goats win! All tigers blocked.");
            return true;
        }
        return false;
    }

    private boolean hasValidMoveOrJump(int r, int c) {
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] dir : directions) {
            int tr = r + dir[0], tc = c + dir[1];
            if (tr >= 0 && tr < 5 && tc >= 0 && tc < 5 && isValidMove(r, c, tr, tc) && board[tr][tc] == '.') {
                return true;
            }
            tr = r + 2 * dir[0]; tc = c + 2 * dir[1];
            if (tr >= 0 && tr < 5 && tc >= 0 && tc < 5 && isValidJump(r, c, tr, tc)) {
                return true;
            }
        }
        return false;
    }

    public boolean isGoatTurn() {
        return isGoatTurn;
    }

    public boolean isPlacementPhase() {
        return placementPhase;
    }

    public void switchTurn() {
        isGoatTurn = !isGoatTurn;
    }
}

public class BaghChalConsole {
    public static void main(String[] args) {
        BaghChalBoard board = new BaghChalBoard();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            board.display();
            if (board.checkWin()) break;

            if (board.isGoatTurn()) {
                if (board.isPlacementPhase()) {
                    System.out.print("Goat player, enter position to place goat (e.g., a1): ");
                    String pos = scanner.nextLine().trim();
                    if (!board.placeGoat(pos)) {
                        System.out.println("Try again.");
                        continue;
                    }
                } else {
                    System.out.print("Goat player, enter move (e.g., a1 a2): ");
                    String[] input = scanner.nextLine().trim().split("\\s+");
                    if (input.length != 2 || !board.movePiece(input[0], input[1])) {
                        System.out.println("Try again.");
                        continue;
                    }
                }
            } else {
                System.out.print("Tiger player, enter move or jump (e.g., a1 a2): ");
                String[] input = scanner.nextLine().trim().split("\\s+");
                if (input.length != 2 || !board.movePiece(input[0], input[1])) {
                    System.out.println("Try again.");
                    continue;
                }
            }
            board.switchTurn();
        }
        scanner.close();
    }
}