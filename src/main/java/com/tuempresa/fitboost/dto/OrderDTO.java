package com.tuempresa.fitboost.dto;

import java.time.LocalDateTime;

public class OrderDTO {
    private Long id;
    private String clientName;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private String status;

    public OrderDTO() {}

    public OrderDTO(Long id, String clientName, LocalDateTime orderDate, Double totalAmount, String status) {
        this.id = id;
        this.clientName = clientName;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
