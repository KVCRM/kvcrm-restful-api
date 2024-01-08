package com.kvcrm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Size;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = Account.TABLE_NAME)
@EqualsAndHashCode(of = {"id", "name"})
@ToString(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class Account {

  protected static final String TABLE_NAME = "accounts";

  @Id
  @SequenceGenerator(
      name = "account_id_seq",
      sequenceName = "account_id_seq",
      initialValue = 1,
      allocationSize = 1
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "account_id_seq"
  )
  @ToString.Include
  private Long id;

  @Size(min = 2, max = 120)
  @Column(length = 120, unique = true)
  @ToString.Include
  private String name;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at", nullable = false, updatable = false)
  @JsonProperty("created_at")
  @ToString.Include
  private Instant createdAt;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_at", nullable = true)
  @JsonProperty("updated_at")
  @ToString.Include
  private Instant updatedAt;

}
