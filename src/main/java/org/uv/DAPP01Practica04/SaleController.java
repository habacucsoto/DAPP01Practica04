/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/RestController.java to edit this template
 */
package org.uv.DAPP01Practica04;

import java.util.ArrayList;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/sales")
public class SaleController {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ClientRepository clientRepository;

    private SaleService saleService = new SaleService();

    @GetMapping()
    public List<SaleDto> list() {
        List<Sale> sales = saleRepository.findAll();
        List<SaleDto> salesDto = new ArrayList<>();

        for (Sale sale : sales) {
            SaleDto saleDto = new SaleDto();
            saleDto.setId(sale.getId());
            saleDto.setDate(sale.getDate());
            saleDto.setAmount(sale.getAmount());
            if (sale.getClient() != null) {
                saleDto.setClientId(sale.getClient().getId());
            }
            saleDto.setSaleDetails(sale.getSaleDetails());

            salesDto.add(saleDto);
        }

        return salesDto;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleDto> get(@PathVariable Long id) {
        Optional<Sale> saleResponse = saleRepository.findById(id);

        if (saleResponse.isPresent()) {
            Sale foundSale = saleResponse.get();

            SaleDto saleDto = new SaleDto();
            saleDto.setId(foundSale.getId());
            saleDto.setDate(foundSale.getDate());
            saleDto.setAmount(foundSale.getAmount());
            if (foundSale.getClient() != null) {
                saleDto.setClientId(foundSale.getClient().getId());
            }
            saleDto.setSaleDetails(foundSale.getSaleDetails());

            return ResponseEntity.ok(saleDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<SaleDto> post(@RequestBody SaleDto saleDto
    ) {
        Optional<Client> optionalClient = clientRepository.findById(saleDto.getClientId());

        if (optionalClient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Client client = optionalClient.get();

        Sale sale = new Sale();
        sale.setDate(saleDto.getDate());
        sale.setClient(client);
        sale.setAmount(saleDto.getAmount());
        sale.setSaleDetails(saleDto.getSaleDetails());

        for (SaleDetail saleDetail : sale.getSaleDetails()) {
            Product product = new Product();
            product.setName(saleDetail.getProduct().getName());
            product.setPrice(saleDetail.getProduct().getPrice());

            saleDetail.setProduct(product);
            saleDetail.setSale(sale);
        }

        Sale savedSale = saleRepository.save(sale);

        saleDto.setId(savedSale.getId());;
        return ResponseEntity.ok(saleDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleDto> update(@PathVariable Long id, @RequestBody SaleDto saleDto) {
        Optional<Sale> optionalSale = saleRepository.findById(id);
        if (optionalSale.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Client> optionalClient = clientRepository.findById(saleDto.getClientId());
        if (optionalClient.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Sale existingSale = optionalSale.get();
        Client client = optionalClient.get();

        existingSale.setDate(saleDto.getDate());
        existingSale.setAmount(saleDto.getAmount());
        existingSale.setClient(client);

        existingSale.getSaleDetails().clear();

        for (SaleDetail detailDto : saleDto.getSaleDetails()) {
            SaleDetail saleDetail = new SaleDetail();
            saleDetail.setSale(existingSale);
            saleDetail.setQuantity(detailDto.getQuantity());
            saleDetail.setPrice(detailDto.getPrice());
            saleDetail.setDescription(detailDto.getDescription());

            Product product = new Product();
            product.setName(detailDto.getProduct().getName());
            product.setPrice(detailDto.getProduct().getPrice());
            saleDetail.setProduct(product);

            existingSale.getSaleDetails().add(saleDetail);
        }

        Sale updatedSale = saleRepository.save(existingSale);

        SaleDto responseDto = new SaleDto();
        responseDto.setId(updatedSale.getId());
        responseDto.setDate(updatedSale.getDate());
        responseDto.setAmount(updatedSale.getAmount());
        responseDto.setClientId(updatedSale.getClient().getId());
        responseDto.setSaleDetails(updatedSale.getSaleDetails());

        return ResponseEntity.ok(responseDto);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<Sale> optionalSale = saleRepository.findById(id);

        if (optionalSale.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        saleRepository.delete(optionalSale.get());

        return ResponseEntity.noContent().build();
    }
}
