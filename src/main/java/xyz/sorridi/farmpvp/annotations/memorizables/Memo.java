package xyz.sorridi.farmpvp.annotations.memorizables;

import lombok.NonNull;
import xyz.sorridi.farmpvp.modules.ModulePioneer;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to register memorizable events.
 * @author Sorridi
 * @since 1.0
 */
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Memo
{
    int priority() default 0;
    @NonNull Class<? extends ModulePioneer> module();
}
