package org.yes.cart.service.order.impl.handler;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.*;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * Initial {@link CustomerOrder#ORDER_STATUS_PENDING} state.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PendingOrderEventHandlerImpl extends AbstractOrderEventHandlerImpl implements OrderEventHandler, ApplicationContextAware {

    private final PaymentProcessorFactory paymentProcessorFactory;
    private OrderStateManager orderStateManager = null;
    private ApplicationContext applicationContext;
    private final WarehouseService warehouseService;
    private final SkuWarehouseService skuWarehouseService;

    /**
     * Construct transition handler.
     *
     * @param paymentProcessorFactory to perform authorize operation
     * @param warehouseService        warehouse service
     * @param skuWarehouseService     sku on warehouse service to change quantity
     */
    public PendingOrderEventHandlerImpl(final PaymentProcessorFactory paymentProcessorFactory,
                                        final WarehouseService warehouseService,
                                        final SkuWarehouseService skuWarehouseService) {
        this.paymentProcessorFactory = paymentProcessorFactory;
        this.warehouseService = warehouseService;
        this.skuWarehouseService = skuWarehouseService;
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private synchronized OrderStateManager getOrderStateManager() {
        if (orderStateManager == null) {
            orderStateManager = (OrderStateManager) applicationContext.getBean("orderStateManager");
        }
        return orderStateManager;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) throws Exception {
        synchronized (OrderEventHandler.syncMonitor) {

            for (CustomerOrderDelivery customerOrderDelivery : orderEvent.getCustomerOrder().getDelivery()) {
                reserveQuantity(customerOrderDelivery);
            }
            handleInternal(orderEvent);
            final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(orderEvent.getCustomerOrder().getPgLabel());
            if (paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().isOnlineGateway()) {
                if (Payment.PAYMENT_STATUS_OK.equals(paymentProcessor.authorize(orderEvent.getCustomerOrder(), orderEvent.getParams()))) {
                    //payment was ok, so quantity oh warehouses will be decreased
                    getOrderStateManager().fireTransition(new OrderEventImpl(OrderStateManager.EVT_PAYMENT_OK, orderEvent.getCustomerOrder()));
                } else {
                    //in case of bad payment reserved product quantity will be returned from reservation
                    getOrderStateManager().fireTransition(new OrderEventImpl(OrderStateManager.EVT_CANCEL, orderEvent.getCustomerOrder()));
                }
            } else {
                // wait for confirmation about payment
                getOrderStateManager().fireTransition(new OrderEventImpl(OrderStateManager.EVT_PAYMENT_OFFLINE, orderEvent.getCustomerOrder()));
            }

            return true;
        }
    }

    /**
     * Allocate sku quantity on warehouses, that belong to shop, where order was made.
     *
     * @param orderDelivery reserve for this delivery
     * @throws Exception in case if can not allocate quantity for each sku
     */
    void reserveQuantity(final CustomerOrderDelivery orderDelivery) throws Exception {


        final Collection<CustomerOrderDeliveryDet> deliveryDetails = orderDelivery.getDetail();

        final List<Warehouse> warehouses = warehouseService.findByShopId(
                orderDelivery.getCustomerOrder().getShop().getShopId());


        for (CustomerOrderDeliveryDet det : deliveryDetails) {
            ProductSku productSku = det.getSku();
            BigDecimal toReserve = det.getQty();
            for (Warehouse warehouse : warehouses) {

                toReserve = skuWarehouseService.reservation(warehouse, productSku, toReserve);
                if (BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE).equals(toReserve.setScale(Constants.DEFAULT_SCALE))) {
                    break; // quantity allocated
                }
            }


            if ((MoneyUtils.isFirstBiggerThanSecond(toReserve, BigDecimal.ZERO))
                    &&
                    (Availability.STANDARD == productSku.getProduct().getAvailability().getAvailabilityId())) {

                /**
                 * Availability.BACKORDER -  can get from somewere
                 * Availability.PREORDER - can order from manafacturer
                 * Availability.ALWAYS - always
                 */
                throw new Exception("PendingOrderEventHandlerImpl. Can not allocate total qty = " + det.getQty()   //TODO need patricular type of exception
                        + " for sku = " + productSku.getCode()
                        + " in delivery " + orderDelivery.getDevileryNum());
            }
        }
        orderDelivery.setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

    }

    /**
     * {@inheritDoc}
     */
    protected String getTransitionTarget(final OrderEvent orderEvent) {
        return CustomerOrder.ORDER_STATUS_PENDING;
    }


}