package com.studyolle.studyolle.modules.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.UserAccount;
import com.studyolle.studyolle.modules.study.Study;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Setter @Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Event {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    private Integer limitOfEnrollments;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public boolean isEnrollableFor(UserAccount userAccount){
        Account account = userAccount.getAccount();
        return LocalDateTime.now().isBefore(endEnrollmentDateTime) && !isEnrolled(account);
    }
    public boolean isDisenrollableFor(UserAccount userAccount){
        Account account = userAccount.getAccount();
        return LocalDateTime.now().isBefore(endEnrollmentDateTime) && isEnrolled(account);
    }

    public boolean isAttended(UserAccount userAccount){
        Account account = userAccount.getAccount();
        for(Enrollment e : this.enrollments){
            if(e.getAccount().equals(account) && e.isAttended()){
                return true;
            }
        }
        return false;
    }

    private boolean isEnrolled(Account account){
        List<Account> collect = enrollments.stream().map(e -> e.getAccount()).collect(Collectors.toList());
        if(collect.contains(account)){
            return true;
        }
        return false;
    }

    public int numberOfRemainSpots(){
        int enrollAmount = enrollments.size();
        return limitOfEnrollments - enrollAmount;

    }

    public void addEnrollment(Enrollment enrollment){
        if(eventType == EventType.FCFS) {
            this.enrollments.add(enrollment);
            enrollment.setEvent(this);
            checkAccept();
        }else if(eventType == EventType.CONFIRMATIVE){
            this.enrollments.add(enrollment);
            enrollment.setEvent(this);
        }
    }

    public void disEnrollment(Enrollment enrollment) {
            this.getEnrollments().remove(enrollment);
            if(eventType == EventType.FCFS)
            this.getEnrollments().forEach(e -> this.checkAccept());
    }

    private void checkAccept(){
        for (int i=0; i < enrollments.size(); i++){
            if(i < limitOfEnrollments && !enrollments.get(i).isAccepted()) enrollments.get(i).setAccepted(true);
        }
    }

    public boolean canAccept(Enrollment enrollment){
        List<Enrollment> temp = new ArrayList<>();
        enrollments.forEach(e -> {if(e.isAccepted()) temp.add(e);});

        if(temp.size() < limitOfEnrollments
                && this.eventType == EventType.CONFIRMATIVE){
            return true;
        }
        return false;
    }

    public boolean canReject(Enrollment enrollment){
        if(enrollments.contains(enrollment) && this.eventType == EventType.CONFIRMATIVE){
            return true;
        }
        return false;
    }


}
