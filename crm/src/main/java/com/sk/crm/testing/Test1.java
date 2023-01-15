package com.sk.crm.testing;

import reactor.core.publisher.Flux;

public class Test1 {
     public static void main(String[] args) {

          Flux<String> fluxFruits = Flux.just("apple", "pear", "plum");

          Flux<String> fluxColors = Flux.just("red", "green", "blue");

          Flux<Integer> fluxAmounts = Flux.just(10, 20, 30);

          Flux.zip(fluxFruits, fluxColors, fluxAmounts)
                  .subscribe(System.out::println);




          Flux<String> fluxCalc = Flux.just(-1, 0, 1)
                  .map(i -> "10 / " + i + " = " + (10 / i))
                  .onErrorReturn(ArithmeticException.class, "Division by 0 not allowed");
          fluxCalc.subscribe(value -> System.out.println("Next: " + value),
                  error -> System.err.println("Error: " + error));


     }
}
