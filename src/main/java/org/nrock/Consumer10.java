package org.nrock;

@FunctionalInterface
public interface Consumer10<A, B, C, D, E, F, G, H, I, J, K, R> {
    R apply(A a, B b, C c, D d, E e,
                F f, G g, H h, I i,
                J j, K k);
}
