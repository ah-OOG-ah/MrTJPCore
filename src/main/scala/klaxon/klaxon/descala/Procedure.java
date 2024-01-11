package klaxon.klaxon.descala;

/**
 * Unlike Runnable or Callable, Procedures are often not thread-safe and should not be put on another thread.
 */
@FunctionalInterface
public interface Procedure {
    void apply();
}
