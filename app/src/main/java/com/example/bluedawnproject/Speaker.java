package com.example.bluedawnproject;

public interface Speaker<Type> {
    public void speakToAll();
    public void update(Type Data);
    public void addListener(Listener listener);
}
