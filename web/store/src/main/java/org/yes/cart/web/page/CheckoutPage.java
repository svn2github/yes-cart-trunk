package org.yes.cart.web.page;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.shoppingcart.AmountCalculationResult;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.OrderInfo;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.SetMultipleDeliveryCommandImpl;
import org.yes.cart.shoppingcart.impl.SetPaymentGatewayLabelCommandImpl;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.cart.ShoppingCartPaymentVerificationView;
import org.yes.cart.web.page.component.customer.address.ManageAddressesView;
import org.yes.cart.web.page.component.customer.auth.LoginPanel;
import org.yes.cart.web.page.component.customer.auth.RegisterPanel;
import org.yes.cart.web.page.component.shipping.ShippingView;
import org.yes.cart.web.page.component.util.PaymentGatewayDescriptorModel;
import org.yes.cart.web.page.component.util.PaymentGatewayDescriptorRenderer;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Checkout page has following main steps:
 * <p/>
 * 1. big shopping cart with coupons, taxes, items manipulations.
 * 2. quick registration, can be skipped if customer is registered.
 * 3. billing and shipping addresses
 * 4. payment page with payment method selection
 * 5. successful/unsuccessful callback page
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/8/11
 * Time: 8:06 PM
 */

public class CheckoutPage extends AbstractWebPage {

    private static final long serialVersionUID = 20101107L;

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    public static final String NAVIGATION_THREE_FRAGMENT = "threeStepNavigationFragment";
    public static final String NAVIGATION_FOUR_FRAGMENT = "fourStepNavigationFragment";
    public static final String LOGIN_FRAGMENT = "loginFragment";

    public static final String ADDRESS_FRAGMENT = "addressFragment";
    public static final String SHIPPING_ADDRESS_VIEW = "shippingAddress";
    public static final String BILLING_ADDRESS_VIEW = "billingAddress";
    public static final String BILLING_THE_SAME_FORM = "billingTheSameForm";
    public static final String BILLING_THE_SAME = "billingTheSame";


    public static final String SHIPMENT_FRAGMENT = "shipmentFragment";
    public static final String SHIPMENT_VIEW = "shipmentView";

    private static final String PAYMENT_FRAGMENT = "paymentFragment";
    private static final String PAYMENT_FRAGMENT_OPTIONS_FORM = "paymentOptionsForm";
    private static final String PAYMENT_FRAGMENT_MD_CHECKBOX = "multipleDelivery";
    private static final String PAYMENT_FRAGMENT_MD_LABEL = "multipleDeliveryLabel";
    private static final String PAYMENT_FRAGMENT_GATEWAY_CHECKBOX = "paymentGateway";
    private static final String PAYMENT_FRAGMENT_PAYMENT_FORM = "dynamicPaymentForm";


    public static final String CONTENT_VIEW = "content";
    public static final String NAVIGATION_VIEW = "navigation";

    public static final String PART_REGISTER_VIEW = "registerView";
    public static final String PART_LOGIN_VIEW = "loginView";

    public static final String STEP = "step";

    public static final String STEP_LOGIN = "login";
    public static final String STEP_ADDR = "address";
    public static final String STEP_SHIPMENT = "ship";
    public static final String STEP_PAY = "payment";

    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    // ---------------------------------- PARAMETER NAMES BEGIN ------------------------------ //
    /**
     * Is Billing panel visible.
     */
    public static final String BILLING_ADDR_VISIBLE = "billingPanelVisible";
    //three steps checkout process, because customer already logged in
    // or registered
    public static final String THREE_STEPS_PROCESS = "thp";
    // ---------------------------------- PARAMETER NAMES  END ------------------------------- //


