import java.util.*;
import java.util.regex.Pattern;
import java.io.*;
import org.json.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
<<<<<<< Updated upstream
import java.net.*; // Import for socket programming
=======
import java.net.*;
>>>>>>> Stashed changes

class GameGui {
    JFrame gui;
    JButton newGameButton, loadGameButton, creditsButton, playWithFriendsButton, exitButton;
    File loadedSave;

    public void mainMenu() {
        gui = new JFrame("Dungeon Crawler");
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setSize(1680, 1050);
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
        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newGameMenu();
            }
        });

        loadGameButton.addActionListener(e -> loadGameMenu());

        playWithFriendsButton.addActionListener(e -> {
            String ipAddress = JOptionPane.showInputDialog(gui, "Enter IP Address to connect:");
            if (ipAddress != null && !ipAddress.isEmpty()) {
                connectToPlayer(ipAddress);
            }
        });

        creditsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(gui, "Game by Harshil Shah, Harshil Amin & Aniket Gaikwad");
            }
        });

        exitButton.addActionListener(e -> {
            System.exit(0);
        });

        gui.setVisible(true);
    }

<<<<<<< Updated upstream
    // Function to connect to another player
=======
>>>>>>> Stashed changes
    private void connectToPlayer(String ipAddress) {
        try {
            Socket socket = new Socket(ipAddress, 12345); // Example port number
            JOptionPane.showMessageDialog(gui, "Connected to " + ipAddress);
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

<<<<<<< Updated upstream
        // Set layout for new game menu
        gui.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel nameLabel = new JLabel("Enter Character Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gui.add(nameLabel, gbc);
=======
        // Main form panel
        JPanel formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createEmptyBorder(100, 0, 0, 0));
        formPanel.setOpaque(false);
>>>>>>> Stashed changes

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
<<<<<<< Updated upstream
        nameField.setFont(new Font("Arial", Font.PLAIN, 24));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gui.add(nameField, gbc);

        // Dropdown for RPG Classes
        String[] classes = {"Warrior", "Mage", "Rogue"};
        JComboBox<String> classDropdown = new JComboBox<>(classes);
        classDropdown.setFont(new Font("Arial", Font.PLAIN, 24));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gui.add(classDropdown, gbc);

        JButton saveButton = new JButton("Create Save");
        saveButton.setFont(new Font("Arial", Font.PLAIN, 24));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gui.add(saveButton, gbc);

        // Save button action
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String selectedClass = (String) classDropdown.getSelectedItem();
                if (isValidName(name)) {
                    createNewSave(name, selectedClass);
                } else {
                    JOptionPane.showMessageDialog(gui, "Invalid name! Use only valid characters.");
                }
            }
        });

=======
        nameField.setFont(fieldFont);
        nameField.setPreferredSize(fieldSize);
        nameField.setForeground(textColor);
        nameField.setBackground(new Color(30, 30, 30));

        // Class selection
        JLabel classLabel = new JLabel("Select Class:");
        classLabel.setFont(labelFont);
        classLabel.setForeground(textColor);
        String[] classes = {"Warrior", "Mage", "Rogue", "Cleric"};
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
        returnButton.addActionListener(e -> {
            gui.getContentPane().removeAll();
            mainMenu();
        });
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
>>>>>>> Stashed changes
        gui.revalidate();
        gui.repaint();
    }

