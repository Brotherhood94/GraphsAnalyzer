package statisticalAnalyzer;

public class InterestingNode {

    private final int ID;
    private int presence = 0;

    //Directed
    private double avgDistance = 0;
    private int maxDistance = 0;
    private int reachToward = 0;
    private int reachBackward = 0;
    private int inDegree = 0;
    private int outDegree = 0;
    private float varianceInDegree = 0;
    private float varianceOutDegree = 0;

    //Undirected
    private double avgDistanceSymm = 0;
    private int maxDistanceSymm = 0;
    private int reached = 0;
    private int degree = 0;
    private float varianceDegree = 0;

    public InterestingNode(int ID){
        this.ID = ID;
    }

    public int getID(){
        return this.ID;
    }


    public void setPresence(){
        this.presence = 1;
    }

    public void setMaxDistance(int maxDistance, boolean symm){
        if(!symm)
            this.maxDistance = maxDistance;
        else
            this.maxDistanceSymm = maxDistance;
    }

    public void setAvgDistance(double avgDistance, boolean symm){
        if(!symm)
            this.avgDistance = avgDistance;
        else
            this.avgDistanceSymm = avgDistance;
    }

    public void setReached(int reachedTo, int reachedBack){
        this.reachToward = reachedTo;
        this.reachBackward = reachedBack;
    }

    public void setReached(int reached){
        this.reached = reached;
    }

    public void setDegree(int out, int in){
        this.outDegree = out;
        this.inDegree = in;
        this.degree = out+in;
    }


    public void setVarianceDegree(float meanN){
        this.varianceOutDegree = Math.abs(meanN-outDegree);
        this.varianceInDegree = Math.abs(meanN-inDegree);
    }

    public void setVarianceDegreeS(float meanS){
        this.varianceDegree = Math.abs(meanS-degree);
    }

    public String getInfos(){
        return this.presence+","+this.maxDistanceSymm+","+this.avgDistanceSymm+","+this.reached+","+this.degree+","+this.varianceDegree+";;"+
                this.maxDistance+","+this.avgDistance+","+this.reachToward+","+this.reachBackward+","+this.outDegree+","+this.inDegree+","+this.varianceOutDegree+","+this.varianceInDegree+";;";
    }

}
