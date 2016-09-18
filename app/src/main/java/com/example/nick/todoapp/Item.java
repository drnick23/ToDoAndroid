package com.example.nick.todoapp;

public class Item {
    public long id;
    public String text;

    public Item(String text) {
        this.text = text;
    }

    public Item(long id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}