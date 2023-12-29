package com.kvcrm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = User.TABLE_NAME)
@EqualsAndHashCode(of = {"id", "email", "firstName", "lastName"})
@EntityListeners(AuditingEntityListener.class)
public class User {

  protected static final String TABLE_NAME = "users";

  @Id
  @SequenceGenerator(
      name = "user_id_seq",
      sequenceName = "user_id_seq",
      initialValue = 1,
      allocationSize = 1
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "user_id_seq"
  )
  private Long id;

  @ManyToOne
  @JoinColumn(name = "account_id")
  private Account account;

  @Builder.Default
  private boolean isDeleted = false;

  @Builder.Default
  private boolean owner = false;

  @Size(min = 1, max = 25)
  @Column(length = 25)
  private String firstName;

  @Size(min = 1, max = 25)
  @Column(length = 25)
  private String lastName;

  @Email
  @Size(min = 5, max = 50)
  @Column(length = 50, unique = true)
  private String email;

  @Column(length = 50, unique = true)
  private String password;

  @Column(length = 50)
  private String photoPath;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at", nullable = false)
  @JsonProperty("created_at")
  private Instant createdAt;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_at", nullable = true)
  @JsonProperty("updated_at")
  private Instant updatedAt;

}
