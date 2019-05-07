package statisticalAnalyzer;

public class GlobalDirectedGraph extends GlobalData{

    private float avgDistance = 0;
    private float meanDegree = 0;
    private float varianceOutDegree = 0;
    private float varianceInDegree = 0;
    private int diameter = 0;
    private long reachableNodes = 0;
    private int stronglyConn = 0;


    public GlobalDirectedGraph(){};

    @Override
    public void setAvgDistance(float avgDistance){
        this.avgDistance = avgDistance;
    }

    @Override
    public void setMeanDegree(float meanDegree){
        this.meanDegree = meanDegree;
    }

    @Override
    public void setDiameter(int diameter){
        this.diameter = diameter;
    }

    @Override
    public void setReachableNodes(){
        this.reachableNodes = (long)((Math.pow(GlobalData.getNumNodes(), 2)-infDistance - GlobalData.getNumNodes())); //-numNodes.size per togliere i cappi
    }

    @Override
    public void setStronglyConn(int stronglyConn){
        this.stronglyConn = stronglyConn;
    }

    @Override
    public void setVarianceDegree(float varOut, float varIn){
        this.varianceOutDegree = varOut;
        this.varianceInDegree = varIn;
    }


    @Override
    public float getMeanDegree(){
        return this.meanDegree;
    }

    @Override
    public int getDiameter(){
        return this.diameter;
    }



    @Override
    public String getData(){
        return this.diameter+","+this.avgDistance+","+this.reachableNodes+","+this.stronglyConn+","+this.meanDegree+","+this.varianceOutDegree+","+this.varianceInDegree+";";
    }

}
