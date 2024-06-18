package helpers

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = ["com.newy.algotrade.web_flux"])
open class TestConfig