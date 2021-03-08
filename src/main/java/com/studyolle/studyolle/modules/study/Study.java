package com.studyolle.studyolle.modules.study;

import com.studyolle.studyolle.modules.account.UserAccount;
import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.tag.Tag;
import com.studyolle.studyolle.modules.zone.Zone;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraph(name = "Study.withAll", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")})

@NamedEntityGraph(name = "Study.tags", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")
})
@Entity
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Study {
    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdateDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;

    private int memberCount = 0;

    public void addManager(Account account) {
        managers.add(account);
    }

    public boolean isJoinable(UserAccount userAccount){
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account) && !this.managers.contains(account);
    }

    public boolean isMember(UserAccount userAccount){
        Account account = userAccount.getAccount();
        return this.members.contains(account);
    }

    public boolean isManager(UserAccount userAccount){
        Account account = userAccount.getAccount();
        return this.managers.contains(account);
    }


}
