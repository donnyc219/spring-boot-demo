package payroll;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Id;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // indicate that any properties not bound in this type should be ignored.
public class ExampleData {

  private @Id Long id;
  private String name;
  private String salary;
  private Integer age;

  ExampleData() {}
  
  ExampleData(Long id, String name, String salary, Integer age) {
    this.id = id;
    this.name = name;
    this.salary = salary;
    this.age = age;
  }

  @Override
  public String toString() {
    return "ExampleData{" +
        "name='" + name + '\'' +
        ", salary=" + salary +
        ", age=" + age +
        '}';
  }
}