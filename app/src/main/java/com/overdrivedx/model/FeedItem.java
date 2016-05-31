package com.overdrivedx.model;

/**
 * Created by babatundedennis on 1/28/15.
 */
public class FeedItem {
    private int id;
    private String item_details;

    public FeedItem() {
    }

    public FeedItem(int id, String item_details) {
        super();
        this.id = id;
        this.item_details = item_details;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getItemDetails() {
        return item_details;
    }

    public void setItemDetails(String item_details) {
        this.item_details = item_details;
    }

}
