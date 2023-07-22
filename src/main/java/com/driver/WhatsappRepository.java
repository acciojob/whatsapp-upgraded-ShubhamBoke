package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class WhatsappRepository {

    private HashMap<String,User> userDB = new HashMap<>();

    private HashMap<Group,List<User>> groupUserListDB = new HashMap<>();

    private HashMap<Group, User> adminDB = new HashMap<>();

    private int customGroupCount=1;
    private int messageId=1;

    private HashMap<Group, List<Message>> groupMessageDB=new HashMap<>();

    private HashMap<Message, User> senderDB=new HashMap<>();


    public boolean createUser(String name, String mobile) {
        if (userDB.containsKey(mobile)) return false;
        userDB.put(mobile,new User(name,mobile));
        return true;
    }

    public Group createGroup(List<User> users) {
        if (users.size()==2){
            Group group = new Group(users.get(1).getName(),2);
            this.groupUserListDB.put(group,users);
            adminDB.put(group,users.get(0));
            return group;
        }

        String name="Group "+this.customGroupCount;
        Group group = new Group(name,users.size());
        this.groupUserListDB.put(group,users);

        adminDB.put(group,users.get(0));
        this.customGroupCount++;
        return group;
    }

    public int createMessage(String content) {
        Message message=new Message(messageId,content);
        messageId++;
        return message.getId();
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        if (!groupUserListDB.containsKey(group)) throw new Exception("Group does not exist");
        if (!groupUserListDB.get(group).contains(sender)) throw new Exception("You are not allowed to send message");
        List<Message> msg = groupMessageDB.getOrDefault(group,new ArrayList<>());
        msg.add(message);
        groupMessageDB.put(group,msg);
        senderDB.put(message,sender);
        return msg.size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if (!groupUserListDB.containsKey(group)) throw new Exception("Group does not exist");
        if (!adminDB.get(group).getName().equals(approver.getName())) throw new Exception("Approver does not have rights");
        if (!groupUserListDB.get(group).contains(user)) throw new Exception("User is not a participant");
        adminDB.put(group,user);
        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception{

        for (Group group:groupUserListDB.keySet()) {
            if (groupUserListDB.get(group).contains(user)){
                if (adminDB.get(group).equals(user)) throw new Exception("Cannot remove admin");

                groupUserListDB.get(group).remove(user);
                List<Message> messages= new ArrayList<>();
                for (Message message:groupMessageDB.get(group)) {
                    if(senderDB.get(message).equals(user)){
                        senderDB.remove(message);
                    }else messages.add(message);
                }
                groupMessageDB.put(group,messages);
                return groupUserListDB.get(group).size()+messages.size()+senderDB.size();
            }
        }
        throw new Exception("User not found");
    }

    public String findMessage(Date start, Date end, int k) throws Exception {
        List<Message> messages=new ArrayList<>();
        for (Message msg:senderDB.keySet()) {
            if (start.before(msg.getTimestamp()) && end.after(msg.getTimestamp())) messages.add(msg);
        }

        if (k>messages.size()) throw new Exception("K is greater than the number of messages");

        Collections.sort(messages,(a, b)->{
            if(a.getTimestamp().before(b.getTimestamp())) return -1;
            return 1;
        });

        return messages.get(messages.size()-k).toString();
    }
}
