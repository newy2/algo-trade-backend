package com.newy.algotrade.domain.common.libs.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [NotBlankElementsValidator::class])
annotation class NotBlankElements(
    val message: String = "List elements must be non-blank",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class NotBlankElementsValidator : ConstraintValidator<NotBlankElements, List<String>> {
    override fun isValid(value: List<String>?, context: ConstraintValidatorContext?): Boolean {
        if (value.isNullOrEmpty()) {
            return true
        }
        return value.all { it.isNotBlank() }
    }
}