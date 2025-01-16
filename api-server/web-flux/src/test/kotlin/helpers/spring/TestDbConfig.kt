package helpers.spring

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType

@Configuration
@ComponentScan(
    basePackages = ["com.newy.algotrade"],
    excludeFilters = [ComponentScan.Filter(type = FilterType.REGEX, pattern = ["com.newy.algotrade.spring.auth.*"])]
)
class TestDbConfig