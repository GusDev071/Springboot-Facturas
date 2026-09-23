package com.gustavo.curso.springboot.di.factura.springboot_difactura.models;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component 
public class Invoice {

    private Client client;

    @Value ("${invoice.description}")
    private String description;

    @Autowired 
    @Qualifier("default")
    private List<Item> items;

    Invoice(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<Item> getItems() {
        return items;
    }
    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getTotal(){
        /* int total = 0;
        for (Item item : items){
            total += item.getImporte();
        }  *///forma con for

        return items.stream()
        .map(item -> item.getImporte())
        .reduce(0, (sum, item) -> sum + item);
    }

    
}
