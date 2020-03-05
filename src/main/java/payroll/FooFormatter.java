package payroll;

import org.springframework.stereotype.Service;

// or @Component, @Repository, depending on situation
@Service("fooFormatter")    
public class FooFormatter {

    public String format() {
        return "foo";
    }

}