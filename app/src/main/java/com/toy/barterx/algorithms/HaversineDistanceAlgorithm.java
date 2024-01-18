package com.toy.barterx.algorithms;

import com.toy.barterx.model.ListingDto;
import com.toy.barterx.model.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.stream.Collectors;

public class HaversineDistanceAlgorithm {

    private List<ListingDto> listingDtoList;
    private Profile user;
    private int earthRadius = 6371;
    private PriorityQueue<ListingDto> dtoQueue;

    public HaversineDistanceAlgorithm(List<ListingDto> listingDto, Profile user) {
        this.user = user;
        this.listingDtoList = listingDto;
        this.dtoQueue = new PriorityQueue<>(Comparator.comparingDouble(ListingDto::getDistance));
    }

    // convert degrees to radians helper method
    private double ToRadian(double degree) {
        return degree * (Math.PI / 180);
    }

    // compute distance using haversine formula
    private double GetHaversineDistance(double latA, double lonA, double latB, double lonB) {
        double latitudeA, latitudeB, longitudeA, longitudeB;
        latitudeA = ToRadian(latA);
        latitudeB = ToRadian(latB);
        longitudeA = ToRadian(lonA);
        longitudeB = ToRadian(lonB);

        double longitudeDifference = (longitudeB - longitudeA);
        double latitudeDifference = (latitudeB - latitudeA);
        double haversine = Math.pow(Math.sin(latitudeDifference / 2), 2)
                + Math.cos(latitudeA) * Math.cos(latitudeB)
                * Math.pow(Math.sin(longitudeDifference / 2), 2);
        double c = 2 * Math.asin(Math.sqrt(haversine));
        double distance = c * earthRadius;

        return Math.round(distance * 100.0) / 100.0;
    }

    // return a list with nearest locations
    public List<ListingDto> FindNearestUsers(double maxDistance) {
        for (ListingDto listing : listingDtoList) {
            if (user != null) {
                double distance = GetHaversineDistance(user.getLatitude(), user.getLongitude(),
                        listing.getLatitude(), listing.getLongitude());
                listing.setDistance(distance);

                if (distance < maxDistance) {
                    dtoQueue.add(listing);
                }
            }
        }

        return new ArrayList<>(dtoQueue);
    }
}