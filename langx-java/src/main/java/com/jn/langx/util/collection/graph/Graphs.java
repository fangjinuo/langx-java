package com.jn.langx.util.collection.graph;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Supplier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Graphs {
    public final static Integer NOT_VISITED = 0;
    public final static Integer VISITING = 1;
    public final static Integer VISITED = 2;

    private static final Supplier<String, VisitStatus> NOT_VISITED_SUPPLIER = new Supplier<String, VisitStatus>() {
        @Override
        public VisitStatus get(String name) {
            return VisitStatus.NOT_VISITED;
        }
    };

    public static Map<String, VisitStatus> newVisitStatusMap() {
        return Collects.emptyNonAbsentHashMap(NOT_VISITED_SUPPLIER);
    }

    public static VisitStatus getVisitStatus(Map<String, VisitStatus> statusMap, String name) {
        return Collects.wrapAsNonAbsentMap(statusMap, NOT_VISITED_SUPPLIER).get(name);
    }

    public static boolean isNotVisited(Map<String, VisitStatus> statusMap, String name) {
        return getVisitStatus(statusMap, name) == VisitStatus.NOT_VISITED;
    }

    public static boolean isVisiting(Map<String, VisitStatus> statusMap, String name) {
        return getVisitStatus(statusMap, name) == VisitStatus.VISITING;
    }

    public static void beginVisit(Map<String, VisitStatus> statusMap, String name) {
        statusMap.put(name, VisitStatus.VISITING);
    }

    public static void finishVisit(Map<String, VisitStatus> statusMap, String name) {
        statusMap.put(name, VisitStatus.VISITED);
    }

    public static List<String> hasCycle(final Graph graph) {
        final List<Vertex> vertices = graph.getVertices();
        final Map<String, VisitStatus> vertexStateMap = Graphs.newVisitStatusMap();
        List<String> retValue = null;
        for (Vertex vertex : vertices) {
            if (Graphs.isNotVisited(vertexStateMap, vertex.getName())) {
                retValue = introducesCycle(vertex, vertexStateMap);
                if (retValue != null) {
                    break;
                }
            }
        }

        return retValue;
    }

    /**
     * This method will be called when an edge leading to given vertex was added and we want to check if introduction of
     * this edge has not resulted in apparition of cycle in the graph
     *
     * @param vertex
     * @param vertexStateMap
     * @return
     */
    public static List<String> introducesCycle(final Vertex vertex, final Map<String, VisitStatus> vertexStateMap) {
        final LinkedList<String> cycleStack = new LinkedList<String>();

        final boolean hasCycle = dfsVisit(vertex, cycleStack, vertexStateMap);

        if (hasCycle) {
            // we have a situation like: [b, a, c, d, b, f, g, h].
            // Label of Vertex which introduced the cycle is at the first position in the list
            // We have to find second occurrence of this label and use its position in the list
            // for getting the sublist of vertex labels of cycle participants
            //
            // So in our case we are searching for [b, a, c, d, b]
            final String label = cycleStack.getFirst();
            final int pos = cycleStack.lastIndexOf(label);
            final List<String> cycle = cycleStack.subList(0, pos + 1);
            Collections.reverse(cycle);
            return cycle;
        }
        return null;
    }

    public static List<String> introducesCycle(final Vertex vertex) {
        final Map<String, VisitStatus> vertexStateMap = Graphs.newVisitStatusMap();
        return introducesCycle(vertex, vertexStateMap);
    }


    private static boolean dfsVisit(final Vertex vertex, final LinkedList<String> cycle,
                                    final Map<String, VisitStatus> vertexStateMap) {
        cycle.addFirst(vertex.getName());

        Graphs.beginVisit(vertexStateMap, vertex.getName());
        List<Vertex> outgoing = vertex.getOutgoingVertices();
        for (Vertex v : outgoing) {
            if (Graphs.isNotVisited(vertexStateMap, v.getName())) {
                final boolean hasCycle = dfsVisit(v, cycle, vertexStateMap);
                if (hasCycle) {
                    return true;
                }
            } else if (Graphs.isVisiting(vertexStateMap, v.getName())) {
                cycle.addFirst(v.getName());
                return true;
            }
        }
        Graphs.finishVisit(vertexStateMap, vertex.getName());
        cycle.removeFirst();
        return false;
    }

}
