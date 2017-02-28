package com.linecorp.example.springelephant;

import java.util.List;
import com.linecorp.example.springelephant.Person;

public interface PersonDao
{
    public List<Person> get();
    public List<Person> getByName(String aName);
    public int registerPerson(String aName, String aPhoneNumber);
};
