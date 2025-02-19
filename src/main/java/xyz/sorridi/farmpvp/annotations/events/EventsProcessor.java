package xyz.sorridi.farmpvp.annotations.events;

import lombok.val;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.terminable.module.TerminableModule;
import xyz.sorridi.stone.annotations.IProcessor;
import xyz.sorridi.stone.annotations.ResourceGatherer;
import xyz.sorridi.stone.utils.constructor.ConstructorCaller;

import java.lang.annotation.ElementType;
import java.util.logging.Logger;

public class EventsProcessor implements IProcessor
{
    private final ExtendedJavaPlugin plugin;
    private final Logger logger;

    public EventsProcessor(ExtendedJavaPlugin plugin)
    {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @Override
    public void process()
    {
        logger.info("Processing events...");

        ResourceGatherer.forEachAnnotation(Event.class, (annotation, found) ->
        {
            val foundClass = (Class<?>) found;

            logger.info("Found event: " + foundClass.getName());

            ConstructorCaller
                    .call(foundClass)
                    .ifPresent(service ->
                    {
                        plugin.bindModule((TerminableModule) service);
                        logger.info("Event " + foundClass.getSimpleName() + " set up.");
                    });

        }, ElementType.TYPE, plugin.getClass());

        logger.info("Events processed.");
    }

}