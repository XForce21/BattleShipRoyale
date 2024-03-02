package io.codeforall.javatars.bsroyale;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.integer.IntegerRangeInputScanner;
import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;

import javax.imageio.IIOException;
import java.io.*;
import java.net.Socket;

public class User implements Runnable {

    private Socket userSocket;
    private String userName;

    private boolean userNameCheck;
    private boolean hasShips;
    private boolean myTurn;
    private char[][] userGrid = new char[ServerEngine.ROWS + 1][ServerEngine.COLS + 1];
    private char[][] enemyGrid = new char[ServerEngine.ROWS + 1][ServerEngine.COLS + 1];

    private Prompt prompt;

    private PrintStream out;
    private BufferedReader in;

    private int hits;

    public User(Socket userSocket) {
        this.userSocket = userSocket;
        hits = 0;
    }

    public Socket getUserSocket() {
        return userSocket;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isUserNameCheck() {
        return userNameCheck;
    }

    public void setUserNameCheck(boolean userNameCheck) {
        this.userNameCheck = userNameCheck;
    }

    public char[][] getUserGrid() {
        return userGrid;
    }

    public void setUserGrid(char[][] userGrid) {
        this.userGrid = userGrid;
    }

    public char[][] getEnemyGrid() {
        return enemyGrid;
    }

    public void setEnemyGrid(char[][] enemyGrid) {
        this.enemyGrid = enemyGrid;
    }

    public boolean hasShips() {
        return hasShips;
    }


    @Override
    public void run() {

        try {
            while (!ServerEngine.gameEnd) {
                if (!userNameCheck) {
                    out = new PrintStream(userSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
                    out.println("UserName: ");
                    userName = in.readLine();
                    Thread.currentThread().setName(userName);
                    userNameCheck = true;
                    ServerEngine.broadcast(ServerEngine.welcomingMessage, this);
                }
                //System.out.println("Step1: " + ServerEngine.step1);
                if (!hasShips && ServerEngine.step1) {
                    System.out.println("User Handler stuck here");
                    ServerEngine.broadcast("\nTime to choose the position of your ships on the grid!\n", this);
                    prompt = new Prompt(userSocket.getInputStream(), out);
                    for (Ships s : Ships.values()) {
                        choseShipPosition(s);
                    }
                    hasShips = true;
                }

                if(ServerEngine.step2 && myTurn){
                    hitAttempt();
                    myTurn = false;
                }

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void choseShipPosition(Ships ship) {
        char[][] tempUserGrid = ServerEngine.cloneGrid(userGrid);
        int xPosition = 1;
        int yPosition = 1;
        String[] options = {"Horizontal", "Vertical"};
        MenuInputScanner menuInputScanner = new MenuInputScanner(options);
        menuInputScanner.setMessage("Choose your ship orientation: ");
        int answer = prompt.getUserInput(menuInputScanner);
        if (answer == 1) {
            IntegerRangeInputScanner inputScanner = new IntegerRangeInputScanner(0, 10 - ship.size);
            inputScanner.setMessage("Choose X initial position for your " + ship + " class ship with " + ship.size + " of length: ");
            xPosition += prompt.getUserInput(inputScanner);
            inputScanner = new IntegerRangeInputScanner(0, 9);
            inputScanner.setMessage("Choose Y position for your " + ship + " class ship: ");
            yPosition += prompt.getUserInput(inputScanner);
            if (checkOccupied(ship.size, xPosition, yPosition, true)) {
                out.println("You chose an already occupied position. Please choose again");
                choseShipPosition(ship);
            }
            addShip(ship.size, xPosition, yPosition, true);

            ServerEngine.broadcast(ServerEngine.printGrid(userGrid), this);

            String[] options2 = {"Yes", "No"};
            MenuInputScanner menuInputScanner2 = new MenuInputScanner(options2);
            menuInputScanner2.setMessage("This is how you placed this ship. Do you want to redo it?");
            if (prompt.getUserInput(menuInputScanner2) == 1){
                userGrid = ServerEngine.cloneGrid(tempUserGrid);
                choseShipPosition(ship);
            }
            return;
        }
        IntegerRangeInputScanner inputScanner = new IntegerRangeInputScanner(0, 9);
        inputScanner.setMessage("Choose X position for your " + ship + " class ship: ");
        xPosition += prompt.getUserInput(inputScanner);
        inputScanner = new IntegerRangeInputScanner(0, 10 - ship.size);
        inputScanner.setMessage("Choose Y initial position for your " + ship + " class ship with " + ship.size + " of length: ");
        yPosition += prompt.getUserInput(inputScanner);
        if (checkOccupied(ship.size, xPosition, yPosition, false)) {
            out.println("You chose an already occupied position. Please choose again");
            choseShipPosition(ship);
        }
        addShip(ship.size, xPosition, yPosition, false);

        ServerEngine.broadcast(ServerEngine.printGrid(userGrid), this);

        String[] options2 = {"Yes", "No"};
        MenuInputScanner menuInputScanner2 = new MenuInputScanner(options2);
        menuInputScanner2.setMessage("This is how you placed this ship. Do you want to redo it?");
        if (prompt.getUserInput(menuInputScanner2) == 1){
            userGrid = ServerEngine.cloneGrid(tempUserGrid);
            choseShipPosition(ship);
        }

    }

    private void addShip(int size, int x, int y, boolean isHorizontal) {
        if (isHorizontal) {
            for (int i = x; i < x + size; i++) {
                userGrid[y][i] = 'O';
            }
            return;
        }
        for (int i = y; i < y + size; i++) {
            userGrid[i][x] = 'O';
        }
    }

    private boolean checkOccupied(int size, int x, int y, boolean isHorizontal) {
        if (isHorizontal) {
            for (int i = x; i < x + size; i++) {
                if (userGrid[y][i] == 'O') {
                    return true;
                }
            }
            return false;
        }
        for (int i = y; i < y + size; i++) {
            if (userGrid[i][x] == 'O') {
                return true;
            }
        }
        return false;
    }

    private void hitAttempt(){
        int x = 1;
        int y = 1;
        ServerEngine.broadcast(ServerEngine.printGrid(enemyGrid), this);
        IntegerRangeInputScanner inputScanner = new IntegerRangeInputScanner(0, 9);
        inputScanner.setMessage("\nYour Turn!\nX Target: ");
        x += prompt.getUserInput(inputScanner);
        inputScanner.setMessage("Y Target: ");
        y += prompt.getUserInput(inputScanner);
        if(enemyGrid[y][x] == 'X' || enemyGrid[y][x] == 'M'){
            out.println("You have already tried to fire at that position. Choose again!");
            hitAttempt();
            return;
        }
        ServerEngine.checkHit(x, y, this);
    }

    public int getHits() {
        return hits;
    }

    public void incrementHits(){
        hits++;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    private enum Ships {
        DESTROYER(2),
        CRUISER(3),
        BATTLESHIP(4);

        private int size;

        Ships(int size) {
            this.size = size;
        }
    }
}
