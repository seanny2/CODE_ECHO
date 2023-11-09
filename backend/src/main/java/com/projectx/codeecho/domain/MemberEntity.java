package com.projectx.codeecho.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Table(name = "member")
public class MemberEntity {
    @Column(nullable = false)
    private String name;

    @Id
    @Column(nullable = false, unique = true)
    private String account;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column
    private String tokenValue;

}
