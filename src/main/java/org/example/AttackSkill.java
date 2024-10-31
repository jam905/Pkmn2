package org.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AttackSkill implements Serializable {

    public static final long serialVersionUID = 1L;

    String name;
    String description;
    String cost;
    int damage;

    public AttackSkill(String cost, String name, int damage){
        this.cost = cost;
        this.name = name;
        this.damage = damage;
    }

    public AttackSkill() {
    }

    @JsonCreator
    public AttackSkill(
            @JsonProperty("cost") String cost,
            @JsonProperty("name") String name,
            @JsonProperty("damage") int damage,
            @JsonProperty("description") String description) {
        this.cost = cost;
        this.name = name;
        this.damage = damage;
        this.description = description;
    }

    @Override
    public String toString(){
        return "\n  name = " + name + "\n" +
                "  description = " + description + "\n" +
                "  cost = " + cost + "\n" +
                "  damage = " + damage;
    }

}
