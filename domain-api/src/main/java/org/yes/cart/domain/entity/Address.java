package org.yes.cart.domain.entity;

/**
 * Customer address. Address type can be S - Shipping B - Billing.
 * First, Last, Middle name can be different in case of gift.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Address extends Auditable {

    /**
     * Billing address type
     */
    String ADDR_TYPE_BILLING = "B";

    /**
     * Shipping address type.
     */
    String ADDR_TYPE_SHIPING = "S";

    /**
     * Get pk value
     *
     * @return pk value
     */
    long getAddressId();

    /**
     * Set pk value.
     *
     * @param addressId value o set
     */
    void setAddressId(long addressId);

    /**
     * Get city.
     *
     * @return city
     */
    String getCity();

    /**
     * Set city
     *
     * @param city value to set
     */
    void setCity(String city);

    /**
     * Get postcode.
     *
     * @return post code
     */
    String getPostcode();

    /**
     * Set post code
     *
     * @param postcode value to set
     */
    void setPostcode(String postcode);

    /**
     * Addr line 1.
     *
     * @return addr line 1
     */
    String getAddrline1();

    /**
     * Set first address line
     *
     * @param addrline1 value to set
     */
    void setAddrline1(String addrline1);

    /**
     * Get second address line
     *
     * @return value to set
     */
    String getAddrline2();

    /**
     * Set address line 2.
     *
     * @param addrline2 value to set
     */
    void setAddrline2(String addrline2);

    /**
     * Get address type
     *
     * @return addr type
     */
    String getAddressType();

    /**
     * Set addr type.
     *
     * @param addressType value to set
     */
    void setAddressType(String addressType);

    /**
     * Get country.
     *
     * @return coubtry.
     */
    String getCountryCode();

    /**
     * Set country.
     *
     * @param countryCode country to set
     */
    void setCountryCode(String countryCode);


    /**
     * Get first name.
     *
     * @return first name
     */
    String getFirstname();

    /**
     * Set first name
     *
     * @param firstname value to set
     */
    void setFirstname(String firstname);

    /**
     * Get last name.
     *
     * @return last name
     */
    String getLastname();

    /**
     * Set last name
     *
     * @param lastname value to set
     */
    void setLastname(String lastname);

    /**
     * Get middle name
     *
     * @return middle name
     */
    String getMiddlename();

    /**
     * Set middle name
     *
     * @param middlename value to set
     */
    void setMiddlename(String middlename);


    /**
     * Get customer.
     *
     * @return {@link org.yes.cart.domain.entity.Customer}
     */
    Customer getCustomer();

    /**
     * Set customer.
     *
     * @param customer customer
     */
    void setCustomer(Customer customer);

    /**
     * Is address default. Only one default addrtess allowed per address type.
     *
     * @return true if address default.
     */
    boolean isDefaultAddress();


    /**
     * Set deafult flag.
     *
     * @param defaultAddress defalt address flag
     */
    void setDefaultAddress(boolean defaultAddress);


    /**
     * State or province code.
     *
     * @return state or province code
     */
    String getStateCode();

    /**
     * Set state or province.
     *
     * @param stateCode state.
     */
    void setStateCode(final String stateCode);


    /**
     * Get comma separated list of phones of one phone.
     *
     * @return comma separated list of phones of one phone.
     */
    String getPhoneList();


    /**
     * set comma separated list of phones of one phone.
     *
     * @param phoneList comma separated list of phones of one phone.
     */
    void setPhoneList(String phoneList);


}


