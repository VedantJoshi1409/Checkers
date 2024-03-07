import java.io.*;

import javax.swing.JFrame;

class Main {
    public static String[][] theBoard = setupBoard();
    public static boolean thePlayer;
    public static int highlightMove = -1;
    public static double theEval;
    public static JFrame frame = new MyFrame();
    public static JFrame gameFrame;
    public static JFrame winFrame;
    public static String[][][] ingameRepetition = new String[13][8][8];
    public static String result;

    public static void main(String[] args) {
        while (!LoadScreen.confirmed) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        frame.dispose();
        gameFrame = new GameFrame();
        if (LoadScreen.gameType == 0) {
            computerGame(LoadScreen.computerDepth);
        } else if (LoadScreen.gameType == 1) {
            playerGame();
        } else {
            showcase(LoadScreen.theWhiteDepth, LoadScreen.theBlackDepth);
        }
    }

    // computerGame
    // int depth - depth of engine
    // This method lets the user play against the engine
    public static void computerGame(int depth) {
        highlightMove = -1;
        String[][] prevBoard;
        String movedPiece = null;
        String moving;
        int differenceX, differenceY, moveRow, moveColumn, pieceRow, pieceColumn, piece, move;
        int[] clicks;
        boolean first = true;
        outerLoop: while (true) {
            if (first) {
                first = false;
            } else {
                thePlayer = false;
            }
            if (!thePlayer) {
                clicks = getMove(theBoard, false, gameFrame);
                if (clicks[0] == -1) {
                    result = "White Wins!";
                    gameFrame.dispose();
                    winFrame = new WinFrame();
                    break outerLoop;
                }
                piece = clicks[0];
                move = clicks[1];
                pieceRow = (piece - piece % 10) / 10;
                pieceColumn = piece % 10;
                moveRow = (move - move % 10) / 10;
                moveColumn = move % 10;
                if (Math.abs(pieceRow - moveRow) == 1) {
                    theBoard[moveRow][moveColumn] = theBoard[pieceRow][pieceColumn];
                    theBoard[pieceRow][pieceColumn] = null;
                    theBoard = updateKings(theBoard);
                    shiftArray(ingameRepetition, theBoard);
                    if (equalArray(ingameRepetition[0], ingameRepetition[4])
                            && equalArray(ingameRepetition[4], ingameRepetition[8])
                            && equalArray(ingameRepetition[8], ingameRepetition[12])) {
                        result = ("Draw By Repetition!");
                        gameFrame.dispose();
                        winFrame = new WinFrame();
                        break outerLoop;
                    }
                } else {
                    while (true) {
                        prevBoard = deepCopy(theBoard);
                        theBoard[moveRow][moveColumn] = theBoard[pieceRow][pieceColumn];
                        theBoard[pieceRow][pieceColumn] = null;
                        differenceX = (pieceRow - moveRow) / 2;
                        differenceY = (pieceColumn - moveColumn) / 2;
                        theBoard[pieceRow - differenceX][pieceColumn - differenceY] = null;
                        theBoard = updateKings(theBoard);
                        shiftArray(ingameRepetition, theBoard);
                        if (equalArray(ingameRepetition[0], ingameRepetition[4])
                                && equalArray(ingameRepetition[4], ingameRepetition[8])
                                && equalArray(ingameRepetition[8], ingameRepetition[12])) {
                            result = ("Draw By Repetition!");
                            gameFrame.dispose();
                            winFrame = new WinFrame();
                            break outerLoop;
                        }
                        moving = movingPiece(prevBoard, theBoard, false);
                        if (pieceCapture(theBoard, moving)) {
                            theEval = evaluation(theBoard, false);
                            gameFrame.repaint();
                            clicks = getMove(theBoard, false, gameFrame);
                            if (clicks[0] == -1) {
                                result = "White Wins!";
                                gameFrame.dispose();
                                winFrame = new WinFrame();
                                break outerLoop;
                            }
                            piece = clicks[0];
                            move = clicks[1];
                            pieceRow = (piece - piece % 10) / 10;
                            pieceColumn = piece % 10;
                            moveRow = (move - move % 10) / 10;
                            moveColumn = move % 10;

                        } else {
                            break;
                        }
                    }
                }
                theEval = evaluation(theBoard, false);
                thePlayer = true;
            }
            if (thePlayer) {
                gameFrame.repaint();
                if (totalPossibleMoves(theBoard, true).length == 0) {
                    result = "Black Wins!";
                    gameFrame.dispose();
                    winFrame = new WinFrame();
                    break;
                } else {
                    if (hasCapture(theBoard, true)) {
                        while (true) {
                            prevBoard = deepCopy(theBoard);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (movedPiece == null) {
                                theBoard = possibleBoards(theBoard,
                                        true)[(int) minMax(theBoard, true, false, depth, depth, movedPiece,
                                                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)];
                            } else {
                                theBoard = captureBoards(theBoard,
                                        movedPiece)[(int) minMax(theBoard, true, false, depth, depth, movedPiece,
                                                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)];
                            }
                            shiftArray(ingameRepetition, theBoard);
                            if (equalArray(ingameRepetition[0], ingameRepetition[4])
                                    && equalArray(ingameRepetition[4], ingameRepetition[8])
                                    && equalArray(ingameRepetition[8], ingameRepetition[12])) {
                                result = ("Draw By Repetition!");
                                gameFrame.dispose();
                                winFrame = new WinFrame();
                                break outerLoop;
                            }
                            movedPiece = movingPiece(prevBoard, theBoard, true);
                            highlightMove = Integer.parseInt(movedPiece);
                            theEval = evaluation(theBoard, true);
                            gameFrame.repaint();
                            if (!(pieceCapture(theBoard, movedPiece))) {
                                break;
                            }
                            if (totalPossibleMoves(theBoard, false).length == 0) {
                                result = "White Wins!";
                                gameFrame.dispose();
                                winFrame = new WinFrame();
                                break;
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        prevBoard = deepCopy(theBoard);
                        theBoard = possibleBoards(theBoard,
                                true)[(int) minMax(theBoard, true, false, depth, depth, null,
                                        Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)];

                        shiftArray(ingameRepetition, theBoard);
                        if (equalArray(ingameRepetition[0], ingameRepetition[4])
                                && equalArray(ingameRepetition[4], ingameRepetition[8])
                                && equalArray(ingameRepetition[8], ingameRepetition[12])) {
                            result = ("Draw By Repetition!");
                            gameFrame.dispose();
                            winFrame = new WinFrame();
                            break outerLoop;
                        }
                        highlightMove = Integer.parseInt(movingPiece(prevBoard, theBoard, true));
                        theEval = evaluation(theBoard, true);
                        gameFrame.repaint();
                    }
                    movedPiece = null;
                }
                if (totalPossibleMoves(theBoard, false).length == 0) {
                    result = "White Wins!";
                    gameFrame.dispose();
                    winFrame = new WinFrame();
                    break;
                }
            }

        }
    }

    // playerGame
    // This method lets the user play against another player
    public static void playerGame() {
        int[] clicks;
        int piece, move, pieceRow, pieceColumn, moveRow, moveColumn, differenceX, differenceY;
        String moving;
        String[][] prevBoard;
        outerLoop: while (true) {
            clicks = getMove(theBoard, thePlayer, gameFrame);
            if (clicks[0] == -1) {
                if (thePlayer) {
                    result = "Black Wins!";
                    gameFrame.dispose();
                    winFrame = new WinFrame();
                } else {
                    result = "White Wins!";
                    gameFrame.dispose();
                    winFrame = new WinFrame();
                }
                break outerLoop;
            }
            piece = clicks[0];
            move = clicks[1];
            pieceRow = (piece - piece % 10) / 10;
            pieceColumn = piece % 10;
            moveRow = (move - move % 10) / 10;
            moveColumn = move % 10;
            if (Math.abs(pieceRow - moveRow) == 1) {
                theBoard[moveRow][moveColumn] = theBoard[pieceRow][pieceColumn];
                theBoard[pieceRow][pieceColumn] = null;
                shiftArray(ingameRepetition, theBoard);
                if (equalArray(ingameRepetition[0], ingameRepetition[4])
                        && equalArray(ingameRepetition[4], ingameRepetition[8])
                        && equalArray(ingameRepetition[8], ingameRepetition[12])) {
                    result = ("Draw By Repetition!");
                    gameFrame.dispose();
                    winFrame = new WinFrame();
                    break outerLoop;
                }
            } else {
                while (true) {
                    prevBoard = deepCopy(theBoard);
                    theBoard[moveRow][moveColumn] = theBoard[pieceRow][pieceColumn];
                    theBoard[pieceRow][pieceColumn] = null;
                    differenceX = (pieceRow - moveRow) / 2;
                    differenceY = (pieceColumn - moveColumn) / 2;
                    theBoard[pieceRow - differenceX][pieceColumn - differenceY] = null;
                    shiftArray(ingameRepetition, theBoard);
                    if (equalArray(ingameRepetition[0], ingameRepetition[4])
                            && equalArray(ingameRepetition[4], ingameRepetition[8])
                            && equalArray(ingameRepetition[8], ingameRepetition[12])) {
                        result = ("Draw By Repetition!");
                        gameFrame.dispose();
                        winFrame = new WinFrame();
                        break outerLoop;
                    }
                    moving = movingPiece(prevBoard, theBoard, thePlayer);
                    if (pieceCapture(theBoard, moving)) {
                        theBoard = updateKings(theBoard);
                        theEval = evaluation(theBoard, thePlayer);
                        gameFrame.repaint();
                        clicks = getMove(theBoard, thePlayer, gameFrame);
                        if (clicks[0] == -1) {
                            if (thePlayer) {
                                result = "Black Wins!";
                                gameFrame.dispose();
                                winFrame = new WinFrame();
                            } else {
                                result = "White Wins!";
                                gameFrame.dispose();
                                winFrame = new WinFrame();
                            }
                            break outerLoop;
                        }
                        piece = clicks[0];
                        move = clicks[1];
                        pieceRow = (piece - piece % 10) / 10;
                        pieceColumn = piece % 10;
                        moveRow = (move - move % 10) / 10;
                        moveColumn = move % 10;
                    } else {
                        break;
                    }
                }
            }
            theBoard = updateKings(theBoard);
            theEval = evaluation(theBoard, thePlayer);
            gameFrame.repaint();
            thePlayer = !thePlayer;
            if (totalPossibleMoves(theBoard, thePlayer).length == 0) {
                if (thePlayer) {
                    result = "Black Wins!";
                    gameFrame.dispose();
                    winFrame = new WinFrame();
                } else {
                    result = "White Wins!";
                    gameFrame.dispose();
                    winFrame = new WinFrame();
                }
                break;
            }
        }
    }

    // showCase
    // int depthWhite - white's engine depth
    // int depthBlack - black's engine depth
    // This method lets the engine play against itself
    public static void showcase(int depthWhite, int depthBlack) {
        String[][] prevBoard;
        String movedPiece = null;
        int depth;
        outerLoop: while (true) {
            if (thePlayer) {
                depth = depthWhite;
            } else {
                depth = depthBlack;
            }
            if (totalPossibleMoves(theBoard, thePlayer).length == 0) {
                if (thePlayer) {
                    result = "Black Wins!";
                    gameFrame.dispose();
                    winFrame = new WinFrame();
                } else {
                    result = "White Wins!";
                    gameFrame.dispose();
                    winFrame = new WinFrame();
                }
                break outerLoop;
            } else {
                if (hasCapture(theBoard, thePlayer)) {
                    while (true) {
                        prevBoard = deepCopy(theBoard);
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (movedPiece == null) {
                            theBoard = possibleBoards(theBoard,
                                    thePlayer)[(int) minMax(theBoard, thePlayer, false, depth, depth, movedPiece,
                                            Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)];
                        } else {
                            theBoard = captureBoards(theBoard,
                                    movedPiece)[(int) minMax(theBoard, thePlayer, false, depth, depth, movedPiece,
                                            Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)];
                        }
                        shiftArray(ingameRepetition, theBoard);
                        if (equalArray(ingameRepetition[0], ingameRepetition[4])
                                && equalArray(ingameRepetition[4], ingameRepetition[8])
                                && equalArray(ingameRepetition[8], ingameRepetition[12])) {
                            result = ("Draw By Repetition!");
                            gameFrame.dispose();
                            winFrame = new WinFrame();
                            break outerLoop;
                        }
                        movedPiece = movingPiece(prevBoard, theBoard, thePlayer);
                        highlightMove = Integer.parseInt(movedPiece);
                        theEval = evaluation(theBoard, thePlayer);
                        gameFrame.repaint();
                        if (!(pieceCapture(theBoard, movedPiece))) {
                            break;
                        }
                    }
                } else {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    prevBoard = deepCopy(theBoard);
                    theBoard = possibleBoards(theBoard,
                            thePlayer)[(int) minMax(theBoard, thePlayer, false, depth, depth, null,
                                    Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)];
                    shiftArray(ingameRepetition, theBoard);
                    if (equalArray(ingameRepetition[0], ingameRepetition[4])
                            && equalArray(ingameRepetition[4], ingameRepetition[8])
                            && equalArray(ingameRepetition[8], ingameRepetition[12])) {
                        result = ("Draw By Repetition!");
                        gameFrame.dispose();
                        winFrame = new WinFrame();
                        break outerLoop;
                    }
                    highlightMove = Integer.parseInt(movingPiece(prevBoard, theBoard, thePlayer));
                    theEval = evaluation(theBoard, thePlayer);
                    gameFrame.repaint();
                }
                movedPiece = null;
            }
            thePlayer = !thePlayer;
        }
    }

    // getMove
    // returns an int array with the piece and move that the user wants or -1 if the
    // user presses the resign button
    // String[][] board - a 2d array that represents the board
    // boolean player - which player's turn it is
    // JFrame frame - the frame for the user's inputs
    // This method collects the user's inputs
    public static int[] getMove(String[][] board, boolean player, JFrame frame) {
        String[][] moves = totalPossibleMoves(theBoard, player);
        int index = -1;
        boolean temp = false;
        int[] clicks = new int[2];
        outerLoop: while (true) {
            if (temp) {
                while (MyPanel.clickPiece < 0) {
                    try {
                        Thread.sleep(100);
                        if (MyPanel.quit) {
                            return new int[] { -1 };
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                clicks[1] = MyPanel.clickPiece;
                for (int j = 1; j < moves[0].length; j++) {
                    if (moves[index][j] != null) {
                        if (clicks[1] == Integer.parseInt(moves[index][j])) {
                            highlightMove = clicks[1];
                            temp = false;
                            MyPanel.clickPiece = -1;
                            break outerLoop;
                        }
                    }
                }
            }
            index = -1;
            while (true) {
                while (MyPanel.clickPiece < 0) {
                    try {
                        Thread.sleep(100);
                        if (MyPanel.quit) {
                            return new int[] { -1 };
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                clicks[0] = MyPanel.clickPiece;
                for (int i = 0; i < moves.length; i++) {
                    if (clicks[0] == Integer.parseInt(moves[i][0])) {
                        index = i;
                        highlightMove = clicks[0];
                        temp = true;
                        gameFrame.repaint();
                    }
                }
                MyPanel.clickPiece = -1;
                if (index != -1) {
                    break;
                }
            }
        }
        return clicks;
    }

    // setupBoard
    // returns a 2d String array that represents the starting checkers board
    // This method outputs a setup checkers board
    public static String[][] setupBoard() {
        String[][] board = new String[8][8];
        for (int i = 0; i < 3; i++) {
            for (int j = 1; j < 8; j += 2) {
                if (i % 2 == 0) {
                    board[i][j] = "WN";
                    board[7 - i][j - 1] = "BN";
                } else {
                    board[i][j - 1] = "WN";
                    board[7 - i][j] = "BN";
                }
            }
        }
        return board;
    }

    // updateKings
    // returns a 2d String array with pieces on the end ranks getting transformed
    // into kings
    // String[][] board - a 2d array that represents the board
    // This method takes a board and changes any pieces on the opposite end of where
    // they started and turns them into kings
    public static String[][] updateKings(String board[][]) {
        for (int i = 0; i < 8; i++) {
            if (board[0][i] != null) {
                if (board[0][i].equals("BN")) {
                    board[0][i] = "BK";
                }
            }
            if (board[7][i] != null) {
                if (board[7][i].equals("WN")) {
                    board[7][i] = "WK";
                }
            }
        }
        return board;
    }

    // possibleMoves
    // returns a String array with whether a capture is possible and the coordinates
    // of the possible moves
    // String[][] board - a 2d array that represents the board
    // int piece - coordinates on the board array to the piece that is being checked
    // for possible moves
    // This method takes a piece and returns the possible moves and whether the
    // moves are captures
    public static String[] possibleMoves(String[][] board, int piece) {
        int amount = 1;
        int captureAmount = 1;
        int row, column;
        boolean capture = false;
        String temp = "";
        String captureTemp = "";
        row = (piece - piece % 10) / 10;
        column = piece % 10;
        char color = board[row][column].charAt(0);
        try {
            if (board[row][column].charAt(1) == 'N') {
                if (board[row][column].charAt(0) == 'B') {
                    try {
                        if (board[row - 1][column + 1] == null) {
                            amount++;
                            temp += row - 1;
                            temp += column + 1;
                        }
                        if (board[row - 1][column + 1].charAt(0) == 'W' && board[row - 2][column + 2] == null) {
                            captureAmount++;
                            captureTemp += row - 2;
                            captureTemp += column + 2;
                            capture = true;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    } catch (NullPointerException e) {
                    }
                    try {
                        if (board[row - 1][column - 1] == null) {
                            amount++;
                            temp += row - 1;
                            temp += column - 1;
                        }
                        if (board[row - 1][column - 1].charAt(0) == 'W' && board[row - 2][column - 2] == null) {
                            captureAmount++;
                            captureTemp += row - 2;
                            captureTemp += column - 2;
                            capture = true;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    } catch (NullPointerException e) {
                    }
                } else {
                    try {
                        if (board[row + 1][column + 1] == null) {
                            amount++;
                            temp += row + 1;
                            temp += column + 1;
                        }
                        if (board[row + 1][column + 1].charAt(0) == 'B' && board[row + 2][column + 2] == null) {
                            captureAmount++;
                            captureTemp += row + 2;
                            captureTemp += column + 2;
                            capture = true;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    } catch (NullPointerException e) {
                    }
                    try {
                        if (board[row + 1][column - 1] == null) {
                            amount++;
                            temp += row + 1;
                            temp += column - 1;
                        }
                        if (board[row + 1][column - 1].charAt(0) == 'B' && board[row + 2][column - 2] == null) {
                            captureAmount++;
                            captureTemp += row + 2;
                            captureTemp += column - 2;
                            capture = true;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    } catch (NullPointerException e) {
                    }
                }
            } else {
                try {
                    if (board[row - 1][column + 1] == null) {
                        amount++;
                        temp += row - 1;
                        temp += column + 1;

                    }
                    if (board[row - 1][column + 1].charAt(0) != color && board[row - 2][column + 2] == null) {
                        captureAmount++;
                        captureTemp += row - 2;
                        captureTemp += column + 2;
                        capture = true;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                } catch (NullPointerException e) {
                }
                try {
                    if (board[row - 1][column - 1] == null) {
                        amount++;
                        temp += row - 1;
                        temp += column - 1;
                    }
                    if (board[row - 1][column - 1].charAt(0) != color && board[row - 2][column - 2] == null) {
                        captureAmount++;
                        captureTemp += row - 2;
                        captureTemp += column - 2;
                        capture = true;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                } catch (NullPointerException e) {
                }
                try {
                    if (board[row + 1][column + 1] == null) {
                        amount++;
                        temp += row + 1;
                        temp += column + 1;
                    }
                    if (board[row + 1][column + 1].charAt(0) != color && board[row + 2][column + 2] == null) {
                        captureAmount++;
                        captureTemp += row + 2;
                        captureTemp += column + 2;
                        capture = true;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                } catch (NullPointerException e) {
                }
                try {
                    if (board[row + 1][column - 1] == null) {
                        amount++;
                        temp += row + 1;
                        temp += column - 1;
                    }
                    if (board[row + 1][column - 1].charAt(0) != color && board[row + 2][column - 2] == null) {
                        captureAmount++;
                        captureTemp += row + 2;
                        captureTemp += column - 2;
                        capture = true;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                } catch (NullPointerException e) {
                }
            }
        } catch (NullPointerException e) {
        }
        if (capture) {
            String[] moves = new String[captureAmount];
            moves[0] = "true";
            for (int i = 1; i < captureAmount; i++) {
                moves[i] = captureTemp.substring(i * 2 - 2, i * 2);
            }
            return moves;
        } else {
            String[] moves = new String[amount];
            moves[0] = "false";
            for (int i = 1; i < amount; i++) {
                moves[i] = temp.substring(i * 2 - 2, i * 2);
            }
            return moves;
        }
    }

    // totalPossibleMoves
    // returns a 2d String array with all the possible moves in a position for a
    // given player
    // String[][] board - a 2d array that represents the board
    // boolean player - which player's turn it is
    // This method takes a position and player and returns all possible moves for
    // that player
    public static String[][] totalPossibleMoves(String[][] board, boolean player) {
        int amount = 0;
        int most = 0;
        String[][] allMoves;
        String[] tempMoves;
        boolean capture = false;
        outerLoop: for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    if ((board[i][j].charAt(0) == 'W') == player) {
                        tempMoves = possibleMoves(board, i * 10 + j);
                        if (tempMoves[0].equals("true")) {
                            capture = true;
                            break outerLoop;
                        }
                        if (tempMoves.length > 1) {
                            amount++;
                        }
                        if (tempMoves.length > most) {
                            most = tempMoves.length;
                        }
                    }
                }
            }
        }
        if (capture) {
            amount = 0;
            most = 0;
            for (int e = 0; e < 8; e++) {
                for (int u = 0; u < 8; u++) {
                    if (board[e][u] != null) {
                        if ((board[e][u].charAt(0) == 'W') == player) {
                            tempMoves = possibleMoves(board, e * 10 + u);
                            if (tempMoves[0].equals("true")) {
                                amount++;
                            }
                            if (tempMoves[0].equals("true") && tempMoves.length > most) {
                                most = tempMoves.length;
                            }
                        }
                    }
                }
            }
        }
        allMoves = new String[amount][most];
        amount = -1;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    if ((board[i][j].charAt(0) == 'W') == player) {
                        tempMoves = possibleMoves(board, i * 10 + j);
                        if (tempMoves.length > 1) {
                            if (capture) {
                                if (tempMoves[0].equals("true")) {
                                    amount++;
                                    allMoves[amount][0] = String.valueOf(i * 10 + j);
                                    for (int y = 1; y < tempMoves.length; y++) {
                                        allMoves[amount][y] = tempMoves[y];
                                    }
                                }
                            } else {
                                amount++;
                                allMoves[amount][0] = String.valueOf(i * 10 + j);
                                for (int y = 1; y < tempMoves.length; y++) {
                                    allMoves[amount][y] = tempMoves[y];
                                }
                            }
                        }
                    }
                }
            }
        }
        return allMoves;
    }

    // evaluation
    // returns a double that represents the evaluation of a position
    // String[][] board - a 2d array that represents the board
    // boolean player - which player's turn it is
    // This method calculates the evaluation given a position and player
    public static double evaluation(String[][] board, boolean player) {
        double whitePieces = 0;
        double blackPieces = 0;
        double whiteKings = 0;
        double blackKings = 0;
        double distance = 0;
        double basicEval;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    if (board[i][j].charAt(0) == 'W') {
                        whitePieces++;
                        if (board[i][j].charAt(1) == 'K') {
                            whiteKings++;
                        }
                    } else {
                        blackPieces++;
                        if (board[i][j].charAt(1) == 'K') {
                            blackKings++;
                        }
                    }
                }
            }
        }
        if (totalPossibleMoves(board, true).length == 0 && player) {
            return -999;
        } else if (totalPossibleMoves(board, false).length == 0 && !player) {
            return 999;
        }
        if ((whitePieces <= 5 || blackPieces <= 5) && Math.abs(whitePieces - blackPieces) > 0) {
            distance = averagePieceDistance(board, (int) whitePieces, (int) blackPieces) / 10;
        }
        basicEval = whitePieces - blackPieces + (whiteKings * 0.5 - blackKings * 0.5);
        if (basicEval == 0) {
            return basicEval;
        }
        if (player) {
            if (basicEval > 0) {
                return basicEval - distance - blackPieces / 48;
            } else if (basicEval < 0) {
                return basicEval + distance + whitePieces / 48;
            }
        } else {
            if (basicEval > 0) {
                return basicEval - distance - blackPieces / 48;
            } else if (basicEval < 0) {
                return basicEval + distance + whitePieces / 48;
            }
        }
        return basicEval;
    }

    // possibleBoards
    // returns a 3d String array with all the possible positions for a given board
    // and player
    // String[][] board - a 2d array that represents the board
    // boolean player - which player's turn it is
    // This method returns all the possible boards given the starting board and
    // player
    public static String[][][] possibleBoards(String[][] board, boolean player) {
        int counter = 0;
        int j = 1;
        int i = 0;
        int piece, move, pieceRow, pieceColumn, moveRow, moveColumn, differenceX, differenceY;
        String[][] allMoves = totalPossibleMoves(board, player);
        for (int r = 0; r < allMoves.length; r++) {
            for (int t = 1; t < allMoves[0].length; t++) {
                if (allMoves[r][t] != null) {
                    counter++;
                }
            }
        }
        String[][][] allBoards = new String[counter][8][8];
        for (int z = 0; z < counter; z++) {
            while (allMoves[i][j] == null) {
                if (j < allMoves[0].length - 1) {
                    j++;
                } else {
                    i++;
                    j = 1;
                }
            }
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    if (x * 10 + y != Integer.parseInt(allMoves[i][0])
                            && x * 10 + y != Integer.parseInt(allMoves[i][j])) {
                        allBoards[z][x][y] = board[x][y];
                    } else {
                        piece = Integer.parseInt(allMoves[i][0]);
                        move = Integer.parseInt(allMoves[i][j]);
                        pieceRow = (piece - piece % 10) / 10;
                        pieceColumn = piece % 10;
                        moveRow = (move - move % 10) / 10;
                        moveColumn = move % 10;
                        if (Math.abs(pieceRow - moveRow) == 1) {
                            if (x * 10 + y == piece) {
                                allBoards[z][x][y] = null;
                            } else {
                                allBoards[z][x][y] = board[pieceRow][pieceColumn];
                            }
                        } else {
                            allBoards[z][moveRow][moveColumn] = board[pieceRow][pieceColumn];
                            allBoards[z][pieceRow][pieceColumn] = null;
                            differenceX = (pieceRow - moveRow) / 2;
                            differenceY = (pieceColumn - moveColumn) / 2;
                            allBoards[z][pieceRow - differenceX][pieceColumn - differenceY] = null;
                        }
                    }
                }
            }
            if (j < allMoves[0].length - 1) {
                j++;
            } else {
                i++;
                j = 1;
            }
        }
        for (int x = 0; x < allBoards.length; x++) {
            allBoards[x] = updateKings(allBoards[x]);
        }
        return allBoards;
    }

    // captureBoards
    // returns a 3d String array with all the positions for a given board and piece
    // String[][] board - a 2d array that represents the board
    // String piece - the moving piece
    // This method returns all the positions involving a capture and the given piece
    // moving
    public static String[][][] captureBoards(String[][] board, String piece) {
        String[][][] allBoards;
        String[][][] output;
        int intPiece = Integer.parseInt(piece);
        int row = (intPiece - intPiece % 10) / 10;
        int column = intPiece % 10;
        String indexes = "";
        if (board[row][column].charAt(0) == 'W') {
            allBoards = possibleBoards(board, true);
        } else {
            allBoards = possibleBoards(board, false);
        }
        for (int i = 0; i < allBoards.length; i++) {
            if (allBoards[i][row][column] == null) {
                indexes += i;
            }
        }
        output = new String[indexes.length()][8][8];
        for (int j = 0; j < indexes.length(); j++) {
            output[j] = allBoards[Integer.parseInt(indexes.substring(j, j + 1))];
        }
        return output;
    }

    // hasCapture
    // returns a boolean value for whether a capture is possible for a given board
    // and player
    // String[][] board - a 2d array that represents the board
    // boolean player - which player's turn it is
    // This method returns true if a capture is possible for a player in a given
    // position and false if not
    public static boolean hasCapture(String[][] board, boolean player) {
        String[][][] allMoves = possibleBoards(board, player);
        int[] pieces = new int[allMoves.length + 1];
        char color;
        if (player) {
            color = 'B';
        } else {
            color = 'W';
        }
        for (int i = 0; i <= allMoves.length; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 8; k++) {
                    if (i == 0) {
                        if (board[j][k] != null) {
                            if (board[j][k].charAt(0) == color) {
                                pieces[i]++;
                            }
                        }
                    } else {
                        if (allMoves[i - 1][j][k] != null) {
                            if (allMoves[i - 1][j][k].charAt(0) == color) {
                                pieces[i]++;
                            }
                        }

                    }
                }
            }
        }
        for (int l = 0; l < pieces.length; l++) {
            if (pieces[0] - pieces[l] > 0) {
                return true;
            }
        }
        return false;
    }

    // pieceCapture
    // returns a boolean value for whether a capture is possible for a given board
    // and piece
    // String[][] board - a 2d array that represents the board
    // boolean piece - the moving piece
    // This method returns true if a capture is possible for a piece in a given
    // position and false if not
    public static boolean pieceCapture(String[][] board, String piece) {
        if (piece == null) {
            return false;
        }
        String[] moves = possibleMoves(board, Integer.parseInt(piece));
        for (int i = 1; i < moves.length; i++) {
            try {
                if (Math.abs(
                        Integer.parseInt(piece.substring(0, 1)) - Integer.parseInt(moves[i].substring(0, 1))) == 2) {
                    return true;
                }
            } catch (NullPointerException e) {
                return false;
            }
        }
        return false;
    }

    // movingPiece
    // returns a String that represents the square a piece has moved to given the
    // before and after positions
    // String[][] board - the board before a move was made
    // String[][] finalBoard - the board after a move was made
    // boolean player - the player that made the move
    // This method takes 2 positions and finds which square the moving piece moved
    // to
    public static String movingPiece(String[][] board, String[][] finalBoard, boolean player) {
        String temp = "";
        String move = "";
        String[] difference;
        int row, column;
        String before, after;
        char color;
        if (player) {
            color = 'W';
        } else {
            color = 'B';
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                try {
                    if (!(board[i][j].equals(finalBoard[i][j]))) {
                        try {
                            if (board[i][j].charAt(0) == color) {
                                temp += i;
                                temp += j;
                            }
                        } catch (NullPointerException e) {
                            try {
                                if (finalBoard[i][j].charAt(0) == color) {
                                    temp += i;
                                    temp += j;
                                }
                            } catch (NullPointerException ee) {
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    if (finalBoard[i][j] != null) {
                        try {
                            if (board[i][j].charAt(0) == color) {
                                temp += i;
                                temp += j;
                            }
                        } catch (NullPointerException ee) {
                            try {
                                if (finalBoard[i][j].charAt(0) == color) {
                                    temp += i;
                                    temp += j;
                                }
                            } catch (NullPointerException eee) {
                            }
                        }
                    }
                }
            }
        }
        difference = new String[temp.length() / 2];
        for (int k = 0; k < temp.length(); k += 2) {
            difference[k / 2] = temp.substring(k, k + 2);
        }
        for (int p = 0; p < difference.length; p++) {
            row = Integer.parseInt(difference[p].substring(0, 1));
            column = Integer.parseInt(difference[p].substring(1));
            before = board[row][column];
            after = finalBoard[row][column];
            if (before == null && after != null) {
                move = difference[p];
                break;
            }
        }
        if (move.length() != 0) {
            return move;
        } else {
            return null;
        }
    }

    // deepCopy
    // returns a 2d String array that is a deepCopy of the given board
    // String[][] board - a 2d array that represents the board
    // This method deep copies a given board
    public static String[][] deepCopy(String[][] board) {
        String[][] output = new String[8][8];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                output[i][j] = board[i][j];
            }
        }
        return output;
    }

    // minMax
    // returns a double which represents either the evaluation or the best move
    // index depending on whether the recursion is complete
    // String[][] board - a 2d array that represents the board
    // boolean player - which player's turn it is
    // int depth - the depth of the engine as well as a countdown for the recursion
    // int firstDepth - the starting depth of the recursion
    // String capturePiece - null if no piece was just captured and the coordintes
    // of the capturing piece if a capture was just made
    // double alpha - the alpha value in alpha-beta pruning
    // double beta - the beta value in alpha-beta pruning
    // This method simulates the possible positions after depth amount of moves. It
    // then evaluates those positions and backtracks, assumes that each player will
    // make the best move, and finally return the index for the best move in either
    // possibleBoards() or captureBoards()
    public static double minMax(String[][] board, boolean player, boolean capture, int depth, int firstDepth,
            String capturePiece, double alpha, double beta) {
        if (depth == 0 || totalPossibleMoves(board, player).length == 0) {
            return evaluation(board, player);
        }
        int bestMove = -1;
        double eval = 0;
        double minEval = Double.POSITIVE_INFINITY;
        double maxEval = Double.NEGATIVE_INFINITY;
        boolean aCapture;
        String[][][] allBoards;
        if (pieceCapture(board, capturePiece)) {
            player = !player;
            aCapture = hasCapture(board, player);
            allBoards = captureBoards(board, capturePiece);
        } else {
            aCapture = hasCapture(board, player);
            allBoards = possibleBoards(board, player);
        }
        for (int i = 0; i < allBoards.length; i++) {
            if (aCapture || pieceCapture(board, capturePiece)) {
                eval = minMax(allBoards[i], !player, aCapture, depth - 1, firstDepth,
                        movingPiece(board, allBoards[i], player), alpha, beta);
            } else {
                eval = minMax(allBoards[i], !player, aCapture, depth - 1, firstDepth, null, alpha, beta);
            }
            if (player) {
                if (eval > maxEval) {
                    maxEval = eval;
                    if (depth == firstDepth) {
                        bestMove = i;
                    }
                }
                alpha = Math.max(alpha, maxEval);
                if (beta <= alpha) {
                    break;
                }
            } else {
                if (eval < minEval) {
                    minEval = eval;
                    if (depth == firstDepth) {
                        bestMove = i;
                    }
                }
                beta = Math.min(beta, minEval);
                if (beta <= alpha) {
                    break;
                }
            }
        }
        if (depth == firstDepth) {
            return bestMove;
        } else {
            if (player) {
                return maxEval;
            } else {
                return minEval;
            }
        }
    }

    // fileSave
    // String[][] board - a 2d array that represents the board
    // boolean player - which player's turn it is
    // int gametype - 0 for computer vs player, 1 for player vs player, and 2 for
    // computer vs computer
    // int depth - the depth of the engine
    // This method takes the position, player, gametype, and depth and writes it
    // into a file
    public static void fileSave(String[][] board, boolean player, int gametype, int depth) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("fileSave.txt", false));
            out.write(String.valueOf(player));
            out.newLine();
            out.write(String.valueOf(gametype));
            out.newLine();
            out.write(String.valueOf(depth));
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    out.newLine();
                    if (board[i][j] == null) {
                        out.write("none");
                    } else {
                        out.write(board[i][j]);
                    }
                }
            }
            out.close();
        } catch (IOException e) {
        }
    }

    // boardFileRead
    // returns a 2d String array that represents the board
    // String fileName - the name of the file the method is reading
    // This method takes a file name and reads it to find the position of the board
    public static String[][] boardFileRead(String fileName) {
        String[][] output = new String[8][8];
        int i = 0;
        int j = -1;
        String lineIn;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            in.readLine();
            in.readLine();
            in.readLine();
            lineIn = in.readLine();
            while (lineIn != null) {
                if (j < 7) {
                    j++;
                } else {
                    j = 0;
                    i++;
                }
                if (lineIn.equals("none")) {
                    output[i][j] = null;
                } else {
                    output[i][j] = lineIn;
                }
                lineIn = in.readLine();
            }
            in.close();
        } catch (IOException e) {
        }
        return output;
    }

    // extrasFileRead
    // returns a String array that contains the player, gametype, and depth
    // String fileName - the name of the file the method is reading
    // This method takes a file name and reads which player's turn it is, the
    // gametype, and engine depth
    public static String[] extrasFileRead(String fileName) {
        String[] output = new String[3];
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            output[0] = in.readLine();
            output[1] = in.readLine();
            output[2] = in.readLine();

            in.close();
        } catch (IOException e) {
        }
        return output;
    }

    // shiftArray
    // returns a 3d String array that has shifted the input into the last index of
    // the given array and removed the information in the first index
    // String[][][] array - the array that will get it's information shifted
    // String[][] input - the input that will go into the last index of an array
    // This method takes an array and information and puts that information into the
    // last index of the array while shifting all the other information down an
    // index and removing the first index's information
    public static String[][][] shiftArray(String[][][] array, String[][] input) {
        input = deepCopy(input);
        String[][] nulls = new String[8][8];
        for (int i = 0; i < array.length; i++) {
            if (equalArray(array[i], nulls)) {
                array[i] = input;
                return array;
            }
        }
        for (int i = 0; i < array.length - 1; i++) {
            array[i] = array[i + 1];
        }
        array[array.length - 1] = input;
        return array;
    }

    // equalArary
    // returns a boolean value that will be true if the given arrays are equal and
    // false if not
    // String[][] array1 - the first array
    // String[][] array2 - the second array
    // This method takes 2 arrays and checks if they are equal
    public static boolean equalArray(String[][] array1, String[][] array2) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (array1[i][j] != array2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // averagePieceDistance
    // returns a double that is the average piece distance between each black and
    // white piece on a given board
    // String[][] board - a 2d array that represents the board
    // int whiteAmount - the amount of white pieces on the board
    // int blackAmount - the amount of black pieces on the board
    // This method takes a position and finds the average distance between each
    // black and white piece
    public static double averagePieceDistance(String[][] board, int whiteAmount, int blackAmount) {
        int[] whitePos = new int[whiteAmount];
        int[] blackPos = new int[blackAmount];
        int whiteCounter = 0;
        int blackCounter = 0;
        int whiteRow, whiteColumn, blackRow, blackColumn;
        double totalAverage = 0;
        double average = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    if (board[i][j].charAt(0) == 'W') {
                        whitePos[whiteCounter] = i * 10 + j;
                        whiteCounter++;
                    } else {
                        blackPos[blackCounter] = i * 10 + j;
                        blackCounter++;
                    }
                }
            }
        }
        for (int i = 0; i < whiteAmount; i++) {
            for (int j = 0; j < blackAmount; j++) {
                whiteRow = (whitePos[i] - whitePos[i] % 10) / 10;
                whiteColumn = whitePos[i] % 10;
                blackRow = (blackPos[j] - blackPos[j] % 10) / 10;
                blackColumn = blackPos[j] % 10;
                average += Math.sqrt(Math.pow(whiteRow - blackRow, 2) + Math.pow(whiteColumn - blackColumn, 2));
            }
            totalAverage += average / blackAmount;
            average = 0;
        }
        return totalAverage / whiteAmount;
    }
}