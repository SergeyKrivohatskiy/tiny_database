package expressions;

import org.jetbrains.annotations.NotNull;
import queries.SecondLevelId;

import java.util.Map;

/**
 * @author adkozlov
 */
public interface Expression<V> {

    @NotNull
    V execute(@NotNull Map<SecondLevelId, Object> values);
}
