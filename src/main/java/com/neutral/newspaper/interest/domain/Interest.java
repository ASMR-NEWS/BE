package com.neutral.newspaper.interest.domain;

import com.neutral.newspaper.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "interests")
@Getter
@NoArgsConstructor
public class Interest {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "interests") // 무결성을 해칠 가능성이 있기 때문에 mappedBy를 이용
    private List<Member> members = new ArrayList<>();

    public Interest(String name) {
        this.name = name;
    }
}
