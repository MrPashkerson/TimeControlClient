package client.mvc;

import client.mvc.observer.Listener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class View implements Listener {
    Model model = new Model();

    String formType = "";
    String searchType = "";

    // Листы
    @FXML
    ListView<String> elListUserSearch = new ListView<>();

    public Label staticStatus;
    public TextField fieldUsernameSearch;

    // Кнопки
    public Button btnLogin;
    public Button btnReport;
    public Button btnLogout;
    public Button btnUser;
    public Button btnEditUser;
    public Button btnAddUser;
    public Button btnReturn;
    public Button btnDeleteUser;
    public Button btnOkInfo;
    public Button btnSeeReport;
    public Button btnGenerateReport;
    public Button btnOkSearch;
    public Button btnOkForm;
    public Button btnCancel;
    public Button btnSearch;

    public void onLogoutButtonClick(ActionEvent event) throws IOException {
        model.logout();
        switchToScene(btnLogout, "clientAuthorization");
    }

    public void onReturnButtonClick(ActionEvent event) {
        switchToScene(btnReturn, "clientAdmin");
    }

    public void onUserButtonClick(ActionEvent event) {
        switchToScene(btnUser, "clientAdminManageUsers");
    }

    public void onReportButtonClick(ActionEvent event) {
        switchToScene(btnReport, "clientReportsAdmin");
    }

    public void onEditUserButtonClick(ActionEvent event) {
        formType = "edit";
        openScene(btnEditUser, "clientUserForm");
    }

    public void onAddUserButtonClick(ActionEvent event) {
        formType = "add";
        openScene(btnAddUser, "clientUserForm");
    }

    public void onDeleteUserButtonClick(ActionEvent event) {
        searchType = "delete";
        openScene(btnDeleteUser, "clientUserSearch");
    }

    public void onSeeReportButtonClick(ActionEvent event) {
        searchType = "see";
        openScene(btnDeleteUser, "clientUserSearch");
    }

    public void onGenerateReportButtonClick(ActionEvent event) {
        searchType = "generate";
        openScene(btnDeleteUser, "clientUserSearch");
    }

    public void onCancelButtonClick(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void onOkSearchButtonClick(ActionEvent event) {
        String selectedItem = this.elListUserSearch.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            switch (searchType) {
                case "delete" -> {
                    model.deleteUser(selectedItem);
                    openScene(btnSearch, "clientInfo");
                    staticStatus.setText("Пользователь удалён!");
                    Stage stage = (Stage) btnOkSearch.getScene().getWindow();
                    stage.close();
                }
                case "see" -> {
                    model.seeReport(selectedItem);
                    Stage stage = (Stage) btnOkSearch.getScene().getWindow();
                    stage.close();
                }
                case "generate" -> {
                    model.generateReport(selectedItem);
                    Stage stage = (Stage) btnOkSearch.getScene().getWindow();
                    stage.close();
                }
                default -> {}
            }
        }
    }

    public void onOkFormButtonClick(ActionEvent event) {
        String user = null;

        // Добавить когда будет БД

        if (user != null) {
            switch (formType) {
                case "add" -> {
                    model.addUser(user);
                    openScene(btnSearch, "clientInfo");
                    staticStatus.setText("Пользователь добавлен!");
                    elListUserSearch.getItems().clear();
                    Stage stage = (Stage) btnOkForm.getScene().getWindow();
                    stage.close();
                }
                case "edit" -> {
                    model.editUser(user);
                    openScene(btnSearch, "clientInfo");
                    staticStatus.setText("Пользователь изменён!");
                    elListUserSearch.getItems().clear();
                    Stage stage = (Stage) btnOkForm.getScene().getWindow();
                    stage.close();
                }
                default -> {
                }
            }
        }
    }

    public void onSearchButtonClick(ActionEvent event) throws IOException {
         String usernameSearch = fieldUsernameSearch.getText();
         if (usernameSearch != null) {
             String line = model.searchUser(usernameSearch);

             elListUserSearch.getItems().clear();
             String[] args = line.split("&");
             for (String arg : args) {
                 elListUserSearch.getItems().add(arg);
             }
         }
    }

    public void onOkInfoButtonClick(ActionEvent event) {
        Stage stage = (Stage) btnOkInfo.getScene().getWindow();
        stage.close();
    }

    View() {
        model.registerListener(this);
    }

    @Override
    public void notification(String message) {
        switch (message) {
            case "switchToSceneAdmin" -> switchToScene(btnLogin, "clientAdmin");
            case "switchToSceneEmployee" -> switchToScene(btnLogin, "clientEmployee");
            case "switchToSceneReportsAnalyst" -> switchToScene(btnLogin, "clientReportsAnalyst");
            default -> {}
        }
    }

    private void switchToScene(Button button, String source) {
        Platform.runLater(() -> {
            Parent root;
            try {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/client/" + source + ".fxml")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage stage = (Stage) button.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        });
    }

    private void openScene(Button button, String source) {
        Platform.runLater(() -> {
            Parent root;
            Stage stage = new Stage();
            try {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/client/" + source + ".fxml")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(button.getScene().getWindow());
            stage.setOnCloseRequest(windowEvent -> stage.close());
            stage.showAndWait();
        });
    }
}
