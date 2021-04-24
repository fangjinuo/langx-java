package com.jn.langx.util.collection.graph.traverser;

import com.jn.langx.util.collection.graph.*;

import java.util.Map;

/**
 * 深度优先遍历，且先子后父
 *
 * @param <T>
 */
public class DeepFirstGraphTraverser<T> implements GraphTraverser<T> {
    @Override
    public void traverse(Graph<T> graph, String vertexName, VertexConsumer<T> consumer) {
        Vertex v = graph.getVertex(vertexName);
        if (v == null) {
            throw new IllegalArgumentException("the vertex (" + vertexName + ") is not exists");
        }
        Map<String, VisitStatus> visitStatusMap = Graphs.newVisitStatusMap();
        traverse(visitStatusMap, graph, v, null, consumer);
    }

    private void traverse(Map<String, VisitStatus> visitStatusMap, Graph<T> graph, Vertex v, Edge<T> edge, VertexConsumer<T> consumer) {
        if (consumer != null) {
            consumer.accept(graph, v, edge);
        }
        Graphs.finishVisit(visitStatusMap, v.getName());
        for (int i = 0; i < v.getOutgoingEdgeCount(); i++) {
            Edge<T> e = v.getOutgoingEdge(i);
            if (Graphs.isNotVisited(visitStatusMap, e.getTo().getName())) {
                traverse(visitStatusMap, graph, e.getTo(), e, consumer);
            }
        }
    }


}
