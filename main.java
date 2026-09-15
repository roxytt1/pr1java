import java.util.Locale;
import java.util.Scanner;

public class Main {

    // Именованные константы
    static final int MAX_HERO_HP = 40;
    static final int HEAL_AMOUNT = 8;
    static final int XP_REWARD = 20;
    static final int HERO_POWER = 10;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        //Массивы варианта А
        String[] enemyNames = {"Крыса", "Скелет", "Страж"};
        int[] enemyMaxHp = {12, 18, 24};
        int[] enemyDamage = {4, 6, 8};

        // Регистрация героя
        String heroName = readName(scanner);
        String heroClass = readClass(scanner);
        int level = readLevel(scanner);
        boolean hasShield = readShield(scanner);

        // Проверка допуска
        String admissionReason = checkAdmission(level, hasShield, heroClass);

        if (admissionReason != null) {
            System.out.println("Отказ: " + admissionReason);
            scanner.close();
            return;
        }

        System.out.println("Допущен: " + heroName + ", " + heroClass);

        // Эти значения сохраняются между боями
        int heroHp = MAX_HERO_HP;
        int xp = 0;
        int victories = 0;
        boolean gameInterrupted = false;

        // Три боя
        for (int i = 0; i < enemyNames.length; i++) {

            // Эти значения создаются заново для каждого боя
            int enemyHp = enemyMaxHp[i];
            boolean healUsed = false;

            System.out.println();
            System.out.println("=== Раунд " + (i + 1) + ": " + enemyNames[i] + " ===");

            while (heroHp > 0 && enemyHp > 0) {

                System.out.println("HP героя: " + heroHp + "/" + MAX_HERO_HP);
                System.out.println("HP противника: " + enemyHp + "/" + enemyMaxHp[i]);
                System.out.println("1 — атаковать");
                System.out.println("2 — лечиться");
                System.out.println("0 — закончить игру");
                System.out.print("Команда: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Ошибка: команда должна быть числом.");
                    scanner.nextLine();
                    continue;
                }

                int command = scanner.nextInt();
                scanner.nextLine();

                switch (command) {
                    case 1:
                        // Атака героя
                        enemyHp = calculateDamage(enemyHp, HERO_POWER);

                        // Проверяем победу сразу после атаки
                        if (enemyHp == 0) {
                            xp += XP_REWARD;
                            victories++;
                            System.out.println("Противник побеждён!");
                        } else {
                            // Противник отвечает только если выжил
                            heroHp = calculateDamage(heroHp, enemyDamage[i]);
                        }
                        break;

                    case 2:
                        if (healUsed) {
                            System.out.println("Лечение уже использовано в этом бою.");
                        } else {
                            heroHp = calculateHeal(heroHp);
                            healUsed = true;

                            System.out.println("Герой восстановил здоровье.");

                            // Лечение расходует ход,
                            // поэтому живой противник отвечает
                            if (enemyHp > 0) {
                                heroHp = calculateDamage(heroHp, enemyDamage[i]);
                            }
                        }
                        break;

                    case 0:
                        gameInterrupted = true;
                        System.out.println("Игра прервана.");
                        break;

                    default:
                        System.out.println("Неизвестная команда. Состояние не изменилось.");
                }

                if (gameInterrupted) {
                    break;
                }

                if (heroHp == 0) {
                    System.out.println("Герой проиграл.");
                }
            }

            if (gameInterrupted || heroHp == 0) {
                break;
            }

            System.out.println(
                    "Раунд " + (i + 1) +
                            ": победа, HP героя " + heroHp +
                            ", XP " + xp
            );
        }

        // Итог
        printResult(
                heroName,
                "А · Руины",
                victories,
                xp,
                heroHp,
                gameInterrupted
        );

        scanner.close();
    }

    // Чтение имени
    static String readName(Scanner scanner) {
        while (true) {
            System.out.print("Введите имя героя: ");

            if (!scanner.hasNextLine()) {
                System.out.println("Ввод завершён.");
                System.exit(0);
            }

            String name = scanner.nextLine().strip();

            if (name.length() >= 1 && name.length() <= 30) {
                return name;
            }

            System.out.println("Имя должно содержать от 1 до 30 символов.");
        }
    }

    // Чтение класса
    static String readClass(Scanner scanner) {
        while (true) {
            System.out.print("Введите класс (воин/маг/лучник): ");

            if (!scanner.hasNextLine()) {
                System.out.println("Ввод завершён.");
                System.exit(0);
            }

            String heroClass = scanner.nextLine()
                    .strip()
                    .toLowerCase(Locale.ROOT);

            if (heroClass.equals("воин")
                    || heroClass.equals("маг")
                    || heroClass.equals("лучник")) {
                return heroClass;
            }

            System.out.println("Неизвестный класс. Повторите ввод.");
        }
    }

    // Чтение уровня
    static int readLevel(Scanner scanner) {
        while (true) {
            System.out.print("Введите уровень (1-80): ");

            if (!scanner.hasNextInt()) {
                System.out.println("Уровень должен быть целым числом.");
                scanner.nextLine();
                continue;
            }

            int level = scanner.nextInt();
            scanner.nextLine();

            if (level >= 1 && level <= 80) {
                return level;
            }

            System.out.println("Уровень должен быть от 1 до 80.");
        }
    }

    // Чтение наличия щита
    static boolean readShield(Scanner scanner) {
        while (true) {
            System.out.print("Есть ли щит? (да/нет): ");

            if (!scanner.hasNextLine()) {
                System.out.println("Ввод завершён.");
                System.exit(0);
            }

            String answer = scanner.nextLine()
                    .strip()
                    .toLowerCase(Locale.ROOT);

            if (answer.equals("да")) {
                return true;
            }

            if (answer.equals("нет")) {
                return false;
            }

            System.out.println("Введите только «да» или «нет».");
        }
    }

    // Проверка допуска
    // null означает, что герой допущен
    static String checkAdmission(int level, boolean hasShield, String heroClass) {

        if (level < 10) {
            return "недостаточный уровень";
        }

        if (!hasShield && !heroClass.equals("маг")) {
            return "отсутствует нужная защита";
        }

        return null;
    }

    // Расчёт HP после получения урона
    static int calculateDamage(int currentHp, int damage) {
        return Math.max(0, currentHp - damage);
    }

    // Расчёт HP после лечения
    static int calculateHeal(int currentHp) {
        return Math.min(MAX_HERO_HP, currentHp + HEAL_AMOUNT);
    }

    // Вывод итогов
    static void printResult(
            String heroName,
            String variant,
            int victories,
            int xp,
            int heroHp,
            boolean gameInterrupted
    ) {
        System.out.println();
        System.out.println("=== ИТОГ ===");
        System.out.println("Имя: " + heroName);
        System.out.println("Вариант: " + variant);
        System.out.println("Побед: " + victories);
        System.out.println("XP: " + xp);
        System.out.println("HP: " + heroHp);

        if (gameInterrupted) {
            System.out.println("Итог: Игра прервана");
        } else if (heroHp == 0) {
            System.out.println("Итог: Поражение");
        } else if (victories == 3) {
            System.out.println("Итог: Арена пройдена");
        } else {
            System.out.println("Итог: Поражение");
        }
    }
}
