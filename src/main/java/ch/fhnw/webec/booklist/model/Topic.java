package ch.fhnw.webec.booklist.model;

import ch.fhnw.webec.booklist.form.SelectOption;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Topic implements SelectOption {
    @Id
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @JsonIgnore
    public String getValue() {
        return String.valueOf(this.getId());
    }

    @Override
    @JsonIgnore
    public String getLabel() {
        return this.getName();
    }
}
