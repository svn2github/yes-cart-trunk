package org.yes.cart.web.page.component.product;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.service.domain.*;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.entity.decorator.ProductDecorator;
import org.yes.cart.web.support.entity.decorator.impl.ProductDecoratorImpl;
import org.yes.cart.web.support.service.AttributableImageService;
import org.yes.cart.web.util.WicketUtil;

import java.util.List;

/**
 * Abstract class to present products list.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 9/18/11
 * Time: 11:06 AM
 */
public abstract class AbstractProductList extends BaseComponent {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    protected final static String NAME = "name";
    protected final static String PRODUCT_LIST = "products";
    protected final static String PRODUCT_NAME_LINK = "productLinkName";
    protected final static String PRODUCT_LINK_IMAGE = "productLinkImage";
    protected final static String PRODUCT_IMAGE = "productDefaultImage";
    // ------------------------------------- MARKUP IDs END   ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.PRODUCT_ASSOCIATIONS_SERVICE)
    protected ProductAssociationService productAssociationService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SKU_SERVICE)
    protected ProductSkuService productSkuService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SERVICE)
    protected ProductService productService;

    @SpringBean(name = StorefrontServiceSpringKeys.ATTRIBUTABLE_IMAGE_SERVICE)
    protected AttributableImageService attributableImageService;

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    protected CategoryService categoryService;

    @SpringBean(name = ServiceSpringKeys.IMAGE_SERVICE)
    protected ImageService imageService;


    /**
     * Construct product list to show.
     *
     * @param id component id.
     */
    public AbstractProductList(final String id) {
        super(id);
    }


    /**
     * Get the value map with parameter to show the product.
     *
     * @param prod given product product
     * @return value map with parameter to show the product
     */
    protected PageParameters getProductPageParameters(final Product prod) {
        return WicketUtil.getFilteredRequestParameters(
                getPage().getPageParameters())
                .set(WebParametersKeys.PRODUCT_ID, String.valueOf(prod.getProductId()));

    }

    /**
     * Get list of product to show.
     *
     * @return list of products to show.
     */
    public abstract List<Product> getProductListToShow();


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {
        add(
                new ListView<Product>(PRODUCT_LIST, getProductListToShow()) {
                    protected void populateItem(ListItem<Product> listItem) {

                        final Product prod = listItem.getModelObject();
                        final ProductDecorator productDecorator = new ProductDecoratorImpl(
                                imageService,
                                attributableImageService,
                                categoryService,
                                prod,
                                WicketUtil.getHttpServletRequest().getContextPath());
                        final Category category = categoryService.getRootCategory();
                        final String width = productDecorator.getThumbnailImageWidth(category);   //TODo size
                        final String height = productDecorator.getThumbnailImageHeight(category);
                        final PageParameters pageParameters = getProductPageParameters(prod);


                        listItem.add(
                                new BookmarkablePageLink<HomePage>(PRODUCT_LINK_IMAGE, HomePage.class, pageParameters).add(
                                        new ContextImage(PRODUCT_IMAGE, productDecorator.getDefaultImage(width, height))
                                                .add(new AttributeModifier(HTML_WIDTH, width))
                                                .add(new AttributeModifier(HTML_HEIGHT, height))
                                                .add(new AttributeModifier(HTML_TITLE, prod.getDescription()))
                                                .add(new AttributeModifier(HTML_ALT, prod.getName()))
                                )
                        );
                        listItem.add(
                                new BookmarkablePageLink<HomePage>(PRODUCT_NAME_LINK, HomePage.class, pageParameters)
                                        .add(
                                                new Label(NAME, prod.getName()).setEscapeModelStrings(false)
                                        )
                        );
                    }

                }
        );

        super.onBeforeRender();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible() {
        return super.isVisible() && !getProductListToShow().isEmpty();
    }


}
