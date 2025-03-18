package com.tribmle.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "lease_history")
public class LeaseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    private LocalDateTime leaseStart;
    private LocalDateTime leaseEnd;

    public LeaseHistory() {
    }

    public LeaseHistory(Car car, User customer, LocalDateTime leaseStart, LocalDateTime leaseEnd) {
        this.car = car;
        this.customer = customer;
        this.leaseStart = leaseStart;
        this.leaseEnd = leaseEnd;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public LocalDateTime getLeaseStart() {
        return leaseStart;
    }

    public void setLeaseStart(LocalDateTime leaseStart) {
        this.leaseStart = leaseStart;
    }

    public LocalDateTime getLeaseEnd() {
        return leaseEnd;
    }

    public void setLeaseEnd(LocalDateTime leaseEnd) {
        this.leaseEnd = leaseEnd;
    }


}
