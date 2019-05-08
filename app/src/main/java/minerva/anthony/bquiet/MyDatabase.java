package minerva.anthony.bquiet;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Database;
import androidx.room.Room;
import minerva.anthony.bquiet.ContactsDatabase;
import minerva.anthony.bquiet.Conversation;
import minerva.anthony.bquiet.ConversationsDatabase;
import minerva.anthony.bquiet.Message;
import minerva.anthony.bquiet.MessagesDatabase;
import minerva.anthony.bquiet.User;

public class MyDatabase {
    private ContactsDatabase contactsDatabase;
    private ConversationsDatabase conversationsDatabase;
    private MessagesDatabase messagesDatabase;
    private List<User> contactTemp = new ArrayList<>();
    private List<Conversation> convoTemp = new ArrayList<>();
    private List<Message> messageTemp = new ArrayList<>();

    public MyDatabase(Context c){
        contactsDatabase =  Room.databaseBuilder(c,
                ContactsDatabase.class, "contacts_db")
                .fallbackToDestructiveMigration()
                .build();
        conversationsDatabase =  Room.databaseBuilder(c,
                ConversationsDatabase.class, "conversations_db")
                .fallbackToDestructiveMigration()
                .build();
        messagesDatabase =  Room.databaseBuilder(c,
                MessagesDatabase.class, "messages_db")
                .fallbackToDestructiveMigration()
                .build();
    }

    void addContact(final User u){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                contactsDatabase.userDao().insertUser(u);
            }
        });
        try{
            t.start();
            t.join();
        } catch(Exception e){
            Log.e("MyDatabase", "Error Joining " + e);
        }
    }

    void addConversation(final Conversation c) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                conversationsDatabase.conversationDao().insertConversation(c);
            }
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            Log.e("MyDatabase", "Error Joining " + e);
        }
    }

    void addMessage(final Message m){
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    messagesDatabase.messageDao().insertMessage(m);
                }
            });
            try{
                t.start();
                t.join();
            } catch(Exception e){
                Log.e("MyDatabase", "Error Joining " + e);
            }
    }

    List<User> getContacts(){
        contactTemp = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                contactTemp = contactsDatabase.userDao().getAllUsers();
            }
        });
        try{
            t.start();
            t.join();
        } catch(Exception e){
            Log.e("MyDatabase", "Error Joining " + e);
        }
        return contactTemp;
    }

    Conversation getConversation(final String CID){
        convoTemp = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                convoTemp.add(conversationsDatabase.conversationDao().getConversation(CID));
            }
        });
        try{
            t.start();
            t.join();
            return convoTemp.get(0);
        } catch(Exception e) {
            Log.e("MyDatabase", "Error Joining " + e);
            return null;
        }
    }

    User getUser(final String UID){
        contactTemp = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                contactTemp.add(contactsDatabase.userDao().getUser(UID));
            }
        });
        try{
            t.start();
            t.join();
            return contactTemp.get(0);
        } catch(Exception e) {
            Log.e("MyDatabase", "Error Joining " + e);
            return null;
        }
    }

    List<Conversation> getConversations(){
        convoTemp = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                convoTemp = conversationsDatabase.conversationDao().getAllConversations();
            }
        });
        try{
            t.start();
            t.join();
        } catch(Exception e){
            Log.e("MyDatabase", "Error Joining " + e);
        }
        return convoTemp;
    }

    List<Message> getMessages(final String CID){
        messageTemp = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                messageTemp = messagesDatabase.messageDao().getMessages(CID);
            }
        });
        try{
            t.start();
            t.join();
        } catch(Exception e){
            Log.e("MyDatabase", "Error Joining " + e);
        }
        return messageTemp;
    }

    void cleanUsers(final String CODE){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                contactsDatabase.userDao().cleanUsers(CODE);
            }
        });
        try{
            t.start();
            t.join();
        } catch(Exception e){
            Log.e("MyDatabase", "Error Joining " + e);
        }
    }

    void deleteConversation(final Conversation c){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                conversationsDatabase.conversationDao().deleteConversation(c);
            }
        });
        try{
            t.start();
            t.join();
        } catch(Exception e){
            Log.e("MyDatabase", "Error Joining " + e);
        }
    }

    void expireMessages(String CID, long currentTime, long expirationTime){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                messagesDatabase.messageDao().expireMessages(CID, currentTime, expirationTime);
            }
        });
        try{
            t.start();
            t.join();
        } catch(Exception e){
            Log.e("MyDatabase", "Error Joining " + e);
        }
    }

    void deleteMessages(final String CID){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                messagesDatabase.messageDao().deleteMessages(CID);
            }
        });
        try{
            t.start();
            t.join();
        } catch(Exception e){
            Log.e("MyDatabase", "Error Joining " + e);
        }
    }

    void deleteMessage(final Message m){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                messagesDatabase.messageDao().deleteMessage(m);
            }
        });
        try{
            t.start();
            t.join();
        } catch(Exception e){
            Log.e("MyDatabase", "Error Joining " + e);
        }
    }
}
