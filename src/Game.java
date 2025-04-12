import java.util.*;
import java.util.regex.Pattern;
import java.io.*;
import org.json.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*; // Import for socket programming

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

    // Function to connect to another player
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
        gui.getContentPane().removeAll(); // Clears the window
        gui.repaint();

        // Set layout for new game menu
        gui.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel nameLabel = new JLabel("Enter Character Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gui.add(nameLabel, gbc);

        JTextField nameField = new JTextField();
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

        gui.revalidate();
        gui.repaint();
    }

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

    public static void main(String[] args) {
        new GameGui().mainMenu();
    }
}
