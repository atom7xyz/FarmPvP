package xyz.sorridi.farmpvp.annotations.events;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to register events.
 * @author Sorridi
 * @since 1.0
 */
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Event { }