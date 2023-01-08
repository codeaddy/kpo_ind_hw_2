package org.concatenator;

import java.io.*;
import java.util.*;

public class Concatenator {
    private final Graph fileGraph;
    private final ArrayList<File> files;
    private final HashMap<String, Integer> indexes;
    private final String rootPath;

    /**
     * Стандартный конструктор конкатенатора
     *
     * @param rootPath Корневой путь
     */
    public Concatenator(String rootPath) {
        this.rootPath = rootPath;
        fileGraph = new Graph();
        files = new ArrayList<>();
        indexes = new HashMap<>();
    }

    /**
     * Метод сканирует папки и файлы по пути path (метод рекурсивный, идет по подпапкам)
     *
     * @param path Рассматриваемый путь
     */
    private void scanFiles(String path) {
        File rootFolder = new File(path);
        if (!rootFolder.exists()) {
            System.out.println("Folder doesn't exist/or can't be accessed! - " + path);
            System.exit(0);
        }
        var listFiles = rootFolder.listFiles();
        if (listFiles != null) {
            for (var item : listFiles) {
                if (item.isFile()) {
                    // чтобы скрытые файлы не сканировались
                    if (item.isHidden()) {
                        continue;
                    }
                    files.add(item);
                } else {
                    scanFiles(item.getPath());
                }
            }
        }
    }

    /**
     * Метод находит зависимости в файлах
     *
     * @return true, если все зависимости в файлах были прочитаны успешно; false - иначе
     */
    private boolean findDependencies() {
        int index = 0;
        for (var file : files) {
            indexes.put(file.getPath(), index++);
            fileGraph.addVertex();
        }
        for (var file : files) {
            try (Scanner sc = new Scanner(file)) {
                while (sc.hasNextLine()) {
                    String current = sc.nextLine();
                    if (current.startsWith("require ")) {
                        int separatorL = current.indexOf('\'');
                        int separatorR = current.lastIndexOf('\'');
                        if (separatorL == -1 || separatorR - separatorL < 2) {
                            System.out.println("Incorrect requirement in file: " + file.getPath());
                            return false;
                        }
                        String depended_path = current.substring(separatorL + 1, separatorR);
                        File depended_file = new File(rootPath + File.separator + depended_path);
                        if (!depended_file.exists() || depended_file.getPath().equals(file.getPath())) {
                            System.out.println("Incorrect requirement in file: " + file.getPath());
                            return false;
                        }
                        fileGraph.addEdge(indexes.get(depended_file.getPath()), indexes.get(file.getPath()));
                    }
                }
            } catch (IOException exc) {
                System.out.println("Unable to load file: " + file.getAbsolutePath());
            }
        }
        return true;
    }

    /**
     * Метод считывает файл
     *
     * @param file   Файл, который необходимо считать
     * @param result Куда следует записать результат
     */
    private void addFileContentToString(File file, StringBuilder result) {
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String current = sc.nextLine();
                result.append(current).append(System.lineSeparator());
            }
        } catch (IOException exc) {
            System.out.println("Unable to read file: " + file.getAbsolutePath());
        }
    }

    /**
     * Метод выполняет сборку строки результата, удовлетворяющей всем файловым зависимостям
     *
     * @return Строка результата, удовлетворяющая всем файловым зависимостям
     */
    public String collectResultString() {
        scanFiles(rootPath);
        if (!findDependencies()) {
            return "";
        }
        ArrayList<Integer> cycle = fileGraph.searchCycle();
        if (!cycle.isEmpty()) {
            System.out.println("Found cycle:");
            for (var index : cycle) {
                System.out.println(files.get(index).getPath());
            }
            return "";
        }
        var topologicalVertexOrder = fileGraph.getTopologicalSortVertexOrder();
        StringBuilder result = new StringBuilder();
        for (var vertex : topologicalVertexOrder) {
            addFileContentToString(files.get(vertex), result);
        }
        return result.toString();
    }
}
