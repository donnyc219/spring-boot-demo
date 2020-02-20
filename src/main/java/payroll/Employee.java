package payroll;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

// @Data is a Lombok annotation to create all the getters, setters, equals, hash, and toString methods, 
// based on the fields.
@Data

// @Entity is a JPA annotation to make this object ready for storage in a JPA-based data store.
@Entity
class Employee {

  private @Id @GeneratedValue Long id;
  private String name;
  private String role;

  Employee() {}

  Employee(String name, String role) {
    this.name = name;
    this.role = role;
  }
}