package xyz.sorridi.farmpvp.annotations.services;

import lombok.Getter;
import lombok.val;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import xyz.sorridi.farmpvp.modules.ModulePioneer;
import xyz.sorridi.stone.annotations.IProcessor;
import xyz.sorridi.stone.annotations.ResourceGatherer;
import xyz.sorridi.stone.utils.constructor.ConstructorCaller;

import java.lang.annotation.ElementType;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.logging.Logger;

public class ServicesProcessor implements IProcessor
{
    private final ExtendedJavaPlugin plugin;
    private final Logger logger;

    @Getter
    private final TreeMap<Integer, LinkedList<ModulePioneer>> serfsPriorities;

    public ServicesProcessor(ExtendedJavaPlugin plugin)
    {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.serfsPriorities = new TreeMap<>();
    }

    @Override
    public void process()
    {
        logger.info("Processing services...");

        ResourceGatherer.forEachAnnotation(Service.class, (annotation, found) ->
        {
            val foundClass = (Class<?>) found;

            logger.info("Found service: " + foundClass.getName());

            boolean isMemorize = ModulePioneer.class.isAssignableFrom(foundClass);

            if (!isMemorize)
            {
                logger.warning("Class " + foundClass.getSimpleName() + " is not a ModulePioneer class!");
                return;
            }

            serfsPriorities.putIfAbsent(annotation.priority(), new LinkedList<>());

            ConstructorCaller
                    .call(foundClass)
                    .ifPresent(service ->
                    {
                        val instance = (ModulePioneer) service;

                        serfsPriorities.get(annotation.priority()).add(instance);
                        plugin.provideService((Class<ModulePioneer>) foundClass, instance);
                    });

        }, ElementType.TYPE, plugin.getClass());

        /*
         * Processing of services.
         */
        logger.info("Processing services considering priorities...");

        for (val entry : serfsPriorities.entrySet())
        {
            for (ModulePioneer module : entry.getValue())
            {
                String name = module.getClass().getSimpleName();

                module.enable();
                plugin.provideService((Class<ModulePioneer>) module.getClass(), module);
                logger.info("Service " + name + " with priority " + entry.getKey() + " enabled.");
            }
        }

        logger.info("Services processed.");

        /*
         * Binding of modules.
         */
        logger.info("Binding modules...");

        serfsPriorities.values().forEach(list -> list.forEach(plugin::bindModule));

        logger.info("Modules bound.");
    }

    /**
     * Shuts down all the modules.
     */
    public void shutdownModules()
    {
        logger.info("Shutting down modules...");

        serfsPriorities.values().forEach(list -> list.forEach(ModulePioneer::disable));

        logger.info("Modules shut down.");
    }

}
