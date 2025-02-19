package xyz.sorridi.farmpvp.annotations.memorizables;

import lombok.val;
import me.lucko.helper.Events;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.event.EventPriority;
import xyz.sorridi.farmpvp.modules.events.api.FPlayerJoinEvent;
import xyz.sorridi.farmpvp.modules.events.api.FPlayerQuitEvent;
import xyz.sorridi.farmpvp.utils.IMemorize;
import xyz.sorridi.stone.annotations.IProcessor;
import xyz.sorridi.stone.annotations.ResourceGatherer;
import xyz.sorridi.stone.utils.bukkit.Serve;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.logging.Logger;

public class MemoProcessor implements IProcessor
{
    private final ExtendedJavaPlugin plugin;
    private final Logger logger;

    public MemoProcessor(ExtendedJavaPlugin plugin)
    {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @Override
    public void process()
    {
        TreeMap<Integer, LinkedHashMap<IMemorize, Memo>> serfsPriorities = new TreeMap<>();

        logger.info("Processing memos...");

        ResourceGatherer.forEachAnnotation(Memo.class, (annotation, found) ->
        {
            val foundField = (Field) found;
            val foundClass = foundField.getType();

            foundField.setAccessible(true);

            logger.info("Found memo: " + foundField.getName());

            boolean isMemorize = Arrays.asList(foundClass.getInterfaces()).contains(IMemorize.class);

            if (!isMemorize)
            {
                logger.warning("Class " + foundClass.getSimpleName() + " is not an IMemorize class!");
                return;
            }

            serfsPriorities.putIfAbsent(annotation.priority(), new LinkedHashMap<>());

            IMemorize iMemorize;
            try
            {
                iMemorize = (IMemorize) foundField.get(Serve.of(annotation.module()));
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }

            serfsPriorities.get(annotation.priority()).put(iMemorize, annotation);

        }, ElementType.FIELD, plugin.getClass());

        /*
         * Processing of services.
         */
        logger.info("Processing memos considering priorities...");

        for (val entry : serfsPriorities.entrySet())
        {
            for (val memoBlock : entry.getValue().entrySet())
            {
                IMemorize module = memoBlock.getKey();
                Memo memo = memoBlock.getValue();

                String name = module.getClass().getSimpleName();
                val pioneer = Serve.of(memo.module());

                if (!pioneer.isEnabled())
                {
                    logger.info("Module " + name + " is not enabled. Skipping...");
                    continue;
                }

                Events.subscribe(FPlayerJoinEvent.class, EventPriority.LOWEST)
                        .filter(e -> pioneer.isEnabled())
                        .handler(e -> module.memorize(e.getFPlayer()))
                        .bindWith(plugin);

                Events.subscribe(FPlayerQuitEvent.class, EventPriority.LOWEST)
                        .filter(e -> pioneer.isEnabled())
                        .handler(e -> module.forget(e.getFPlayer()))
                        .bindWith(plugin);

                logger.info("Memo " + name + " with priority " + entry.getKey() + " enabled.");
            }
        }

        logger.info("Memos processed.");
    }

}
