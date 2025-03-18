package com.tribmle.service;


import com.tribmle.entity.Car;
import com.tribmle.entity.LeaseHistory;
import com.tribmle.entity.User;

import java.util.List;
import java.util.Optional;

public interface CarService {

    public User registerUser(User user);
    public Car addCar(Car car);
    public List<Car> getAllCars();
    public Optional<Car> getCarById(Long id);
    public Car leaseCar(Long carId, Long customerId);
    public Car endLease(Long carId);
    //    public List<LeaseHistory> getLeaseHistory(Long userId);
    public List<LeaseHistory> getLeaseHistory(Long userId, Long requesterId);

}
