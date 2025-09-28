package com.example.rest_web_service.user;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class User {
    private Integer id;
    @Size(min = 2,message = "name should have atleast 2 characters")
    private String name;
    @Past(message = "BirthDate should be past") // 必须是过去日期
    private LocalDate birthdate;

    public User(Integer id, String name, LocalDate birthdate) {
        this.id = id;
        this.name = name;
        this.birthdate = birthdate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthdate=" + birthdate +
                '}';
    }

}
