package com.tuempresa.fitboost.repository;

import com.tuempresa.fitboost.model.Order;
import com.tuempresa.fitboost.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByOrderDateDesc(User user);
}
