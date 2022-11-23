package client.mvc;

import client.mvc.observer.Listener;
import client.mvc.observer.Observer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Model implements Observer {
    private List<Listener> listeners;
    private String ip = "";
    private int port = 0;
    private String login = "";
    private String password = "";
    private Socket client = null;
    PrintWriter out = null;
    BufferedReader in = null;

    Model() {
        this.listeners = new LinkedList<>();
    }

    // регистрация слушателя
    @Override
    public void registerListener(Listener listener) {
        this.listeners.add(listener);
    }

    // уведомление слушателей
    @Override
    public void notifyListeners(String message) {
        for (Listener listener : listeners) {
            listener.notification(message);
        }
    }

    // сеттеры
    void setIp(String ip) {
        this.ip = ip;
    }

    void setPort(int port) {
        this.port = port;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // вычисления + notifyListeners("message");
    public String connectToServer() throws IOException {
        try {
            client = new Socket(InetAddress.getByName(ip), port);
        } catch (IOException e) {
            return "Неверный ip или порт сервера!";
        }
        out = new PrintWriter(client.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        return "";
    }

    public void authorization() throws IOException {
        out.println("Authorization" + "&&&" + login + "&&&" + password + "\n");

        String line = in.readLine();
        switch (line) {
            case "Admin" -> notifyListeners("switchToSceneAdmin");
            case "Analyst" -> notifyListeners("switchToSceneReportsAnalyst");
            case "Employee" -> notifyListeners("switchToSceneEmployee");
            default -> {}
        }
    }

    public void logout() {
        out.println("Logout" + "\n");
    }

    public void deleteUser(String selectedItem) {
        out.println("deleteUser" + "&" + selectedItem + "\n");
    }

    public void seeReport(String selectedItem) {
        out.println("seeReport" + "&" + selectedItem + "\n");
    }

    public void generateReport(String selectedItem) {
        out.println("generateReport" + "&" + selectedItem + "\n");
    }

    public void addUser(String user) {
        out.println("addUser" + "&" + user + "\n");
    }

    public void editUser(String user) {
        out.println("editUser" + "&" + user + "\n");
    }

    public String searchUser(String usernameSearch) throws IOException {
        out.println("searchUser" + "&" + usernameSearch + "\n");

        return in.readLine();
    }
}
