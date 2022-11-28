package client.mvc;

import javafx.fxml.FXML;

import java.io.IOException;
import java.util.Objects;

public class UserFormController extends View {
    @FXML
    public void initialize() throws IOException {
        // CheckBox init
        String[] department = singletonModel.model.getAllDepartment();
        String[] argsDepartment;
        for (String arg : department) {
            argsDepartment = arg.split("; ");
            choiceBoxDepartment.getItems().add(argsDepartment[0] + " " + argsDepartment[1]);
        }

        String[] position = singletonModel.model.getAllPosition();
        String[] argsPosition;
        for (String arg : position) {
            argsPosition = arg.split("; ");
            choiceBoxPosition.getItems().add(argsPosition[0] + " " + argsPosition[1] + " " + argsPosition[2]);
        }

        String[] equipment = singletonModel.model.getAllEquipment();
        String[] argsEquipment;
        for (String arg : equipment) {
            argsEquipment = arg.split("; ");
            choiceBoxComp.getItems().add(argsEquipment[0] + " " + argsEquipment[1]);
        }

        // When editing user
        String user = singletonModel.getEditUser();
        if(!Objects.equals(user, "")) {
            String[] args = user.split("; ");
            fieldUsernameForm.setText(args[1]);
            fieldLastnameForm.setText(args[2]);
            fieldFirstnameForm.setText(args[3]);
            fieldPasswordForm.setText(args[4]);
            for (String obj : department) {
                argsDepartment = obj.split("; ");
                if (Objects.equals(args[5], argsDepartment[0])) {
                    choiceBoxDepartment.setValue(argsDepartment[0] + " " + argsDepartment[1]);
                }
            }
            for (String obj : position) {
                argsPosition = obj.split("; ");
                if (Objects.equals(args[6], argsPosition[0])) {
                    choiceBoxPosition.setValue(argsPosition[0] + " " + argsPosition[1] + " " + argsPosition[2]);
                }
            }
            equipment = singletonModel.model.getOccupiedEquipment();
            for (String obj : equipment) {
                argsEquipment = obj.split("; ");
                if (Objects.equals(args[7], argsEquipment[0])) {
                    choiceBoxComp.setValue(argsEquipment[0] + " " + argsEquipment[1]);
                }
            }
        }
    }
}
