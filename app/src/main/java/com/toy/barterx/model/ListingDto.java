package com.toy.barterx.model;

import java.util.List;
import java.util.Objects;

public class ListingDto implements Comparable<ListingDto> {
    private String productId;
    private String merchantId;
    private String title;
    private String category;
    private String condition;
    private String description;
    private double latitude;
    private double longitude;
    private double distance;

    @Override
    public String toString() {
        return "ListingDto{" +
                "merchantId='" + merchantId + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", condition='" + condition + '\'' +
                ", description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", distance=" + distance +
                ", listingImages=" + listingImages +
                '}';
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public ListingDto() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private List<String> listingImages;

    public void setListingImages(List<String> listingImages) {
        this.listingImages = listingImages;
    }

    public ListingDto(String title, String category, String condition, String description) {
        this.title = title;
        this.category = category;
        this.condition = condition;
        this.description = description;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getCondition() {
        return condition;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getListingImages() {
        return listingImages;
    }


    @Override
    public int compareTo(ListingDto o) {
        if(this.distance == o.distance){
            return 0;
        }else if(this.distance > o.distance){
            return 1;
        }else{
            return -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListingDto dto = (ListingDto) o;
        return Double.compare(dto.latitude, latitude) == 0 && Double.compare(dto.longitude, longitude) == 0 && Double.compare(dto.distance, distance) == 0 && Objects.equals(merchantId, dto.merchantId) && Objects.equals(title, dto.title) && Objects.equals(category, dto.category) && Objects.equals(condition, dto.condition) && Objects.equals(description, dto.description) && Objects.equals(listingImages, dto.listingImages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(merchantId, title, category, condition, description, latitude, longitude, distance, listingImages);
    }
}
