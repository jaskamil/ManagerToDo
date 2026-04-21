import java.util.*;
import java.io.*;

// Klasa reprezentująca pojedyncze zadanie
class Task {
    private String title;
    private String description;
    private boolean completed;

    // Konstruktor tworzący nowe zadanie
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.completed = false;
    }

    // Oznaczenie zadania jako ukończone
    public void markCompleted() {
        this.completed = true;
    }

    // Sprawdzenie czy zadanie jest ukończone
    public boolean isCompleted() {
        return completed;
    }

    // Pobranie tytułu zadania
    public String getTitle() {
        return title;
    }

    // Zamiana obiektu Task na format tekstowy (do zapisu w pliku)
    public String serialize() {
        return title + "|" + description + "|" + completed;
    }

    // Odtworzenie obiektu Task z tekstu
    public static Task deserialize(String line) {
        String[] parts = line.split("\\|");
        Task t = new Task(parts[0], parts[1]);
        if (Boolean.parseBoolean(parts[2])) {
            t.markCompleted();
        }
        return t;
    }

    // Reprezentacja tekstowa zadania (do wyświetlania)
    @Override
    public String toString() {
        return (completed ? "[✔] " : "[ ] ") + title + " - " + description;
    }
}

// Klasa zarządzająca listą zadań
class TaskManager {
    private List<Task> tasks = new ArrayList<>();
    private final String FILE_NAME = "tasks.txt";

    // Konstruktor – przy uruchomieniu ładuje dane z pliku
    public TaskManager() {
        loadFromFile();
    }

    // Dodanie nowego zadania
    public void addTask(String title, String description) {
        tasks.add(new Task(title, description));
        saveToFile(); // zapis po dodaniu
    }

    // Wyświetlenie wszystkich zadań
    public void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(i + ": " + tasks.get(i));
        }
    }

    // Oznaczenie zadania jako ukończone
    public void completeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).markCompleted();
            saveToFile(); // zapis po zmianie
        } else {
            System.out.println("Invalid index");
        }
    }

    // Usunięcie zadania ("zakończenie")
    public void deleteTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            saveToFile();
            System.out.println("Task removed.");
        } else {
            System.out.println("Invalid index");
        }
    }

    // --- SORTOWANIE I FILTROWANIE ---

    // Wyświetlenie tylko ukończonych zadań
    public void showCompleted() {
        tasks.stream()
                .filter(Task::isCompleted)
                .forEach(System.out::println);
    }

    // Wyświetlenie tylko nieukończonych zadań
    public void showPending() {
        tasks.stream()
                .filter(t -> !t.isCompleted())
                .forEach(System.out::println);
    }

    // Sortowanie zadań alfabetycznie po tytule
    public void sortByTitle() {
        tasks.sort(Comparator.comparing(Task::getTitle));
        System.out.println("Sorted by title.");
    }

    // --- OPERACJE NA PLIKACH ---

    // Zapis listy zadań do pliku
    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Task task : tasks) {
                writer.println(task.serialize());
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    // Wczytanie zadań z pliku
    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tasks.add(Task.deserialize(line));
            }
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }
}

// Główna klasa aplikacji (CLI)
public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static TaskManager manager = new TaskManager();

    public static void main(String[] args) {
        while (true) {
            // Menu użytkownika
            System.out.println("\n1. Add task");
            System.out.println("2. List tasks");
            System.out.println("3. Complete task");
            System.out.println("4. Delete task");
            System.out.println("5. Show completed");
            System.out.println("6. Show pending");
            System.out.println("7. Sort by title");
            System.out.println("8. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            // Obsługa wyboru użytkownika
            switch (choice) {
                case 1:
                    addTask();
                    break;
                case 2:
                    manager.listTasks();
                    break;
                case 3:
                    completeTask();
                    break;
                case 4:
                    deleteTask();
                    break;
                case 5:
                    manager.showCompleted();
                    break;
                case 6:
                    manager.showPending();
                    break;
                case 7:
                    manager.sortByTitle();
                    break;
                case 8:
                    System.exit(0);
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    // Pobranie danych od użytkownika i dodanie zadania
    private static void addTask() {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        manager.addTask(title, description);
    }

    // Pobranie indeksu i oznaczenie zadania jako ukończone
    private static void completeTask() {
        System.out.print("Task index: ");
        int index = scanner.nextInt();
        manager.completeTask(index);
    }

    // Pobranie indeksu i usunięcie zadania
    private static void deleteTask() {
        System.out.print("Task index: ");
        int index = scanner.nextInt();
        manager.deleteTask(index);
    }
}
