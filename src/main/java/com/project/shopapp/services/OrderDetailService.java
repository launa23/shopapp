package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.OrderDetailReposiroty;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService{
    private final OrderDetailReposiroty orderDetailReposiroty;
    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;
    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception {
        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find order by id: " + orderDetailDTO.getOrderId()));
        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find product by id: " + orderDetailDTO.getProductId()));
        OrderDetail orderDetail = OrderDetail.builder()
                .order(existingOrder)
                .product(existingProduct)
                .numberOfProduct(orderDetailDTO.getNumberOfProduct())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .price(orderDetailDTO.getPrice())
                .color(orderDetailDTO.getColor())
                .build();
        OrderDetail orderDetail1 = orderDetailReposiroty.save(orderDetail);
        return orderDetail1;
    }

    @Override
    public OrderDetail getOrderDetail(long id) throws Exception {
        OrderDetail orderDetail = orderDetailReposiroty.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order detail by id: " + id));
        return orderDetail;
    }

    @Override
    public OrderDetail updateOrderDetail(long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        OrderDetail existingOrderDetail = orderDetailReposiroty.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order detail by id: " + id));
        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find order by id: " + orderDetailDTO.getOrderId()));
        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find product by id: " + orderDetailDTO.getProductId()));
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setPrice(orderDetailDTO.getPrice());
        existingOrderDetail.setColor(orderDetailDTO.getColor());
        existingOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        existingOrderDetail.setNumberOfProduct(orderDetailDTO.getNumberOfProduct());
        existingOrderDetail.setProduct(existingProduct);
        return orderDetailReposiroty.save(existingOrderDetail);
    }

    @Override
    public void deleteOrderDetail(long id) {
        orderDetailReposiroty.deleteById(id);
    }

    @Override
    public List<OrderDetail> getAllOrderDetail(long orderId) throws Exception {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order by id: " + orderId));
        List<OrderDetail> orderDetailList = orderDetailReposiroty.findByOrderId(orderId);
        return orderDetailList;
    }
}
