package helpers.spring

import com.newy.algotrade.spring.auth.annotation.AdminOnly
import com.newy.algotrade.spring.auth.annotation.LoginUserInfo
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation

open class AdminOnlyAnnotationTestHelper(private val clazz: KClass<*>) {
    fun hasAdminOnly(methodName: String): Boolean =
        getMethod(methodName).hasAnnotation<AdminOnly>()

    fun hasLoginUserInfo(methodName: String, parameterName: String): Boolean =
        getParameter(methodName, parameterName).hasAnnotation<LoginUserInfo>()

    private fun getMethod(methodName: String): KFunction<*> =
        clazz.functions.find { it.name == methodName }!!

    private fun getParameter(methodName: String, parameterName: String): KParameter =
        getMethod(methodName).parameters.find { it.name == parameterName }!!
}
