package payroll;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// @RestController indicates that the data returned by each method 
// will be written straight into the response body instead of rendering a template.
@RestController
@Slf4j
class EmployeeController {

  private final EmployeeRepository repository;

  // An EmployeeRepository is injected by constructor into the controller.
  EmployeeController(EmployeeRepository repository) {
    this.repository = repository;
  }

  // Aggregate root

  @GetMapping("/employees")
  List<Employee> all() {
    return repository.findAll();
  }

  @GetMapping("/quote")
  ResponseEntity<Quote> getQuote(){
    String url = "https://gturnquist-quoters.cfapps.io/api/random";
    Quote quote = getRequest(url, Quote.class);
    return new ResponseEntity<Quote>(quote, HttpStatus.OK);
  }

  <T> T getRequest(String url, Class<T> c){
    return new RestTemplate().getForObject(url, c);
  }

  @GetMapping("/allWithName")
  List<Employee> allWithName(@RequestParam(required = false) String name) {
    return repository.findAll();
  }


  @PostMapping("/employees")
  ResponseEntity<Employee> newEmployee(@RequestBody Employee newEmployee) {
    // return repository.save(newEmployee);
    return new ResponseEntity<Employee>(repository.save(newEmployee), HttpStatus.NOT_FOUND);
  }

  // Single item

  @GetMapping("/employees/{id}")
  EntityModel<Employee> one(@PathVariable Long id) {
  
    Employee employee = repository.findById(id)
      .orElseThrow(() -> new EmployeeNotFoundException(id));
  
    return new EntityModel<>(employee,
      linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
      linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
  }

  @PutMapping("/employees/{id}")
  Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {

    return repository.findById(id)
      .map(employee -> {
        employee.setName(newEmployee.getName());
        employee.setRole(newEmployee.getRole());
        return repository.save(employee);
      })
      .orElseGet(() -> {
        newEmployee.setId(id);
        return repository.save(newEmployee);
      });
  }

  @DeleteMapping("/employees/{id}")
  void deleteEmployee(@PathVariable Long id) {
    repository.deleteById(id);
  }
}