package org.yes.cart.web.filter.payment;

import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.util.HttpUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * This filter responsible for delegate google call back handling  notifications
 * to google payment gateway implementation and start order transition as usual,
 * acouring to external form processing.
 * See "External payment form processing. Back/silent filter. Handler" sequence diagram.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2012-Jan-07
 * Time: 11:44:41
 */
public class GoogleCheckoutCallBackFilter extends BasePaymentGatewayCallBackFilter {

    /**
     * Construct filter.
     *
     * @param paymentCallBackHandlerFacade handler.
     * @param applicationDirector          app director
     */
    public GoogleCheckoutCallBackFilter(final ApplicationDirector applicationDirector,
                                        final PaymentCallBackHandlerFacade paymentCallBackHandlerFacade) {
        super(applicationDirector, paymentCallBackHandlerFacade);
    }

    /**
     * {@inheritDoc}
     */
    public ServletRequest doBefore(final ServletRequest servletRequest,
                                   final ServletResponse servletResponse) throws IOException, ServletException {

        if (isCallerIpAllowed()) {

            HttpUtil.dumpRequest(servletRequest);

            final Map parameters = servletRequest.getParameterMap();

            final String paymentGatewayLabel = getFilterConfig().getInitParameter("paymentGatewayLabel");

            getPaymentCallBackHandlerFacade().handlePaymentCallback(parameters, paymentGatewayLabel);

            ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_OK);

        } else {

            ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_NOT_FOUND);

        }

        return null;  //no forwarding, just return
    }

}
