import java.util.ArrayList;
import java.util.List;

public class Token extends Range {
	private int tokenSize;
	private boolean solved;
	private List<Cluster> clusterList;
	private TokenStatus status;
	private Cluster cluster;

	public Token(int tokenSize) {
		super(0);
		this.tokenSize = tokenSize;
		this.solved = false;
		this.status = TokenStatus.PENDING;
		this.max = Position.puzzleSize - 1;
		clusterList = new ArrayList<>();
	}

	public List<Cluster> getClusterList() {
		return clusterList;
	}
	
	public boolean hasClusterWithFieldIndex(int fieldIndex) {
		for(Cluster cluster:clusterList) {
			if(cluster.hasFieldIndex(fieldIndex))
				return true;
		}
		return false;
	}
	

	public void setClusterList(List<Cluster> clusterList) {
		this.clusterList = clusterList;
	}

	public TokenStatus getStatus() {
		return status;
	}

	public void setStatus(TokenStatus status) {
		this.status = status;
	}

	public void reduceRangeWhenMatched(Cluster cluster) {
		int min = cluster.getMax() - this.tokenSize + 1;
		int max = cluster.getMin() + this.tokenSize - 1;
		//System.out.println("\nmin: " + min + "----    max:  " + max + "\n" );
		if (min > this.min)
			this.setMin(min);
		if (max < this.max)
			this.setMax(max);
	}

	public void identifyCheckedFields() {

	}

	public void reduceRange() {
		int min = this.cluster.getMax() - this.tokenSize + 1;
		int max = this.cluster.getMin() + this.tokenSize - 1;
		if (min > this.min)
			this.setMin(min);
		if (max < this.max)
			this.setMax(max);
	}

	public int getSize() {
		return tokenSize;
	}

	public void setSize(int size) {
		this.tokenSize = size;
	}

	public boolean isSolved() {
		return solved;
	}
	
	public void setSolved() {
		this.solved = true;
		this.setStatus(TokenStatus.FINISHED);
	}

	public Cluster getCluster() {
		return cluster;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}
	
	public void finalizeToken(Field[] rowFieldList) {
		if(this.status == TokenStatus.MATCHED && this.tokenSize == cluster.getRange()) {
			this.setSolved();
			if (cluster.getMin() > 0)
				rowFieldList[cluster.getMin() - 1].setStatus(FieldStatus.EMPTY);
			if (cluster.getMax() < Position.puzzleSize - 1)
				rowFieldList[cluster.getMax() + 1].setStatus(FieldStatus.EMPTY);
		}
	}

}
