package statisticalAnalyzer;

public abstract class GlobalData {

    private static int numNodes = 0;
    private static long numEdges = 0L;

    protected int infDistance = 0;

    public GlobalData(){};

    public static void setNumNodes(int numNodes){
        GlobalData.numNodes = numNodes;
    }

    public static void setNumEdges(long numEdges){
        GlobalData.numEdges = numEdges;
    }

    public static int getNumNodes(){
        return GlobalData.numNodes;
    }

    public static long getNumEdges(){
        return GlobalData.numEdges;
    }

    public abstract void setAvgDistance(float avgDistance);

    public abstract void setMeanDegree(float meanDegree);

    public abstract float getMeanDegree();

    public abstract void setDiameter(int diameter);

    public abstract int getDiameter();

    public abstract void setReachableNodes();

    public abstract void setStronglyConn(int stronglyConn);

    public abstract void setVarianceDegree(float varOut, float varIn);

    public abstract String getData();
    /////

    public void setInfDistance(int add){
        this.infDistance += add;
    }
}
