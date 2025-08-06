package com.server.ShareDoo.repository;

import com.server.ShareDoo.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    // Lấy tất cả đơn hàng mà sản phẩm thuộc về userId (chủ sở hữu)
    List<Rental> findByProduct_UserIdAndDeletedAtIsNull(Integer userId);
    // Lấy tất cả đơn hàng mà sản phẩm thuộc về userId (chủ sở hữu) và trạng thái
    List<Rental> findByProduct_UserIdAndStatusAndDeletedAtIsNull(Integer userId, String status);
    boolean existsByUser_UserIdAndProduct_ProductIdAndStatusNotAndDeletedAtIsNull(Integer userId, Long productId, String status);
    List<Rental> findByUser_UserIdAndDeletedAtIsNull(Integer userId);
    List<Rental> findTop100ByDeletedAtIsNullOrderByCreatedAtDesc();
    Rental findByOrderCode(Long orderCode);
}