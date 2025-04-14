import java.util.*;
import java.util.regex.Pattern;
import java.util.List;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.BoxLayout;
import javax.swing.Box;

class GameGui {
    private JFrame gui;
    private JButton newGameButton, loadGameButton, creditsButton, playWithFriendsButton, exitButton;
    private File loadedSave;
    private PlayerCharacter player;
    private Dungeon currentDungeon;
    private Room currentRoom;
    private JPanel currentRoomPanel;
    private GraphPanel graphPanel;
    private JPanel combatPanel;
    private int monsterCurrentHP;
    private int monsterCurrentStamina;
    private int monsterMaxStamina;
    private int monsterDamageReduction;
    private JSONObject monsterSkillsData;
    private JSONObject playerSkillsData;
    private Map<String, Integer> activeConditions = new HashMap<>();
    private JTextArea combatLog;
    private Monster currentMonster;  // Store current monster
    private boolean inCombat = false;  // Track combat state
    private JProgressBar monsterHPBar;
    private JProgressBar monsterStaminaBar;

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

        // Use larger sizes for better visibility
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        Dimension fieldSize = new Dimension(300, 40);
        Dimension buttonSize = new Dimension(300, 50);

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

        // Return to main menu button
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

        // Add components with spacing
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(classLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(classDropdown);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(createButton);
        formPanel.add(Box.createVerticalStrut(20));
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
            // Initialize a new JSON object for character data instead of using player.getCharacterData()
            JSONObject characterData = new JSONObject();

            // Base info
            characterData.put("Name", name.trim());
            characterData.put("Class", characterClass);

            // Class-based stats
            switch (characterClass) {
                case "Warrior":
                    characterData.put("Strength", 15);
                    characterData.put("Dexterity", 8);
                    characterData.put("Toughness", 12);
                    characterData.put("Magic", 5);
                    break;
                case "Mage":
                    characterData.put("Strength", 5);
                    characterData.put("Dexterity", 8);
                    characterData.put("Toughness", 6);
                    characterData.put("Magic", 15);
                    break;
                case "Rogue":
                    characterData.put("Strength", 8);
                    characterData.put("Dexterity", 15);
                    characterData.put("Toughness", 8);
                    characterData.put("Magic", 5);
                    break;
            }

            // Common stats
            characterData.put("Level", 1);
            characterData.put("Exp", 0);
            characterData.put("HP", 100);
            characterData.put("MaxStamina", 100);
            characterData.put("CurrentStamina", 100);
            characterData.put("Gold", 50);
            characterData.put("Base Seed", Math.random());
            characterData.put("Dungeons Completed", 0);

            // Starting inventory: 1 Health Potion, 1 Stamina Tonic
            JSONArray inventory = new JSONArray();
            inventory.put("HLT");
            inventory.put("STM");
            characterData.put("Inventory", inventory);

            // Starting skills
            JSONArray skills = new JSONArray();
            switch (characterClass) {
                case "Warrior":
                    skills.put("ATS");
                    break;
                case "Mage":
                    skills.put("ATM");
                    break;
                case "Rogue":
                    skills.put("ATD");
                    break;
            }
            characterData.put("Skills", skills);

            // Save to file
            try (FileWriter fileWriter = new FileWriter(save)) {
                fileWriter.write(characterData.toString(4));
                fileWriter.flush();
                JOptionPane.showMessageDialog(gui, "Character created successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadSave(save); // Load into game
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

    private void activateCombat(Monster monster) {
        // Check if this monster has already been defeated
        if (currentRoom.isMonsterDefeated(monster.getId())) {
            updateRoomPanel();
            return;
        }

        inCombat = true;
        currentMonster = monster;  // Store the monster
        currentRoomPanel.removeAll();
        combatPanel = new JPanel();
        combatPanel.setLayout(new BoxLayout(combatPanel, BoxLayout.Y_AXIS));
        combatPanel.setBackground(new Color(30, 30, 30));

        try {
            int playerLevel = player.getLevel();
            String difficulty = monster.getDifficulty();
            double difficultyMultiplier = getDifficultyMultiplier(difficulty);
            
            // Calculate monster stats based on monster's base stats and difficulty
            int monsterStr = calculateMonsterStat(monster.getStr(), playerLevel, difficultyMultiplier);
            int monsterDex = calculateMonsterStat(monster.getDex(), playerLevel, difficultyMultiplier);
            int monsterMag = calculateMonsterStat(monster.getMag(), playerLevel, difficultyMultiplier);
            int monsterFor = calculateMonsterStat(monster.getFort(), playerLevel, difficultyMultiplier);
            
            // Calculate monster HP based on FOR stat and level
            int monsterMaxHP = (int) (100 * playerLevel * (monsterFor / 10.0) * difficultyMultiplier);
            monsterMaxStamina = calculateMonsterStamina(monster.getStamina(), playerLevel, difficultyMultiplier);
            monsterCurrentHP = monsterMaxHP;
            monsterCurrentStamina = monsterMaxStamina;
            monsterDamageReduction = Math.max(Math.max(monsterStr, monsterDex), Math.max(monsterMag, monsterFor));

            // Load skill data
            monsterSkillsData = new JSONObject(new String(Files.readAllBytes(Paths.get("lib/MonsterSkills.json"))));
            playerSkillsData = new JSONObject(new String(Files.readAllBytes(Paths.get("lib/Skills.json"))));

            // Create UI components
            JLabel monsterLabel = new JLabel("Fighting: " + monster.getName() + " (" + difficulty + ")");
            monsterLabel.setForeground(Color.WHITE);
            monsterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            combatPanel.add(monsterLabel);

            // Add monster stats display
            JPanel statsPanel = new JPanel();
            statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
            statsPanel.setBackground(new Color(30, 30, 30));
            statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel statsLabel = new JLabel("Monster Stats:");
            statsLabel.setForeground(Color.WHITE);
            statsPanel.add(statsLabel);

            JLabel strLabel = new JLabel("Strength: " + monsterStr);
            strLabel.setForeground(Color.WHITE);
            statsPanel.add(strLabel);

            JLabel dexLabel = new JLabel("Dexterity: " + monsterDex);
            dexLabel.setForeground(Color.WHITE);
            statsPanel.add(dexLabel);

            JLabel magLabel = new JLabel("Magic: " + monsterMag);
            magLabel.setForeground(Color.WHITE);
            statsPanel.add(magLabel);

            JLabel forLabel = new JLabel("Toughness: " + monsterFor);
            forLabel.setForeground(Color.WHITE);
            statsPanel.add(forLabel);

            combatPanel.add(statsPanel);
            combatPanel.add(new JSeparator());

            // Add HP and Stamina bars after stats panel
            monsterHPBar = new JProgressBar(0, monsterMaxHP);
            monsterHPBar.setValue(monsterCurrentHP);
            monsterHPBar.setString("Monster HP: " + monsterCurrentHP + "/" + monsterMaxHP);
            monsterHPBar.setStringPainted(true);
            monsterHPBar.setAlignmentX(Component.CENTER_ALIGNMENT);
            combatPanel.add(monsterHPBar);

            monsterStaminaBar = new JProgressBar(0, monsterMaxStamina);
            monsterStaminaBar.setValue(monsterCurrentStamina);
            monsterStaminaBar.setString("Monster Stamina: " + monsterCurrentStamina + "/" + monsterMaxStamina);
            monsterStaminaBar.setStringPainted(true);
            monsterStaminaBar.setAlignmentX(Component.CENTER_ALIGNMENT);
            combatPanel.add(monsterStaminaBar);

            // Add combat log with proper sizing and scrolling
            JPanel logPanel = new JPanel(new BorderLayout());
            logPanel.setBackground(new Color(30, 30, 30));
            combatLog = new JTextArea(10, 30);
            combatLog.setEditable(false);
            combatLog.setBackground(new Color(30, 30, 30));
            combatLog.setForeground(Color.WHITE);
            combatLog.setLineWrap(true);
            combatLog.setWrapStyleWord(true);
            JScrollPane logScroll = new JScrollPane(combatLog);
            logScroll.setPreferredSize(new Dimension(400, 200));
            logScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            logPanel.add(logScroll, BorderLayout.CENTER);
            combatPanel.add(logPanel);

            combatPanel.add(new JSeparator());

            // Add items section
            JLabel itemsLabel = new JLabel("Use Items:");
            itemsLabel.setForeground(Color.WHITE);
            itemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            combatPanel.add(itemsLabel);

            // Create a panel for item buttons
            JPanel itemsPanel = new JPanel();
            itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
            itemsPanel.setBackground(new Color(30, 30, 30));

            if (player.getCharacterData().has("Inventory")) {
                JSONArray inventory = player.getCharacterData().getJSONArray("Inventory");
                Map<String, Integer> itemCounts = new HashMap<>();
                
                // Count combat items
                for (int i = 0; i < inventory.length(); i++) {
                    String itemId = inventory.getString(i);
                    if (isCombatItem(itemId)) {
                        itemCounts.put(itemId, itemCounts.getOrDefault(itemId, 0) + 1);
                    }
                }

                // Add buttons for each combat item
                for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
                    String itemId = entry.getKey();
                    int count = entry.getValue();
                    JButton itemButton = createItemButton(itemId, count);
                    itemButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    itemsPanel.add(itemButton);
                }
            }
            combatPanel.add(itemsPanel);

            // Player skills
            JLabel skillsLabel = new JLabel("Your Skills:");
            skillsLabel.setForeground(Color.WHITE);
            skillsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            combatPanel.add(skillsLabel);

            // Create a panel for skill buttons
            JPanel skillsPanel = new JPanel();
            skillsPanel.setLayout(new BoxLayout(skillsPanel, BoxLayout.Y_AXIS));
            skillsPanel.setBackground(new Color(30, 30, 30));

            JSONArray playerSkills = player.getSkills();
            for (int i = 0; i < playerSkills.length(); i++) {
                String skillId = playerSkills.getString(i);
                JSONObject skill = findSkill(playerSkillsData, skillId);
                if (skill != null && !skill.getJSONObject("scaling").getString("damage").equals("N/A")) {
                    JButton skillButton = createSkillButton(skill, monsterHPBar, monsterStaminaBar);
                    skillButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    skillsPanel.add(skillButton);
                }
            }
            combatPanel.add(skillsPanel);

            // Add padding and ensure proper layout
            combatPanel.add(Box.createVerticalGlue());
            currentRoomPanel.add(combatPanel);
            currentRoomPanel.revalidate();
            currentRoomPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(gui, "Error initializing combat: " + e.getMessage());
        }
    }

    private boolean isCombatItem(String itemId) {
        return itemId.equals("FIR") || itemId.equals("PSN") || itemId.equals("FLA") || 
               itemId.equals("ICE") || itemId.equals("STR") || itemId.equals("SMK");
    }

    private JButton createItemButton(String itemId, int count) {
        String buttonText = "";
        switch (itemId) {
            case "FIR": buttonText = "Fire Bomb (" + count + ")"; break;
            case "PSN": buttonText = "Poison Vial (" + count + ")"; break;
            case "FLA": buttonText = "Flash Powder (" + count + ")"; break;
            case "ICE": buttonText = "Frost Shard (" + count + ")"; break;
            case "STR": buttonText = "Strength Elixir (" + count + ")"; break;
            case "SMK": buttonText = "Smoke Bomb (" + count + ")"; break;
        }

        JButton button = new JButton(buttonText);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(30, 30, 30));
        button.setMaximumSize(new Dimension(200, 30));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addActionListener(e -> {
            try {
                useCombatItem(itemId);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        });
        return button;
    }

    private void useCombatItem(String itemId) throws JSONException {
        JSONArray inventory = player.getCharacterData().getJSONArray("Inventory");
        boolean itemFound = false;
        
        // Find and remove the item from inventory
        for (int i = 0; i < inventory.length(); i++) {
            if (inventory.getString(i).equals(itemId)) {
                inventory.remove(i);
                itemFound = true;
                break;
            }
        }

        if (!itemFound) {
            combatLog.append("You don't have that item!\n");
            return;
        }

        // Apply item effects
        switch (itemId) {
            case "FIR":
                int fireDamage = (int)(monsterCurrentHP * 0.2); // Deal 20% of monster's current HP
                monsterCurrentHP = Math.max(0, monsterCurrentHP - fireDamage);
                combatLog.append("You threw a Fire Bomb, dealing " + fireDamage + " damage!\n");
                break;
            case "PSN":
                activeConditions.put("POISON", 3); // Poison lasts 3 turns
                combatLog.append("You poisoned the monster! It will take damage over time.\n");
                break;
            case "FLA":
                activeConditions.put("BLIND", 2); // Blind lasts 2 turns
                combatLog.append("You blinded the monster! Its accuracy is reduced.\n");
                break;
            case "ICE":
                activeConditions.put("FROZEN", 1); // Freeze lasts 1 turn
                combatLog.append("You froze the monster! It can't act next turn.\n");
                break;
            case "STR":
                activeConditions.put("STRENGTH_BUFF", 3); // Strength buff lasts 3 turns
                combatLog.append("You drank a Strength Elixir! Your attacks will deal more damage.\n");
                break;
            case "SMK":
                combatLog.append("You threw a Smoke Bomb and escaped from combat!\n");
                endCombat(false);
                return;
        }

        // Update monster HP bar if needed
        if (monsterHPBar != null) {
            monsterHPBar.setValue(monsterCurrentHP);
            monsterHPBar.setString("Monster HP: " + monsterCurrentHP + "/" + monsterHPBar.getMaximum());
        }

        // Check if monster is defeated
        if (monsterCurrentHP <= 0) {
            combatLog.append("You defeated the monster!\n");
            endCombat(true);
            return;
        }

        // Monster's turn
        monsterAttack(monsterHPBar, monsterStaminaBar);

        // Refresh stats panel
        refreshStatsPanel();
    }

    private JButton createSkillButton(JSONObject skill, JProgressBar monsterHPBar, JProgressBar monsterStaminaBar) throws JSONException {
        JButton button = new JButton(skill.getString("name"));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(30, 30, 30));
        button.setMaximumSize(new Dimension(200, 30));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addActionListener(e -> {
            try {
                int staminaCost = skill.getInt("staminaCost");
                int currentStamina = player.getCurrentStamina();
                
                if (currentStamina >= staminaCost) {
                    player.getCharacterData().put("Stamina", currentStamina - staminaCost);
                    
                    // Calculate damage based on the skill's stat
                    String stat = skill.getString("stat");
                    int playerStat = 0;
                    switch (stat) {
                        case "STR":
                            playerStat = player.getCharacterData().getInt("Strength");
                            break;
                        case "DEX":
                            playerStat = player.getCharacterData().getInt("Dexterity");
                            break;
                        case "MAG":
                            playerStat = player.getCharacterData().getInt("Magic");
                            break;
                        case "FOR":
                            playerStat = player.getCharacterData().getInt("Toughness");
                            break;
                    }
                    int baseDamage = calculateDamage(skill.getJSONObject("scaling"), playerStat);
                    int finalDamage = Math.max(0, baseDamage - monsterDamageReduction);
                    
                    monsterCurrentHP = Math.max(0, monsterCurrentHP - finalDamage);
                    monsterHPBar.setValue(monsterCurrentHP);
                    monsterHPBar.setString("Monster HP: " + monsterCurrentHP + "/" + monsterHPBar.getMaximum());

                    // Add combat log entry
                    combatLog.append("You used " + skill.getString("name") + " and dealt " + finalDamage + " damage!\n");
                    combatLog.setCaretPosition(combatLog.getDocument().getLength());

                    if (monsterCurrentHP <= 0) {
                        combatLog.append("You defeated the monster!\n");
                        JOptionPane.showMessageDialog(gui, "You defeated the monster!");
                        endCombat(true);
                        return;
                    }

                    // Monster turn
                    monsterAttack(monsterHPBar, monsterStaminaBar);

                    // Update player stats display
                    refreshStatsPanel();

                } else {
                    combatLog.append("Not enough stamina to use " + skill.getString("name") + "!\n");
                    combatLog.setCaretPosition(combatLog.getDocument().getLength());
                    JOptionPane.showMessageDialog(gui, "Not enough stamina!");
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        });
        return button;
    }

    private void monsterAttack(JProgressBar monsterHPBar, JProgressBar monsterStaminaBar) {
        try {
            // Check for frozen condition
            if (activeConditions.containsKey("FROZEN")) {
                combatLog.append("The monster is frozen and can't act!\n");
                activeConditions.remove("FROZEN");
                return;
            }

            // Check for blind condition
            boolean isBlind = activeConditions.containsKey("BLIND");
            if (isBlind) {
                int blindTurns = activeConditions.get("BLIND") - 1;
                if (blindTurns <= 0) {
                    activeConditions.remove("BLIND");
                } else {
                    activeConditions.put("BLIND", blindTurns);
                }
            }

            // Check for poison condition
            if (activeConditions.containsKey("POISON")) {
                int poisonDamage = (int)(monsterCurrentHP * 0.1); // 10% of current HP
                monsterCurrentHP = Math.max(0, monsterCurrentHP - poisonDamage);
                combatLog.append("The monster takes " + poisonDamage + " poison damage!\n");
                
                int poisonTurns = activeConditions.get("POISON") - 1;
                if (poisonTurns <= 0) {
                    activeConditions.remove("POISON");
                } else {
                    activeConditions.put("POISON", poisonTurns);
                }
            }

            // Simple monster AI: use random skill
            JSONArray skills = monsterSkillsData.getJSONArray("skills");
            JSONObject skill = skills.getJSONObject(new Random().nextInt(skills.length()));
            
            if (monsterCurrentStamina >= skill.getInt("staminaCost")) {
                monsterCurrentStamina -= skill.getInt("staminaCost");
                
                // Calculate damage based on the skill's stat
                String stat = skill.getString("stat");
                int monsterStat = 0;
                switch (stat) {
                    case "STR":
                        monsterStat = calculateMonsterStat(currentMonster.getStr(), player.getLevel(), getDifficultyMultiplier(currentMonster.getDifficulty()));
                        break;
                    case "DEX":
                        monsterStat = calculateMonsterStat(currentMonster.getDex(), player.getLevel(), getDifficultyMultiplier(currentMonster.getDifficulty()));
                        break;
                    case "MAG":
                        monsterStat = calculateMonsterStat(currentMonster.getMag(), player.getLevel(), getDifficultyMultiplier(currentMonster.getDifficulty()));
                        break;
                    case "FOR":
                        monsterStat = calculateMonsterStat(currentMonster.getFort(), player.getLevel(), getDifficultyMultiplier(currentMonster.getDifficulty()));
                        break;
                }
                
                int baseDamage = calculateDamage(skill.getJSONObject("scaling"), monsterStat);
                
                // Apply blind effect (50% accuracy reduction)
                if (isBlind) {
                    baseDamage = (int)(baseDamage * 0.5);
                }
                
                int playerDR = calculatePlayerDR();
                int finalDamage = Math.max(0, baseDamage - playerDR);
                
                int newHP = player.getCurrentHp() - finalDamage;
                player.getCharacterData().put("HP", newHP);
                
                // Add combat log entry
                combatLog.append("The monster used " + skill.getString("name") + " and dealt " + finalDamage + " damage!\n");
                if (isBlind) {
                    combatLog.append("The attack was weakened due to blindness!\n");
                }
                combatLog.setCaretPosition(combatLog.getDocument().getLength());
                
                if (newHP <= 0) {
                    combatLog.append("You were defeated!\n");
                    JOptionPane.showMessageDialog(gui, "You were defeated!");
                    endCombat(false);
                    mainMenu();
                    return;
                }
            } else {
                monsterCurrentStamina += (int)(monsterMaxStamina * 0.1);
                combatLog.append("The monster recovered some stamina!\n");
                combatLog.setCaretPosition(combatLog.getDocument().getLength());
            }

            monsterStaminaBar.setValue(monsterCurrentStamina);
            monsterStaminaBar.setString("Monster Stamina: " + monsterCurrentStamina + "/" + monsterMaxStamina);
            
            // Update player stats display
            refreshStatsPanel();

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    private int calculatePlayerDR() throws JSONException {
        JSONObject cd = player.getCharacterData();
        return Math.max(Math.max(cd.getInt("Strength"), cd.getInt("Dexterity")), 
               Math.max(cd.getInt("Magic"), cd.getInt("Toughness")));
    }
    
    private int calculateDamage(JSONObject scaling, int stat) throws JSONException {
        String dmg = scaling.getString("damage");
        switch (dmg) {
            case "LOW": return stat * 2;  // Increased from 1x to 2x
            case "MED": return stat * 3;  // Increased from 2x to 3x
            case "HIG": return stat * 4;  // Increased from 3x to 4x
            default: return 0;
        }
    }
    
    private JSONObject findSkill(JSONObject skillsData, String skillId) throws JSONException {
        JSONArray skills = skillsData.getJSONArray("skills");
        for (int i = 0; i < skills.length(); i++) {
            JSONObject skill = skills.getJSONObject(i);
            if (skill.getString("id").equals(skillId)) return skill;
        }
        return null;
    }
    
    private double getDifficultyMultiplier(String difficulty) {
        switch (difficulty) {
            case "LOW": return 0.75;
            case "MED": return 1.0;
            case "HIG": return 1.25;
            default: return 1.0;
        }
    }

    public void dungeonScreen() {
        gui.getContentPane().removeAll();
        gui.setLayout(new BorderLayout());

        // -- Get or generate the dungeon as usual --
        long newDungeonSeed = 0;
        try {
            double base = player.getCharacterData().getDouble("Base Seed");
            int dungeonsCompleted = player.getCharacterData().getInt("Dungeons Completed");

            // Initialize random with base seed converted into a long.
            Random r = new Random((long) (base * 100000));

            // Use a for loop to repeatedly generate a new seed.
            for (int i = 0; i <= dungeonsCompleted; i++) {
                newDungeonSeed = r.nextLong();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        currentDungeon = new Dungeon((double) newDungeonSeed);

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

    private void saveGame() {
        try {
            // Update HP and Stamina from current values
            player.getCharacterData().put("HP", player.getCurrentHp());
            player.getCharacterData().put("Stamina", player.getCurrentStamina());

            // Increment dungeons completed
            int dungeonsCompleted = player.getCharacterData().getInt("Dungeons Completed");
            player.getCharacterData().put("Dungeons Completed", dungeonsCompleted + 1);

            // Save to file
            try (FileWriter fileWriter = new FileWriter(loadedSave)) {
                fileWriter.write(player.getCharacterData().toString(4));
                fileWriter.flush();
            }
        } catch (IOException | JSONException e) {
            JOptionPane.showMessageDialog(gui, "Failed to save game: " + e.getMessage());
        }
    }

    private void updateRoomPanel() {
        // Clear the panel first
        currentRoomPanel.removeAll();
        currentRoomPanel.setLayout(new BoxLayout(currentRoomPanel, BoxLayout.Y_AXIS));
        currentRoomPanel.setBackground(new Color(30, 30, 30));

        // Add exit button if in starting room
        if ("startingRoom".equals(currentRoom.getType())) {
            JButton exitButton = new JButton("Exit Dungeon");
            exitButton.addActionListener(e -> {
                if (inCombat) {
                    JOptionPane.showMessageDialog(gui, "You cannot exit while in combat!");
                    return;
                }
                // Save game first
                saveGame();
                try {
                    // Increment the dungeons completed count.
                    int dungeonsCompleted = player.getCharacterData().getInt("Dungeons Completed");
                    dungeonsCompleted++;
                    player.getCharacterData().put("Dungeons Completed", dungeonsCompleted);

                    // Obtain the base seed and prepare a Random instance.
                    double base = player.getCharacterData().getDouble("Base Seed");
                    Random r = new Random((long) (base * 100000));

                    // Skip forward as many values as dungeons completed.
                    long newSeed = r.nextLong();
                    for (int i = 1; i < dungeonsCompleted; i++) {
                        newSeed = r.nextLong();
                    }

                    // Generate a new dungeon using the new seed.
                    currentDungeon = new Dungeon((double) newSeed);

                    // Find the new starting room.
                    for (Room room : currentDungeon.getRoom()) {
                        if (room.getId() == 1 && "startingRoom".equals(room.getType())) {
                            currentRoom = room;
                            break;
                        }
                    }
                    if (currentRoom == null && !currentDungeon.getRoom().isEmpty()) {
                        currentRoom = currentDungeon.getRoom().get(0);
                    }

                    // Refresh UI
                    updateRoomPanel();
                    graphPanel = new GraphPanel(currentDungeon, currentRoom,
                            player.getCharacterData().getString("Class"));

                    graphPanel.setCurrentRoom(currentRoom);
                    graphPanel.repaint();
                    JSplitPane centerSplit = (JSplitPane) gui.getContentPane().getComponent(2);
                    centerSplit.setRightComponent(graphPanel);

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            });
            currentRoomPanel.add(exitButton);
            currentRoomPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        }

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

        // Add shop UI if this is a shop room
        if ("Shop".equals(currentRoom.getType())) {
            JLabel shopTitle = new JLabel("Shop Items:");
            shopTitle.setForeground(Color.WHITE);
            currentRoomPanel.add(shopTitle);

            try {
                int playerGold = player.getCharacterData().getInt("Gold");
                JLabel goldLabel = new JLabel("Your Gold: " + playerGold);
                goldLabel.setForeground(Color.YELLOW);
                currentRoomPanel.add(goldLabel);

                List<JSONObject> shopItems = currentDungeon.getShopItems();
                for (JSONObject item : shopItems) {
                    JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    itemPanel.setBackground(new Color(30, 30, 30));

                    String itemText = String.format("%s - %d gold",
                            item.getString("name"),
                            item.getInt("goldCost"));
                    JLabel itemLabel = new JLabel(itemText);
                    itemLabel.setForeground(Color.WHITE);
                    itemPanel.add(itemLabel);

                    JButton buyButton = new JButton("Buy");
                    buyButton.addActionListener(e -> {
                        try {
                            int cost = item.getInt("goldCost");
                            int currentGold = player.getCharacterData().getInt("Gold");

                            if (currentGold >= cost) {
                                // Deduct gold
                                player.getCharacterData().put("Gold", currentGold - cost);

                                // Add item to inventory
                                if (!player.getCharacterData().has("Inventory")) {
                                    player.getCharacterData().put("Inventory", new JSONArray());
                                }
                                JSONArray inventory = player.getCharacterData().getJSONArray("Inventory");
                                inventory.put(item.getString("id"));

                                JOptionPane.showMessageDialog(gui,
                                        "Purchased " + item.getString("name") + "!\n" +
                                                "Effect: " + item.getString("effect"));

                                // Refresh UI
                                updateRoomPanel();
                                refreshStatsPanel();
                            } else {
                                JOptionPane.showMessageDialog(gui,
                                        "Not enough gold! You need " + cost + " but only have " + currentGold,
                                        "Purchase Failed",
                                        JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    });
                    itemPanel.add(buyButton);
                    currentRoomPanel.add(itemPanel);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            currentRoomPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        }

        // Process room-specific functions
        if ("Upgrade".equals(currentRoom.getType()) && !currentRoom.isVisited()) {
            try {
                player.applyUpgradeRoomBonus();
                JOptionPane.showMessageDialog(gui,
                        "Upgrade room activated!\n+50 XP\n+1 to Strength, Dexterity, Magic, and Toughness\nHP recalculated.");
                currentRoom.markVisited();
                
                // Check for level up after upgrade
                player.checkLevelUp();
                refreshStatsPanel();
            } catch (JSONException ex) {
                JOptionPane.showMessageDialog(gui,
                        "Error applying upgrade: " + ex.getMessage(),
                        "Upgrade Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        if (currentRoom instanceof MonsterRoom && !inCombat && !currentRoom.isMonsterDefeated(((MonsterRoom) currentRoom).getMonster().getId())) {
            activateCombat(((MonsterRoom) currentRoom).getMonster());
            return;
        }
        if ("Treasure".equals(currentRoom.getType()) && !currentRoom.isVisited()) {
            try {
                TreasureRoom tr = (TreasureRoom) currentRoom;
                JSONObject charData = player.getCharacterData();

                // Add treasure gold
                int currentGold = charData.getInt("Gold");
                charData.put("Gold", currentGold + tr.getTreasureGold());

                // Ensure inventory exists
                JSONArray inventory;
                if (charData.has("Inventory") && charData.get("Inventory") instanceof JSONArray) {
                    inventory = charData.getJSONArray("Inventory");
                } else {
                    inventory = new JSONArray();
                    charData.put("Inventory", inventory);
                }

                // Add each treasure item to inventory
                for (String item : tr.getTreasureItems()) {
                    inventory.put(item);
                }

                // Mark room visited
                currentRoom.markVisited();

                JOptionPane.showMessageDialog(gui,
                        "Treasure room activated!\n+"
                                + tr.getTreasureGold() + " Gold\nFound items: " + tr.getTreasureItems());

                // Check for level up after treasure
                player.checkLevelUp();
                refreshStatsPanel();

            } catch (JSONException ex) {
                JOptionPane.showMessageDialog(gui,
                        "Error accessing treasure: " + ex.getMessage(),
                        "Treasure Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (ClassCastException ex) {
                JOptionPane.showMessageDialog(gui,
                        "Invalid room type - expected TreasureRoom",
                        "Room Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        // Add connected rooms section
        JLabel connectedLabel = new JLabel("Connected Rooms:");
        connectedLabel.setForeground(Color.WHITE);
        currentRoomPanel.add(connectedLabel);

        // Create a panel for neighbor movement buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(30, 30, 30));

        for (Room neighbor : currentRoom.getNeighbors()) {
            JButton moveButton = new JButton("Go to Room " + neighbor.getId());
            moveButton.addActionListener(e -> {
                if (inCombat) {
                    JOptionPane.showMessageDialog(gui, "You cannot move while in combat!");
                    return;
                }
                // If the destination room has an obstacle, check bypass options
                if (neighbor.hasObstacle()) {
                    String obstacleType = neighbor.getObstacleType();
                    JSONArray inventory = player.getCharacterData().optJSONArray("Inventory");
                    // Build a list of bypass options available from inventory and skills
                    List<String> bypassOptions = new ArrayList<>();

                    // Check for items in inventory that match the obstacle type
                    if (inventory != null) {
                        for (int i = 0; i < inventory.length(); i++) {
                            try {
                                String itemId = inventory.getString(i);
                                if (itemId.equals(obstacleType)) {
                                    bypassOptions.add("Use item: " + itemId);
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    // Check for skills that match the obstacle type
                    try {
                        JSONArray skills = player.getSkills();
                        if (skills != null) {
                            for (int i = 0; i < skills.length(); i++) {
                                String skillId = skills.getString(i);
                                if (skillId.equals(obstacleType)) {
                                    bypassOptions.add("Use skill: " + skillId);
                                }
                            }
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                    // If no bypass option is available, tell the player and cancel the move
                    if (bypassOptions.isEmpty()) {
                        JOptionPane.showMessageDialog(gui,
                                "This room is blocked by an obstacle (" + obstacleType
                                        + ") and you have no bypass available.");
                        return; // Do not change room
                    } else {
                        // Let the player choose an option to bypass the obstacle
                        int choice = JOptionPane.showOptionDialog(gui,
                                "This room is blocked by an obstacle (" + obstacleType + ").\n" +
                                        "Select an option to bypass it or cancel:",
                                "Obstacle Detected",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                bypassOptions.toArray(),
                                bypassOptions.get(0));

                        // If the player cancels (choice is closed or negative), do not move
                        if (choice < 0) {
                            return;
                        } else {
                            String selectedOption = bypassOptions.get(choice);
                            // If an inventory item was chosen (option starts with "Use item:"), remove it
                            if (selectedOption.startsWith("Use item: ")) {
                                String usedItem = selectedOption.substring("Use item: ".length());
                                // Remove one instance of the used item
                                for (int i = 0; i < inventory.length(); i++) {
                                    try {
                                        if (inventory.getString(i).equals(usedItem)) {
                                            inventory.remove(i);
                                            break;
                                        }
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }

                // If no obstacle or bypassed, update currentRoom
                currentRoom = neighbor;
                updateRoomPanel();
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
            statsPanel.add(createStatLine("Dungeons Completed: " + characterData.getInt("Dungeons Completed")));

            // Add inventory section
            statsPanel.add(Box.createVerticalStrut(20));
            JLabel inventoryLabel = new JLabel("Inventory:");
            inventoryLabel.setForeground(Color.WHITE);
            statsPanel.add(inventoryLabel);

            if (characterData.has("Inventory")) {
                JSONArray inventory = characterData.getJSONArray("Inventory");
                Map<String, Integer> itemCounts = new HashMap<>();
                
                // Count items
                for (int i = 0; i < inventory.length(); i++) {
                    String itemId = inventory.getString(i);
                    itemCounts.put(itemId, itemCounts.getOrDefault(itemId, 0) + 1);
                }

                // Display items with counts
                for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
                    String itemId = entry.getKey();
                    int count = entry.getValue();
                    String itemName = getItemName(itemId);
                    statsPanel.add(createStatLine(itemName + " (" + count + ")"));
                }
            }

            // Add skills section
            statsPanel.add(Box.createVerticalStrut(20));
            JLabel skillsLabel = new JLabel("Skills:");
            skillsLabel.setForeground(Color.WHITE);
            statsPanel.add(skillsLabel);

            if (characterData.has("Skills")) {
                JSONArray skills = characterData.getJSONArray("Skills");
                for (int i = 0; i < skills.length(); i++) {
                    String skillId = skills.getString(i);
                    String skillName = getSkillName(skillId);
                    statsPanel.add(createStatLine(skillName));
                }
            }

            // Add consumables section
            statsPanel.add(Box.createVerticalStrut(20));
            JLabel consumablesLabel = new JLabel("Use Items:");
            consumablesLabel.setForeground(Color.WHITE);
            statsPanel.add(consumablesLabel);

            if (characterData.has("Inventory")) {
                JSONArray inventory = characterData.getJSONArray("Inventory");
                
                // Count health potions and stamina tonics
                int healthPots = 0;
                int staminaPots = 0;
                for (int i = 0; i < inventory.length(); i++) {
                    String itemId = inventory.getString(i);
                    if ("HLT".equals(itemId))
                        healthPots++;
                    if ("STM".equals(itemId))
                        staminaPots++;
                }

                // Add health potion button if available
                if (healthPots > 0) {
                    JButton healthButton = new JButton("Health Potion (" + healthPots + ")");
                    healthButton.addActionListener(e -> {
                        try {
                            // Remove one potion
                            for (int i = 0; i < inventory.length(); i++) {
                                if ("HLT".equals(inventory.getString(i))) {
                                    inventory.remove(i);
                                    break;
                                }
                            }

                            // Restore 20% HP
                            int maxHP = characterData.getInt("HP");
                            int currentHP = characterData.getInt("HP");
                            int healAmount = (int) (maxHP * 0.2);
                            characterData.put("HP", Math.min(maxHP, currentHP + healAmount));

                            // Refresh stats panel
                            refreshStatsPanel();
                            JOptionPane.showMessageDialog(gui, "Restored " + healAmount + " HP!");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    });
                    statsPanel.add(healthButton);
                }

                // Add stamina tonic button if available
                if (staminaPots > 0) {
                    JButton staminaButton = new JButton("Stamina Tonic (" + staminaPots + ")");
                    staminaButton.addActionListener(e -> {
                        try {
                            // Remove one tonic
                            for (int i = 0; i < inventory.length(); i++) {
                                if ("STM".equals(inventory.getString(i))) {
                                    inventory.remove(i);
                                    break;
                                }
                            }

                            // Restore 20% Stamina
                            int maxStamina = characterData.getInt("Stamina");
                            int currentStamina = characterData.getInt("Stamina");
                            int restoreAmount = (int) (maxStamina * 0.2);
                            characterData.put("Stamina", Math.min(maxStamina, currentStamina + restoreAmount));

                            // Refresh stats panel
                            refreshStatsPanel();
                            JOptionPane.showMessageDialog(gui, "Restored " + restoreAmount + " Stamina!");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    });
                    statsPanel.add(staminaButton);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return statsPanel;
    }

    private String getItemName(String itemId) {
        try {
            String content = new String(Files.readAllBytes(Paths.get("lib/Items.json")));
            JSONObject itemsData = new JSONObject(content);
            JSONArray items = itemsData.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                if (item.getString("id").equals(itemId)) {
                    return item.getString("name");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemId;
    }

    private String getSkillName(String skillId) {
        try {
            String content = new String(Files.readAllBytes(Paths.get("lib/Skills.json")));
            JSONObject skillsData = new JSONObject(content);
            JSONArray skills = skillsData.getJSONArray("skills");
            for (int i = 0; i < skills.length(); i++) {
                JSONObject skill = skills.getJSONObject(i);
                if (skill.getString("id").equals(skillId)) {
                    return skill.getString("name");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return skillId;
    }

    private void refreshStatsPanel() {
        try {
            // Instead of removing and recreating the panel, just update the existing one
            JPanel statsPanel = (JPanel) gui.getContentPane().getComponent(0);
            statsPanel.removeAll();
            
            // Rebuild the stats panel content
            statsPanel.add(createStatLine("Name: " + player.getCharacterData().getString("Name")));
            statsPanel.add(createStatLine("Class: " + player.getCharacterData().getString("Class")));
            statsPanel.add(createStatLine("Level: " + player.getCharacterData().getInt("Level")));
            statsPanel.add(createStatLine("Exp: " + player.getCharacterData().getInt("Exp")));
            statsPanel.add(createStatLine("HP: " + player.getCharacterData().getInt("HP")));
            statsPanel.add(createStatLine("Stamina: " + player.getCharacterData().getInt("Stamina")));
            statsPanel.add(createStatLine("Gold: " + player.getCharacterData().getInt("Gold")));
            statsPanel.add(createStatLine("Strength: " + player.getCharacterData().getInt("Strength")));
            statsPanel.add(createStatLine("Dexterity: " + player.getCharacterData().getInt("Dexterity")));
            statsPanel.add(createStatLine("Toughness: " + player.getCharacterData().getInt("Toughness")));
            statsPanel.add(createStatLine("Magic: " + player.getCharacterData().getInt("Magic")));
            statsPanel.add(createStatLine("Dungeons Completed: " + player.getCharacterData().getInt("Dungeons Completed")));
            
            // Add consumables section
            statsPanel.add(Box.createVerticalStrut(20));
            JLabel consumablesLabel = new JLabel("Use Items:");
            consumablesLabel.setForeground(Color.WHITE);
            statsPanel.add(consumablesLabel);
            
            if (player.getCharacterData().has("Inventory")) {
                JSONArray inventory = player.getCharacterData().getJSONArray("Inventory");
                
                // Count health potions and stamina tonics
                int healthPots = 0;
                int staminaPots = 0;
                for (int i = 0; i < inventory.length(); i++) {
                    String itemId = inventory.getString(i);
                    if ("HLT".equals(itemId)) healthPots++;
                    if ("STM".equals(itemId)) staminaPots++;
                }
                
                // Add health potion button if available
                if (healthPots > 0) {
                    JButton healthButton = new JButton("Health Potion (" + healthPots + ")");
                    healthButton.addActionListener(e -> {
                        try {
                            // Remove one potion
                            for (int i = 0; i < inventory.length(); i++) {
                                if ("HLT".equals(inventory.getString(i))) {
                                    inventory.remove(i);
                                    break;
                                }
                            }
                            
                            // Restore 20% HP
                            int maxHP = player.getCharacterData().getInt("HP");
                            int currentHP = player.getCharacterData().getInt("HP");
                            int healAmount = (int)(maxHP * 0.2);
                            player.getCharacterData().put("HP", Math.min(maxHP, currentHP + healAmount));
                            
                            // Refresh stats panel
                            refreshStatsPanel();
                            JOptionPane.showMessageDialog(gui, "Restored " + healAmount + " HP!");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    });
                    statsPanel.add(healthButton);
                }
                
                // Add stamina tonic button if available
                if (staminaPots > 0) {
                    JButton staminaButton = new JButton("Stamina Tonic (" + staminaPots + ")");
                    staminaButton.addActionListener(e -> {
                        try {
                            // Remove one tonic
                            for (int i = 0; i < inventory.length(); i++) {
                                if ("STM".equals(inventory.getString(i))) {
                                    inventory.remove(i);
                                    break;
                                }
                            }
                            
                            // Restore 20% Stamina
                            int maxStamina = player.getCharacterData().getInt("Stamina");
                            int currentStamina = player.getCharacterData().getInt("Stamina");
                            int restoreAmount = (int)(maxStamina * 0.2);
                            player.getCharacterData().put("Stamina", Math.min(maxStamina, currentStamina + restoreAmount));
                            
                            // Refresh stats panel
                            refreshStatsPanel();
                            JOptionPane.showMessageDialog(gui, "Restored " + restoreAmount + " Stamina!");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    });
                    statsPanel.add(staminaButton);
                }
            }
            
            // Refresh the panel
            statsPanel.revalidate();
            statsPanel.repaint();
        } catch (JSONException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(gui, "Error updating stats panel: " + e.getMessage());
        }
    }

    private JLabel createStatLine(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        return label;
    }

    private void endCombat(boolean monsterDefeated) {
        if (monsterDefeated && currentMonster != null) {
            currentRoom.markMonsterDefeated(currentMonster.getId());
            
            // Award experience based on monster difficulty
            try {
                int expAwarded = 0;
                switch (currentMonster.getDifficulty()) {
                    case "LOW":
                        expAwarded = 20;
                        break;
                    case "MED":
                        expAwarded = 30;
                        break;
                    case "HIG":
                        expAwarded = 40;
                        break;
                }
                
                int currentExp = player.getCharacterData().getInt("Exp");
                player.getCharacterData().put("Exp", currentExp + expAwarded);
                
                // Check for level up
                player.checkLevelUp();
                
                // Update stats panel
                refreshStatsPanel();
                
                // Update graph visualization
                graphPanel.setCurrentRoom(currentRoom);
                graphPanel.repaint();
                
                JOptionPane.showMessageDialog(gui, "You gained " + expAwarded + " experience points!");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        // Reset combat state
        inCombat = false;
        currentMonster = null;
        activeConditions.clear();
        
        // Update room panel to show normal room view
        updateRoomPanel();
    }

    public static void main(String[] args) {
        GameGui g = new GameGui();
        g.mainMenu();
    }

    private int calculateMonsterStat(String statValue, int playerLevel, double difficultyMultiplier) {
        try {
            // Parse the stat value which could be a number or a formula
            if (statValue.matches("\\d+")) {
                return (int) (Integer.parseInt(statValue) * playerLevel * difficultyMultiplier);
            } else if (statValue.matches("\\d+d\\d+")) {
                String[] parts = statValue.split("d");
                int numDice = Integer.parseInt(parts[0]);
                int diceSides = Integer.parseInt(parts[1]);
                int total = 0;
                for (int i = 0; i < numDice; i++) {
                    total += new Random().nextInt(diceSides) + 1;
                }
                return (int) (total * playerLevel * difficultyMultiplier);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Default fallback
        return (int) (7 * playerLevel * difficultyMultiplier);
    }

    private int calculateMonsterStamina(String staminaValue, int playerLevel, double difficultyMultiplier) {
        try {
            if (staminaValue.matches("\\d+")) {
                return (int) (Integer.parseInt(staminaValue) * playerLevel * difficultyMultiplier);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Default fallback
        return 50 * playerLevel;
    }
}

class Room {
    protected int id;
    protected String type;
    protected boolean hasObstacle;
    protected String obstacleType;
    protected java.util.List<Room> neighbors;
    protected boolean visited;
    protected int x, y;
    protected Set<Integer> defeatedMonsters; // Track defeated monsters by their IDs

    // Base constructor for rooms without an obstacle.
    public Room(int id, String type) {
        this.id = id;
        this.type = type;
        this.hasObstacle = false;
        this.obstacleType = "None";
        this.neighbors = new ArrayList<>();
        this.visited = false;
        this.defeatedMonsters = new HashSet<>();
    }

    // Base constructor for rooms with an obstacle.
    public Room(int id, String type, String obstacleType) {
        this.id = id;
        this.type = type;
        this.hasObstacle = true;
        this.obstacleType = obstacleType;
        this.neighbors = new ArrayList<>();
        this.visited = false;
        this.defeatedMonsters = new HashSet<>();
    }

    // Add method to mark a monster as defeated
    public void markMonsterDefeated(int monsterId) {
        defeatedMonsters.add(monsterId);
    }

    // Add method to check if a monster is defeated
    public boolean isMonsterDefeated(int monsterId) {
        return defeatedMonsters.contains(monsterId);
    }

    // Common methods.
    public void addNeighbor(Room room) {
        if (!neighbors.contains(room)) {
            neighbors.add(room);
        }
    }

    public java.util.List<Room> getNeighbors() {
        return neighbors;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public boolean hasObstacle() {
        return hasObstacle;
    }

    public String getObstacleType() {
        return obstacleType;
    }

    public boolean isVisited() {
        return visited;
    }

    public void markVisited() {
        this.visited = true;
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
        StringBuilder sb = new StringBuilder("Room " + id + " (" + type + ") -> ");
        for (Room neighbor : neighbors) {
            sb.append(neighbor.getId()).append(" ");
        }
        return sb.toString();
    }
}

// Subclass for Monster rooms.
class MonsterRoom extends Room {
    private Monster currentMonster;

    // Overloaded constructors to optionally support obstacles.
    public MonsterRoom(int id) {
        super(id, "monsterRoom");
        this.currentMonster = loadRandomMonster(getThemeFromDungeon());
    }

    public MonsterRoom(int id, String obstacleType) {
        super(id, "monsterRoom", obstacleType);
        this.currentMonster = loadRandomMonster(getThemeFromDungeon());
    }

    // This method retrieves the theme for the dungeon.
    // For simplicity, we assume the first theme from a random monster is used.
    private String getThemeFromDungeon() {
        // In a full implementation you might pass the dungeon theme into the
        // constructor.
        return "Dark Forest"; // Placeholder: replace with actual theme logic.
    }

    private Monster loadRandomMonster(String theme) {
        try {
            String content = new String(Files.readAllBytes(Paths.get("lib/Monsters.json")));
            JSONObject json = new JSONObject(content);
            JSONArray allMonsters = json.getJSONArray("monsters");
            List<JSONObject> valid = new ArrayList<>();
            for (int i = 0; i < allMonsters.length(); i++) {
                JSONObject monster = allMonsters.getJSONObject(i);
                JSONArray themes = monster.getJSONArray("themes");
                for (int j = 0; j < themes.length(); j++) {
                    if (themes.getString(j).equalsIgnoreCase(theme)) {
                        valid.add(monster);
                        break;
                    }
                }
            }
            if (valid.isEmpty())
                return null;
            JSONObject selected = valid.get(new Random().nextInt(valid.size()));
            return new Monster(selected);
        } catch (IOException e) {
            System.out.println("Error reading Monsters.json: " + e.getMessage());
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currentMonster;
    }

    public Monster getMonster() {
        return currentMonster;
    }
}

// Subclass for Treasure rooms.
class TreasureRoom extends Room {
    private int treasureGold;
    private List<String> treasureItems;
    // Use an instance-level random seeded using the dungeon seed and room id.
    private Random treasureRand;

    // Constructor without obstacle: dungeonSeed is passed in.
    public TreasureRoom(int id, long dungeonSeed) {
        super(id, "Treasure");
        treasureRand = new Random(dungeonSeed + id);
        generateTreasure();
    }

    // Constructor with obstacle.
    public TreasureRoom(int id, String obstacleType, long dungeonSeed) {
        super(id, "Treasure", obstacleType);
        treasureRand = new Random(dungeonSeed + id);
        generateTreasure();
    }

    private void generateTreasure() {
        // Generate gold between 100 and 500 in multiples of 10.
        treasureGold = 100 + treasureRand.nextInt(41) * 10;
        treasureItems = new ArrayList<>();

        // Load items from Items.json
        try {
            String itemsJson = new String(Files.readAllBytes(Paths.get("lib/Items.json")));
            JSONObject itemsData = new JSONObject(itemsJson);
            JSONArray allItems = itemsData.getJSONArray("items");

            // Convert to list for shuffling
            List<JSONObject> itemList = new ArrayList<>();
            for (int i = 0; i < allItems.length(); i++) {
                itemList.add(allItems.getJSONObject(i));
            }

            // Shuffle using dungeon seed + room ID for consistency
            Collections.shuffle(itemList, treasureRand);

            // Select 1-3 random items
            int numItems = 1 + treasureRand.nextInt(3); // 1 to 3 items
            for (int i = 0; i < Math.min(numItems, itemList.size()); i++) {
                treasureItems.add(itemList.get(i).getString("id"));
            }
        } catch (IOException | JSONException e) {
            // Fallback to placeholder if file read fails
            e.printStackTrace();
            for (int i = 0; i < 3; i++) {
                treasureItems.add("Item" + treasureRand.nextInt(1000));
            }
        }
    }

    public int getTreasureGold() {
        return treasureGold;
    }

    public List<String> getTreasureItems() {
        return treasureItems;
    }
}

// Subclass for Upgrade rooms.
class UpgradeRoom extends Room {
    public UpgradeRoom(int id) {
        super(id, "Upgrade");
    }

    public UpgradeRoom(int id, String obstacleType) {
        super(id, "Upgrade", obstacleType);
    }
}

// Subclass for Shop rooms.
class ShopRoom extends Room {
    public ShopRoom(int id) {
        super(id, "Shop");
    }

    public ShopRoom(int id, String obstacleType) {
        super(id, "Shop", obstacleType);
    }
}

class Dungeon {
    private java.util.List<Room> rooms;
    private Random random;
    private double size;
    private String theme;
    private List<JSONObject> shopItems;
    private long dungeonSeed; // added to store the seed

    public Dungeon(double seed) {
        rooms = new ArrayList<>();
        shopItems = new ArrayList<>();
        // Store dungeon seed (casting to long) so it can be reused.
        dungeonSeed = (long) seed;
        random = new Random(dungeonSeed);
        size = (random.nextDouble() * 1.25) + 0.75;

        // Load and randomize shop items
        try {
            String itemsJson = new String(Files.readAllBytes(Paths.get("lib/Items.json")));
            JSONObject itemsData = new JSONObject(itemsJson);
            JSONArray allItems = itemsData.getJSONArray("items");

            // Shuffle all items and pick first 5
            List<JSONObject> itemList = new ArrayList<>();
            for (int i = 0; i < allItems.length(); i++) {
                itemList.add(allItems.getJSONObject(i));
            }
            Collections.shuffle(itemList, random);

            // Store first 5 items as shop inventory
            for (int i = 0; i < Math.min(5, itemList.size()); i++) {
                shopItems.add(itemList.get(i));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
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
        };

        // Create rooms.
        for (int i = 0; i < noRooms; i++) {
            Room newRoom;
            // Room with id 1 is special.
            if (i == 1) {
                newRoom = new Room(i, "startingRoom");
                    } else {
                double chance = random.nextDouble();
                boolean hasObstacle = random.nextDouble() < 0.25;
                String obstacle = hasObstacle ? roomObstacles[random.nextInt(roomObstacles.length)] : null;
                if (chance < 0.5) { // Monster room.
                    newRoom = hasObstacle ? new MonsterRoom(i, obstacle) : new MonsterRoom(i);
                } else if (chance < 0.8) { // Treasure room.
                    if (hasObstacle) {
                        newRoom = new TreasureRoom(i, obstacle, dungeonSeed);
                    } else {
                        newRoom = new TreasureRoom(i, dungeonSeed);
                    }
                } else if (chance < 0.9) { // Upgrade room.
                    newRoom = hasObstacle ? new UpgradeRoom(i, obstacle) : new UpgradeRoom(i);
                } else { // Shop room.
                    newRoom = hasObstacle ? new ShopRoom(i, obstacle) : new ShopRoom(i);
                }
            }
            rooms.add(newRoom);
        }

        // Create a spanning tree and extra connections (unchanged).
        List<Room> connected = new ArrayList<>();
        List<Room> unconnected = new ArrayList<>(rooms);
        Room first = unconnected.remove(random.nextInt(unconnected.size()));
        connected.add(first);

        while (!unconnected.isEmpty()) {
            Room toConnect = unconnected.remove(random.nextInt(unconnected.size()));
            Room connectTo = connected.get(random.nextInt(connected.size()));

            toConnect.addNeighbor(connectTo);
            connectTo.addNeighbor(toConnect);
            connected.add(toConnect);
        }

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

        // Set random positions.
        for (Room node : rooms) {
            int x = 50 + random.nextInt(1200);
            int y = 50 + random.nextInt(800);
            node.setPosition(x, y);
        }
    }

    public java.util.List<Room> getRoom() {
        return rooms;
    }

    public List<JSONObject> getShopItems() {
        return shopItems;
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

    public void applyUpgradeRoomBonus() throws JSONException {
        // Add XP bonus
        int currentExp = characterData.getInt("Exp");
        characterData.put("Exp", currentExp + 50);

        // Increase each stat by 1
        characterData.put("Strength", characterData.getInt("Strength") + 1);
        characterData.put("Dexterity", characterData.getInt("Dexterity") + 1);
        characterData.put("Magic", characterData.getInt("Magic") + 1);
        characterData.put("Toughness", characterData.getInt("Toughness") + 1);

        // Recalculate HP based on new Toughness and current Level.
        int toughness = characterData.getInt("Toughness");
        int level = characterData.getInt("Level");
        int newHp = level * toughness / 10 * 100;
        characterData.put("HP", newHp);
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

    public void checkLevelUp() {
        try {
            int currentExp = characterData.getInt("Exp");
            int currentLevel = characterData.getInt("Level");
            if (currentExp >= 100) {
                levelUp();
                characterData.put("Level", currentLevel + 1);
                characterData.put("Exp", currentExp - 100); // Reset XP after leveling up
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

    public int getCurrentHp() throws JSONException {
        return characterData.getInt("HP");
    }

    public int getCurrentStamina() throws JSONException {
        return characterData.getInt("CurrentStamina");
    }

    public int getMaxStamina() throws JSONException {
        return characterData.getInt("MaxStamina");
    }

    public void setCurrentStamina(int value) throws JSONException {
        characterData.put("CurrentStamina", Math.min(value, getMaxStamina()));
    }

    public JSONArray getSkills() throws JSONException {
        if (!characterData.has("Skills")) {
            characterData.put("Skills", new JSONArray());
        }
        return characterData.getJSONArray("Skills");
    }

    public void addSkill(String skillId) throws JSONException {
        JSONArray skills = getSkills();
        skills.put(skillId);
        characterData.put("Skills", skills);
    }

    public JSONArray getEligibleSkills() throws JSONException, IOException {
        JSONArray eligible = new JSONArray();
        String charClass = characterData.getString("Class");

        // Read skills from Skills.json
        String skillsJsonStr = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("lib/Skills.json")));
        JSONObject skillsJson = new JSONObject(skillsJsonStr);
        JSONArray allSkills = skillsJson.getJSONArray("skills");

        for (int i = 0; i < allSkills.length(); i++) {
            JSONObject skill = allSkills.getJSONObject(i);
            String restriction = skill.optString("classRestriction", "all");
            if (restriction.equals("all") || restriction.equals(charClass)) {
                eligible.put(skill.getString("id"));
            }
        }
        return eligible;
    }

    public void levelUp() throws JSONException {
        // Existing level-up calculations.
        int toughness = characterData.getInt("Toughness");
        int newHp = getLevel() * toughness / 10 * 100;
        characterData.put("HP", newHp);

        // Increase max stamina by 100 per level (base level-up bonus)
        int newMaxStamina = getMaxStamina() + 100;
        characterData.put("MaxStamina", newMaxStamina);
        // Restore current stamina to max
        characterData.put("CurrentStamina", newMaxStamina);

        // Increment level.
        characterData.put("Level", getLevel() + 1);

        // --- New Skill Selection ---
        // Build the list of choices
        List<String> skillOptions = new ArrayList<>();
        
        // Add multiclass option if not already multiclassed
        if (!characterData.has("SecondaryClass")) {
            skillOptions.add("Multiclass (Add Secondary Class)");
        }
        
        // Add stat buff options
        skillOptions.add("Buff a stat (+1)");
        skillOptions.add("Increase Max Stamina (+50)");

        // Load and add eligible skills
        try {
            String skillsJsonStr = new String(Files.readAllBytes(Paths.get("lib/Skills.json")));
            JSONObject skillsJson = new JSONObject(skillsJsonStr);
            JSONArray allSkills = skillsJson.getJSONArray("skills");

            String primaryClass = characterData.getString("Class").toLowerCase();
            String secondaryClass = characterData.optString("SecondaryClass", "").toLowerCase();

            for (int i = 0; i < allSkills.length(); i++) {
                JSONObject skill = allSkills.getJSONObject(i);
                String skillId = skill.getString("id");
                
                // Skip if player already has this skill
                if (hasSkill(skillId)) {
                    continue;
                }

                // Check class requirements
                String restriction = skill.optString("classRestriction", "all").toLowerCase();
                if (restriction.equals("all") || 
                    restriction.equals(primaryClass) || 
                    (!secondaryClass.isEmpty() && restriction.equals(secondaryClass))) {
                    
                    // Add skill to options with description
                    String skillName = skill.getString("name");
                    String skillDesc = skill.optString("description", "");
                    String stat = skill.getString("stat");
                    int staminaCost = skill.getInt("staminaCost");
                    String scaling = skill.getJSONObject("scaling").getString("damage");
                    
                    skillOptions.add(String.format("Learn %s - %s (Cost: %d %s, %s damage)", 
                        skillName, skillDesc, staminaCost, stat, scaling));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] optionsArray = skillOptions.toArray(new String[0]);
        String selectedSkill = (String) JOptionPane.showInputDialog(
                null,
                "Choose a skill for your level up:",
                "Skill Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                optionsArray,
                optionsArray[0]);

        if (selectedSkill != null) {
            if (selectedSkill.contains("Multiclass")) {
                // Prompt for secondary class selection
                String primaryClass = characterData.getString("Class");
                String[] classes = { "Warrior", "Mage", "Rogue" };
                List<String> availableClasses = new ArrayList<>();
                for (String cls : classes) {
                    if (!cls.equals(primaryClass)) {
                        availableClasses.add(cls);
                    }
                }
                String[] availableArray = availableClasses.toArray(new String[0]);
                String secondary = (String) JOptionPane.showInputDialog(
                        null,
                        "Select a secondary class:",
                        "Multiclass Selection",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        availableArray,
                        availableArray[0]);
                if (secondary != null && !secondary.trim().isEmpty()) {
                    characterData.put("SecondaryClass", secondary);
                    JOptionPane.showMessageDialog(null, "Secondary class " + secondary + " added!");
                }
                // Record that the multiclass skill was chosen
                addSkill("MULTICLASS");
            } else if (selectedSkill.contains("Buff a stat")) {
                // Let the player choose which stat to buff
                String[] statsOptions = { "Strength", "Dexterity", "Toughness", "Magic" };
                String selectedStat = (String) JOptionPane.showInputDialog(
                        null,
                        "Select a stat to buff (+1):",
                        "Stat Buff Selection",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        statsOptions,
                        statsOptions[0]);
                if (selectedStat != null && !selectedStat.trim().isEmpty()) {
                    int currentStat = characterData.getInt(selectedStat);
                    characterData.put(selectedStat, currentStat + 1);
                    addSkill("BUFF_" + selectedStat.toUpperCase());
                    JOptionPane.showMessageDialog(null, selectedStat + " increased by 1!");
                }
            } else if (selectedSkill.contains("Increase Max Stamina")) {
                // Increase Max Stamina by 50
                int currentMaxStamina = getMaxStamina();
                characterData.put("MaxStamina", currentMaxStamina + 50);
                // Restore current stamina to new max
                characterData.put("CurrentStamina", currentMaxStamina + 50);
                addSkill("BUFF_STAMINA");
                JOptionPane.showMessageDialog(null, "Max Stamina increased by 50!");
            } else if (selectedSkill.startsWith("Learn ")) {
                // Extract skill name from the option
                String skillName = selectedSkill.substring(6).split(" - ")[0];
                
                // Find the skill in Skills.json
                try {
                    String skillsJsonStr = new String(Files.readAllBytes(Paths.get("lib/Skills.json")));
                    JSONObject skillsJson = new JSONObject(skillsJsonStr);
                    JSONArray allSkills = skillsJson.getJSONArray("skills");
                    
                    for (int i = 0; i < allSkills.length(); i++) {
                        JSONObject skill = allSkills.getJSONObject(i);
                        if (skill.getString("name").equals(skillName)) {
                            addSkill(skill.getString("id"));
                            JOptionPane.showMessageDialog(null, "Learned " + skillName + "!");
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean hasSkill(String skillId) throws JSONException {
        if (!characterData.has("Skills")) {
            return false;
        }
        JSONArray skills = characterData.getJSONArray("Skills");
        for (int i = 0; i < skills.length(); i++) {
            if (skills.getString(i).equals(skillId)) {
                return true;
            }
        }
        return false;
    }
}

class Monster {
    private int id;
    private String name;
    private String difficulty;
    private List<String> themes;
    private String str;
    private String dex;
    private String mag;
    private String fort;
    private String stamina;
    private List<String> skillIds;

    public Monster(JSONObject json) {
        try {
            this.id = json.getInt("id");
            this.name = json.getString("name");
            this.difficulty = json.getString("difficulty");
            this.themes = new ArrayList<>();
            JSONArray themeArray = json.getJSONArray("themes");
            for (int i = 0; i < themeArray.length(); i++) {
                themes.add(themeArray.getString(i));
            }
            JSONObject stats = json.getJSONObject("stats");
            this.str = stats.getString("STR");
            this.dex = stats.getString("DEX");
            this.mag = stats.getString("MAG");
            this.fort = stats.getString("FOR");
            this.stamina = stats.getString("Stamina");
            this.skillIds = new ArrayList<>();
            String skillsStr = json.optString("skills", "").trim();
            if (!skillsStr.isEmpty()) {
                for (String id : skillsStr.split(",")) {
                    skillIds.add(id.trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public List<String> getThemes() {
        return themes;
    }

    public String getStr() {
        return str;
    }

    public String getDex() {
        return dex;
    }

    public String getMag() {
        return mag;
    }

    public String getFort() {
        return fort;
    }

    public String getStamina() {
        return stamina;
    }

    public List<String> getSkillIds() {
        return skillIds;
    }

    @Override
    public String toString() {
        return name + " (" + difficulty + ") with skills " + skillIds;
    }
}