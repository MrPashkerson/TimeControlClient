package client.mvc;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class Controller extends View {
    public TextField fieldIP;
    public TextField fieldPort;
    public TextField fieldLogin;
    public TextField fieldPassword;
    public Label staticUsername;

    public void onLoginButtonClick() throws IOException {
        if(!Objects.equals(singletonModel.getIsConnected(), "")) {
            fieldPort.setDisable(true);
            singletonModel.model.setIp(fieldIP.getText());
            int port = 6568;
            if (isPort(fieldPort.getText())) {
                port = Integer.parseInt(fieldPort.getText());
            } else {
                fieldPort.setText(Integer.toString(port));
            }
            singletonModel.model.setPort(port);

            singletonModel.setIsConnected(singletonModel.model.connectToServer());
            if(!Objects.equals(singletonModel.getIsConnected(), "")) {
                fieldPort.setDisable(false);
                staticAuthError.setText(singletonModel.getIsConnected());
                return;
            }
            staticAuthError.setText("");
        }
        fieldLogin.setDisable(true);
        fieldPassword.setDisable(true);
        new Thread(new AuthHandler()).start();
    }

    class AuthHandler implements Runnable {
        @Override
        public void run() {
            String login = fieldLogin.getText();
            String password = fieldPassword.getText();
            staticAuthError.setTextFill(Color.color(1,0,0));
            if(login.length() <= 3 || login.length() >= 16) {
                Platform.runLater(() -> staticAuthError.setText("Неверный логин!"));
            } else {
                singletonModel.model.setLogin(login);
                if(password.length() <= 3 || password.length() >= 16) {
                    Platform.runLater(() -> staticAuthError.setText("Неверная длина пароля!"));
                } else {
                    singletonModel.model.setPassword(password);
                    Platform.runLater(() -> {
                        try {
                            staticAuthError.setText(singletonModel.model.authorization());
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
            fieldLogin.setDisable(false);
            fieldPassword.setDisable(false);
            try {
                staticUsername.setText(login);
            } catch (Exception ignored) {}
        }
    }

    public boolean isPort(String strNum) {
        if (strNum == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("\\d\\d\\d\\d");
        return pattern.matcher(strNum).matches();
    }
}