package filter;

import jakarta.servlet.annotation.WebFilter;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

@WebFilter("/*")
public class SiteMeshFilter extends ConfigurableSiteMeshFilter {
    // Sitemesh filter sẽ tự động sử dụng /WEB-INF/sitemesh3.xml
}

