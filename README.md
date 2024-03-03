# BATTLESHIP ROYALE

## Overview
BATTLESHIP ROYALE is a Java-based implementation of the classic game Battleships, designed for two players to compete against one another over a network. This project was developed as a 7th week weekend project by Luis Carvalho ([LemanuelPC](https://github.com/LemanuelPC)) and Tiago Cunha ([XForce21](https://github.com/XForce21)) during their full-stack bootcamp at Code for All. The game uses the Prompt View library ([Prompt View](https://github.com/academia-de-codigo/prompt-view)) for handling user input and output, creating an engaging terminal-based gaming experience.

## How to Play

### Server Setup
1. **Starting the Server:** A `.jar` file is provided to start the server easily. Run the jar file on the host machine where the game server will operate.
2. **Connecting Players:** Players can connect to the game by using the netcat command in a terminal, targeting the IP address of the machine running the server on port 8080.

### Game Play
- **Username Entry:** Upon connection, players are prompted to enter a username, which will be their displayed player name throughout the game.
- **Placing Ships:** Players choose positions for three types of ships on their grid:
  - **Destroyer:** 2 cells size
  - **Cruiser:** 3 cells size
  - **Battleship:** 4 cells size
  
  Ships can be oriented either horizontally or vertically. For horizontal placement, the initial X position marks the left side of the ship, and for vertical placement, the initial Y position marks the top.

- **Battle Phase:** The game starts once both players have positioned their ships. Players take turns firing at coordinates on the grid, aiming to sink their opponent's ships. The game continues until all ships of one player are sunk.

- **Feedback:** Players are informed of hits, but no details are given on whether a ship has been destroyed, adding a layer of strategy and difficulty.

## Future Improvements
- **Multiplayer Mode:** We are considering the addition of a multiplayer mode that allows more than two players to participate, with adjustments to the gameplay mechanics and rules to accommodate a shared battle grid and ensure a balanced and enjoyable experience.
- **Concurrent Matches:** Plans are in place to enable the server to host multiple matches simultaneously. This enhancement will allow more players to connect and compete, increasing the game's accessibility and community engagement.
- **Local and AI Play:** We plan to create a local version of the game that can be played against the CPU, offering players an opportunity to practice and enjoy the game independently. Additionally, we aim to integrate an option in both the local and server versions for players to choose whether they wish to compete against the CPU or another player. This feature will be available in both the 2-player and potential 2+ player modes, providing a versatile gaming experience that caters to different preferences and scenarios.

## Installation and Running the Server
To start the server, run the provided `.jar` file on the host machine. Players connect to the game by executing the netcat command with the server's IP address on port 8080.

## Contributing
Contributions are welcome! If you have ideas for improvements or have found a bug, feel free to fork the repository, make your changes, and submit a pull request.

## License
This project is licensed under the GNU General Public License v3.0. For more details, see the LICENSE file in the repository.