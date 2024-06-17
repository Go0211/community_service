package com.community.zerobase.repository;

import com.community.zerobase.entity.TemporaryPost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRepository extends CrudRepository<TemporaryPost, String> {
}
