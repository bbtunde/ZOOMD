package com.overdrivedx.model;

/**
 * Created by babatundedennis on 10/18/15.
 */
public class Item {
    String id,
    item_details,
    sender_name,
    sender_phone,
    sender_town,
    sender_address,
    recipient_first_name,
    recipient_last_name,
    recipient_phone,
    recipient_town,
    recipient_address,
    seller_id,
    seller_money,
    date_posted,
    weight,
    price,
    size,
    status,
    type;


    // Empty constructor
    public Item(){

    }
    // constructor
    public Item(String id,
                String item_details,
                String sender_name,
                String sender_phone,
                String sender_town,
                String sender_address,
                String recipient_address,
                String recipient_first_name,
                String recipient_last_name,
                String recipient_phone,
                String recipient_town,
                String seller_id,
                String seller_money,
                String weight,
                String price,
                String size,
                String date_posted,
                String status,
                String type
    ){
        super();
                this.id = id;
                this.item_details = item_details;
                this.sender_name = sender_name;
                this.sender_phone = sender_phone;
                this.sender_town = sender_town;
                this.sender_address = sender_address;
                this.recipient_address = recipient_address;
                this.recipient_first_name = recipient_first_name;
                this.recipient_last_name = recipient_last_name;
                this.recipient_phone = recipient_phone;
                this.recipient_town = recipient_town;
                this.seller_id = seller_id;
                this.seller_money = seller_money;
                this.weight = weight;
                this.size = size;
                this.date_posted = date_posted;
                this.price = price;
                this.status = status;
                this.type = type;
    }

    // constructor
    public Item(
            String item_details,
            String sender_name,
            String sender_phone,
            String sender_town,
            String sender_address,
            String recipient_address,
            String recipient_first_name,
            String recipient_last_name,
            String recipient_phone,
            String recipient_town,
            String seller_id,
            String seller_money,
            String weight,
            String size,
            String date_posted,
            String price,
            String status,
            String type
    ){
        this.item_details = item_details;
        this.sender_name = sender_name;
        this.sender_phone = sender_phone;
        this.sender_town = sender_town;
        this.sender_address = sender_address;
        this.recipient_address = recipient_address;
        this.recipient_first_name = recipient_first_name;
        this.recipient_last_name = recipient_last_name;
        this.recipient_phone = recipient_phone;
        this.recipient_town = recipient_town;
        this.seller_id = seller_id;
        this.seller_money = seller_money;
        this.weight = weight;
        this.size = size;
        this.date_posted = date_posted;
        this.price = price;
        this.status = status;
        this.type = type;
    }
    // getting ID
    public String getID(){
        return this.id;
    }

    // setting id
    public void setID(String id){
        this.id = id;
    }

    public String getItemDetails(){
        return this.item_details;
    }

    // setting id
    public void setItemDetails(String item_details){
        this.item_details = item_details;
    }


    // getting name
    public String getSenderName(){
        return this.sender_name;
    }

    // setting name
    public void setSenderName(String name){
        this.sender_name= name;
    }

    // setting phone number
    public void setSenderPhone(String phone){
        this.sender_phone = phone;
    }
    // getting phone number
    public String getSenderPhone(){
        return this.sender_phone;
    }

    public void setSenderTown(String town){
        this.sender_town = town;
    }
    // getting phone number
    public String getSenderTown(){
        return this.sender_town;
    }

    public void setSenderAddress(String address){
        this.sender_address = address;
    }
    // getting phone number
    public String getSenderAddress(){
        return this.sender_address;
    }

    // getting name
    public String getRecipientFirstName(){
        return this.recipient_first_name;
    }

    // setting name
    public void setRecipientFirstName(String name){
        this.recipient_first_name= name;
    }

    // getting phone number
    public String getRecipientLastName(){
        return this.recipient_last_name;
    }

    public void setRecipientLastName(String name){
        this.recipient_last_name = name;
    }

    // setting phone number
    public void setRecipientPhone(String phone){
        this.recipient_phone = phone;
    }
    // getting phone number
    public String getRecipientPhone(){
        return this.recipient_phone;
    }

    public void setRecipientTown(String town){
        this.recipient_town = town;
    }
    // getting phone number
    public String getRecipientTown(){
        return this.recipient_town;
    }

    public void setRecipientAddress(String address){
        this.recipient_address = address;
    }
    // getting phone number
    public String getRecipientAddress(){
        return this.recipient_address;
    }

    public void setWeight(String weight){
        this.weight = weight;
    }
    // getting phone number
    public String getWeight(){
        return this.weight;
    }

    public void setSize(String size){
        this.size = size;
    }
    // getting phone number
    public String getSize(){
        return this.size;
    }

    public void setSellerID(String seller_id){
        this.seller_id = seller_id;
    }
    // getting phone number
    public String getSellerID(){
        return this.seller_id;
    }

    public void setSellerMoney(String seller_money){
        this.seller_money = seller_money;
    }
    // getting phone number
    public String getSellerMoney(){
        return this.seller_money;
    }

    public void setPrice(String price){
        this.price = price;
    }
    // getting phone number
    public String getPrice(){
        return this.price;
    }

    public void setDatePosted(String date_posted){
        this.date_posted = date_posted;
    }
    // getting phone number
    public String getDatePosted(){
        return this.date_posted;
    }

    public void setStatus(String status){
        this.status = status;
    }
    // getting phone number
    public String getStatus(){
        return this.status;
    }

    public void setType(String type){
        this.type = type;
    }
    // getting phone number
    public String getType(){
        return this.type;
    }


}

