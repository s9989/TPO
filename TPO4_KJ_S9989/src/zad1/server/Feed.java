package zad1.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class Feed
{
    private HashMap<String, ArrayList<String>> topicsList = new HashMap<>();

    Feed(HashSet<String> topics)
    {
//        for(String topic : topics) {
//            topicsList.put(topic, new ArrayList<>());
//        }
    }

    void putMessage(String topic, String message)
    {
        if (!topicsList.containsKey(topic)) {
            return; // not subscribed
        }

        ArrayList<String> news = topicsList.get(topic);
        news.add(message);
    }

    HashSet<String> getMessages()
    {
        HashSet<String> messages = new HashSet<>();

        for (HashMap.Entry<String, ArrayList<String>> entry : topicsList.entrySet())
        {
            String topic = entry.getKey();
            ArrayList<String> news = entry.getValue();

            for (String singleNews : news)
            {
                messages.add(String.join("¦", topic, singleNews));
            }

            topicsList.put(topic, new ArrayList<>());
        }

        return messages;
    }

    public void subscribe(String topic)
    {
        if (topicsList.containsKey(topic)) {
            return;
        }

        topicsList.put(topic, new ArrayList<>());
    }

    public void unsubscribe(String topic)
    {
        if (!topicsList.containsKey(topic)) {
            return;
        }

        topicsList.remove(topic);
    }
}
