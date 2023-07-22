package com.driver;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WhatsappService {

    WhatsappRepository whatsappRepository = new WhatsappRepository();
    public String createUser(String name, String mobile) throws Exception{
        if(whatsappRepository.createUser(name, mobile))
            return "SUCCESS";
        throw new Exception("User already exists");
    }

    public Group createGroup(List<User> users) {
        if(users.size() > 2){
            User admin = users.get(0);
            int grpId = whatsappRepository.getGroupCount() + 1;
            whatsappRepository.setGroupCount(grpId);
            String name = "Group " + grpId;
            Group group = new Group(name, users.size());
            whatsappRepository.createGroup(admin, group, users);
            return group;
        } else{
            String name = users.get(1).getName();
            User admin = users.get(0);
            Group group = new Group(name, 2);
            whatsappRepository.createGroup(admin, group, users);
            return group;
        }
    }

    public Message createMessage(String content) {
        int id = whatsappRepository.getMessageId();
        whatsappRepository.setMessageId(id+1);
        return new Message(id, content);
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        List<User> list = whatsappRepository.getUsersByGroup(group);
        if(list != null){
            if(list.contains(group)){
                whatsappRepository.sendMessage(message, sender, group);
                return whatsappRepository.getGroupMessageCount(group);
            }
            throw new Exception("You are not allowed to send message");
        }
        throw new Exception("Group does not exist");
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        List<User> list = whatsappRepository.getUsersByGroup(group);
        if(list == null) throw new Exception("Group does not exist");
        User admin = whatsappRepository.getAdmin(group);
        if(admin != approver) throw new Exception("Approver does not have rights");
        if(!list.contains(user)) throw new Exception("User is not a participant");

        whatsappRepository.changeAdmin(user, group);
        return "SUCCESS";
    }

    public int removeUser(User user) {
        return 0;
    }

    public String findMessage(Date start, Date end, int k) {
        return "SUCCESS";
    }
}
