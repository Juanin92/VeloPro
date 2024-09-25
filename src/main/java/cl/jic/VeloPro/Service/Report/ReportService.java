package cl.jic.VeloPro.Service.Report;

import cl.jic.VeloPro.Model.DTO.*;
import cl.jic.VeloPro.Repository.Sale.SaleRepo;
import cl.jic.VeloPro.Service.Report.Interfaces.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService implements IReportService {

    @Autowired private SaleRepo saleRepo;

    @Override
    public List<DailySaleCountDTO> getDailySale(LocalDate start, LocalDate end) {
        try{
            if (end.isAfter(start)){
                throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
            }else {
                Date startDate = Date.valueOf(start);
                Date endDate = Date.valueOf(end);
                List<Object[]> results = saleRepo.findSalesByDateRange(endDate, startDate);
                List<DailySaleCountDTO> dtoList = new ArrayList<>();
                for (Object[] result : results) {
                    Date date = (Date) result[0];
                    Long count = (Long) result[1];
                    dtoList.add(new DailySaleCountDTO(date.toLocalDate(), count));
                }
                return dtoList;
            }
        }catch (Exception e){
            throw new IllegalArgumentException("Debe ingresar fechas válidas en los campos");
        }
    }

    @Override
    public List<DailySaleSumDTO> getTotalSaleDaily(LocalDate start, LocalDate end) {
        try{
            Date startDate = Date.valueOf(start);
            Date endDate = Date.valueOf(end);
            List<Object[]> results = saleRepo.findTotalSalesByDateRange(endDate, startDate);
            List<DailySaleSumDTO> dtoList = new ArrayList<>();
            for (Object[] result : results) {
                Date date = (Date) result[0];
                BigDecimal sum = (BigDecimal) result[1];
                dtoList.add(new DailySaleSumDTO(date.toLocalDate(), sum));
            }
            return dtoList;
        }catch (Exception e){
            throw new IllegalArgumentException("Debe ingresar fechas válidas en los campos");
        }
    }

    @Override
    public List<DailySaleAvgDTO> getAverageTotalSaleDaily(LocalDate start, LocalDate end) {
        try{
            Date startDate = Date.valueOf(start);
            Date endDate = Date.valueOf(end);
            List<Object[]> results = saleRepo.findAverageSalesPerDay(endDate, startDate);
            List<DailySaleAvgDTO> dtoList = new ArrayList<>();
            for (Object[] result : results) {
                Date date = (Date) result[0];
                BigDecimal avg = (BigDecimal) result[1];
                dtoList.add(new DailySaleAvgDTO(date.toLocalDate(), avg));
            }
            return dtoList;
        }catch (Exception e){
            throw new IllegalArgumentException("Debe ingresar fechas válidas en los campos");
        }
    }

    @Override
    public List<DailySaleEarningDTO> getEarningSale(LocalDate start, LocalDate end) {
        try{
            Date startDate = Date.valueOf(start);
            Date endDate = Date.valueOf(end);
            List<Object[]> results = saleRepo.findEarningPerDay(endDate, startDate);
            List<DailySaleEarningDTO> dtoList = new ArrayList<>();
            for (Object[] result : results) {
                Date date = (Date) result[0];
                BigDecimal profit = (BigDecimal) result[1];
                dtoList.add(new DailySaleEarningDTO(date.toLocalDate(), profit));
            }
            return dtoList;
        }catch (Exception e){
            throw new IllegalArgumentException("Debe ingresar fechas válidas en los campos");
        }
    }

    @Override
    public List<ProductSaleDTO> getMostProductSale(LocalDate start, LocalDate end) {
        try{
            Date startDate = Date.valueOf(start);
            Date endDate = Date.valueOf(end);
            List<Object[]> results = saleRepo.findMostProductSale(endDate, startDate);
            List<ProductSaleDTO> dtoList = new ArrayList<>();
            for (Object[] result : results) {
                Long id = (Long) result[0];
                String brand = (String) result[1];
                String description = (String) result[2];
                BigDecimal total = (BigDecimal) result[3];
                dtoList.add(new ProductSaleDTO(id, brand, description, total));
            }
            return dtoList;
        }catch (Exception e){
            throw new IllegalArgumentException("Debe ingresar fechas válidas en los campos");
        }
    }

    @Override
    public List<CategoriesSaleDTO> getMostCategorySale(LocalDate start, LocalDate end) {
        try{
            Date startDate = Date.valueOf(start);
            Date endDate = Date.valueOf(end);
            List<Object[]> results = saleRepo.findMostCategorySale(endDate, startDate);
            List<CategoriesSaleDTO> dtoList = new ArrayList<>();
            for (Object[] result : results) {
                Long id = (Long) result[0];
                String name = (String) result[1];
                BigDecimal total = (BigDecimal) result[2];
                dtoList.add(new CategoriesSaleDTO(id, name, total));
            }
            return dtoList;
        }catch (Exception e){
            throw new IllegalArgumentException("Debe ingresar fechas válidas en los campos");
        }
    }
}
