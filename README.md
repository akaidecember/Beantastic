# Beantastic

## Overview

Beantastic is a game built using the RAGE gaming engine (RAGE Game Engine files are required to run that are not provided in this repo), featuring assets that are either self-created or provided by Prof. Gordon. This game offers both single-player and multiplayer modes, allowing you to enjoy it solo or with friends.

## Getting Started

### Prerequisites

Ensure you have the following installed on your system:
- Java Development Kit (JDK)
- A suitable Integrated Development Environment (IDE) (optional, for development purposes)
- Rage Files

### Directory Structure

The main directory structure of the Beantastic project is as follows:

```
Beantastic/
├── src/
│   ├── startSinglePlayer.bat
│   ├── Compile.bat
│   ├── SERVER.bat
│   └── CLIENT.bat
├── assets/
│   └── (game assets here)
├── README.md
└── (other project files)
```

### Running the Game

#### Single Player Mode

To start the game in single-player mode:

1. Navigate to the `Beantastic/src` directory.
2. Double-click or run the `startSinglePlayer.bat` file.

#### Multiplayer Mode

To play in multiplayer mode, follow these steps:

1. **Compile the Game:**
   - Navigate to the `Beantastic/src` directory.
   - Double-click or run the `Compile.bat` file to compile the game.

2. **Start the Server:**
   - After compiling, run the `SERVER.bat` file to start the game server.

3. **Start the Client:**
   - Finally, run the `CLIENT.bat` file to start the game client.

Make sure all players run the `CLIENT.bat` file to join the multiplayer session.

## Disclaimer

All assets used in this project are either self-created or provided by Prof. Gordon, including the RAGE gaming engine.

## Troubleshooting

If you encounter any issues while running the game, consider the following:

- **Java Installation:** Ensure that Java is correctly installed and the PATH variable is set.
- **File Permissions:** Make sure you have the necessary permissions to execute `.bat` files.
- **Network Issues:** For multiplayer mode, ensure all players are on the same network or configure your router/firewall to allow connections.

## Additional Information

For more details on the RAGE gaming engine or to contribute to the project, please refer to the project's documentation or contact the development team.

Enjoy playing Beantastic!
