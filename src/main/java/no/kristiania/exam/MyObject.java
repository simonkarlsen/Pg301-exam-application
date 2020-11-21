package no.kristiania.exam;

import net.minidev.json.annotate.JsonIgnore;
import no.kristiania.exam.user.User;
import org.hibernate.validator.constraints.Length;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

public class MyObject {

    private Integer id;

    private String description;


    public MyObject(int id, String description) {
        this.description = description;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
