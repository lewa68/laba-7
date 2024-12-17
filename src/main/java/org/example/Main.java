package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().create();

        try (Reader reader = new FileReader("src/main/resources/books.json")) {
            Type listType = new TypeToken<List<LibraryVisitor>>() {}.getType();
            List<LibraryVisitor> visitors = gson.fromJson(reader, listType);

            // Task 1
            System.out.println("Task 1: Visitors and their count:");
            visitors
                    .forEach(visitor -> System.out.println(visitor.getName() + " " + visitor.getSurname()));
            long visitorCount = visitors.size();
            System.out.println("Total visitors: " + visitorCount);
            System.out.println();

            // Task 2
            System.out.println("Task 2: Unique books and their count:");
            List<Book> uniqueBooks = visitors.stream()
                    .flatMap(visitor -> visitor.getFavoriteBooks().stream())
                    .distinct().toList();
            uniqueBooks.forEach(System.out::println);
            System.out.println("Total unique books: " + uniqueBooks.size());
            System.out.println();

            // Task 3
            System.out.println("Task 3: Sorted book list by publication year:");
            visitors.stream()
                    .flatMap(visitor -> visitor.getFavoriteBooks().stream())
                    .sorted(Comparator.comparingInt(Book::getPublishingYear))
                    .forEach(book -> System.out.println(book.getName() + " (" + book.getPublishingYear() + ")"));
            System.out.println();


            // Task 4
            System.out.println("Task 4: Checking if any visitor has a book by Jane Austen:");
            boolean hasJaneAustenBook = visitors.stream()
                    .anyMatch(visitor -> visitor.getFavoriteBooks().stream()
                            .anyMatch(book -> book.getAuthor().equals("Jane Austen")));
            System.out.println("Does any visitor have a book by Jane Austen? " + hasJaneAustenBook);
            System.out.println();


            // Task 5
            System.out.println("Task 5: Maximum number of favorite books:");
            int maxFavoriteBooks = visitors.stream()
                    .map(visitor -> visitor.getFavoriteBooks().size())
                    .max(Comparator.naturalOrder())
                    .orElse(0);
            System.out.println("Maximum number of favorite books: " + maxFavoriteBooks);
            System.out.println();

            // Task 6
            System.out.println("Task 6: SMS messages for newsletter subscribers:");
            double averageFavoriteBooks = visitors.stream()
                    .mapToInt(visitor -> visitor.getFavoriteBooks().size())
                    .average()
                    .orElse(0);

            Map<String, List<SmsMessage>> smsMessagesByCategory = visitors.stream()
                    .filter(LibraryVisitor::isSubscribed)
                    .collect(Collectors.groupingBy(
                            visitor -> {
                                int favoriteBookCount = visitor.getFavoriteBooks().size();
                                if (favoriteBookCount > averageFavoriteBooks) {
                                    return "bookworm";
                                } else if (favoriteBookCount < averageFavoriteBooks) {
                                    return "read more";
                                } else {
                                    return "fine";
                                }
                            },
                            Collectors.mapping(visitor -> new SmsMessage(
                                            visitor.getPhone(),
                                            getSmsMessage(visitor, averageFavoriteBooks)),
                                    Collectors.toList())));

            smsMessagesByCategory.forEach((category, smsMessages) -> {
                System.out.println("Category: " + category);
                smsMessages.forEach(smsMessage -> System.out.println("Phone: " + smsMessage.getPhoneNumber() +
                        ", Message: " + smsMessage.getMessage()));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String getSmsMessage(LibraryVisitor visitor, double averageFavoriteBooks) {
        int favoriteBookCount = visitor.getFavoriteBooks().size();
        return switch (favoriteBookCount > averageFavoriteBooks ? 1 :
                favoriteBookCount < averageFavoriteBooks ? 2 : 3) {
            case 1 -> "you are a bookworm";
            case 2 -> "read more";
            case 3 -> "fine";
            default -> "";
        };
    }
}


//        Методы для работы с JSON:
//
//        GsonBuilder().create(): создает экземпляр класса Gson с настройками по умолчанию 1.
//        gson.fromJson(reader, listType): десериализует JSON-строку в объект Java 5.
//
//        Методы для работы с коллекциями:
//
//        visitors.forEach(visitor -> System.out.println(visitor.getName() + " " + visitor.getSurname())): выводит имя и фамилию каждого посетителя.
//        visitors.stream().flatMap(visitor -> visitor.getFavoriteBooks().stream()).distinct().toList(): возвращает список уникальных книг, которые
//        нравятся посетителям.
//        visitors.stream().flatMap(visitor -> visitor.getFavoriteBooks().stream()).sorted(Comparator.comparingInt(Book::getPublishingYear)).forEach(book ->
//        System.out.println(book.getName() + " (" + book.getPublishingYear() + ")")): выводит список книг, отсортированных по году публикации.
//
//        Методы для фильтрации и поиска:
//
//        visitors.stream().anyMatch(visitor -> visitor.getFavoriteBooks().stream().anyMatch(book -> book.getAuthor().equals("Jane Austen"))):
//        проверяет, есть ли среди посетителей те, кто любит книги Джейн Остин.
//        visitors.stream().map(visitor -> visitor.getFavoriteBooks().size()).max(Comparator.naturalOrder()).orElse(0):
//        возвращает максимальное количество любимых книг среди посетителей.
//
//        Методы для группировки и сортировки:
//
//        visitors.stream().filter(LibraryVisitor::isSubscribed).collect(Collectors.groupingBy(visitor -> { ... }, Collectors.mapping(visitor ->
//        new SmsMessage(visitor.getPhone(), getSmsMessage(visitor, averageFavoriteBooks)), Collectors.toList()))): группирует посетителей по категориям и
//        создает список SMS-сообщений для каждого посетителя.
//
//        Методы для работы со строками:
//
//        getSmsMessage(visitor, averageFavoriteBooks): возвращает текст SMS-сообщения в зависимости от количества любимых книг посетителя.