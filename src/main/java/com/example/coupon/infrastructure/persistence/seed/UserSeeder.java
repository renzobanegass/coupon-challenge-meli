package com.example.coupon.infrastructure.persistence.seed;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.example.coupon.infrastructure.persistence.entity.UserEntity;
import com.example.coupon.infrastructure.persistence.repository.UserRepository;
import com.github.javafaker.Faker;

@Component
public class UserSeeder {
    private final UserRepository userRepository;
    private final Faker faker = new Faker();

    public UserSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void seed() {
        if (userRepository.count() > 0) return;

        List<UserEntity> users = IntStream.range(0, 10)
            .mapToObj(i -> {
                UserEntity user = new UserEntity();
                user.setNickname(faker.name().fullName());
                return user;
            }).toList();

        userRepository.saveAll(users);
    }
}
