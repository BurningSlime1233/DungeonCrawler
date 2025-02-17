import java.util.*;
import java.util.regex.Pattern;
import java.io.*;
import org.json.JSONObject;

public class Game {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String name;
        String regex = "^(?!\\.|\\.\\.)([^<>:\"/\\\\|?*]+)$";
        do{
            System.out.println("Enter Name of Character:");
            name = sc.nextLine();
        }while(!(Pattern.matches(regex,name)));
        File save = new File(name+".json");
        if (save.createNewFile()){
            System.out.println("File created: " + save.getName());
        }
        else{
            System.out.println("File already exists.");
        }

        try (FileWriter fileWriter = new FileWriter(save)) {
            JSONObject characterData = new JSONObject();
            characterData.put("character_name", name);

            fileWriter.write(characterData.toString(4));
            fileWriter.flush();

            System.out.println("Character name saved to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }

        sc.close();
    }
}
