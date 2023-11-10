package com.projectx.codeecho.domain.entity;

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
}
