package com.chattanon.Chattanon;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Random;
import java.util.*;
import java.lang.Math;

@CrossOrigin("*")
@RestController
public class Controller {

    Hashtable<String, String[]> messages = new Hashtable<String, String[]>();
    Hashtable<String, Integer> userCount = new Hashtable<String, Integer>();
    int totalUsers = 0;

    @PostMapping("/joinChannel")
    public ResponseEntity<String> joinChannel (@RequestBody String requestBody){
        JSONObject obj = new JSONObject(requestBody);
        String channelName = obj.getString("channelName");
        int users = 1;
        if (userCount.containsKey(channelName)){
            users = userCount.get(channelName) + 1;
            userCount.put(channelName, users);
        }
        else{
            userCount.put(channelName, users);
        }
        totalUsers++;
        return ResponseEntity.ok("Current users in the server: " + users);
    }

    @PostMapping("/leaveChannel")
    public ResponseEntity<String> leaveChannel (@RequestBody String requestBody){
        JSONObject obj = new JSONObject(requestBody);
        String channelName = obj.getString("channelName");
        int users = userCount.get(channelName) - 1;
        userCount.put(channelName, users);
        if (users == 0){
            messages.put(channelName, new String[2]);
        }
        totalUsers--;
        return ResponseEntity.ok("Current users in the server: " + users);
    }

    @PostMapping("/postMessage")
    public ResponseEntity<String> postMessage (@RequestBody String requestBody){
        JSONObject obj = new JSONObject(requestBody);
        String channelName = obj.getString("channelName");
        String userName = obj.getString("userName");
        String message = obj.getString("message");
        String[] messageToPut = new String[2];
        messageToPut[0] = userName;
        messageToPut[1] = message;
        messages.put(channelName,messageToPut);
        return ResponseEntity.ok(requestBody);
    }

    @GetMapping("/getMessage/{channelName:.+}")
    public ResponseEntity<String> getMessage (@PathVariable String channelName){
        String[] message = messages.get(channelName);
        return ResponseEntity.ok("Message owner: " + message[0] + "\nMessage: " + message[1]);
    }

    @GetMapping("/getUserCount")
    public ResponseEntity<String> getUserCount (){
       // return ResponseEntity.ok("" + totalUsers);
        return ResponseEntity.ok("" + Math.abs(new Random().nextInt()));
    }

}
