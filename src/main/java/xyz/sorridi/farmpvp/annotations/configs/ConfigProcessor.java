package xyz.sorridi.farmpvp.annotations.configs;

import lombok.val;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import pl.mikigal.config.Config;
import pl.mikigal.config.ConfigAPI;
import pl.mikigal.config.annotation.ConfigName;
import pl.mikigal.config.style.CommentStyle;
import pl.mikigal.config.style.NameStyle;
import xyz.sorridi.stone.annotations.IProcessor;
import xyz.sorridi.stone.annotations.ResourceGatherer;

import java.lang.annotation.ElementType;
import java.util.logging.Logger;

public class ConfigProcessor implements IProcessor
{
    private final ExtendedJavaPlugin plugin;
    private final Logger logger;

    public ConfigProcessor(ExtendedJavaPlugin plugin)
    {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @Override
    public void process()
    {
        logger.info("Processing configurations...");

        ResourceGatherer.forEachAnnotation(ConfigName.class, (annotation, found) ->
        {
            val foundClass = (Class<Config>) found;

            logger.info("Found config: " + foundClass.getName());

            val config = ConfigAPI.init(
                    foundClass,
                    NameStyle.UNDERSCORE,
                    CommentStyle.ABOVE_CONTENT,
                    true,
                    plugin
            );

            plugin.provideService(foundClass, config);
            logger.info("Config " + foundClass.getSimpleName() + " for " + annotation.value() +  " set up.");

        }, ElementType.TYPE, plugin.getClass());
    }

}
