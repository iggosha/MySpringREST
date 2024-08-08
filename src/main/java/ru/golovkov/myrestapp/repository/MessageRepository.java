package ru.golovkov.myrestapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.golovkov.myrestapp.model.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findAllByReceiver_IdAndSender_Id(Long receiverId,
                                                   Long senderId,
                                                   Pageable pageable);

    Page<Message> findAllByReceiver_IdAndSender_IdAndContentContainingIgnoreCase(Long receiverId,
                                                                                 Long senderId,
                                                                                 String content,
                                                                                 Pageable pageable);

    @Query("SELECT m FROM Message m " +
            "JOIN m.sender s " +
            "JOIN m.receiver r " +
            "WHERE (s.id = :senderId AND r.id = :receiverId) " +
            "OR (s.id = :receiverId AND r.id = :senderId)")
    Page<Message> findAllWithSenderByIds(@Param("receiverId") Long receiverId,
                                         @Param("senderId") Long senderId,
                                         Pageable pageable);

    @Query("SELECT m FROM Message m " +
            "JOIN m.sender s " +
            "JOIN m.receiver r " +
            "WHERE (s.id = :senderId AND r.id = :receiverId)  AND m.content LIKE %:content% " +
            "OR (s.id = :receiverId AND r.id = :senderId) AND m.content LIKE %:content%")
    Page<Message> findAllWithSenderByIdsAndContent(@Param("receiverId") Long receiverId,
                                                   @Param("senderId") Long senderId,
                                                   @Param("content") String content,
                                                   Pageable pageable);
}
