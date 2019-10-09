import java.util.List;

public class Field {

	
	private FieldStatus status;
	

	public Field() {
		super();
		this.status = FieldStatus.UNKNOWN;
	}

	public void setStatus(FieldStatus status) {
		this.status = status;
	}
	

	//private Boolean status;
	public FieldStatus getStatus() {
		return status;
	}
	
	public boolean isClustered(List<Cluster> clusterList) {
		for (Cluster cluster : clusterList) {
			if (cluster.FieldList.contains(this))
				return true;
		}
		return false;
	}
	
	public Cluster getCluster(List<Cluster> clusterList) {
		for (Cluster cluster : clusterList) {
			if (cluster.FieldList.contains(this))
				return cluster;
		}
		return null;
	}
}
