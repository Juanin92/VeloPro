package cl.jic.VeloPro.Controller.User;

import cl.jic.VeloPro.Model.Entity.LocalData;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Service.Record.IRecordService;
import cl.jic.VeloPro.Service.User.ILocalDataService;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Utility.NotificationManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class LocalDataController implements Initializable {

    @FXML private Button btnUpdate;
    @FXML private CustomTextField txtAccessApp, txtAddress, txtEmail, txtName, txtPhone;

    @Autowired private ILocalDataService localDataService;
    @Autowired private NotificationManager notificationManager;
    @Autowired private IRecordService recordService;
    @Autowired private Session session;
    private List<LocalData> list;
    private User user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user = session.getCurrentUser();
        loadData();
    }

    @FXML
    public void manegeData(){
        try{
            if (list.isEmpty()){
                localDataService.saveData(txtName.getText(), txtPhone.getText(), txtEmail.getText(),
                        txtAccessApp.getText(), txtAddress.getText());
                notificationManager.successNotification("Acción Exitosa", "Se ha agregados los datos correctamente al sistema", Pos.CENTER);
            }else {
                LocalData localData = list.getFirst();
                localData.setName(txtName.getText());
                localData.setPhone(txtPhone.getText());
                localData.setEmail(txtEmail.getText());
                localData.setEmailSecurityApp(txtAccessApp.getText());
                localData.setAddress(txtAddress.getText());
                localDataService.updateData(localData);
                notificationManager.successNotification("Acción Exitosa", "Se han actualizado los datos correctamente al sistema", Pos.CENTER);
            }
            recordService.registerAction(user, "CHANGE", "Cambio info importante");
        }catch (Exception e){
            notificationManager.errorNotification("Error", "Ha ocurrido un problema. El error es "+ e.getMessage(),Pos.CENTER);
        }
    }

    private void loadData(){
        list = localDataService.getData();
        if (!list.isEmpty()){
            txtName.setText(list.getFirst().getName());
            txtPhone.setText(list.getFirst().getPhone());
            txtEmail.setText(list.getFirst().getEmail());
            txtAccessApp.setText(list.getFirst().getEmailSecurityApp());
            txtAddress.setText(list.getFirst().getAddress());
        }
    }
}
