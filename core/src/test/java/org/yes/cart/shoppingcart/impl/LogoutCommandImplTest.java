package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class LogoutCommandImplTest {
    @Test
    public void testExecute() {
        ShoppingCart shoppingCart = new ShoppingCartImpl();


        Map<String, String> params = new HashMap<String, String>();
        params.put(LoginCommandImpl.EMAIL, "test@test.com");
        params.put(LoginCommandImpl.NAME, "John Doe");

        LoginCommandImpl loginCommand = new LoginCommandImpl(
                null,
                params
        );

        loginCommand.execute(shoppingCart);

        assertNotNull(shoppingCart.getShoppingContext().getCustomerEmail());

        assertEquals("Test that auth in spring security context",
                 "test@test.com",
                shoppingCart.getShoppingContext().getCustomerEmail());

        LogoutCommandImpl command = new LogoutCommandImpl(null, null);
        command.execute(shoppingCart);


        assertNull(shoppingCart.getCustomerEmail());

        assertNull(shoppingCart.getCustomerName());

        assertEquals(ShoppingCart.NOT_LOGGED, shoppingCart.getLogonState());

    }
}
