package payroll;

// import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service("fooFormatter")
public class FooFormatter {

    public String format() {
        return "foo";
    }

}