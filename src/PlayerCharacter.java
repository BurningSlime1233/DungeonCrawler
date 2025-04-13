import java.util.*;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import org.json.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

class GameGui {
    private JFrame gui;
    private JButton newGameButton, loadGameButton, creditsButton, playWithFriendsButton, exitButton;
    private File loadedSave;
    private PlayerCharacter player;
    private Dungeon currentDungeon;
    private Room currentRoom;
    private JPanel currentRoomPanel;
    private GraphPanel graphPanel;

    public void mainMenu() {
        if (gui == null) {
            gui = new JFrame("Dungeon Crawler");
            gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gui.setSize(1680, 1050);
        }
        // Clear old components (if any)
        gui.getContentPane().removeAll();
        // Rebuild main menu components
        buildMainMenu();
        gui.revalidate();
        gui.repaint();
        gui.setVisible(true);
    }

    private void buildMainMenu() {
        gui.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Set background color using only Swing
        gui.getContentPane().setBackground(new javax.swing.plaf.ColorUIResource(30, 30, 30));

        // Title Label
        JLabel titleLabel = new JLabel("DungeonCrawler");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 20, 0); // Top padding
        gui.add(titleLabel, gbc);

        // New Game Button
        newGameButton = new JButton("New Game");
        newGameButton.setPreferredSize(new Dimension(200, 50));
        gbc.gridy = 1;
        gui.add(newGameButton, gbc);

        // Load Game Button
        loadGameButton = new JButton("Load Game");
        loadGameButton.setPreferredSize(new Dimension(200, 50));
        gbc.gridy = 2;
        gui.add(loadGameButton, gbc);

        // Play with Friends Button
        playWithFriendsButton = new JButton("Play with Friends");
        playWithFriendsButton.setPreferredSize(new Dimension(200, 50));
        gbc.gridy = 3;
        gui.add(playWithFriendsButton, gbc);

        // Credits Button
        creditsButton = new JButton("Credits");
        creditsButton.setPreferredSize(new Dimension(200, 50));
        gbc.gridy = 4;
        gui.add(creditsButton, gbc);

