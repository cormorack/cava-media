package cavamedia;

import io.swagger.core.filter.SwaggerSpecFilter;
import io.swagger.model.ApiDescription;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.Property;
import java.util.List;
import java.util.Map;

public class AccessHiddenSpecFilter implements SwaggerSpecFilter {

    @Override
    public boolean isOperationAllowed(Operation arg0, ApiDescription arg1, Map<String, List<String>> arg2, Map<String, String> arg3, Map<String, List<String>> arg4) {
        return true;
    }

    @Override
    public boolean isParamAllowed(Parameter param, Operation operation, ApiDescription desc, Map<String, List<String>> arg3, Map<String, String> arg4, Map<String, List<String>> arg5) {
        final String paramAccess = param.getAccess();

        System.out.println("paramAccess is " + paramAccess);

        return !paramAccess.equalsIgnoreCase("Some(hidden)");
    }

    @Override
    public boolean isPropertyAllowed(Model model, Property property, String s, Map<String, List<String>> map, Map<String, String> map1, Map<String, List<String>> map2) {
        return false;
    }
}
