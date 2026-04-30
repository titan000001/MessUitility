package com.messutility.models.users;

public abstract class User {
    protected String id;
    protected String name;
    protected String contact;
    protected String password;

    public User(String id, String name, String contact, String password) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Abstract methods can be added as needed
}
