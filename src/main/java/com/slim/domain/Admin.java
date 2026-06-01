package com.slim.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "admins")
public class Admin {

    @Id
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String oras;

    public Admin() {}

    public Admin(String username, String password, String name, String oras) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.oras = oras;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOras() { return oras; }
    public void setOras(String oras) { this.oras = oras; }
}
