import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cluster extends Range {

	private ClusterStatus clusterStatus;
	private Set<Token> clusterTokenSet;
	private Token tempToken;

	public Cluster(int min) {
		super(min);
		clusterStatus = ClusterStatus.NO_MATCH;
		clusterTokenSet = new HashSet<>();
	}

	public void findPossibleTokens(Token[] rowTokenList) {
		this.clusterTokenSet.clear();
		for (Token token : rowTokenList) {
			/*
			 * if (token.getStatus() == TokenStatus.FINISHED) continue;
			 */
			if (this.isEnclosedBy(token.getMin(), token.getMax()) && this.getRange() <= token.getSize()) {
				this.clusterTokenSet.add(token);
				token.getClusterList().add(this);
				this.clusterStatus = ClusterStatus.POSSIBLE_MATCH;
			} else {
				token.getClusterList().remove(this);
				this.clusterTokenSet.remove(token);
			}
		}
		if (this.clusterTokenSet.size() == 1) {
			this.tempToken = (Token) this.clusterTokenSet.toArray()[0];
			this.clusterStatus = ClusterStatus.MATCH;
			this.tempToken.setCluster(this);
			this.tempToken.reduceRangeWhenMatched(this);
			if (this.getRange() == this.tempToken.getSize()) {
				this.tempToken.setSolved();
			} else {
				this.tempToken.setStatus(TokenStatus.MATCHED);
			}
		} else if (this.clusterTokenSet.size() > 1 && noTokenIsLonger()) {
			this.setStatus(clusterStatus.FINISHED);
		}
	}

	private boolean noTokenIsLonger() {
		for (Token token : this.clusterTokenSet) {
			if (token.getSize() > this.getRange())
				return false;
		}
		return true;
	}

	public boolean hasFieldIndex(int fieldIndex) {
		return fieldIndex >= this.getMin() && fieldIndex <= this.getMax();
	}

	public ClusterStatus getStatus() {
		return clusterStatus;
	}

	public void setStatus(ClusterStatus status) {
		this.clusterStatus = status;
	}

	public Set<Token> getPossibleTokens() {
		return clusterTokenSet;
	}

	public void setPossibleTokens(Set<Token> clusterTokenSet) {
		this.clusterTokenSet = clusterTokenSet;
	}

	public void finalizeCluster(Field[] rowFieldList) {
		if (this.clusterStatus == ClusterStatus.FINISHED) {
			if (this.getMin() > 0)
				rowFieldList[this.getMin() - 1].setStatus(FieldStatus.EMPTY);
			if (this.getMax() < rowFieldList.length - 1)
				rowFieldList[this.getMax() + 1].setStatus(FieldStatus.EMPTY);
		}

	}
}
