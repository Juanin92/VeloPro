package cl.jic.VeloPro.Service.Record;

import cl.jic.VeloPro.Model.Entity.Record;
import cl.jic.VeloPro.Model.Entity.User;

import java.util.List;

public interface IRecordService {
    void registerEntry(User user);
    void registerEnd(User user);
    void registerAction(User user, String action, String comment);
    List<Record> getAllRecord();
}
