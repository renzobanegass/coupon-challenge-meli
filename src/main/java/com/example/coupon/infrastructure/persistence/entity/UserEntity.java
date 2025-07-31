package com.example.coupon.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FavoriteEntity> favorites;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getNickname() { return nickname; }

    public void setNickname(String nickname) { this.nickname = nickname; }

    public List<FavoriteEntity> getFavorites() { return favorites; }

    public void setFavorites(List<FavoriteEntity> favorites) { this.favorites = favorites; }
}
