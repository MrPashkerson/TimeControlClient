package client.mvc;

import client.mvc.observer.Listener;
import client.mvc.observer.Observer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import java.awt.Desktop;
import java.io.File;

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

    public void disconnectServer() {
        out.println("Disconnect");
    }

    public String authorization() throws IOException {
        out.println("Authorization" + "&" + login + "&" + password);

        String line = in.readLine();
        if (Objects.equals(line, "Пользователь не найден!") || Objects.equals(line, "Пароль неверный!")) {
            return line;
        } else {
            String[] args = line.split("; ");
            switch (args[5]) {
                case "4" -> notifyListeners("switchToSceneAdmin");
                case "5" -> notifyListeners("switchToSceneReportsAnalyst");
                default -> { notifyListeners("switchToSceneEmployee"); }
            }
            return "";
        }
    }

    public void logout() {
        out.println("Logout");
    }

    public void seeReport() throws IOException {
        openDirectory("");
    }

    public String generateReport(String selectedItem) throws IOException {
        out.println("generateReport" + "&" + selectedItem);
        String response = in.readLine();
        if(!Objects.equals(response, "")) {
            String[] userArgs = selectedItem.split("; ");
            String[] responseArgs = response.split("&");
            String[] dateArgs = responseArgs[0].split("; ");
            try {
                File myObj = new File("reports\\" + userArgs[2] + "_" + dateArgs[4] + ".txt");
                myObj.createNewFile();
                FileWriter myWriter = new FileWriter("reports\\" + userArgs[2] + "_" + dateArgs[4] + ".txt");
                myWriter.write("Отчёт\n\n");
                String[] writeArgs;
                for (String arg : responseArgs) {
                    writeArgs = arg.split("; ");
                    myWriter.write("Название приложения: " + writeArgs[1]
                            + "; Время работы: " + writeArgs[2]
                            + "; ID компьютера: " + writeArgs[3] + ";\n");
                }
                myWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            openDirectory("\\" + userArgs[2] + "_" + dateArgs[4] + ".txt");
            return "";
        }
        return "Записей для создания отчёта не найдено!";
    }

    public void addUser(String user) {
        out.println("addUser" + "&" + user);
    }

    public void editUser(String user) {
        out.println("editUser" + "&" + user);
    }

    public void deleteUser(String selectedItem) {
        selectedItem = selectedItem.split("; ")[0];
        out.println("deleteUser" + "&" + selectedItem);
    }

    public String searchUser(String usernameSearch) throws IOException {
        out.println("searchUser" + "&" + usernameSearch);
        return in.readLine();
    }

    public String[] getAllEmployee() throws IOException {
        out.println("getAllEmployee");
        return in.readLine().split("&");
    }

    public String[] getAllDepartment() throws IOException {
        out.println("getAllDepartment");
        return in.readLine().split("&");
    }

    public String[] getAllPosition() throws IOException {
        out.println("getAllPosition");
        return in.readLine().split("&");
    }

    public String[] getAllEquipment() throws IOException {
        out.println("getAllEquipment");
        return in.readLine().split("&");
    }

    public String[] getOccupiedEquipment() throws IOException {
        out.println("getOccupiedEquipment");
        return in.readLine().split("&");
    }

    private void openDirectory(String path) throws IOException {
        Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "\\reports" + path));
    }
}
