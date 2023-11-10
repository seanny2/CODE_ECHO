package com.projectx.codeecho.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Table(name="message")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name="memberId", nullable = false)
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name="roomId", nullable = false)
    private RoomEntity room;
}
