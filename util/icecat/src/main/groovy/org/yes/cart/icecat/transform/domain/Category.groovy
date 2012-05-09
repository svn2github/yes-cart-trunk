package org.yes.cart.icecat.transform.domain

/**
 * 
 * User: igora Igor Azarny
 * Date: 5/9/12
 * Time: 2:56 PM
 * 
 */
class Category {

    String id;
    String lowPic;
    String score;
    String searchable;
    String thumbPic
    String UNCATID;
    String visible;

    String description;
    String keywords;
    String name;
    
    List<ProductPointer> productPointer = new ArrayList<ProductPointer>();
    List<Product> product = new ArrayList<Product>();

    List<CategoryFeatureGroup> categoryFeatureGroup = new ArrayList<CategoryFeatureGroup>();

    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", lowPic='" + lowPic + '\'' +
                ", score='" + score + '\'' +
                ", searchable='" + searchable + '\'' +
                ", thumbPic='" + thumbPic + '\'' +
                ", UNCATID='" + UNCATID + '\'' +
                ", visible='" + visible + '\'' +
                ", description='" + description + '\'' +
                ", keywords='" + keywords + '\'' +
                ", name='" + name + '\'' +
                ", productPointer  ='" + productPointer.size() + '\'' +
                ", categoryFeatureGroup  ='" + categoryFeatureGroup.size() + '\'' +
                '}';
    }


}
