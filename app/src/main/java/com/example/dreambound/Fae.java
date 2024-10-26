package com.example.dreambound;

import java.io.Serializable;

public class Fae extends CreatureEntity implements Serializable {
    private String name;
    private String ability;
    private String description;


    public Fae(String name, String ability, String description, float x, float y, float _enemiesWidth, float _enemiesHeight) {
        super(x, y, _enemiesWidth, _enemiesHeight);
        this.name = name;
        this.ability = ability;
        this.description = description;
    }

// Getters and Setters
public String getName() { return name; }
public void setName(String name) { this.name = name; }

public String getAbility() { return ability; }
public void setAbility(String ability) { this.ability = ability; }

public String getDescription() { return description; }
public void setDescription(String description) { this.description = description; }


}
