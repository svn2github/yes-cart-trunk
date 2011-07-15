package org.yes.cart.domain.entity;


/**
 * Product availability.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Availability extends Auditable {

    /**
     * When available on warehouse.
     */
    long STANDARD = 1;
    /**
     * For preorder.
     */
    long PREORDER = 2;

    /**
     * Available for backorder.
     */
    long BACKORDER = 4;
    /**
     * Always available
     */
    long ALWAYS = 8;

    /**
     * Get the pk
     *
     * @return pk
     */
    long getAvailabilityId();

    /**
     * Set pk
     *
     * @param availabilityId pk
     */
    void setAvailabilityId(long availabilityId);

    /**
     * Get name.
     *
     * @return name
     */
    String getName();

    /**
     * Set name.
     *
     * @param name name
     */
    void setName(String name);

    /**
     * Get description.
     *
     * @return description
     */
    String getDescription();

    /**
     * Set description
     *
     * @param description description of availability
     */
    void setDescription(String description);

}


