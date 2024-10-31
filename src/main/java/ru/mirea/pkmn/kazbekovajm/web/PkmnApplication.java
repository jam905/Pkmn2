package ru.mirea.pkmn.kazbekovajm.web;

import com.fasterxml.jackson.databind.JsonNode;
import ru.mirea.pkmn.AttackSkill;
import ru.mirea.pkmn.Card;
import ru.mirea.pkmn.kazbekovajm.web.http.PkmnHttpClient;
import ru.mirea.pkmn.kazbekovajm.web.jdbc.DatabaseServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class PkmnApplication {

    public static void main(String[] args) throws IOException, SQLException {

        //путь my_card.txt
        CardImport cardImport = new CardImport("C:\\Users\\kdzam\\IdeaProjects\\Pkmn4\\src\\main\\resources\\my_card.txt");
        Card pokemon = cardImport.createPokemon();

        PkmnHttpClient pkmnHttpClient = new PkmnHttpClient();

        JsonNode cardData = pkmnHttpClient.getPokemonCard(pokemon.getName(), pokemon.getNumber());
        System.out.println(cardData.toPrettyString());

        List<AttackSkill> skills = new ArrayList<>();

        JsonNode dataNode = cardData.path("data");
        if (dataNode.isArray()) {
            JsonNode cardInfo = dataNode.get(0);

            JsonNode attacks = cardInfo.path("attacks");
            if (attacks.isArray()) {
                for (JsonNode attack : attacks) {
                    String name = attack.path("name").asText();
                    String damage = attack.path("damage").asText("");
                    String cost = attack.path("cost").toString();
                    String text = attack.path("text").asText("");
                    AttackSkill skill = new AttackSkill(cost, name, damage.isEmpty() ? 0 : Integer.parseInt(damage));
                    skill.setDescription(text);
                    skills.add(skill);
                }
            }
        }

        pokemon.setSkills(skills);

        Files.write(Paths.get("pokemon_card_" + pokemon.getNumber() + ".json"), cardData.toPrettyString().getBytes());

        CardExport cardExport = new CardExport();
        cardExport.saveCard(pokemon);

        Card deserializedPokemon = cardImport.loadCard(pokemon.getName() + ".crd");

        DatabaseServiceImpl base = new DatabaseServiceImpl();
        base.createPokemonOwner(pokemon.getPokemonOwner());
        base.saveCardToDatabase(pokemon);
        System.out.println(base.getCardFromDatabase(pokemon.getName()));
        System.out.println(base.getStudentFromDatabase(pokemon.getPokemonOwner().getFirstName() + " " +
                pokemon.getPokemonOwner().getSurName() + " " +
                pokemon.getPokemonOwner().getFamilyName()));
    }
}