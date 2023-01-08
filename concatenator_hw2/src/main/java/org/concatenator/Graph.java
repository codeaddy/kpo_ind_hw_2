package org.concatenator;

import java.util.*;

/**
 * Класс представляет структуру графа
 */
public class Graph {
    private Integer vertexCount;
    private HashMap<Integer, ArrayList<Integer>> dependencies;
    private ArrayList<Integer> inDegree;
    private ArrayList<Integer> parent;

    /**
     * Стандартный конструктор
     */
    public Graph() {
        dependencies = new HashMap<>();
        inDegree = new ArrayList<>();
        parent = new ArrayList<>();
        vertexCount = 0;
    }

    /**
     * Метод добавляет новую вершину в граф
     */
    public void addVertex() {
        dependencies.put(dependencies.size(), new ArrayList<Integer>());
        inDegree.add(0);
        parent.add(-1);
        vertexCount++;
    }

    /**
     * Метод добавляет новое ребро в граф
     *
     * @param from Начало ребра
     * @param to   Конец ребра
     */
    public void addEdge(int from, int to) {
        dependencies.get(from).add(to);
        inDegree.set(to, inDegree.get(to) + 1);
    }

    /**
     * Метод представляет собой поиск в глубину по графу в целях поиска цикла
     *
     * @param vertex Рассматриваемая вершина
     * @param color  Массив цветов вершин
     * @param cycle  Список вершин в цикле
     * @return true, если цикл был найден; false - иначе
     */
    private boolean findCycleDFS(int vertex, Integer[] color, ArrayList<Integer> cycle) {
        color[vertex] = 1;
        boolean foundCycle = false;
        if (dependencies.containsKey(vertex)) {
            for (var to : dependencies.get(vertex)) {
                if (color[to] == 0) {
                    parent.set(to, vertex);
                    foundCycle |= findCycleDFS(to, color, cycle);
                } else if (color[to] == 1) {
                    int current = vertex;
                    cycle.clear();
                    cycle.add(current);
                    do {
                        current = parent.get(current);
                        cycle.add(current);
                    } while (current != to);
                    Collections.reverse(cycle);
                    return true;
                }
            }
        }
        color[vertex] = 2;
        return foundCycle;
    }

    /**
     * Метод выполняет поиск цикла в графе
     *
     * @return Список вершин в цикле (или пустой список, если цикла в графе нет)
     */
    public ArrayList<Integer> searchCycle() {
        Integer[] color = new Integer[vertexCount];
        Arrays.fill(color, 0);
        for (int v = 0; v < vertexCount; v++) {
            if (color[v] == 0) {
                var cycle = new ArrayList<Integer>();
                if (findCycleDFS(v, color, cycle)) {
                    return cycle;
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Метод представляет собой поиск в глубину по графу в целях получения вершин в порядке топологической сортировки
     *
     * @param vertex               Рассматриваемая вершина
     * @param color                Массив цветов вершин
     * @param topologicalSortOrder Список вершин в топологическом порядке
     */
    private void topologicalSortDFS(int vertex, Integer[] color, ArrayList<Integer> topologicalSortOrder) {
        color[vertex] = 1;
        if (dependencies.containsKey(vertex)) {
            for (var to : dependencies.get(vertex)) {
                if (color[to] == 0) {
                    topologicalSortDFS(to, color, topologicalSortOrder);
                }
            }
        }
        topologicalSortOrder.add(vertex);
        color[vertex] = 2;
    }

    /**
     * Метод находит список вершин в топологическом порядке
     *
     * @return Список вершин в топологическом порядке
     */
    public ArrayList<Integer> getTopologicalSortVertexOrder() {
        Integer[] color = new Integer[vertexCount];
        Arrays.fill(color, 0);
        var topologicalVertexOrder = new ArrayList<Integer>();
        for (int v = 0; v < vertexCount; v++) {
            if (color[v] == 0 && inDegree.get(v) == 0) {
                topologicalSortDFS(v, color, topologicalVertexOrder);
            }
        }
        Collections.reverse(topologicalVertexOrder);
        return topologicalVertexOrder;
    }
}
