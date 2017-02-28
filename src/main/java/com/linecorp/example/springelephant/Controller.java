
package com.linecorp.example.springelephant;

import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpPost;

import org.json.JSONObject;
import org.json.JSONArray;

import java.sql.*;

@RestController
@RequestMapping(value="/line")
public class Controller
{
    @Autowired
    PersonDao mDao;
    
    @RequestMapping(value="/callback", method=RequestMethod.GET)
    public ResponseEntity<String> callback()
    {
        String regResult = RegProcessor("Jedidiah Wahana", "0123456789");
        System.out.println("Reg result: " + regResult);
        String findResult = FindProcessor("Jedidiah Wahana");
        System.out.println("Find result: " + findResult);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private String RegProcessor(String aName, String aPhoneNumber){
        String regStatus;
        String exist = FindProcessor(aName);
        if(exist=="Person not found")
        {
            int reg=mDao.registerPerson(aName, aPhoneNumber);
            if(reg==1)
            {
                regStatus="Successfully Registered";
            }
            else
            {
                regStatus="Registration process failed";
            }
        }
        else
        {
            regStatus="Already registered";
        }
        
        return regStatus;
    }
    
    private String FindProcessor(String aName){
        String txt="Find Result:";
        System.out.println("Call getByName function");
        List<Person> self=mDao.getByName("%"+aName+"%");
        System.out.println("getByName function finished");
        if(self.size() > 0)
        {
            for (int i=0; i<self.size(); i++){
                Person prs=self.get(i);
                txt=txt+"\\n\\n";
                txt=txt+getPersonString(prs);
            }
            
        }
        else
        {
            txt="Person not found";
        }
        return txt;
    }
    
    private String getPersonString(Person aPerson)
    {
        return String.format("Name: %s\\nPhone Number: %s\\n", aPerson.name, aPerson.phoneNumber);
    }
}
