package com.wolfiez.wallpaper.repository;

import com.wolfiez.wallpaper.entity.Board;
import com.wolfiez.wallpaper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByUser(User user);

    @Query("SELECT b FROM Board b WHERE b.isPrivate = false")
    List<Board> findPublicBoards();

    @Query("SELECT b FROM Board b WHERE b.user = :user AND b.name LIKE %:keyword%")
    List<Board> searchUserBoards(@Param("user") User user, @Param("keyword") String keyword);


    List<Board> findByUserAndIsPrivate(User user, boolean isPrivate);
}