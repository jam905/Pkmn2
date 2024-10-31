package org.example;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.*;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class CardImport {

    public static final long serialVersionUID = 1L;

    String filename;
    BufferedReader reader;

    public CardImport(String fileName) throws FileNotFoundException {
        this.filename = fileName;
        reader = new BufferedReader(new FileReader(fileName));
    }

    public Card createPokemon() throws IOException {

        String s = reader.readLine();
        PokemonStage stage = PokemonStage.valueOf(s.toUpperCase());

        String name = reader.readLine();

        int hp = Integer.parseInt(reader.readLine());

        s = reader.readLine();
        EnergyType pokemonType = !s.equals("-") ? EnergyType.valueOf(s.toUpperCase()) : null;

        String pathEvolvesFrom = reader.readLine();
        Card card = null;
        if (!pathEvolvesFrom.equals("-")) {
            CardImport cardImport = new CardImport(pathEvolvesFrom);
            card = cardImport.createPokemon();
        }


        s = reader.readLine();
        List<AttackSkill> attackSkillList = new ArrayList<>();
        String[] tokens = s.split(",");
        if (tokens.length == 1){
            String[] args = tokens[0].split("/");
            attackSkillList.add(new AttackSkill(args[0], args[1], Integer.valueOf(args[2])));
        }
        else if (tokens.length == 2){
            String[] args = tokens[0].split("/");
            attackSkillList.add(new AttackSkill(args[0], args[1], Integer.valueOf(args[2])));
            args = tokens[1].split("/");
            attackSkillList.add(new AttackSkill(args[0], args[1], Integer.valueOf(args[2])));
        }

        s = reader.readLine();
        EnergyType weaknessType = !s.equals("-") ? EnergyType.valueOf(s.toUpperCase()) : null;

        s = reader.readLine();
        EnergyType resistanceType = !s.equals("-") ? EnergyType.valueOf(s.toUpperCase()) : null;

        String cost = reader.readLine();

        String setName = reader.readLine();

        char regulationMark = reader.readLine().charAt(0);

        s = reader.readLine();
        Student student = null;
        if (!s.equals("") && !s.equals(" ") && !s.equals("-")) {
            tokens = s.split("/");
            student = new Student(tokens[1], tokens[0], tokens[2], tokens[3]);
        }

        String number = reader.readLine();

        reader.close();
        return new Card(stage, name, hp, pokemonType, card, attackSkillList, weaknessType, resistanceType,
                cost, setName, regulationMark, student, number);
    }

    public Card loadCard(String filename) {
        Card card = null;
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            card = (Card) in.readObject();
            System.out.println("Десериализованная карта " + card.getName() + ":");
            System.out.println(card);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return card;
    }

}
