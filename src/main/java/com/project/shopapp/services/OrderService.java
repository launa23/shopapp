package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderStatus;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Override
    public Order createOrder(OrderDTO orderDTO, long userId) throws Exception {
        // Tìm xem userId có tồn tạ hay ko
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find user id: " + userId));
        // Convert orderDTO -> order
        // Dùng thư viện để chuyển cho nhanh dùng model mapper
        // Ánh xạ từ OrderDTO sang Order và bỏ qua trường id, vì trong orderDTO không có trường id
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderNumber("RT"+generateRandomString(10));
        //Kiểm tra shipping date phải >= ngày hôm nay
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())){
            throw new DataNotFoundException("Date must be at least today");
        }
        if (orderDTO.getPaymentMethod() == 0){
            order.setActive(true);
        }
        else order.setActive(false);
        order.setShippingDate(shippingDate);
        orderRepository.save(order);

        return order;
    }

    @Override
    public Order getOrderById(long id) throws Exception {
        return orderRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order by id " + id));
    }

    @Override
    public List<Order> getAllOrdersByUser(long userId) throws Exception {
//        boolean existingUser = userRepository.existsById(userId);
        if (!userRepository.existsById(userId)){
            throw new DataNotFoundException("Cannot find user id " + userId);
        }
        return orderRepository.findByUserIdAndActive(userId, true);

    }

    @Override
    public Order updateOrder(long id, OrderDTO orderDTO) throws DataNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order by id " + id));

        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
//        Order order = new Order();
        modelMapper.map(orderDTO, order);

        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(long id) throws DataNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order by id " + id));
        order.setActive(false);
        orderRepository.save(order);
    }

    @Override
    public void updateStatusOrder(long id, String status) throws Exception {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order by id " + id));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public void updateActionOrder(long id) throws DataNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order by id " + id));
        order.setActive(true);
        orderRepository.save(order);
    }

    private static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        // Tạo chuỗi số ngẫu nhiên
        for (int i = 0; i < length; i++) {
            int randomNumber = random.nextInt(10); // Giới hạn số ngẫu nhiên từ 0 đến 9
            sb.append(randomNumber);
        }

        return sb.toString();
    }


}
