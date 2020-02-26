package payroll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

// @RestController indicates that the data returned by each method 
// will be written straight into the response body instead of rendering a template.
@RestController
@Slf4j
class EmployeeController {

  private final EmployeeRepository repository;
  

  // An EmployeeRepository is injected by constructor into the controller.
  EmployeeController(final EmployeeRepository repository) {
    this.repository = repository;
  }

  // Aggregate root

  @GetMapping("/employees")
  List<Employee> all() {
    postRequestWithWebClient();
    return repository.findAll();
  }

  @GetMapping("/quote")
  ResponseEntity<Quote> getQuote(){
    final String url = "https://gturnquist-quoters.cfapps.io/api/random";
    // Quote quote = getRequest(url, Quote.class);
    final Quote quote = getRequestWithWebClient(url, Quote.class);
    return new ResponseEntity<Quote>(quote, HttpStatus.OK);
  }

  <T> T getRequest(final String url, final Class<T> c){
    return new RestTemplate().getForObject(url, c);
  }

  <T> T getRequestWithWebClient(final String url, final Class<T> c){
    final WebClient client = WebClient.create(url);
    final Mono<T> result = client.get().retrieve().bodyToMono(c);
    final T res = result.block();
    return res;
  }

  void postRequestWithWebClient() {

    // Step 1: build a WebClient
    String url = "http://dummy.restapiexample.com/api/v1";
    WebClient client = WebClient.builder()
      .baseUrl(url)
      .build();

    // Step 2: post body (example with MultiValueMap)
    // post with uri and body
    MultiValueMap<String, Object> body = buildMapBody();
    WebClient.RequestHeadersSpec<?> request = client
      .post()
      .uri("/create")
      .body(BodyInserters.fromMultipartData(body));
      
    // Step 2: post body (example with Class)
    // post with uri and body
    //  ExampleData body = new ExampleData(Long.valueOf(12), "Peter", "111", Integer.valueOf(25));
    // WebClient.RequestHeadersSpec<?> request = client
    // .post()
    // .uri("/create")
    // .body(BodyInserters.fromPublisher(Mono.just(body), ExampleData.class));

    // Step 3: get the response
    ExamplePostResponse response = request.exchange()
    .block()
    .bodyToMono(ExamplePostResponse.class)
    .block();

    System.out.println("post response: " + response.toString());
  }

  MultiValueMap<String, Object> buildMapBody() {
    // body to be post
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
    body.add("name", "test");
    body.add("salary", "123");
    body.add("age", 23);
    return body;
  }

  @GetMapping("/allWithName")
  List<Employee> allWithName(@RequestParam(required = false) final String name) {
    return repository.findAll();
  }


  @PostMapping("/employees")
  ResponseEntity<Employee> newEmployee(@RequestBody final Employee newEmployee) {
    // return repository.save(newEmployee);
    return new ResponseEntity<Employee>(repository.save(newEmployee), HttpStatus.NOT_FOUND);
  }

  // Single item

  @GetMapping("/employees/{id}")
  ResponseEntity<Employee> one(@PathVariable final Long id) {
  
    final Employee employee = repository.findById(id)
      .orElseThrow(() -> new EmployeeNotFoundException(id));
  
      return new ResponseEntity<Employee>(employee, HttpStatus.OK);
  }

  @PutMapping("/employees/{id}")
  Employee replaceEmployee(@RequestBody final Employee newEmployee, @PathVariable final Long id) {

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
  void deleteEmployee(@PathVariable final Long id) {
    repository.deleteById(id);
  }
}