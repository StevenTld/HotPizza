package com.mappers;

import com.dtos.CartDto;
import com.dtos.CartItemDto;
import com.entities.Cart;
import com.entities.CartItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CartMapper {

    /**
     * Convertit une entité Cart en DTO
     */
    public CartDto toDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setTotal(cart.getTotal());
        dto.setCreatedAt(cart.getCreatedAt());
        dto.setUpdatedAt(cart.getUpdatedAt());

        // Conversion des items
        if (cart.getItems() != null) {
            dto.setItems(
                    cart.getItems().stream()
                            .map(this::toItemDto)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    /**
     * Convertit un DTO CartDto en entité
     */
    public Cart toEntity(CartDto dto) {
        if (dto == null) {
            return null;
        }

        Cart cart = new Cart();
        cart.setId(dto.getId());
        cart.setUserId(dto.getUserId());
        cart.setTotal(dto.getTotal());
        cart.setCreatedAt(dto.getCreatedAt());
        cart.setUpdatedAt(dto.getUpdatedAt());

        return cart;
    }

    /**
     * Convertit une entité CartItem en DTO
     */
    public CartItemDto toItemDto(CartItem item) {
        if (item == null) {
            return null;
        }

        CartItemDto dto = new CartItemDto();
        dto.setId(item.getId());
        dto.setIngredientId(item.getIngredientId());
        dto.setIngredientName(item.getIngredientName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());

        return dto;
    }

    /**
     * Convertit un DTO CartItemDto en entité
     */
    public CartItem toItemEntity(CartItemDto dto) {
        if (dto == null) {
            return null;
        }

        CartItem item = new CartItem();
        item.setId(dto.getId());
        item.setIngredientId(dto.getIngredientId());
        item.setIngredientName(dto.getIngredientName());
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());
        item.setTotalPrice(dto.getTotalPrice());

        return item;
    }
}