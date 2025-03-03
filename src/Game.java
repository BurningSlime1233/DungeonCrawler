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

    public void mainMenu() {
        gui = new JFrame("Dungeon Crawler");
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setSize(400, 300);
        gui.setLayout(null);

        // Set background color using only Swing
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
            loadedSave = fileChooser.getSelectedFile();
            displayStats();
        }
    }


    public static void main(String[] args) {
        new GameGui().mainMenu();
    }
}