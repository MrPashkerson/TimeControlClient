package client.mvc;

import javafx.application.Platform;
import javafx.event.ActionEvent;
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
    public Label staticAuthError;
    String isConnected = null;

    public void onLoginButtonClick(ActionEvent event) throws IOException {
        if(isConnected == null) {
            fieldPort.setDisable(true);
            isConnected = model.connectToServer();
        }
        if (Objects.equals(isConnected, "")){
            fieldPort.setDisable(true);
            fieldLogin.setDisable(true);
            fieldPassword.setDisable(true);
            new Thread(new AuthHandler()).start();
        } else {
            fieldPort.setDisable(false);
            staticAuthError.setText(isConnected);
        }
    }

    class AuthHandler implements Runnable {
        @Override
        public void run() {
            model.setIp(fieldIP.getText());
            int port = 6568;
            if (isPort(fieldPort.getText())) {
                port = Integer.parseInt(fieldPort.getText());
            } else {
                fieldPort.setText(Integer.toString(port));
            }
            model.setPort(port);

            String login = fieldLogin.getText();
            String password = fieldPassword.getText();
            staticAuthError.setTextFill(Color.color(1,0,0));
            if(login.length() <= 6 || login.length() >= 12) {
                Platform.runLater(() -> staticAuthError.setText("Неверный логин!"));
            } else {
                model.setLogin(login);
                if(password.length() <= 6 || password.length() >= 12) {
                    Platform.runLater(() -> staticAuthError.setText("Неверная длина пароля!"));
                } else {
                    model.setPassword(password);
                    staticAuthError.setText("");
                    try {
                        model.authorization();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            fieldPort.setDisable(false);
            fieldLogin.setDisable(false);
            fieldPassword.setDisable(false);
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