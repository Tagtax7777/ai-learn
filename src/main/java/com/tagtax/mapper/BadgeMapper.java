package com.tagtax.mapper;

import com.tagtax.entity.Badge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BadgeMapper {
    /**
     * 查询所有徽章（按所需积分升序排列）
     */
    List<Badge> findAllBadges();

    /**
     * 查询用户已获得的徽章
     */
    List<Badge> findUserBadges(@Param("userId") Long userId);

    /**
     * 查询用户尚未获得的徽章
     */
    List<Badge> findUnearnedBadges(@Param("userId") Long userId);

    /**
     * 为用户添加徽章
     */
    Integer addUserBadge(@Param("userId") Long userId, @Param("badgeId") Long badgeId);

    /**
     * 查询用户徽章总数
     */
    Integer countUserBadges(@Param("userId") Long userId);

    /**
     * 查询用户可获得的徽章
     */
    List<Badge> findByPointsThreshold(Integer points);

    /**
     * 查询用户是否可获得此徽章
     */
    boolean exists(@Param("userId") Long userId, @Param("badgeId") Long badgeId);
}