<<<<<<< Updated upstream
=======
    public void createNewSave(String name, String characterClass) {
        File save = new File(name + ".json");
        try {
            JSONObject characterData = new JSONObject();
            characterData.put("Name", name);
            characterData.put("Class", characterClass);
            
            // Set stats based on class
            switch(characterClass) {
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
                case "Cleric":
                    characterData.put("Strength", 8);
                    characterData.put("Dexterity", 8);
                    characterData.put("Toughness", 10);
                    characterData.put("Magic", 12);
                    characterData.put("Willpower", 15);
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
                loadedSave = save;
                JOptionPane.showMessageDialog(gui, "Character created!");
                displayStats();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(gui, "Error creating character: " + e.getMessage());
        }
    }

    public void loadSave(File file) throws JSONException {
        try {
            player = new PlayerCharacter(file);
            loadedSave = file;
            dungeonScreen();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(gui, "Failed to load save file: " + e.getMessage());
            e.printStackTrace();
        }
    }

>>>>>>> Stashed changes
    // Function to check if the save name is valid
    public boolean isValidName(String name) {
        String regex = "^(?!\\.|\\.\\.)([^<>:\"/\\\\|?*]+)$";
        return Pattern.matches(regex, name);
    }

    public void createNewSave(String name, String selectedClass) {
        File save = new File(name + ".json");
        try {
            if (save.createNewFile()) {
                JOptionPane.showMessageDialog(gui, "File created: " + save.getName());
            } else {
                JOptionPane.showMessageDialog(gui, "File already exists.");
            }
    
            try (FileWriter fileWriter = new FileWriter(save)) {
                JSONObject characterData = new JSONObject();
                try {
                    characterData.put("Name", name);
                    characterData.put("Class", selectedClass);
                    characterData.put("Level", 1);
                    characterData.put("Exp", 0);
                    characterData.put("HP", selectedClass.equals("Warrior") ? 150 : selectedClass.equals("Mage") ? 100 : 120);
                    characterData.put("Stamina", 100);
                    characterData.put("Gold", 50);
                    characterData.put("Strength", selectedClass.equals("Warrior") ? 15 : 10);
                    characterData.put("Dexterity", selectedClass.equals("Rogue") ? 15 : 10);
                    characterData.put("Toughness", 10);
                    characterData.put("Magic", selectedClass.equals("Mage") ? 15 : 10);
                    characterData.put("Willpower", 10);
                    characterData.put("Base Seed", Math.random());
                    characterData.put("Dungeons Completed", 0);
                    fileWriter.write(characterData.toString(4));
                    fileWriter.flush();
                    JOptionPane.showMessageDialog(gui, "Character name saved.");
                    loadedSave = save;
                } catch (JSONException e) {
                    JOptionPane.showMessageDialog(gui, "JSON Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(gui, "Error saving file: " + e.getMessage());
            e.printStackTrace();
        }
        displayStats();
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
        
        // Add return to main menu button
        JButton returnButton = new JButton("Return to Main Menu");
        returnButton.setFont(new Font("Arial", Font.PLAIN, 8));
        returnButton.setPreferredSize(new Dimension(48, 10));
        returnButton.addActionListener(e -> {
            statsFrame.dispose();
            mainMenu();
        });
        statsFrame.add(returnButton);

            // Show the stats window
            statsFrame.setVisible(true);
        } catch (IOException | JSONException e) {
            JOptionPane.showMessageDialog(gui, "Error loading stats: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadGameMenu() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Save File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON Files", "json"));

        int userSelection = fileChooser.showOpenDialog(gui);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            loadedSave = fileChooser.getSelectedFile();
            displayStats();
        }
    }

<<<<<<< Updated upstream
=======
    public void dungeonScreen() {
        gui.getContentPane().removeAll();
        gui.repaint();
    
        long seed = 0;
        try {
            seed = (long)(player.getCharacterData().getDouble("Base Seed") * 100000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Random r = new Random(seed);
        Dungeon dungeon = new Dungeon(r.nextDouble());
    
        JPanel dungeonPanel = new GraphPanel(dungeon);
        dungeonPanel.setBounds(0, 0, 300, 200);  // Smaller dungeon grid
    
        gui.setSize(320, 250);  // Smaller window
        gui.setLayout(new BorderLayout());
        gui.add(dungeonPanel, BorderLayout.CENTER);
        
        // Add return to main menu button
        JButton returnButton = new JButton("Return to Main Menu");
        returnButton.setFont(new Font("Arial", Font.PLAIN, 8));
        returnButton.setPreferredSize(new Dimension(48, 10));
        returnButton.addActionListener(e -> mainMenu());
        gui.add(returnButton, BorderLayout.SOUTH);
        gui.revalidate();
        gui.repaint();
    }


>>>>>>> Stashed changes
    public static void main(String[] args) {
        new GameGui().mainMenu();
    }
}
<<<<<<< Updated upstream
=======


class Room {
    private int id;
    private java.util.List<Room> neighbors;
    private int x, y;

    public Room(int id) {
        this.id = id;
        this.neighbors = new ArrayList<>();
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
        StringBuilder sb = new StringBuilder("Node " + id + " -> ");
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
        random = new Random((long)seed);
        size = (random.nextDouble()*1.25) + 0.75;
        int noRooms = (int)(size*8);
        int noDoors = (int)(((noRooms -1)*noRooms/2)*(random.nextDouble()*0.05) + 0.1);
        String[] dungeonThemes = {
            "Swampy",
            "Sewer",
            "Magical",
            "Volcanic",
            "Frozen Ice Cavern",
            "Ancient Ruins",
            "Haunted Crypt",
            "Dark Forest",
            "Desert Tomb",
            "Clockwork Fortress",
            "Corrupted Temple",
            "Crystal Cavern",
            "Underwater Shrine",
            "Abandoned Mine",
            "Blood Catacombs",
            "Shadow Realm",
            "Celestial Tower",
            "Fungal Hollow",
            "Cursed Library",
            "Beast Lair"
        };
        
        this.theme = dungeonThemes[random.nextInt(20)];

        // Create nodes
        for (int i = 0; i < noRooms; i++) {
            rooms.add(new Room(i));
        }

        // Create edges randomly
        int edgesCreated = 0;
        while (edgesCreated < noDoors) {
            int fromIndex = random.nextInt(noRooms);
            int toIndex = random.nextInt(noRooms);

            if (fromIndex != toIndex) {
                Room fromRoom = rooms.get(fromIndex);
                Room toRoom = rooms.get(toIndex);

                if (!fromRoom.getNeighbors().contains(toRoom)) {
                    fromRoom.addNeighbor(toRoom);
                    toRoom.addNeighbor(fromRoom);
                    edgesCreated++;
                }
            }
        }

        // Assign random positions to nodes
        for (Room node : rooms) {
            int x = 50 + random.nextInt(600);
            int y = 50 + random.nextInt(400);
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

    public GraphPanel(Dungeon dungeon) {
        this.dungeon = dungeon;
        setPreferredSize(new Dimension(700, 500));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGraph(g);
    }

    private void drawGraph(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));

        // Draw edges
        g2d.setColor(Color.GRAY);
        for (Room room : dungeon.getRoom()) {
            Point p1 = room.getPosition();
            for (Room neighbor : room.getNeighbors()) {
                Point p2 = neighbor.getPosition();
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        // Draw nodes
        for (Room node : dungeon.getRoom()) {
            Point p = node.getPosition();
            g2d.setColor(Color.CYAN);
            g2d.fillOval(p.x - 15, p.y - 15, 30, 30);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(p.x - 15, p.y - 15, 30, 30);
            g2d.drawString("N" + node.getId(), p.x - 10, p.y + 5);
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
        BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        characterData = new JSONObject(sb.toString());
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

>>>>>>> Stashed changes
