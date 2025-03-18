package com.tribmle.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String model;

    @Enumerated(EnumType.STRING)
    private CarStatus status = CarStatus.IDLE; // Default status

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties("cars")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "leased_by_id", nullable = true)
    private User leasedBy;

    public Car() {
    }

    public Car(Long id, String model, CarStatus status, User owner, User leasedBy) {
        this.id = id;
        this.model = model;
        this.status = status;
        this.owner = owner;
        this.leasedBy = leasedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getLeasedBy() {
        return leasedBy;
    }

    public void setLeasedBy(User leasedBy) {
        this.leasedBy = leasedBy;
    }

    public void setAvailable(boolean b) {
    }
}
