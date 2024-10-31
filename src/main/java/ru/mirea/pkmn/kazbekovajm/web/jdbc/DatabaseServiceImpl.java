package ru.mirea.pkmn.kazbekovajm.web.jdbc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.mirea.pkmn.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class DatabaseServiceImpl implements DatabaseService {

    private final Connection connection;

    private final Properties databaseProperties;

    public DatabaseServiceImpl() throws SQLException, IOException {


        databaseProperties = new Properties();
        //подставить путь database.properties
        databaseProperties.load(new FileInputStream("C:\\Users\\kdzam\\IdeaProjects\\Pkmn4\\src\\main\\resources\\database.properties"));

        connection = DriverManager.getConnection(
                databaseProperties.getProperty("database.url"),
                databaseProperties.getProperty("database.user"),
                databaseProperties.getProperty("database.password")
        );
        System.out.println("Connection is "+(connection.isValid(0) ? "up" : "down"));
    }

    @Override
    public Card getCardFromDatabase(String cardName) {
        String sql = "SELECT * FROM card WHERE name = ?";
        Card card = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cardName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                int hp = rs.getInt("hp");
                String evolvesFromName = rs.getString("evolves_from");
                String gameSet = rs.getString("game_set");
                UUID ownerId = (UUID) rs.getObject("pokemon_owner");
                String stage = rs.getString("stage");
                String retreatCost = rs.getString("retreat_cost");
                String weaknessType = rs.getString("weakness_type");
                String resistanceType = rs.getString("resistance_type");
                String attackSkillsJson = rs.getString("attack_skills");
                String pokemonType = rs.getString("pokemon_type");
                char regulationMark = rs.getString("regulation_mark").charAt(0);
                String cardNumber = rs.getString("card_number");

                ObjectMapper objectMapper = new ObjectMapper();
                List<AttackSkill> skills = objectMapper.readValue(attackSkillsJson, new TypeReference<List<AttackSkill>>() {});

                Student owner = getStudentFromDatabaseById(ownerId);

                Card evolvesFrom = evolvesFromName != null ? getCardFromDatabase(evolvesFromName) : null;

                card = new Card(
                        PokemonStage.valueOf(stage),
                        name,
                        hp,
                        EnergyType.valueOf(pokemonType),
                        evolvesFrom,
                        skills,
                        weaknessType != null ? EnergyType.valueOf(weaknessType) : null,
                        resistanceType != null ? EnergyType.valueOf(resistanceType) : null,
                        retreatCost,
                        gameSet,
                        regulationMark,
                        owner,
                        cardNumber
                );
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return card;
    }

    @Override
    public Student getStudentFromDatabase(String studentFullName) {
        String[] nameParts = studentFullName.split(" ");
        if (nameParts.length != 3) {
            throw new IllegalArgumentException("Full name must include first name, surname, and patronymic.");
        }

        String sql = "SELECT * FROM student WHERE \"firstName\" = ? AND \"familyName\" = ? AND \"patronicName\" = ?";
        Student student = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nameParts[0]);
            pstmt.setString(2, nameParts[1]);
            pstmt.setString(3, nameParts[2]);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String firstName = rs.getString("firstName");
                String familyName = rs.getString("familyName");
                String patronicName = rs.getString("patronicName");
                String group = rs.getString("group");

                student = new Student(firstName, familyName, patronicName, group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return student;
    }

    @Override
    public void saveCardToDatabase(Card card) {
        String sql = "INSERT INTO card (id, name, hp, evolves_from, game_set, pokemon_owner, stage, retreat_cost, weakness_type, " +
                "resistance_type, attack_skills, pokemon_type, regulation_mark, card_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, UUID.randomUUID());

            pstmt.setString(2, card.getName());
            pstmt.setInt(3, card.getHp());

            if (card.getEvolvesFrom() != null) {
                UUID evolvesFromId = UUID.fromString(card.getEvolvesFrom().getName());
                pstmt.setObject(4, evolvesFromId);
            } else {
                pstmt.setObject(4, null);
            }

            pstmt.setString(5, card.getGameSet());

            UUID ownerId = getStudentIdByFullName(card.getPokemonOwner());
            pstmt.setObject(6, ownerId);

            pstmt.setString(7, card.getPokemonStage().name());
            pstmt.setString(8, card.getRetreatCost());
            pstmt.setString(9, card.getWeaknessType() != null ? card.getWeaknessType().name() : null);
            pstmt.setString(10, card.getResistanceType() != null ? card.getResistanceType().name() : null);
            pstmt.setString(11, new ObjectMapper().writeValueAsString(card.getSkills()));
            pstmt.setString(12, card.getPokemonType().name());
            pstmt.setString(13, String.valueOf(card.getRegulationMark()));
            pstmt.setString(14, card.getNumber());

            pstmt.executeUpdate();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createPokemonOwner(Student owner) {
        String sql = "INSERT INTO student (id, \"familyName\", \"firstName\", \"patronicName\", \"group\") VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, UUID.randomUUID());
            pstmt.setString(2, owner.getSurName());
            pstmt.setString(3, owner.getFirstName());
            pstmt.setString(4, owner.getFamilyName());
            pstmt.setString(5, owner.getGroup());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UUID getStudentIdByFullName(Student student) throws SQLException {
        String sql = "SELECT id FROM student WHERE \"firstName\" = ? AND \"familyName\" = ? AND \"patronicName\" = ?";
        UUID studentId = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getSurName());
            pstmt.setString(3, student.getFamilyName());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                studentId = (UUID) rs.getObject("id");
            }
        }

        return studentId;
    }

    public Student getStudentFromDatabaseById(UUID studentId) {
        String sql = "SELECT * FROM student WHERE id = ?";
        Student student = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String firstName = rs.getString("firstName");
                String familyName = rs.getString("familyName");
                String patronicName = rs.getString("patronicName");
                String group = rs.getString("group");

                student = new Student(firstName, familyName, patronicName, group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return student;
    }

}
