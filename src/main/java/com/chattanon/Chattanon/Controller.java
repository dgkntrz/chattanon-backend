package com.chattanon.Chattanon;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.*;
import java.lang.Math;

@CrossOrigin("*")
@RestController
public class Controller {

    Hashtable<String, String[]> messages = new Hashtable<String, String[]>();
    Hashtable<String, String[]> serverProps = new Hashtable<String, String[]>();
    Hashtable<String, ArrayList<String>> userList = new Hashtable<String, ArrayList<String>>();
    int totalUsers = 0;

    @PostMapping("/joinChannel")
    public ResponseEntity<String> joinChannel (@RequestBody String requestBody){
        JSONObject obj = new JSONObject(requestBody);
        String channelName = obj.getString("channelName");
        String maxUsers = obj.getString("maxUsers");
        String password = obj.getString("password");
        String userName = obj.getString("userName");
        int users = 1;
        if (userList.containsKey(channelName) && userList.get(channelName).size() > 0){
            users = userList.get(channelName).size();
            if (users == 0){
                String[] props = {maxUsers, password};
                serverProps.put(channelName, props);
            }
            else{
                if (users == Integer.parseInt(serverProps.get(channelName)[0])){
                    return ResponseEntity.ok("Room full!");
                }
                else if (password.compareTo(serverProps.get(channelName)[1]) != 0){
                    return ResponseEntity.ok("Wrong password!");
                }
                if (userList.containsKey(channelName)){
                    ArrayList<String> temp = userList.get(channelName);
                    temp.add(userName);
                    userList.put(channelName, temp);
                }
                else{
                    ArrayList<String> temp = new ArrayList<String>();

                    temp.add(userName);
                    userList.put(channelName, temp);
                }
            }

        }
        else{ ;
            String[] props = {maxUsers, password};
            serverProps.put(channelName, props);
            if (userList.containsKey(channelName)){
                ArrayList<String> temp = userList.get(channelName);
                temp.add(userName);
                userList.put(channelName, temp);
            }
            else{
                ArrayList<String> temp = new ArrayList<String>();
                temp.add(userName);
                userList.put(channelName, temp);
            }
        }
        totalUsers++;
        return ResponseEntity.ok("Current users in the server: " + users);
    }

    @PostMapping("/leaveChannel")
    public ResponseEntity<String> leaveChannel (@RequestBody String requestBody){
        JSONObject obj = new JSONObject(requestBody);
        String channelName = obj.getString("channelName");
        String userName = obj.getString("userName");
        int users = userList.get(channelName).size() - 1;
        if (userList.get(channelName).size() - 1 == 0){
            messages.put(channelName, new String[2]);
            serverProps.put(channelName, new String[2]);
            userList.put(channelName, new ArrayList<String>());
        }
        else{
            ArrayList<String> temp = userList.get(channelName);
            temp.remove(userName);
            userList.put(channelName, temp);
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
        if (message == null){
            return ResponseEntity.ok("No message");
        }
        return ResponseEntity.ok(message[0] + "-" + message[1]);
    }

    @GetMapping("/getServerProps/{channelName:.+}")
    public ResponseEntity<String> getServerProps (@PathVariable String channelName){
        try{


        StringBuilder s = new StringBuilder();
        ArrayList<String> userNames = userList.get(channelName);
        for (int i = 0; i < userNames.size(); i++){
            s.append(userNames.get(i) + "/");
        }
        return ResponseEntity.ok(userList.get(channelName).size() + "-" + s.toString() + "-" + serverProps.get(channelName)[0]);
        }catch(Exception e){
            return ResponseEntity.ok(e.toString());
        }
    }

    @GetMapping("/getUserCount")
    public ResponseEntity<String> getUserCount (){
       // return ResponseEntity.ok("" + totalUsers);
        return ResponseEntity.ok("" + Math.abs(new Random().nextInt()));
    }

    @PostMapping("/feedback")
    public ResponseEntity<String> getFeedback (@RequestBody String requestBody){
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter("feedback.txt", true);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            JSONObject obj = new JSONObject(requestBody);
            String name = obj.getString("name");
            pw.println(name);
            pw.println();
            String feedback = obj.getString("feedback");
            pw.println(feedback);
            pw.println();
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                pw.close();
                bw.close();
                fw.close();
            } catch (IOException io) { } }



        return ResponseEntity.ok("Success");
    }

}
