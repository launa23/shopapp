package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.responses.OrderResponse;

import java.util.List;

public interface IOrderService {
    Order createOrder(OrderDTO orderDTO, long userId) throws Exception;
    Order getOrderById(long id) throws Exception;
    List<Order> getAllOrdersByUser(long userId) throws Exception;
    List<OrderResponse> getAllOrders() throws Exception;
    Order updateOrder(long id, OrderDTO orderDTO) throws DataNotFoundException;
    void deleteOrder(long id) throws DataNotFoundException;
    void updateStatusOrder(long id, String status) throws Exception;
    void updateActionOrder(long id) throws DataNotFoundException;
}
