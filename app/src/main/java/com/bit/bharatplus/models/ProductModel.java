package com.bit.bharatplus.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductModel {
    // settings up serialized names for dummy data from https://dummyjson.com/products
    @SerializedName("id")
    private int productId;
    @SerializedName("title")
    private String productName;
    @SerializedName("description")
    private String productDescription;
    @SerializedName("price")
    private double productPrice;
    @SerializedName("discountPercentage")
    private double productDiscount;
    @SerializedName("rating")
    private double productRating;
    @SerializedName("stock")
    private int productStocks;
    @SerializedName("brand")
    private String productBrand;
    @SerializedName("category")
    private String productCategory;
    @SerializedName("thumbnail")
    private String productThumbnailURL;
    @SerializedName("images")
    private List<String> productImages;


    public ProductModel() {
    }

    public ProductModel(int productId, String productName, String productDescription, double productPrice, double productDiscount, double productRating, int productStocks, String productBrand, String productCategory, String productThumbnailURL, List<String> productImages) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productDiscount = productDiscount;
        this.productRating = productRating;
        this.productStocks = productStocks;
        this.productBrand = productBrand;
        this.productCategory = productCategory;
        this.productThumbnailURL = productThumbnailURL;
        this.productImages = productImages;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public double getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(double productDiscount) {
        this.productDiscount = productDiscount;
    }

    public double getProductRating() {
        return productRating;
    }

    public void setProductRating(double productRating) {
        this.productRating = productRating;
    }

    public int getProductStocks() {
        return productStocks;
    }

    public void setProductStocks(int productStocks) {
        this.productStocks = productStocks;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductThumbnailURL() {
        return productThumbnailURL;
    }

    public void setProductThumbnailURL(String productThumbnailURL) {
        this.productThumbnailURL = productThumbnailURL;
    }

    public List<String> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<String> productImages) {
        this.productImages = productImages;
    }
}
