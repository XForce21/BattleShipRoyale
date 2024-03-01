package io.codeforall.javatars.bsroyale;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerEngine {

    private static LinkedList<User> users = new LinkedList<>();

    public static int NUMBER_OF_PLAYERS = 2;

    public static final int ROWS = 10;
    public static final int COLS = 10;

    public static boolean step1, step2, step3;


    public static void main(String[] args) {

        int portNumber = 8080;

        //NUMBER_OF_PLAYERS = Integer.parseInt(args[0]);

        ExecutorService connectionPool = Executors.newFixedThreadPool(NUMBER_OF_PLAYERS);

        ServerSocket serverSocket = null;
        try {

            // STEP2: Bind to local port and block while waiting for client connections
            serverSocket = new ServerSocket(portNumber);
            int count = 0;
            while (serverSocket.isBound()) {
                Socket userSocket = serverSocket.accept();
                User user = new User(userSocket);
                connectionPool.submit(user);
                // STEP3: Setup input and output streams
                users.add(user);
                System.out.println("new user connected: " + userSocket);

                if (users.size() == NUMBER_OF_PLAYERS) {
                    while (count != NUMBER_OF_PLAYERS) {
                        for (User u : users) {
                            if (u.getUserName() != null && !u.isUserNameCheck()) {
                                count++;
                                u.setUserNameCheck(true);
                                System.out.println(count);
                            }
                        }
                    }
                    broadcast("\nAll Players are connected! Game will start now!\n");
                    buildGrid();
                    broadcast(printGrid(user.getEnemyGrid()));
                    step1 = true;

                    //notifyAll();
                    if (users.get(0).hasShips() && users.get(1).hasShips()) { // change if more than 2 players
                        step2 = true;
                    }
                    if (step2) {

                    }
                }

            }
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getSize() {
        return users.size();
    }

    public static void broadcast(String message) {

        for (User u : users) {
            Socket s = u.getUserSocket();
            PrintWriter out = null;
            try {
                out = new PrintWriter(s.getOutputStream(), true);
                out.println(message);
            } catch (IOException e) {
                e.getMessage();
            }

        }
    }

    private static void buildGrid() {
        char[][] grid = new char[ROWS + 1][COLS + 1];
        grid[0][0] = '|';
        for (int i = 0; i < ROWS + 1; i++) {
            for (int j = 0; j < COLS + 1; j++) {
                if (i == 0 && j > 0) {
                    grid[i][j] = (char) ((j - 1) + '0');

                }
                if (j == 0 && i > 0) {
                    grid[i][j] = (char) ((i - 1) + '0');
                }
                if (i > 0 && j > 0) {
                    grid[i][j] = '~';
                }
            }
        }
        for (User u : users) {
            u.setUserGrid(grid);
            u.setEnemyGrid(grid);
        }
    }

    private static String printGrid(char[][] grid) {
        String message = "";
        for (int i = 0; i < ROWS + 1; i++) {
            for (int j = 0; j < COLS + 1; j++) {
                message += grid[i][j] + " ";
            }
            message += "\n";
        }
        return message;
    }


}
