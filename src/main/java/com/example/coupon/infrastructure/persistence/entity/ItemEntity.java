package com.example.coupon.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "items")
public class ItemEntity {

    @Id
    private String id;

    private String title;

    private String thumbnail;

    private double price;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<FavoriteEntity> favorites;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getThumbnail() { return thumbnail; }

    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }

    public List<FavoriteEntity> getFavorites() { return favorites; }

    public void setFavorites(List<FavoriteEntity> favorites) { this.favorites = favorites; }
}
