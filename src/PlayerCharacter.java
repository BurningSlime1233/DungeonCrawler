import java.util.*;
import java.util.regex.Pattern;
import java.io.*;
import org.json.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GameGui {
    JFrame gui;
    JButton newGameButton, loadGameButton, creditsButton;
    File loadedSave;
    PlayerCharacter player;

    public void mainMenu() {
        gui = new JFrame("Dungeon Crawler");
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setSize(400, 300);
        gui.setLayout(null);

        gui.getContentPane().setBackground(new javax.swing.plaf.ColorUIResource(30, 30, 30));

        newGameButton = new JButton("New Game");
        newGameButton.setBounds(125, 50, 150, 40);

        loadGameButton = new JButton("Load Game");
        loadGameButton.setBounds(125, 110, 150, 40);

        creditsButton = new JButton("Credits");
        creditsButton.setBounds(125, 170, 150, 40);

        // Button Actions
        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newGameMenu();
            }
        });

        loadGameButton.addActionListener(e -> loadGameMenu());

        creditsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(gui, "Game by Harshil Shah, Harshil Amin & Aniket Gaikwad");
            }
        });

        // Add buttons to frame
        gui.add(newGameButton);
        gui.add(loadGameButton);
        gui.add(creditsButton);

        gui.setVisible(true);
    }

    // Function to show new game menu
    public void newGameMenu() {
        gui.getContentPane().removeAll(); // Clears the window
        gui.repaint();

        JLabel nameLabel = new JLabel("Enter Character Name:");
        nameLabel.setBounds(50, 50, 200, 30);

        JTextField nameField = new JTextField();
        nameField.setBounds(50, 90, 200, 30);

        JButton saveButton = new JButton("Create Save");
        saveButton.setBounds(50, 140, 150, 40);

        // Save button action
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                if (isValidName(name)) {
                    createNewSave(name);
                } else {
                    JOptionPane.showMessageDialog(gui, "Invalid name! Use only valid characters.");
                }
            }
        });

        // Add components
        gui.add(nameLabel);
        gui.add(nameField);
        gui.add(saveButton);

        gui.revalidate();
        gui.repaint();
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

    // Function to check if the save name is valid
    public boolean isValidName(String name) {
        String regex = "^(?!\\.|\\.\\.)([^<>:\"/\\\\|?*]+)$";
        return Pattern.matches(regex, name);
    }

    public void createNewSave(String name) {
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
                    characterData.put("Level", 1);
                    characterData.put("Exp", 0);
                    characterData.put("HP", 100);
                    characterData.put("Stamina", 100);
                    characterData.put("Gold", 50);
                    characterData.put("Strength",10);
                    characterData.put("Dexterity",10);
                    characterData.put("Toughness",10);
                    characterData.put("Magic",10);
                    characterData.put("Willpower",10);
                    characterData.put("Base Seed",Math.random());
                    characterData.put("Dungeons Completed",0);
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
        try {
            loadSave(save);
        } catch (JSONException e) {
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
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Save File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON Files", "json"));

        int userSelection = fileChooser.showOpenDialog(gui);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                loadSave(selectedFile);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            displayStats();
        }
    }

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
        dungeonPanel.setBounds(0, 0, 700, 500);
    
        gui.setSize(720, 550);
        gui.setLayout(null);
        gui.add(dungeonPanel);
        gui.revalidate();
        gui.repaint();
    }


    public static void main(String[] args) {
        new GameGui().mainMenu();
    }
}


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