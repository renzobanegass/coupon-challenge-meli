package com.example.coupon.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "favorites", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "item_id"})
})
public class FavoriteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public UserEntity getUser() { return user; }

    public void setUser(UserEntity user) { this.user = user; }

    public ItemEntity getItem() { return item; }

    public void setItem(ItemEntity item) { this.item = item; }
}
