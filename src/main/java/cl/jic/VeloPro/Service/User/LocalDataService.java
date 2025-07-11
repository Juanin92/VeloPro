package cl.jic.VeloPro.Service.User;

import cl.jic.VeloPro.Model.Entity.LocalData;
import cl.jic.VeloPro.Repository.LocalDataRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalDataService implements ILocalDataService{

    @Autowired private LocalDataRepo localDataRepo;

    @Override
    public void saveData(String name, String phone, String email, String access, String address) {
        LocalData localData =  new LocalData();
        localData.setName(name);
        localData.setPhone(phone);
        localData.setEmail(email);
        localData.setEmailSecurityApp(access);
        localData.setAddress(address);
        localDataRepo.save(localData);
    }

    @Override
    public void updateData(LocalData localData) {
        localDataRepo.save(localData);
    }

    @Override
    public List<LocalData> getData() {
        return localDataRepo.findAll();
    }
}
