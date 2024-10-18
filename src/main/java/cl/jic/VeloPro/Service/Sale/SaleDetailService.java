package cl.jic.VeloPro.Service.Sale;

import cl.jic.VeloPro.Model.DTO.DetailSaleDTO;
import cl.jic.VeloPro.Model.Entity.Product.Product;
import cl.jic.VeloPro.Model.Entity.Sale.Sale;
import cl.jic.VeloPro.Model.Entity.Sale.SaleDetail;
import cl.jic.VeloPro.Repository.Sale.SaleDetailRepo;
import cl.jic.VeloPro.Service.Sale.Interfaces.ISaleDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SaleDetailService implements ISaleDetailService {

    @Autowired private SaleDetailRepo saleDetailRepo;

    @Override
    public void createSaleDetails(DetailSaleDTO dto, Sale sale, Product product) {
        SaleDetail saleDetail = new SaleDetail();
        saleDetail.setTotal(dto.getTotal());
        saleDetail.setTax(dto.getTax());
        saleDetail.setPrice(dto.getSalePrice());
        saleDetail.setQuantity(dto.getQuantity());
        saleDetail.setProduct(product);
        saleDetail.setSale(sale);
        saleDetailRepo.save(saleDetail);
    }

    @Override
    public List<SaleDetail> getAll() {
        return saleDetailRepo.findAll();
    }

    @Override
    public DetailSaleDTO createDTO(Product product) {
        if (product != null) {
            DetailSaleDTO dto = new DetailSaleDTO();
            dto.setId(product.getId());
            dto.setDescription(product.getDescription());
            dto.setCategory(String.valueOf(product.getCategory()));
            dto.setUnit(String.valueOf(product.getUnit()));
            dto.setStock(product.getStock());
            dto.setSalePrice((int) (product.getSalePrice() * 1.19));
            dto.setTax((int) (product.getSalePrice() * 0.19));
            dto.setQuantity(1);
            dto.setTotal((int) (product.getSalePrice() * 1.19));
            return dto;
        }
        return null;
    }

    @Override
    public int deleteProduct(List<DetailSaleDTO> dtoList, Long id, int total) {
        Optional<DetailSaleDTO> optionalDto = dtoList.stream()
                .filter(dto -> Objects.equals(dto.getId(), id))
                .findFirst();

        if (optionalDto.isPresent()) {
            DetailSaleDTO dto = optionalDto.get();
            int price = dto.getTotal();
            total -= price;
            dtoList.remove(dto);
        }

        return Math.max(total, 0);
    }
}
