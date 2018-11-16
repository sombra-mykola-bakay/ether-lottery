package com.sombrainc.ether.lottery.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
/**
 * Utility class to handle checked exceptions in lambdas.
 * <p>
 * From <a href="http://stackoverflow.com/questions/27644361/how-can-i-throw-checked-exceptions-from-inside-java-8-streams">How can I throw CHECKED exceptions from inside Java 8 streams?</a>.
 *   This class helps to handle checked exceptions with lambdas. Example, with Class.forName method, which throws checked exceptions:
 * </p>
 * <pre>
 *   Stream.of("java.lang.Object", "java.lang.Integer", "java.lang.String")
 *       .map(rethrowFunction(Class::forName))
 *       .collect(Collectors.toList());
 * </pre>
 *
 * @author http://stackoverflow.com/users/3411681/marcg
 */
public final class LambdaExceptionUtil {

  private LambdaExceptionUtil(){

  }

  @FunctionalInterface
  public interface Consumer_WithExceptions<T, E extends Throwable> {

    void accept(T t) throws E;
  }

  @FunctionalInterface
  public interface BiConsumer_WithExceptions<T, U, E extends Throwable> {

    void accept(T t, U u) throws E;
  }

  @FunctionalInterface
  public interface Function_WithExceptions<T, R, E extends Throwable> {

    R apply(T t) throws E;
  }

  @FunctionalInterface
  public interface Supplier_WithExceptions<T, E extends Throwable> {

    T get() throws E;
  }

  @FunctionalInterface
  public interface Runnable_WithExceptions<E extends Throwable> {

    void run() throws E;
  }

  /**
   * .forEach(rethrowConsumer(name -> System.out.println(Class.forName(name)))); or
   * .forEach(rethrowConsumer(ClassNameUtil::println));
   */
  public static <T, E extends Throwable> Consumer<T> rethrowConsumer(
      Consumer_WithExceptions<T, E> consumer) throws E {
    return t -> {
      try {
        consumer.accept(t);
      } catch (Throwable exception) {
        throwAsUnchecked(exception);
      }
    };
  }

  public static <T, U, E extends Throwable> BiConsumer<T, U> rethrowBiConsumer(
      BiConsumer_WithExceptions<T, U, E> biConsumer) throws E {
    return (t, u) -> {
      try {
        biConsumer.accept(t, u);
      } catch (Throwable exception) {
        throwAsUnchecked(exception);
      }
    };
  }

  /**
   * .map(rethrowFunction(name -> Class.forName(name))) or .map(rethrowFunction(Class::forName))
   */
  public static <T, R, E extends Throwable> Function<T, R> rethrowFunction(
      Function_WithExceptions<T, R, E> function) throws E {
    return t -> {
      try {
        return function.apply(t);
      } catch (Throwable exception) {
        throwAsUnchecked(exception);
        return null;
      }
    };
  }

  /**
   * rethrowSupplier(() -> new StringJoiner(new String(new byte[]{77, 97, 114, 107}, "UTF-8"))),
   */
  public static <T, E extends Throwable> Supplier<T> rethrowSupplier(
      Supplier_WithExceptions<T, E> function) throws E {
    return () -> {
      try {
        return function.get();
      } catch (Throwable exception) {
        throwAsUnchecked(exception);
        return null;
      }
    };
  }

  /**
   * uncheck(() -> Class.forName("xxx"));
   */
  public static void uncheck(Runnable_WithExceptions t) {
    try {
      t.run();
    } catch (Throwable exception) {
      throwAsUnchecked(exception);
    }
  }

  /**
   * uncheck(() -> Class.forName("xxx"));
   */
  public static <R, E extends Throwable> R uncheck(Supplier_WithExceptions<R, E> supplier) {
    try {
      return supplier.get();
    } catch (Throwable exception) {
      throwAsUnchecked(exception);
      return null;
    }
  }

  /**
   * uncheck(Class::forName, "xxx");
   */
  public static <T, R, E extends Throwable> R uncheck(Function_WithExceptions<T, R, E> function,
      T t) {
    try {
      return function.apply(t);
    } catch (Throwable exception) {
      throwAsUnchecked(exception);
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private static <E extends Throwable> void throwAsUnchecked(Throwable exception) throws E {
    throw (E) exception;
  }

}
