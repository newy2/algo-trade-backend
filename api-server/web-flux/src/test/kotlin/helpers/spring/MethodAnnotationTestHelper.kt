package helpers.spring

import com.newy.algotrade.spring.auth.annotation.AdminOnly
import com.newy.algotrade.spring.auth.annotation.LoginUserInfo
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.hasAnnotation

class MethodAnnotationTestHelper(private val method: KFunction<*>) {
    fun hasAdminOnlyAnnotation(): Boolean {
        return method.hasAnnotation<AdminOnly>()
    }

    fun hasLoginUserInfoAnnotation(parameterName: String): Boolean {
        return method.findParameterByName(parameterName)?.hasAnnotation<LoginUserInfo>() == true
    }

    fun hasPostMappingAnnotation(value: String): Boolean {
        return method.findAnnotation<PostMapping>()?.value?.first() == value
    }

    fun hasDeleteMappingAnnotation(value: String): Boolean {
        return method.findAnnotation<DeleteMapping>()?.value?.first() == value
    }

    fun hasGetMappingAnnotation(value: String): Boolean {
        return method.findAnnotation<GetMapping>()?.value?.first() == value
    }
}