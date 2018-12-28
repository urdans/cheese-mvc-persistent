package org.launchcode.models;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Menu {
    @Id
    @GeneratedValue
    private int id;

    @NotNull
    @Size(min=3, message = "Menu name must be at least 3 character long")
    private String name;

    @ManyToMany
    private List<Cheese> cheeses = new ArrayList<>();

    public Menu() {
    }

    public Menu(String name) {
        this.name = name;
    }

    public void addItem(Cheese item){
        cheeses.add(item);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public List<Cheese> getCheeses() {
        return cheeses;
    }
}
