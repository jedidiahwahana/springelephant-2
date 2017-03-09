
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
import com.linecorp.example.springelephant.db.*;

@RestController
@RequestMapping(value="/line")
public class Controller
{
    PostgresHelper client = new PostgresHelper(DbContract.DB_URL, DbContract.DB_USERNAME, DbContract.DB_PASSWORD);

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
        String regStatus = "";
        try {
            if (client.connect()) {
                System.out.println("DB connected");
                if (client.insert("phonebook", aName, aPhoneNumber) == 1) {
                    regStatus = "Record added";
                }
            }

        } catch (ClassNotFoundException | SQLException e) {
            regStatus = "Exception is raised ";
            e.printStackTrace();
        }
        catch(Exception e)
        {
            regStatus = "Unknown exception occurs";
        }

        return regStatus;
    }

    private String FindProcessor(String aName){
        String txt="Find Result: ";
        Person existsData = null;
        try {
            if (client.connect()) {
                existsData = client.getPerson("phonebook", aName);
                txt = existsData.name + " " + existsData.phoneNumber;
            }
        } catch (ClassNotFoundException | SQLException e) {
            txt = "Exception is raised ";
            e.printStackTrace();
        }
        catch(Exception e)
        {
            txt = "Unknown exception occurs";
        }
        return txt;
    }
}
