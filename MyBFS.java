package statisticalAnalyzer;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.webgraph.ImmutableGraph;

import java.util.LinkedList;
import java.util.Queue;

public class MyBFS {

    public static DoubleArrayList BFS(ImmutableGraph g, int s){
        DoubleArrayList list = new DoubleArrayList();
        int[] distTo = new int[g.numNodes()];
        boolean marked[] = new boolean[g.numNodes()];
        Queue<Integer> q= new LinkedList<>();
        for (int v = 0; v < g.numNodes(); v++)
            distTo[v] = Integer.MAX_VALUE;
        distTo[s] = 0;
        list.add(distTo[s]);
        marked[s] = true;
        q.add(s);
        while (!q.isEmpty()) {
            int v = q.remove();
            int[] succ = g.successorArray( v );
            for (int w : succ) {
                if (!marked[w]) {
                    distTo[w] = distTo[v] + 1;
                    marked[w] = true;
                    q.add(w);
                    list.add(distTo[w]); //ci deve anche essere v? cioè s con valore 0?
                }
            }
        }
        return list;
    }

    public static int getMaximum( DoubleArrayList seq ) {
        int max=-1;
        for ( int i = 0; i < seq.size(); i++ )
            if(seq.getDouble(i)>max && seq.getDouble(i)!=Integer.MAX_VALUE)
                max=(int) seq.getDouble(i);
        return max;
    }

    public static double getAverage(DoubleArrayList seq ) {
        int sum=0, count = 0;
        for ( int i = 0; i < seq.size(); i++ )
            if(seq.getDouble(i)!=Integer.MAX_VALUE) {
                sum+=seq.getDouble(i);
                count++;
            }
        return sum/(double)count;
    }

}
