package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
public class Card implements Serializable {

    public static final long serialVersionUID = 1L;

    PokemonStage pokemonStage;
    String name;
    int hp;
    EnergyType pokemonType;
    Card evolvesFrom;
    List<AttackSkill> skills;
    EnergyType weaknessType;
    EnergyType resistanceType;
    String retreatCost;
    String gameSet;
    char regulationMark;
    Student pokemonOwner;
    String number;

    public String toString(){
        return "{\n" + " pokemonStage = " + pokemonStage + "\n" +
                " name = " + name + "\n" +
                " hp = " + hp + "\n" +
                " pokemonType = " + pokemonType + "\n" +
                " evolvesFrom = " + evolvesFrom + "\n" +
                " skills = " + skills + "\n" +
                " weaknessType = " + weaknessType + "\n" +
                " resistanceType = " + resistanceType + "\n" +
                " retreatCost = " + retreatCost + "\n" +
                " gameSet = " + gameSet + "\n" +
                " regulationMark = " + regulationMark + "\n" +
                " pokemonOwner = " + pokemonOwner + "\n" +
                " number = " + number + "\n }";
    }

}
