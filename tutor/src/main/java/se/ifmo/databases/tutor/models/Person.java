package se.ifmo.databases.tutor.models;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Persistable;

@MappedSuperclass
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true, onlyExplicitlyIncluded = true)
public abstract class Person implements Persistable<Long>, Serializable {

  @Id @EqualsAndHashCode.Include protected Long id;

  @OneToOne(cascade = ALL, fetch = LAZY, optional = false, orphanRemoval = true)
  @JoinColumn(name = "id")
  @MapsId
  @ToString.Exclude
  protected User user;

  @NotBlank
  @Column(length = 63, nullable = false)
  protected String name;

  @NotBlank
  @Column(length = 63, nullable = false)
  protected String surname;

  @Column(length = 63)
  protected String middleName;

  protected LocalDate birthDate;

  @Column(length = 63)
  protected String city;

  @Column(length = 63)
  protected String phoneNumber;

  @Override
  public Long getId() {
    return id;
  }

  @Transient
  @Override
  public boolean isNew() {
    return id == null;
  }

  public User getUser() {
    if (user == null) {
      user = new User();
    }
    return user;
  }

  public String getFullName() {
    return name + " " + surname;
  }
}
