package vvs_webapp.services;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import webapp.persistence.PersistenceException;
import webapp.persistence.SaleDeliveryRowDataGateway;
import webapp.persistence.SaleRowDataGateway;
import webapp.services.ApplicationException;
import webapp.services.SaleService;
import webapp.services.SalesDTO;
import webapp.services.SaleDTO;

@RunWith(MockitoJUnitRunner.class)
public class SaleServiceTest {

    @Mock
    private SaleRowDataGateway saleRowDataGateway;

    @Mock
    private SaleDeliveryRowDataGateway saleDeliveryRowDataGateway;

    @InjectMocks
    private SaleService saleService;

    @Before
    public void setUp() throws Exception {
        saleService = new SaleService(saleRowDataGateway, saleDeliveryRowDataGateway);
    }

    @Test
    public void testGetSaleByCustomerVat() throws Exception {
        int customerVat = 123456789;
        List<SaleRowDataGateway> sales = new ArrayList<>();
        SaleRowDataGateway sale = mock(SaleRowDataGateway.class);
        when(sale.getId()).thenReturn(1);
        when(sale.getData()).thenReturn(new java.sql.Date(new Date().getTime())); // Corrected to use java.sql.Date
        when(sale.getTotal()).thenReturn(100.0);
        when(sale.getStatusId()).thenReturn("O");
        when(sale.getCustomerVat()).thenReturn(customerVat);
        sales.add(sale);

        when(saleRowDataGateway.getAllSales(customerVat)).thenReturn(sales);

        SalesDTO salesDTO = saleService.getSaleByCustomerVat(customerVat);

        assertEquals(1, salesDTO.sales.size());
        verify(saleRowDataGateway).getAllSales(customerVat);
    }

    @Test
    public void testGetAllSales() throws Exception {
        List<SaleRowDataGateway> sales = new ArrayList<>();
        SaleRowDataGateway sale = mock(SaleRowDataGateway.class);
        when(sale.getId()).thenReturn(1);
        when(sale.getData()).thenReturn(new java.sql.Date(new Date().getTime()));
        when(sale.getTotal()).thenReturn(100.0);
        when(sale.getStatusId()).thenReturn("O");
        when(sale.getCustomerVat()).thenReturn(123456789);
        sales.add(sale);

        when(saleRowDataGateway.getAllSales()).thenReturn(sales);

        SalesDTO salesDTO = saleService.getAllSales();

        assertEquals(1, salesDTO.sales.size());
        verify(saleRowDataGateway).getAllSales();
    }

}
