package com.tagtax.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    // JWT密钥
    private static final String SECRET = "mySecretKeyForJWTTokenGenerationAndValidation123456789";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Token过期时间（24小时）
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    public static String generateToken(Integer userId, String username, Integer permissionLevel, Integer departmentId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("permissionLevel", permissionLevel);
        claims.put("departmentId", departmentId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 验证JWT Token
     * @param token JWT Token
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 从Token中获取用户ID
     * @param token JWT Token
     * @return 用户ID
     */
    public static Integer getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? (Integer) claims.get("userId") : null;
    }

    /**
     * 从Token中获取用户名
     * @param token JWT Token
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * 从Token中获取权限级别
     * @param token JWT Token
     * @return 权限级别
     */
    public static Integer getPermissionLevelFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? (Integer) claims.get("permissionLevel") : null;
    }

    /**
     * 从Token中获取部门ID
     * @param token JWT Token
     * @return 部门ID
     */
    public static Integer getDepartmentIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? (Integer) claims.get("departmentId") : null;
    }

    /**
     * 从Token中获取Claims
     * @param token JWT Token
     * @return Claims
     */
    private static Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 检查Token是否过期
     * @param token JWT Token
     * @return 是否过期
     */
    public static boolean isTokenExpired(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return true;
        }
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}