package com.projectx.codeecho.domain.entity;

import com.projectx.codeecho.domain.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "room")
public class RoomEntity {
    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @OneToOne
    @JoinColumn(name = "memberId", nullable = false)
    private MemberEntity host;

    @OneToMany
    @JoinColumn(name = "messageId")
    private List<MessageEntity> message;
}
