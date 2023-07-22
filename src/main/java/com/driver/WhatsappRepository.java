package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private HashMap<String, User> users;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.users = new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public boolean createUser(String name, String mobile) {
        if(userMobile.contains(mobile))
            return false;
        userMobile.add(mobile);
        users.put(mobile, new User(name, mobile));
        return true;
    }



    public int getGroupCount(){
        return customGroupCount;
    }
    public void setGroupCount(int count){
        this.customGroupCount = count;
    }
    public int getMessageId(){
        return messageId;
    }
    public void setMessageId(int id){
        this.messageId = id;
    }

    public void createGroup(User admin, Group group, List<User> userList) {
        this.groupUserMap.put(group, userList);
        this.adminMap.put(group, admin);
    }

    public List<User> getUsersByGroup(Group group) {
        return groupUserMap.getOrDefault(group, null);
    }

    public void sendMessage(Message message, User sender, Group group) {
        List<Message> msgList = this.groupMessageMap.getOrDefault(group, new ArrayList<>());
        msgList.add(message);
        this.senderMap.put(message, sender);
    }

    public int getGroupMessageCount(Group group) {
        return this.groupMessageMap.get(group).size();
    }

    public User getAdmin(Group group) {
        return this.adminMap.get(group);
    }

    public void changeAdmin(User user, Group group) {
        this.adminMap.remove(group);
        this.adminMap.put(group, user);
    }
}
