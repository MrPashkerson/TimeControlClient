package client.mvc;

import client.mvc.observer.Listener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class View implements Listener {
    SingletonModel singletonModel = SingletonModel.getInstance();


    @FXML
    ListView<String> elListUserSearch = new ListView<>();
    public Label staticAuthError;

    public TextField fieldUsernameSearch;
    public TextField fieldUsernameForm;
    public TextField fieldLastnameForm;
    public TextField fieldFirstnameForm;
    public TextField fieldPasswordForm;
    public ChoiceBox choiceBoxDepartment = new ChoiceBox();
    public ChoiceBox choiceBoxPosition = new ChoiceBox();
    public ChoiceBox choiceBoxComp = new ChoiceBox();

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

    public void onLogoutButtonClick() {
        singletonModel.model.logout();
        switchToScene(btnLogout, "clientAuthorization");
    }

    public void onReturnButtonClick() {
        switchToScene(btnReturn, "clientAdmin");
    }

    public void onUserButtonClick() {
        switchToScene(btnUser, "clientAdminManageUsers");
    }

    public void onReportButtonClick() {
        switchToScene(btnReport, "clientReportsAdmin");
    }

    public void onEditUserButtonClick() {
        singletonModel.setSearchType("edit");
        openScene(btnEditUser, "clientUserSearch");
    }

    public void onAddUserButtonClick() {
        singletonModel.setFormType("add");
        openScene(btnAddUser, "clientUserForm");
    }

    public void onDeleteUserButtonClick() {
        singletonModel.setSearchType("delete");
        openScene(btnDeleteUser, "clientUserSearch");
    }

    public void onSeeReportButtonClick() throws IOException {
        singletonModel.model.seeReport();
    }

    public void onGenerateReportButtonClick() {
        singletonModel.setSearchType("generate");
        openScene(btnGenerateReport, "clientUserSearch");
    }

    public void onCancelButtonClick() {
        singletonModel.setEditUser("");
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void onOkSearchButtonClick() throws IOException {
        String selectedItem = elListUserSearch.getSelectionModel().getSelectedItem();
        if (!Objects.equals(selectedItem, "")) {
            switch (singletonModel.getSearchType()) {
                case "delete" -> {
                    singletonModel.model.deleteUser(selectedItem);
                    openScene(btnSearch, "clientInfoDelete");
                    Stage stage = (Stage) btnOkSearch.getScene().getWindow();
                    stage.close();
                }
                case "generate" -> {
                    if (!Objects.equals(singletonModel.model.generateReport(selectedItem), "")) {
                        openScene(btnOkSearch, "clientInfoReport");
                    }
                    Stage stage = (Stage) btnOkSearch.getScene().getWindow();
                    stage.close();
                }
                case "edit" -> {
                    singletonModel.setFormType("edit");
                    singletonModel.setEditUser(selectedItem);
                    openScene(btnOkSearch, "clientUserForm");
                    Stage stage = (Stage) btnOkSearch.getScene().getWindow();
                    stage.close();
                }
                default -> {}
            }
        }
    }

    public void onOkFormButtonClick() {
        String username = fieldUsernameForm.getText();
        String lastname = fieldLastnameForm.getText();
        String firstname = fieldFirstnameForm.getText();
        String password = fieldPasswordForm.getText();
        String department = (String) choiceBoxDepartment.getValue();
        department = department.split(" ")[0];
        String position = (String) choiceBoxPosition.getValue();
        position = position.split(" ")[0];
        String equipment = (String) choiceBoxComp.getValue();
        equipment = equipment.split(" ")[0];
        if (Objects.equals(username, "") || Objects.equals(lastname, "")
                || Objects.equals(firstname, "") || Objects.equals(password, "")
                || Objects.equals(department, "") || Objects.equals(position, "")
                || Objects.equals(equipment, "")) {
            staticAuthError.setText("Заполните все поля!");
            staticAuthError.setTextFill(Color.color(1,0,0));
        } else {
            if (Objects.equals(singletonModel.getEditUser(), "")) {
                String newUser = username + "; " + lastname
                        + "; " + firstname + "; " + password
                        + "; " + department + "; " + position
                        + "; " + equipment;
                singletonModel.model.addUser(newUser);
                openScene(btnOkForm, "clientInfoAdd");
                elListUserSearch.getItems().clear();
                Stage stage = (Stage) btnOkForm.getScene().getWindow();
                stage.close();
            } else {
                String editUserId = singletonModel.getEditUser().split("; ")[0];
                String editUser = editUserId + "; " + username + "; "
                        + lastname + "; " + firstname + "; "
                        + password + "; " + department
                        + "; " + position + "; " + equipment;
                singletonModel.model.editUser(editUser);
                openScene(btnOkForm, "clientInfoEdit");
                elListUserSearch.getItems().clear();
                Stage stage = (Stage) btnOkForm.getScene().getWindow();
                stage.close();
                singletonModel.setEditUser("");
            }
        }
    }

    public void onSearchButtonClick() throws IOException {
         String usernameSearch = fieldUsernameSearch.getText();
         if (!Objects.equals(usernameSearch, "")) {
             String line = singletonModel.model.searchUser(usernameSearch);
             elListUserSearch.getItems().clear();
             elListUserSearch.getItems().add(line);
         } else {
             elListUserSearch.getItems().clear();
             String[] args = singletonModel.model.getAllEmployee();
             for (String arg : args) {
                 elListUserSearch.getItems().add(arg);
             }
         }
    }

    public void onOkInfoButtonClick() {
        Stage stage = (Stage) btnOkInfo.getScene().getWindow();
        stage.close();
    }

    View() {
        singletonModel.model.registerListener(this);
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
            Stage stage;
            Scene scene;
            try {
                stage = (Stage) button.getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.setOnCloseRequest(windowEvent -> {
                    singletonModel.model.disconnectServer();
                    stage.close();
                });
                stage.show();
            } catch (Exception ignore) {}
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
            try {
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(button.getScene().getWindow());
                stage.setOnCloseRequest(windowEvent -> stage.close());
                stage.showAndWait();
            } catch (Exception ignore) {}
        });
    }
}
