package com.chibao.orderservice.application.service;

import com.chibao.orderservice.application.mapper.OrderMapper;
import com.chibao.orderservice.application.ports.inbound.OrderManagementUseCase;
import com.chibao.orderservice.application.ports.inbound.command.CreateOrderCommand;
import com.chibao.orderservice.application.ports.inbound.result.OrderResult;
import com.chibao.orderservice.application.ports.outbound.KitchenClient;
import com.chibao.orderservice.application.ports.outbound.OrderRepository;
import com.chibao.orderservice.application.ports.outbound.PaymentClient;
import com.chibao.orderservice.domain.model.Order;
import com.chibao.orderservice.domain.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderManagementService implements OrderManagementUseCase {
    private final OrderRepository repository;
    private final KitchenClient kitchenClient;
    private final PaymentClient paymentClient;


    @Override
    @Transactional
    public OrderResult createOrder(CreateOrderCommand command) {
        List<OrderItem> items = OrderMapper.toDomainItems(command);
        Order order = new Order(UUID.randomUUID().toString(), command.getConsumerId(), command.getRestaurantId(), command.getTotalAmount(), items);
        Order savedOrder = repository.save(order);

        try {
            // 1. Gọi Kitchen - Nếu mạch OPEN hoặc Kitchen sập, adapter tự trả về false nhờ Fallback
            boolean ticketCreated = kitchenClient.createTicket(savedOrder.getId(), savedOrder.getRestaurantId());
            if (!ticketCreated) {
                return rejectAndSaveOrder(savedOrder);
            }

            // 2. Gọi Payment
            boolean paymentAuthorized = paymentClient.authorizePayment(savedOrder.getConsumerId(), savedOrder.getTotalAmount());
            if (!paymentAuthorized) {
                // Nếu payment lỗi -? hủy ticket bên Kitchen.
                safelyRejectKitchenTicket(savedOrder.getId());
                return rejectAndSaveOrder(savedOrder);
            }

            // 3. Quy trình hoàn thành thành công hoàn toàn
            savedOrder.approve();
            repository.save(savedOrder);
            safelyConfirmKitchenTicket(savedOrder.getId());

        } catch (Exception ex) {
            System.err.println("Fatal error during order creation lifecycle: " + ex.getMessage());
            safelyRejectKitchenTicket(savedOrder.getId());
            return rejectAndSaveOrder(savedOrder);
        }

        return OrderMapper.toResult(savedOrder);
    }

    private OrderResult rejectAndSaveOrder(Order order) {
        order.reject();
        Order updatedOrder = repository.save(order);
        return OrderMapper.toResult(updatedOrder);
    }

    private void safelyRejectKitchenTicket(String orderId) {
        try {
            kitchenClient.rejectTicket(orderId);
        } catch (Exception e) {
            System.err.println("[SAGA COMPENSATION FAILED] Không thể gửi lệnh hủy vé sang Kitchen cho đơn hàng: "
                    + orderId + ". Cần cơ chế Retry tự động hoặc can thiệp thủ công! Lỗi: " + e.getMessage());
        }
    }

    private void safelyConfirmKitchenTicket(String orderId) {
        try {
            kitchenClient.confirmTicket(orderId);
        } catch (Exception e) {
            System.err.println("[CRITICAL] Không thể gửi lệnh xác nhận vé sang Kitchen cho đơn hàng: "
                    + orderId + ". Lỗi: " + e.getMessage());
        }
    }

    @Override
    public OrderResult getOrder(String orderId) {
        Order order = repository.findById(orderId);
        return OrderMapper.toResult(order);
    }
}