        // Exit Button
        exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(200, 50));
        gbc.gridy = 5;
        gui.add(exitButton, gbc);

        // Button Actions
        newGameButton.addActionListener(e -> newGameMenu());
        loadGameButton.addActionListener(e -> loadGameMenu());
        playWithFriendsButton.addActionListener(e -> {
            String ipAddress = JOptionPane.showInputDialog(gui, "Enter IP Address to connect:");
            if (ipAddress != null && !ipAddress.isEmpty()) {
                connectToPlayer(ipAddress);
            }
        });
        creditsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(gui, "Game by Harshil Shah, Harshil Amin & Aniket Gaikwad");
        });
        exitButton.addActionListener(e -> {
            System.exit(0);
        });
    }

    private void connectToPlayer(String ipAddress) {
        try {
            Socket socket = new Socket(ipAddress, 12345); // Example port number
            // Obtain the local IP address from the socket.
            String localIp = socket.getLocalAddress().getHostAddress();
            JOptionPane.showMessageDialog(gui,
                    "Connected to " + ipAddress + "\nYour IP Address: " + localIp);
            // Handle further communication with the player here
        } catch (IOException e) {
            JOptionPane.showMessageDialog(gui, "Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Function to show new game menu
    public void newGameMenu() {
        gui.getContentPane().removeAll();
        gui.repaint();
        gui.setLayout(new BorderLayout());

        // Main form panel
        JPanel formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createEmptyBorder(100, 0, 0, 0));
        formPanel.setOpaque(false);

        // Use same sizes as mainMenu
        Font labelFont = new Font("Arial", Font.PLAIN, 8);
        Font fieldFont = new Font("Arial", Font.PLAIN, 8);
        Dimension fieldSize = new Dimension(120, 24);
        Dimension buttonSize = new Dimension(120, 24);

        // Set text color to white
        Color textColor = Color.WHITE;

        // Name field
        JLabel nameLabel = new JLabel("Character Name:");
        nameLabel.setFont(labelFont);
        nameLabel.setForeground(textColor);
        JTextField nameField = new JTextField();
        nameField.setFont(fieldFont);
        nameField.setPreferredSize(fieldSize);
        nameField.setForeground(textColor);
        nameField.setBackground(new Color(30, 30, 30));

        // Class selection
        JLabel classLabel = new JLabel("Select Class:");
        classLabel.setFont(labelFont);
        classLabel.setForeground(textColor);
        String[] classes = { "Warrior", "Mage", "Rogue" };
        JComboBox<String> classDropdown = new JComboBox<>(classes);
        classDropdown.setFont(fieldFont);
        classDropdown.setPreferredSize(fieldSize);
        classDropdown.setForeground(textColor);
        classDropdown.setBackground(new Color(30, 30, 30));

        // Create button
        JButton createButton = new JButton("Create Character");
        createButton.setFont(fieldFont);
        createButton.setPreferredSize(buttonSize);
        createButton.setForeground(textColor);
        createButton.setBackground(new Color(30, 30, 30));

        // Return to main menu button - now resets panel instead of creating new window
        JButton returnButton = new JButton("Return to Main Menu");
        returnButton.setFont(fieldFont);
        returnButton.setPreferredSize(buttonSize);
        returnButton.setForeground(textColor);
        returnButton.setBackground(new Color(30, 30, 30));
        returnButton.addActionListener(e -> mainMenu());
        createButton.addActionListener(e -> {
            String name = nameField.getText();
            String selectedClass = (String) classDropdown.getSelectedItem();
            if (isValidName(name)) {
                createNewSave(name, selectedClass);
            } else {
                JOptionPane.showMessageDialog(gui, "Invalid name! Use only valid characters.");
            }
        });

        // Add components
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(classLabel);
        formPanel.add(classDropdown);
        formPanel.add(createButton);
        formPanel.add(returnButton);

        gui.add(formPanel, BorderLayout.CENTER);
        gui.revalidate();
        gui.repaint();
    }

    public void createNewSave(String name, String characterClass) {
        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(gui, "Character name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (characterClass == null) {
            JOptionPane.showMessageDialog(gui, "Please select a character class", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File save = new File(name + ".json");
        try {
            JSONObject characterData = new JSONObject();
            characterData.put("Name", name.trim());
            characterData.put("Class", characterClass);

            // Set stats based on class
            switch (characterClass) {
                case "Warrior":
                    characterData.put("Strength", 15);
                    characterData.put("Dexterity", 8);
                    characterData.put("Toughness", 12);
                    characterData.put("Magic", 5);
                    characterData.put("Willpower", 8);
                    break;
                case "Mage":
                    characterData.put("Strength", 5);
                    characterData.put("Dexterity", 8);
                    characterData.put("Toughness", 6);
                    characterData.put("Magic", 15);
                    characterData.put("Willpower", 12);
                    break;
                case "Rogue":
                    characterData.put("Strength", 8);
                    characterData.put("Dexterity", 15);
                    characterData.put("Toughness", 8);
                    characterData.put("Magic", 5);
                    characterData.put("Willpower", 8);
                    break;
            }

            // Common stats
            characterData.put("Level", 1);
            characterData.put("Exp", 0);
            characterData.put("HP", 100);
            characterData.put("Stamina", 100);
            characterData.put("Gold", 50);
            characterData.put("Base Seed", Math.random());
            characterData.put("Dungeons Completed", 0);

            try (FileWriter fileWriter = new FileWriter(save)) {
                fileWriter.write(characterData.toString(4));
                fileWriter.flush();
                JOptionPane.showMessageDialog(gui, "Character created successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadSave(save); // Load the newly created save which will open dungeon screen
            } catch (IOException e) {
                JOptionPane.showMessageDialog(gui,
                        "Failed to save character data:\n" + e.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (JSONException e) {
            JOptionPane.showMessageDialog(gui,
                    "Error creating character data:\n" + e.getMessage(),
                    "Creation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadSave(File file) {
        try {
            player = new PlayerCharacter(file);
            loadedSave = file;
            dungeonScreen();
        } catch (IOException e) {
            String message = e.getMessage().contains("Missing required field")
                    ? "Invalid save file format:\n" + e.getMessage() + "\n\nThe save file may be corrupted."
                    : "Failed to load save file:\n" + e.getMessage()
                            + "\n\nPlease make sure the file exists and is accessible.";

            JOptionPane.showMessageDialog(gui,
                    message,
                    "Load Error", JOptionPane.ERROR_MESSAGE);
            mainMenu(); // Return to main menu on error
        }
    }

    // Function to check if the save name is valid
    public boolean isValidName(String name) {
        String regex = "^(?!\\.|\\.\\.)([^<>:\"/\\\\|?*]+)$";
        return Pattern.matches(regex, name);
    }

    public void displayStats() {
        if (!loadedSave.exists()) {
            JOptionPane.showMessageDialog(gui, "Save file not found!");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(loadedSave))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONObject characterData = new JSONObject(jsonContent.toString());

            // Create a new JFrame to display stats
            JFrame statsFrame = new JFrame(loadedSave.getName() + "'s Stats");
            statsFrame.setSize(300, 400);
            statsFrame.setLayout(new BoxLayout(statsFrame.getContentPane(), BoxLayout.Y_AXIS));

            // Display stats in a label
            String statsText = "<html>";
            statsText += "<b>Name:</b> " + characterData.getString("Name") + "<br>";
            statsText += "<b>Class:</b> " + characterData.getString("Class") + "<br>";
            statsText += "<b>Level:</b> " + characterData.getInt("Level") + "<br>";
            statsText += "<b>Exp:</b> " + characterData.getInt("Exp") + "<br>";
            statsText += "<b>HP:</b> " + characterData.getInt("HP") + "<br>";
            statsText += "<b>Stamina:</b> " + characterData.getInt("Stamina") + "<br>";
            statsText += "<b>Gold:</b> " + characterData.getInt("Gold") + "<br>";
            statsText += "<b>Strength:</b> " + characterData.getInt("Strength") + "<br>";
            statsText += "<b>Dexterity:</b> " + characterData.getInt("Dexterity") + "<br>";
            statsText += "<b>Toughness:</b> " + characterData.getInt("Toughness") + "<br>";
            statsText += "<b>Magic:</b> " + characterData.getInt("Magic") + "<br>";
            statsText += "<b>Willpower:</b> " + characterData.getInt("Willpower") + "<br>";
            statsText += "<b>Dungeons Completed:</b> " + characterData.getInt("Dungeons Completed") + "<br>";
            statsText += "</html>";

            JLabel statsLabel = new JLabel(statsText);
            statsFrame.add(statsLabel);

            // Show the stats window
            statsFrame.setVisible(true);
        } catch (IOException | JSONException e) {
            JOptionPane.showMessageDialog(gui, "Error loading stats: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadGameMenu() {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setDialogTitle("Select Save File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON Files", "json"));

        int userSelection = fileChooser.showOpenDialog(gui);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            loadedSave = fileChooser.getSelectedFile();
            loadSave(loadedSave); // loads the save and calls dungeonScreen().
        }
    }

    public void dungeonScreen() {
        gui.getContentPane().removeAll();
        gui.setLayout(new BorderLayout());

        // -- Get or generate the dungeon as usual --
        long seed = 0;
        try {
            seed = (long) (player.getCharacterData().getDouble("Base Seed") * 100000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        currentDungeon = new Dungeon((double) seed);

        // Set currentRoom based on dungeon generation
        for (Room room : currentDungeon.getRoom()) {
            if (room.getId() == 1 && "startingRoom".equals(room.getType())) {
                currentRoom = room;
                break;
            }
        }
        if (currentRoom == null && !currentDungeon.getRoom().isEmpty()) {
            currentRoom = currentDungeon.getRoom().get(0);
        }

        // 1) LEFT: stats panel
        JPanel statsPanel = buildStatsPanel();
        statsPanel.setPreferredSize(new Dimension(250, 0));
        gui.add(statsPanel, BorderLayout.WEST);

        // 2) TOP: return button panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(30, 30, 30));
        JButton returnButton = new JButton("Return to Main Menu");
        returnButton.addActionListener(e -> mainMenu());
        topPanel.add(returnButton);
        gui.add(topPanel, BorderLayout.NORTH);

        // 3) CENTER: a split pane dividing the room info panel (left side) and the
        // graph (right side)
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplit.setDividerLocation(400); // Adjust how much space goes to the left
        centerSplit.setOneTouchExpandable(true); // Little arrows for easy resizing
        centerSplit.setContinuousLayout(true);

        // (A) The room info panel
        currentRoomPanel = new JPanel();
        currentRoomPanel.setBackground(new Color(30, 30, 30));
        currentRoomPanel.setLayout(new BoxLayout(currentRoomPanel, BoxLayout.Y_AXIS));
        updateRoomPanel();
        centerSplit.setLeftComponent(currentRoomPanel);

        // (B) The graph panel
        String playerClass;
        try {
            playerClass = player.getCharacterData().getString("Class");
        } catch (JSONException e1) {
            e1.printStackTrace();
            playerClass = "Unknown";
        }
        graphPanel = new GraphPanel(currentDungeon, currentRoom, playerClass);
        centerSplit.setRightComponent(graphPanel);

        gui.add(centerSplit, BorderLayout.CENTER);

        gui.revalidate();
        gui.repaint();
        gui.setVisible(true);
    }

    private void updateRoomPanel() {
        currentRoomPanel.removeAll();

        // Room details as individual labels.
        JLabel roomTitle = new JLabel("Room " + currentRoom.getId());
        roomTitle.setForeground(Color.WHITE);
        currentRoomPanel.add(roomTitle);

        JLabel typeLabel = new JLabel("Type: " + currentRoom.getType());
        typeLabel.setForeground(Color.WHITE);
        currentRoomPanel.add(typeLabel);

        JLabel obstacleLabel = new JLabel("Has Obstacle: " + currentRoom.hasObstacle());
        obstacleLabel.setForeground(Color.WHITE);
        currentRoomPanel.add(obstacleLabel);

        if (currentRoom.hasObstacle()) {
            JLabel obstacleTypeLabel = new JLabel("Obstacle Type: " + currentRoom.getObstacleType());
            obstacleTypeLabel.setForeground(Color.WHITE);
            currentRoomPanel.add(obstacleTypeLabel);
        }

        currentRoomPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

        JLabel connectedLabel = new JLabel("Connected Rooms:");
        connectedLabel.setForeground(Color.WHITE);
        currentRoomPanel.add(connectedLabel);

        // Create a panel for neighbor movement buttons.
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(30, 30, 30));
        for (Room neighbor : currentRoom.getNeighbors()) {
            JButton moveButton = new JButton("Go to Room " + neighbor.getId());
            moveButton.addActionListener(e -> {
                currentRoom = neighbor;
                updateRoomPanel();
                // Inform the graph panel to update the current room.
                graphPanel.setCurrentRoom(currentRoom);
            });
            buttonPanel.add(moveButton);
        }
        currentRoomPanel.add(buttonPanel);

        currentRoomPanel.revalidate();
        currentRoomPanel.repaint();
    }

    private JPanel buildStatsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(new Color(50, 50, 50));
        statsPanel.setPreferredSize(new Dimension(250, 0)); // Fixed width

        // Add a title.
        JLabel titleLabel = new JLabel("Player Stats");
        titleLabel.setForeground(Color.WHITE);
        statsPanel.add(titleLabel);
        statsPanel.add(Box.createVerticalStrut(10));

        // Retrieve stats from the player's JSON object.
        try {
            JSONObject characterData = player.getCharacterData();
            statsPanel.add(createStatLine("Name: " + characterData.getString("Name")));
            statsPanel.add(createStatLine("Class: " + characterData.getString("Class")));
            statsPanel.add(createStatLine("Level: " + characterData.getInt("Level")));
            statsPanel.add(createStatLine("Exp: " + characterData.getInt("Exp")));
            statsPanel.add(createStatLine("HP: " + characterData.getInt("HP")));
            statsPanel.add(createStatLine("Stamina: " + characterData.getInt("Stamina")));
            statsPanel.add(createStatLine("Gold: " + characterData.getInt("Gold")));
            statsPanel.add(createStatLine("Strength: " + characterData.getInt("Strength")));
            statsPanel.add(createStatLine("Dexterity: " + characterData.getInt("Dexterity")));
            statsPanel.add(createStatLine("Toughness: " + characterData.getInt("Toughness")));
            statsPanel.add(createStatLine("Magic: " + characterData.getInt("Magic")));
            statsPanel.add(createStatLine("Willpower: " + characterData.getInt("Willpower")));
            statsPanel.add(createStatLine("Dungeons Completed: " + characterData.getInt("Dungeons Completed")));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return statsPanel;
    }

    private JLabel createStatLine(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        return label;
    }

    public static void main(String[] args) {
        GameGui g = new GameGui();
        g.mainMenu();
    }
}

class Room {
    private int id;
    private String type;
    private boolean hasObstacle;
    private String obstacleType;
    private java.util.List<Room> neighbors;
    private boolean visited;
    private int x, y;

    public Room(int id, String type, String y) {
        this.id = id;
        this.type = type;
        hasObstacle = true;
        obstacleType = y;
        this.neighbors = new ArrayList<>();
        visited = false;
    }

    public boolean hasObstacle() {
        return (hasObstacle);
    }

    public String getObstacleType() {
        return (obstacleType);
    }

    public boolean isVisited() {
        return (visited);
    }

    public String getType() {
        return type;
    }

    public Room(int id, String type) {
        this.id = id;
        this.type = type;
        hasObstacle = false;
        obstacleType = "None";
        this.neighbors = new ArrayList<>();
        visited = false;
    }

    public int getId() {
        return id;
    }

    public java.util.List<Room> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(Room room) {
        if (!neighbors.contains(room)) {
            neighbors.add(room);
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point getPosition() {
        return new Point(x, y);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Room " + id + " -> ");
        for (Room neighbor : neighbors) {
            sb.append(neighbor.getId()).append(" ");
        }
        return sb.toString();
    }
}

class Dungeon {
    private java.util.List<Room> rooms;
    private Random random;
    private double size;
    private String theme;

    public Dungeon(double seed) {
        rooms = new ArrayList<>();
        // Initialize random with seed cast to long.
        random = new Random((long) seed);
        size = (random.nextDouble() * 1.25) + 0.75;
        int noRooms = (int) (size * 8);
        int noDoors = (int) (((noRooms - 1) * noRooms / 2) * (random.nextDouble() * 0.05) + 0.1);

        String[] dungeonThemes = {
                "Swampy", "Sewer", "Magical", "Volcanic", "Frozen Ice Cavern",
                "Ancient Ruins", "Haunted Crypt", "Dark Forest", "Desert Tomb",
                "Clockwork Fortress", "Corrupted Temple", "Crystal Cavern",
                "Underwater Shrine", "Abandoned Mine", "Blood Catacombs",
                "Shadow Realm", "Celestial Tower", "Fungal Hollow",
                "Cursed Library", "Beast Lair"
        };

        String[] roomObstacles = {
                "LCK", // Lock
                "TRP", // Damaging trap
                "MAG", // Magic Barrier
                "BRW", // Breakable Wall
                "DMN" // Demon Guardian
        };

        // Define the probabilities for each room type (except the starting room)
        // Monster: 50%, Treasure: 30%, Upgrade: 10%, Shop: 10%
        // The cumulative probability values are used to decide the type.
        // (i.e. chance < 0.5 for Monster, chance >= 0.5 and < 0.8 for Treasure, etc.)

        this.theme = dungeonThemes[random.nextInt(dungeonThemes.length)];

        // Create rooms with correct room type and obstacle assignment.
        for (int i = 0; i < noRooms; i++) {
            String roomType;
            // Room with id 1 is special.
            if (i == 1) {
                roomType = "startingRoom";
            } else {
                double chance = random.nextDouble();
                if (chance < 0.5) {
                    roomType = "Monster";
                } else if (chance < 0.8) {
                    roomType = "Treasure";
                } else if (chance < 0.9) {
                    roomType = "Upgrade";
                } else {
                    roomType = "Shop";
                }
            }
            // Determine if an obstacle should be added (25% chance).
            boolean hasObstacle = random.nextDouble() < 0.25;
            if (hasObstacle) {
                // Choose an obstacle type randomly from the available options.
                String obstacleType = roomObstacles[random.nextInt(roomObstacles.length)];
                // Use the three-argument constructor.
                rooms.add(new Room(i, roomType, obstacleType));
            } else {
                // Use the two-argument constructor.
                rooms.add(new Room(i, roomType));
            }
        }
        // First create minimum spanning tree to ensure connectivity
        List<Room> connected = new ArrayList<>();
        List<Room> unconnected = new ArrayList<>(rooms);

        // Start with random room
        Room first = unconnected.remove(random.nextInt(unconnected.size()));
        connected.add(first);

        while (!unconnected.isEmpty()) {
            Room toConnect = unconnected.remove(random.nextInt(unconnected.size()));
            Room connectTo = connected.get(random.nextInt(connected.size()));

            toConnect.addNeighbor(connectTo);
            connectTo.addNeighbor(toConnect);
            connected.add(toConnect);
        }

        // Add additional random connections
        int extraConnections = noDoors - (noRooms - 1);
        while (extraConnections > 0) {
            int fromIndex = random.nextInt(noRooms);
            int toIndex = random.nextInt(noRooms);

            if (fromIndex != toIndex) {
                Room fromRoom = rooms.get(fromIndex);
                Room toRoom = rooms.get(toIndex);

                if (!fromRoom.getNeighbors().contains(toRoom)) {
                    fromRoom.addNeighbor(toRoom);
                    toRoom.addNeighbor(fromRoom);
                    extraConnections--;
                }
            }
        }

        // Assign random positions to nodes
        for (Room node : rooms) {
            int x = 50 + random.nextInt(1200);
            int y = 50 + random.nextInt(800);
            node.setPosition(x, y);

        }
    }

    public java.util.List<Room> getRoom() {
        return rooms;
    }

    public void printGraph() {
        for (Room room : rooms) {
            System.out.println(room);
        }
    }
}

class GraphPanel extends JPanel {
    private Dungeon dungeon;
    private Room currentRoom;
    private String playerEmoji;
    // Increase node size: Using a radius of 30 (i.e., diameter of 60)
    private final int NODE_RADIUS = 30;
    private final int NODE_DIAMETER = NODE_RADIUS * 2;
    // Increase font size for labels/emoji.
    private final int FONT_SIZE = 28;

    public GraphPanel(Dungeon dungeon, Room currentRoom, String playerClass) {
        this.dungeon = dungeon;
        this.currentRoom = currentRoom;
        this.playerEmoji = getEmojiForClass(playerClass);
        // Increase preferred size if needed.
        setPreferredSize(new Dimension(900, 600));
        setBackground(Color.BLACK); // Black background
    }

    // Setter to update current room and then repaint the graph.
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
        repaint();
    }

    // Utility method to map player class to an emoji.
    private String getEmojiForClass(String playerClass) {
        switch (playerClass) {
            case "Warrior":
                return "⚔️";
            case "Mage":
                return "🧙‍♂️";
            case "Rogue":
                return "🐱‍👤";
            default:
                return "🕹️";
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGraph(g);
    }

    private void drawGraph(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // Use a thicker stroke to match the enlarged nodes.
        g2d.setStroke(new BasicStroke(3));

        // Draw edges with a light gray color.
        g2d.setColor(Color.LIGHT_GRAY);
        for (Room room : dungeon.getRoom()) {
            Point p1 = room.getPosition();
            for (Room neighbor : room.getNeighbors()) {
                Point p2 = neighbor.getPosition();
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        // Set a common font for node labels.
        g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, FONT_SIZE));

        // Draw nodes.
        for (Room node : dungeon.getRoom()) {
            Point p = node.getPosition();
            if (currentRoom != null && node.getId() == currentRoom.getId()) {
                // Current room: draw a yellow circle and the emoji.
                g2d.setColor(Color.YELLOW);
                g2d.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_DIAMETER, NODE_DIAMETER);
                g2d.setColor(Color.BLACK);
                // Draw the emoji centered in the circle.
                g2d.drawString(playerEmoji, p.x - (NODE_RADIUS / 2), p.y + (NODE_RADIUS / 2));
            } else {
                // Other nodes: draw a cyan circle with room id.
                g2d.setColor(Color.CYAN);
                g2d.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_DIAMETER, NODE_DIAMETER);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_DIAMETER, NODE_DIAMETER);
                // Draw the room id centered in the circle.
                g2d.drawString("R" + node.getId(), p.x - (NODE_RADIUS / 2), p.y + (NODE_RADIUS / 2));
            }
        }
    }
}

public class PlayerCharacter {
    private JSONObject characterData;

    public PlayerCharacter(File jsonFile) throws IOException {
        try {
            loadCharacterData(jsonFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadCharacterData(File jsonFile) throws IOException, JSONException {
        if (!jsonFile.exists()) {
            throw new IOException("Save file does not exist");
        }

        BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();

        // Parse and validate JSON
        characterData = new JSONObject(sb.toString());

        // Validate required fields
        String[] requiredFields = { "Name", "Class", "Level", "HP", "Stamina", "Gold" };
        for (String field : requiredFields) {
            if (!characterData.has(field)) {
                throw new JSONException("Missing required field: " + field);
            }
        }
    }

    public JSONObject getCharacterData() {
        return characterData;
    }

    public String getName() throws JSONException {
        return characterData.getString("Name");
    }

    public int getLevel() throws JSONException {
        return characterData.getInt("Level");
    }
}
