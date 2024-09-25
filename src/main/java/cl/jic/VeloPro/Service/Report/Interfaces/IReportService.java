package cl.jic.VeloPro.Service.Report.Interfaces;

import cl.jic.VeloPro.Model.DTO.*;

import java.time.LocalDate;
import java.util.List;

public interface IReportService {

    List<DailySaleCountDTO> getDailySale(LocalDate start, LocalDate end);
    List<DailySaleSumDTO> getTotalSaleDaily(LocalDate start, LocalDate end);
    List<DailySaleAvgDTO> getAverageTotalSaleDaily(LocalDate start, LocalDate end);
    List<DailySaleEarningDTO> getEarningSale(LocalDate start, LocalDate end);
    List<ProductSaleDTO> getMostProductSale(LocalDate start, LocalDate end);
    List<CategoriesSaleDTO> getMostCategorySale(LocalDate start, LocalDate end);
}
