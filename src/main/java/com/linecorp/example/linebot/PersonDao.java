package com.linecorp.example.linebot;

import java.util.List;
import com.linecorp.example.linebot.Person;

public interface PersonDao
{
    public List<Person> get();
    public List<Person> getByName(String aName);
    public int registerPerson(String aName, String aPhoneNumber);
};
