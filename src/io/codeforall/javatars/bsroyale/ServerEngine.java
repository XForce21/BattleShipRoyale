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

    public static boolean step1, step2, step3, gameEnd;


    public static void main(String[] args) {

        int portNumber = 8080;

        //NUMBER_OF_PLAYERS = Integer.parseInt(args[0]);

        ExecutorService connectionPool = Executors.newFixedThreadPool(NUMBER_OF_PLAYERS);

        ServerSocket serverSocket = null;
        try {

            // STEP2: Bind to local port and block while waiting for client connections
            serverSocket = new ServerSocket(portNumber);
            boolean allHaveNick = false;
            boolean allHaveShips = false;
            while (serverSocket.isBound()) {
                if (!step1) {
                    while (users.size() < NUMBER_OF_PLAYERS) {
                        Socket userSocket = serverSocket.accept();
                        User user = new User(userSocket);
                        connectionPool.submit(user);

                        users.add(user);
                        System.out.println("new user connected: " + userSocket);
                    }

                    for (User u : users) {
                        if (!u.isUserNameCheck()) {
                            allHaveNick = false;
                            break;
                        }
                        allHaveNick = true;
                    }

                    if (!allHaveNick) {
                        continue;
                    }
                    broadcast("\nAll players are connected! This is your game grid:\n");
                    for (User u : users) {
                        u.setUserGrid(buildGrid());
                        u.setEnemyGrid(buildGrid());
                    }
                    broadcast(printGrid(users.getFirst().getEnemyGrid()));
                    step1 = true;
                }

                if (!step2) {
                    //System.out.println("here 1");
                    for (User u : users){
                        //System.out.println("here 2 " + u.getUserName());
                        if (!u.hasShips()){
                            allHaveShips = false;
                            //System.out.println("here 3");
                            break;
                        }
                        allHaveShips = true;
                    }

                    if (!allHaveShips){
                        continue;
                    }
                    for (User u : users) {
                        //System.out.println("here 4");
                        broadcast(printGrid(u.getEnemyGrid()), u);
                    }
                    step2 = true;
                }

                if (!step3) {
                    //System.out.println("Arrived at step 3");
                }
            }

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

    public static void broadcast(String message, User user){
        try {
            PrintWriter out = new PrintWriter(user.getUserSocket().getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            e.getMessage();
        }
    }

    private static char[][] buildGrid() {
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
        return grid;
    }

    public static String printGrid(char[][] grid) {
        String message = "";
        for (int i = 0; i < ROWS + 1; i++) {
            for (int j = 0; j < COLS + 1; j++) {
                message += grid[i][j] + " ";
            }
            message += "\n";
        }
        return message;
    }

    public static char[][] cloneGrid(char[][] original) {
        char[][] clone = original.clone();
        for (int i = 0; i < original.length; i++) {
            clone[i] = original[i].clone();
        }
        return clone;
    }


}
