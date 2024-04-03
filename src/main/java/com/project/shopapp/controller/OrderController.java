package com.project.shopapp.controller;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.responses.OrderResponse;
import com.project.shopapp.services.OrderService;
import com.project.shopapp.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    @PostMapping("")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDTO orderDTO,
                                         HttpServletRequest request,
                                         BindingResult result){
        try {
            if (result.hasErrors()) {
                List<String> errorMess = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMess);
            }
            Long userId = userService.getCurrent(request).getId();
            Order order = orderService.createOrder(orderDTO, userId);
            OrderResponse orderResponse = OrderResponse.builder()
                    .id(order.getId())
                    .fullName(order.getFullName())
                    .email(order.getEmail())
                    .address(order.getAddress())
                    .phoneNumber(order.getPhoneNumber())
                    .note(order.getNote())
                    .totalMoney(order.getTotalMoney())
                    .orderDate(order.getOrderDate())
                    .status(order.getStatus())
                    .shippingAdress(order.getShippingAdress())
                    .shippingDate(order.getShippingDate())
                    .shippingMethod(order.getShippingMethod())
                    .trackingNumber(order.getTrackingNumber())
                    .paymentMethod(order.getPaymentMethod())
                    .build();
            return ResponseEntity.ok(orderResponse);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getOrdersByUser(@PathVariable("user_id") int userId){
        try {
            List<OrderResponse> orders = orderService.getAllOrdersByUser(userId).stream()
                    .map(order -> OrderResponse.builder()
                            .id(order.getId())
                            .fullName(order.getFullName())
                            .email(order.getEmail())
                            .address(order.getAddress())
                            .phoneNumber(order.getPhoneNumber())
                            .note(order.getNote())
                            .totalMoney(order.getTotalMoney())
                            .orderDate(order.getOrderDate())
                            .status(order.getStatus())
                            .shippingAdress(order.getShippingAdress())
                            .shippingDate(order.getShippingDate())
                            .shippingMethod(order.getShippingMethod())
                            .trackingNumber(order.getTrackingNumber())
                            .paymentMethod(order.getPaymentMethod())
                            .build()).toList();
            return ResponseEntity.ok(orders);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable("id") int id){
        try {
            Order order = orderService.getOrderById(id);
            OrderResponse orderResponse = OrderResponse.builder()
                    .id(order.getId())
                    .fullName(order.getFullName())
                    .email(order.getEmail())
                    .address(order.getAddress())
                    .phoneNumber(order.getPhoneNumber())
                    .note(order.getNote())
                    .totalMoney(order.getTotalMoney())
                    .orderDate(order.getOrderDate())
                    .status(order.getStatus())
                    .shippingAdress(order.getShippingAdress())
                    .shippingDate(order.getShippingDate())
                    .shippingMethod(order.getShippingMethod())
                    .trackingNumber(order.getTrackingNumber())
                    .paymentMethod(order.getPaymentMethod())
                    .build();
            return ResponseEntity.ok(orderResponse);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @Valid @PathVariable long id,
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult result
    ){
        try {
            if (result.hasErrors()) {
                List<String> errorMess = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMess);
            }
            Order order = orderService.updateOrder(id, orderDTO);
            OrderResponse orderResponse = OrderResponse.builder()
                    .id(order.getId())
                    .fullName(order.getFullName())
                    .email(order.getEmail())
                    .address(order.getAddress())
                    .phoneNumber(order.getPhoneNumber())
                    .note(order.getNote())
                    .totalMoney(order.getTotalMoney())
                    .orderDate(order.getOrderDate())
                    .status(order.getStatus())
                    .shippingAdress(order.getShippingAdress())
                    .shippingDate(order.getShippingDate())
                    .shippingMethod(order.getShippingMethod())
                    .trackingNumber(order.getTrackingNumber())
                    .paymentMethod(order.getPaymentMethod())
                    .build();
            return ResponseEntity.ok(orderResponse);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(
            @Valid @PathVariable long id
    ){
//        Xóa mềm
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok("Xóa đơn hàng: "+id);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