    @SpringBean(name = ServiceSpringKeys.CUSTOMER_SERVICE)
    private CustomerService customerService;

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_ORDER_SERVICE)
    private CustomerOrderService customerOrderService;

    @SpringBean(name = ServiceSpringKeys.PAYMENT_MODULES_MANAGER)
    private PaymentModulesManager paymentModulesManager;

    @SpringBean(name = StorefrontServiceSpringKeys.AMOUNT_CALCULATION_STRATEGY)
    private AmountCalculationStrategy amountCalculationStrategy;


    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public CheckoutPage(final PageParameters params) {
        super(params);
        final boolean threeStepsProcess = params.get(THREE_STEPS_PROCESS).toBoolean(
                ((AuthenticatedWebSession) getSession()).isSignedIn()
        );
        final String currentStep =
                params.get(STEP).toString(threeStepsProcess ? STEP_ADDR : STEP_LOGIN);

        if (STEP_PAY.equals(currentStep)) {
            customerOrderService.createFromCart(
                    ApplicationDirector.getShoppingCart(),
                    true            //todo customer service isOrderCanHasMultipleDeliveries
                    //ApplicationDirector.getShoppingCart().isMultipleDelivery()
            );
        }


        add(
                new FeedbackPanel(FEEDBACK)
        ).add(
                new Fragment(NAVIGATION_VIEW, threeStepsProcess ?
                        NAVIGATION_THREE_FRAGMENT : NAVIGATION_FOUR_FRAGMENT, this)
        ).add(
                getContent(currentStep)
        );


    }


    /**
     * Resolve content by given current step.
     *
     * @param currentStep current step label
     * @return markup container
     */
    private MarkupContainer getContent(final String currentStep) {
        if (STEP_ADDR.equals(currentStep)) {
            return createAddressFragment();
        } else if (STEP_SHIPMENT.equals(currentStep)) {
            return createShippmentFragment();
        } else if (STEP_PAY.equals(currentStep)) {
            return createPaymentFragment();
        } else {
            return createLoginFragment();
        }
    }

    /**
     * The default fragment is login/register page.
     *
     * @return login fragment
     */
    private MarkupContainer createLoginFragment() {
        return new Fragment(CONTENT_VIEW, LOGIN_FRAGMENT, this)
                .add(
                        new LoginPanel(PART_LOGIN_VIEW, true))
                .add(
                        new RegisterPanel(PART_REGISTER_VIEW, true)
                );
    }

    /**
     * Create payment fragment with order verification and payment methods forms.
     * <p/>
     * Shopping cart form. Used to show products in cart , adjust product quantity.
     * <p/>
     * <p/>
     * Complex form with several deliveries the shopping cart form will show following items:
     * <pre>
     *  -----------------------------------
     * name             price   qty    amount
     * sku item 1        2       2      4
     * sku item 2        3       3      6
     * subtotal                         10
     * delivery                         2
     * tax                              3
     * total                            15
     *
     * sku item 3        1       3      3
     * sku item 4        1       5      5
     * subtotal                         8
     * delivery                         2
     * tax                              3
     * total                            13
     *
     * grand total                      28
     *
     * ----------------------------------------
     * payment form
     * ----------------------------------------
     * </pre>
     *
     * @return payment fragment of checkout process.
     */
    private MarkupContainer createPaymentFragment() {

        final MarkupContainer rez = new Fragment(CONTENT_VIEW, PAYMENT_FRAGMENT, this);
        final ShoppingCart shoppingCart = ApplicationDirector.getShoppingCart();
        final OrderInfo orderInfo = shoppingCart.getOrderInfo();
        final boolean showMultipleDelivery = customerOrderService.isOrderCanHasMultipleDeliveries(shoppingCart);

        rez
                .add(
                        new Label(PAYMENT_FRAGMENT_PAYMENT_FORM)
                )
                .addOrReplace(

                        new ShoppingCartPaymentVerificationView("orderVerificationView", shoppingCart.getShoppingContext(), getDeliveries(shoppingCart))

                )
                .add(
                        new Form(PAYMENT_FRAGMENT_OPTIONS_FORM)
                                .add(
                                        new CheckBox(PAYMENT_FRAGMENT_MD_CHECKBOX, new PropertyModel(orderInfo, "multipleDelivery")) {

                                            /** {@inheritDoc} */
                                            protected boolean wantOnSelectionChangedNotifications() {
                                                return true;
                                            }

                                            @Override
                                            public void onSelectionChanged() {
                                                setModelObject(!getModelObject());
                                                new SetMultipleDeliveryCommandImpl(
                                                        null,
                                                        Collections.singletonMap(SetMultipleDeliveryCommandImpl.CMD_KEY, getModelObject().toString()))
                                                        .execute(ApplicationDirector.getShoppingCart());

                                                super.onSelectionChanged();
                                            }

                                        }.setVisible(showMultipleDelivery)

                                )
                                .add(
                                        new Label(PAYMENT_FRAGMENT_MD_LABEL,
                                                getLocalizer().getString(PAYMENT_FRAGMENT_MD_LABEL, this)

                                        ).setVisible(showMultipleDelivery)

                                )
                                .add(
                                        new DropDownChoice<PaymentGatewayDescriptor>(
                                                PAYMENT_FRAGMENT_GATEWAY_CHECKBOX,
                                                new PaymentGatewayDescriptorModel(
                                                        new PropertyModel<String>(orderInfo, "paymentGatewayLabel"),
                                                        paymentModulesManager.getPaymentGatewaysDescriptors(false)
                                                ),
                                                paymentModulesManager.getPaymentGatewaysDescriptors(false)) {

                                            /** {@inheritDoc} */
                                            protected void onSelectionChanged(final PaymentGatewayDescriptor descriptor) {

                                                final PaymentGateway gateway = paymentModulesManager.getPaymentGateway(descriptor.getLabel());
                                                final ShoppingCart cart = ApplicationDirector.getShoppingCart();
                                                final CustomerOrder order = customerOrderService.findByGuid(cart.getGuid());
                                                final AmountCalculationResult amountCalculationResult = amountCalculationStrategy.calculate(
                                                        shoppingCart.getShoppingContext(), getDeliveries(shoppingCart)
                                                );
                                                final BigDecimal grandTotal = amountCalculationResult.getTotalAmount();


                                                //pay pall express checkout support

                                                //gateway.

                                                order.setPgLabel(descriptor.getLabel());
                                                customerOrderService.update(order);



                                                final String htmlForm = getPaymentForm(gateway, cart, order, grandTotal);

                                                rez.addOrReplace(
                                                        new Label(PAYMENT_FRAGMENT_PAYMENT_FORM, htmlForm)
                                                                .setEscapeModelStrings(false)
                                                );

                                                new SetPaymentGatewayLabelCommandImpl(
                                                        null,
                                                        Collections.singletonMap(SetPaymentGatewayLabelCommandImpl.CMD_KEY, descriptor.getLabel()))
                                                        .execute(ApplicationDirector.getShoppingCart());

                                            }


                                            /** {@inheritDoc} */
                                            protected boolean wantOnSelectionChangedNotifications() {
                                                return true;
                                            }

                                        }.setChoiceRenderer(new PaymentGatewayDescriptorRenderer())
                                )
                );


        return rez;
    }

    /**
     * Get html form for payment.
     *
     * @param gateway    gateway
     * @param cart       current cart
     * @param order      order
     * @param grandTotal amount
     * @return payment form
     */
    protected String getPaymentForm(final PaymentGateway gateway,
                                    final ShoppingCart cart,
                                    final CustomerOrder order,
                                    final BigDecimal grandTotal) {
        final String fullName = (order.getCustomer().getFirstname()
                + " "
                + order.getCustomer().getLastname()).toUpperCase();
        final String submitBtnValue = getLocalizer().getString("paymentSubmit", this);
        final String postActionUrl = getPostActionUrl(gateway);
        final String htmlFragment = gateway.getHtmlForm(
                fullName,
                cart.getCurrentLocale(),
                grandTotal,
                cart.getCurrencyCode(),
                cart.getGuid());

        return MessageFormat.format(
                "<form action=\"{0}\">\n" +
                        "{1}\n" +
                        "<div id=\"paymentDiv\">\n" +
                        "<input type=\"submit\" value=\"{2}\">" +
                        "</div></form>",
                postActionUrl,
                htmlFragment,
                submitBtnValue
        );

    }

    /**
     * Get the post action url for payment.
     *
     * @param gateway gateway
     * @return url for post
     */
    private String getPostActionUrl(final PaymentGateway gateway) {
        if (gateway instanceof PaymentGatewayExternalForm) {
            //paypal express checkout will return internal url , than mounted with "paymentpaypalexpress" url
            return ((PaymentGatewayExternalForm) gateway).getPostActionUrl();
        }
        /**
         * By default all payment processorsand gateways  parked to page, that mounted with this url
         */
        return "payment";
    }

    /**
     * Create shipment method selection fragment.
     *
     * @return shipment method fragment
     */

    private MarkupContainer createShippmentFragment() {
        return new Fragment(CONTENT_VIEW, SHIPMENT_FRAGMENT, this)
                .add(
                        new ShippingView(SHIPMENT_VIEW)
                );
    }

    /**
     * Create address fragment to manage shipping and billing addresses.
     *
     * @return address fragment.
     */
    private MarkupContainer createAddressFragment() {

        MarkupContainer rez;

        boolean billingAddressHidden = getRequest().getRequestParameters().getParameterValue(
                BILLING_ADDR_VISIBLE).toBoolean(true);

        final Customer customer = customerService.findCustomer(
                ApplicationDirector.getShoppingCart().getCustomerEmail());

        final Model<Customer> customerModel = new Model<Customer>(customer);

        final ManageAddressesView shipppingAddress =
                new ManageAddressesView(SHIPPING_ADDRESS_VIEW, customerModel, Address.ADDR_TYPE_SHIPING, true);

        final ManageAddressesView billingAddress =
                new ManageAddressesView(BILLING_ADDRESS_VIEW, customerModel, Address.ADDR_TYPE_BILLING, true);

        rez = new Fragment(CONTENT_VIEW, ADDRESS_FRAGMENT, this);

        rez.add(
                shipppingAddress
        ).add(
                billingAddress.setVisible(!billingAddressHidden)
        );

        rez.add(
                new Form(BILLING_THE_SAME_FORM).add(
                        new CheckBox(BILLING_THE_SAME, new Model<Boolean>(billingAddressHidden)) {

                            @Override
                            protected boolean wantOnSelectionChangedNotifications() {
                                return true;
                            }

                            @Override
                            public void onSelectionChanged() {
                                final boolean newVal = !getModelObject();
                                setModelObject(newVal);
                                billingAddress.setVisible(!newVal);
                            }
                        }
                )
        );
        return rez;
    }

    /**
     * Get deliveries.
     *
     * @param cart {@link ShoppingCart}
     * @return list of deliveries
     */
    private List<CustomerOrderDelivery> getDeliveries(final ShoppingCart cart) {

        return new ArrayList<CustomerOrderDelivery>(customerOrderService.findByGuid(cart.getGuid()).getDelivery());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        processCommands();

    }


}
