package cl.jic.VeloPro.Service.User;

import cl.jic.VeloPro.Model.Entity.LocalData;

import java.util.List;

public interface ILocalDataService {

    void saveData(String name, String phone, String email, String access, String address);
    void updateData(LocalData localData);
    List<LocalData> getData();
}
