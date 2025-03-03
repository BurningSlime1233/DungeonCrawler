// import java.util.*;
// import java.util.regex.Pattern;
// import java.io.*;
// import org.json.JSONObject;
import javax.swing.*;

public class GUI {
    public static void main(String[] args) {
        JFrame application = new JFrame("My GUI");
        JButton newGame = new JButton("New Game");
        application.add(newGame);
        newGame.setBounds(150,200,220,50);
        application.setSize(1920,1080);
        application.setLayout(null);
        application.setVisible(true);
    }
}
