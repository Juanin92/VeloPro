package cl.jic.VeloPro.Service.Record;

import cl.jic.VeloPro.Model.Entity.Record;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecordService implements IRecordService{
    @Autowired private RecordRepository recordRepo;

    @Override
    public void registerEntry(User user) {
        Record record = new Record();
        record.setEntryDate(LocalDateTime.now());
        record.setEndaDate(null);
        record.setActionDate(null);
        record.setAction("LOGIN");
        record.setComment(null);
        record.setUser(user);
        recordRepo.save(record);
    }

    @Override
    public void registerEnd(User user) {
        Record record = new Record();
        record.setEntryDate(null);
        record.setEndaDate(LocalDateTime.now());
        record.setActionDate(null);
        record.setAction("LOGOUT");
        record.setComment(null);
        record.setUser(user);
        recordRepo.save(record);
    }

    @Override
    public void registerAction(User user, String action, String comment) {
        Record record = new Record();
        record.setEntryDate(null);
        record.setEndaDate(null);
        record.setActionDate(LocalDateTime.now());
        record.setAction(action);
        record.setComment(comment);
        record.setUser(user);
        recordRepo.save(record);
    }

    @Override
    public List<Record> getAllRecord() {
        return recordRepo.findAll();
    }
}
