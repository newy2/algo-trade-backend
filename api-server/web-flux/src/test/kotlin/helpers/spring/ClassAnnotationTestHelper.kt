package helpers.spring

import org.springframework.web.bind.annotation.RestController
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation

class ClassAnnotationTestHelper(private val clazz: KClass<*>) {
    fun hasRestControllerAnnotation(): Boolean {
        return clazz.hasAnnotation<RestController>()
    }
}