package client.mvc;

import client.mvc.observer.Listener;
import client.mvc.observer.Observer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.awt.Desktop;
import java.io.File;

public class Model implements Observer {
    private List<Listener> listeners;
    private List<AppStatInfo> appStatInfo;
    private String ip = "";
    private int port = 0;
    private String login = "";
    private String password = "";
    private Socket client = null;
    private String user = null;
    PrintWriter out = null;
    BufferedReader in = null;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");

    Model() {
        this.listeners = new LinkedList<>();
        this.appStatInfo = new ArrayList<>();
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
        if (!appStatInfo.isEmpty()) {
            for (AppStatInfo arg : appStatInfo) {
                arg.setAppEndTime(Instant.now());
                out.println("addStat" + "&" + arg.getAppName() + "; " + arg.calcElapsedTimeInMillis() + "; " + getUser().split("; ")[7] + "; " + dtf.format(LocalDate.now()));
            }
        }
        user = null;
        out.println("Disconnect");
    }

    public String authorization() throws IOException, InterruptedException {
        out.println("Authorization" + "&" + login + "&" + password);

        String line = in.readLine();
        if (Objects.equals(line, "Пользователь не найден!") || Objects.equals(line, "Пароль неверный!")) {
            return line;
        } else {
            user = line;
            AppStatusCheck appStatusCheck = new AppStatusCheck();
            if (Objects.equals(user, null)) {
                return "Перезапустите сервер!";
            }
            String[] args = line.split("; ");
            switch (args[6]) {
                case "13" -> notifyListeners("switchToSceneAdmin");
                case "14" -> notifyListeners("switchToSceneReportsAnalyst");
                default -> { notifyListeners("switchToSceneEmployee"); }
            }
            return "";
        }
    }

    public boolean isUser() {
        return user != null;
    }

    public String getUser() {
        return user;
    }

    public void logout() {
        for (AppStatInfo arg : appStatInfo) {
            arg.setAppEndTime(Instant.now());
            out.println("addStat" + "&" + arg.getAppName() + "; " + arg.calcElapsedTimeInMillis() + "; " + getUser().split("; ")[7] + "; " + dtf.format(LocalDate.now()));
        }
        user = null;
        appStatInfo.clear();
        out.println("Logout");
    }

    class AppStatusCheck implements Runnable {
        Thread thread;
        public AppStatusCheck()
        {
            this.thread = new Thread(this, "appStat Thread");
            this.thread.start();
        }
        @Override
        public void run() {
            while (isUser()) {
                try {
                    Thread.sleep(10000);
                    Process process = new ProcessBuilder("powershell","\"gps| ? {$_.mainwindowtitle.length -ne 0} | Format-Table -HideTableHeaders  name").start();
                    new Thread(() -> {
                        Scanner sc = new Scanner(process.getInputStream());
                        if (sc.hasNextLine()) sc.nextLine();
                        List<String> runAppList = new ArrayList<>();
                        while (sc.hasNextLine()) {
                            String line = sc.nextLine();
                            runAppList.add(line);
                        }
                        boolean flagNew = false;
                        boolean flagOld = false;
                        for (AppStatInfo arg : appStatInfo) {
                            for (String obj : runAppList) {
                                if(Objects.equals(arg.getAppName(), obj)) {
                                    flagOld = true;
                                    break;
                                }
                            }
                            if (!flagOld) {
                                arg.setAppEndTime(Instant.now());
                            } else {
                                flagOld = false;
                            }
                        }
                        for (String obj : runAppList) {
                            for (AppStatInfo arg : appStatInfo) {
                                if(Objects.equals(obj, arg.getAppName())) {
                                    flagNew = true;
                                    break;
                                }
                            }
                            if (!flagNew) {
                                AppStatInfo newAppStatInfo = new AppStatInfo(obj, Instant.now());
                                appStatInfo.add(newAppStatInfo);
                            } else {
                                flagNew = false;
                            }
                        }
                    }).start();
                    process.waitFor();
                }
                catch (InterruptedException e) {
                    System.out.println("Caught:" + e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
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
            System.out.println(Arrays.toString(responseArgs));
            String[] dateArgs = responseArgs[0].split("; ");
            System.out.println(Arrays.toString(dateArgs));
            try {
                File myObj = new File("reports\\" + userArgs[2] + "_" + dateArgs[4] + ".txt");
                myObj.createNewFile();
                FileWriter myWriter = new FileWriter("reports\\" + userArgs[2] + "_" + dateArgs[4] + ".txt");
                myWriter.write("Отчёт\n\n");
                String[] writeArgs;
                for (String arg : responseArgs) {
                    writeArgs = arg.split("; ");
                    myWriter.write("Название приложения: " + writeArgs[1]
                            + "; Время работы: " + Duration.ofMillis(Long.parseLong(writeArgs[2])).toSeconds()
                            + "секунд; ID компьютера: " + writeArgs[3] + ";\n\n");
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

    public String[] getAllUserStat() throws IOException {
        out.println("getAllUserStat");
        return in.readLine().split("&");
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
