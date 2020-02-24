package payroll;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // indicate that any properties not bound in this type should be ignored.
public class ExamplePostResponse {

  private String status;
  private ExampleData data;

  ExamplePostResponse() {

  }

  @Override
  public String toString() {
    return "ExamplePostResponse{" +
        "status='" + status + '\'' +
        ", data=" + data +
        '}';
  }
}