package client.mvc.observer;

public interface Observer {
    void registerListener(Listener o);

    void notifyListeners(String message);
}

