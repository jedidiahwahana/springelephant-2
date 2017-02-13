
package com.linecorp.example.linebot;

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
import com.google.gson.Gson;

import java.sql.*;

import com.linecorp.bot.client.LineSignatureValidator;

@RestController
@RequestMapping(value="/linebot")
public class LineBotController
{
    @Autowired
    PersonDao mDao;
    
    @Autowired
    @Qualifier("com.linecorp.channel_secret")
    String lChannelSecret;
    
    @Autowired
    @Qualifier("com.linecorp.channel_access_token")
    String lChannelAccessToken;
    
    @RequestMapping(value="/callback", method=RequestMethod.POST)
    public ResponseEntity<String> callback(
        @RequestHeader("X-Line-Signature") String aXLineSignature,
        @RequestBody String aPayload)
    {
        // compose body
        final String text=String.format("The Signature is: %s",
            (aXLineSignature!=null && aXLineSignature.length() > 0) ? aXLineSignature : "N/A");
        
        System.out.println(text);
        
        final boolean valid=new LineSignatureValidator(lChannelSecret.getBytes()).validateSignature(aPayload.getBytes(), aXLineSignature);
        
        System.out.println("The signature is: " + (valid ? "valid" : "tidak valid"));
        
        //Get events from source
        if(aPayload!=null && aPayload.length() > 0)
        {
            System.out.println("Payload: " + aPayload);
        }
        
        Gson gson = new Gson();
        Payload payload = gson.fromJson(aPayload, Payload.class);
        String idTarget = payload.events[0].source.userId;
        System.out.println("ID Target: " + idTarget);
        String messageText = payload.events[0].message.text;
        System.out.println("Text Message: " + messageText);
        
        processText(idTarget, messageText);
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private void processText(String aTargetId, String aText)
    {
        System.out.println("message text: " + aText + " from: " + aTargetId);
        
        if (aText.indexOf("\"") == -1){
            pushManual(aTargetId, "Unknown keyword");
            return;
        }
        
        String [] words=aText.trim().split("\\s+");
        String intent=words[0];
        System.out.println("intent: " + intent);
        String msg = " ";
        
        String name = " ";
        String phoneNumber = " ";
        
        if(intent.equalsIgnoreCase("reg"))
        {
            String target=words.length>1 ? words[1] : "";
            if (target.length()<=3)
            {
                msg = "Need more than 3 character to find person";
            }
            else
            {
                name = aText.substring(aText.indexOf("\"") + 1, aText.lastIndexOf("\""));
                System.out.println("Name: " + name);
                phoneNumber = aText.substring(aText.indexOf("#") + 1);
                System.out.println("Phone Number: " + phoneNumber);
                String status = RegProcessor(name, phoneNumber);
                pushManual(aTargetId, status);
                return;
            }
        }
        else if(intent.equalsIgnoreCase("find"))
        {
            name = aText.substring(aText.indexOf("\"") + 1, aText.lastIndexOf("\""));
            System.out.println("Name: " + name);
            String txtMessage = FindProcessor(name);
            pushManual(aTargetId, txtMessage);
            return;
        }
        
        // if msg is invalid
        if(msg == " ")
        {
            pushManual(aTargetId, "Unknown keyword");
        }
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
        List<Person> self=mDao.getByName("%"+aName+"%");
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

    private void pushManual(String id_target, String message_text){
        String url = "https://api.line.me/v2/bot/message/push";
        
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        
        try{
            // add header
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + lChannelAccessToken);

            String jsonData = "{\"to\":\""+id_target+"\",\"messages\":[{\"type\":\"text\",\"text\":\""+message_text+"\"}]}";
            System.out.println(jsonData);
            
            StringEntity params =new StringEntity(jsonData);
            
            post.setEntity(params);
            
            HttpResponse response = client.execute(post);
            System.out.println("Response Code : "
                               + response.getStatusLine().getStatusCode());
            
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e){
            System.out.println("Exception is raised ");
            e.printStackTrace();
        }
    }
}
