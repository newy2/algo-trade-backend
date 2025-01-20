package helpers.spring

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan

@SpringBootTest
@ComponentScan(
    basePackages = ["com.newy.algotrade"],
)
class BaseSpringBootTest : RdbTestContainer()