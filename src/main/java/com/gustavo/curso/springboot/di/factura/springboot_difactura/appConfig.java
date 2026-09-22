package com.gustavo.curso.springboot.di.factura.springboot_difactura;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.gustavo.curso.springboot.di.factura.springboot_difactura.models.Item;
import com.gustavo.curso.springboot.di.factura.springboot_difactura.models.Product;

@Configuration
@PropertySource ("classpath:data.properties")
public class appConfig {

    @Bean 
    List<Item> itemsInvoice(){
        Product p1 = new Product("Camara sony",800);
        Product p2 = new Product("Camara samsung",900);
        Product p3 = new Product("Camara nokia",1000);
        return Arrays.asList(new Item(p1, 2), new Item(p2, 4), new Item(p3,6));
    }

}
