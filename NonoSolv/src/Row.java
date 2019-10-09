import java.util.ArrayList;
import java.util.List;

public class Row {
	private Token[] rowTokenList;
	private Field[] rowFieldList;
	private boolean solved;
	private List<Cluster> clusterList;
	private Range range;
	private Cluster tempCluster;
	private int tokenStartIndex;
	private int tokensCount;
	private int fieldsCount;

	public Row(int tokensCount, int fieldsCount) {
		super();
		this.rowTokenList = new Token[tokensCount];
		this.rowFieldList = new Field[fieldsCount];
		this.tokensCount = tokensCount;
		this.fieldsCount = fieldsCount;
		this.solved = false;
		clusterList = new ArrayList<>();
	}

	public Row(int tokensCount, Field[] fields) {
		super();
		this.rowTokenList = new Token[tokensCount];
		this.solved = false;
	}

	public Token getToken(int id) {
		if (rowTokenList.length > id)
			return rowTokenList[id];
		else
			return null;
	}

	public void setToken(int id, int value) {
		this.rowTokenList[id] = new Token(value);
	}

	public boolean allTokensSolved() {
		for (Token token : rowTokenList) {
			if (!token.isSolved())
				return false;
		}
		return true;
	}

	public boolean isSolved() {
		if (!solved && allTokensSolved())
			solved = true;
		return solved;
	}

	public void finalize() {
		for (Field field : rowFieldList) {
			if (field.getStatus().equals(FieldStatus.UNKNOWN)) {
				field.setStatus(FieldStatus.EMPTY);
			}
		}
	}

	public void setField(int id, Field field) {
		this.rowFieldList[id] = field;
	}

	public Field getField(int id) {
		return rowFieldList[id];
	}

	public Field[] getFields() {
		return rowFieldList;
	}

	public Token[] getTokens() {
		return rowTokenList;
	}

	// Bildet Kluster aus checked-Fields
	public void findCluster() {
		this.clusterList.clear();
		boolean isCluster = false;
		Field field;
		FieldStatus fieldStatus;
		for (int i = 0; i < rowFieldList.length; i++) {
			field = this.rowFieldList[i];
			fieldStatus = field.getStatus();
			if (fieldStatus == FieldStatus.CHECKED) {
				if (isCluster) {
					tempCluster.setMax(i);
				} else {
					tempCluster = new Cluster(i);
					tempCluster.setMax(i);
					this.clusterList.add(tempCluster);
					isCluster = true;
				}
			} else {
				isCluster = false;
			}
		}
	}

	// ermittelt die Relationen zwischen Cluster und Tokens
	public void findClusterTokenRelation() {
		for (Cluster cluster : this.clusterList) {
			cluster.findPossibleTokens(this.rowTokenList);
		}
	}

	// Ermittelt die Ranges der Tokens
	public void checkRange() {
		Token tempToken;
		tokenStartIndex = 0;
		for (Token token : rowTokenList) {
			if(token.getStatus() == TokenStatus.FINISHED) {
				tokenStartIndex = token.getMax() + 1;
				continue;
			} 
			tokenStartIndex = findNextTokenStart(tokenStartIndex, token, true);
			if (tokenStartIndex > token.getMin())
				token.setMin(tokenStartIndex);
			tokenStartIndex += token.getSize() + 1;
		}
		tokenStartIndex = rowFieldList.length - 1;
		for (int i = rowTokenList.length - 1; i >= 0; i--) {
			tempToken = rowTokenList[i];
			if(tempToken.getStatus() == TokenStatus.FINISHED) {
				tokenStartIndex = tempToken.getMin() - 1;
				continue;
			} 
			tokenStartIndex = findNextTokenStart(tokenStartIndex, tempToken, false);
			//System.out.println("\ntokenStartIndex: " + tokenStartIndex + "\n");
			if (tokenStartIndex < tempToken.getMax()) {
				tempToken.setMax(tokenStartIndex);
			}
			tokenStartIndex -= tempToken.getSize() + 1;
		}
	}

	public void finalizTokens() {
		for (Token token : rowTokenList) {
			token.finalizeToken(rowFieldList);
		}
	}
	
	public void finalizCluster() {
		for (Cluster cluster : clusterList) {
			cluster.finalizeCluster(rowFieldList);
		}
	}
	// Findet einen geeigneten Start-Index
	private int findNextTokenStart(int tokenStartIndex, Token token, boolean ascending) {
		int countFields = 0 ;
		FieldStatus fieldStatus;
		Cluster cluster;
		Field field;
		int tokenLength = token.getSize();
		while (countFields < tokenLength ) {
			field = rowFieldList[tokenStartIndex];
			fieldStatus = field.getStatus();
			if (fieldStatus == FieldStatus.EMPTY) {
				countFields = 0;
			} else if (fieldStatus == FieldStatus.CHECKED && this.fieldHasCluster(tokenStartIndex)) {
				cluster = this.getClusterByFieldId(tokenStartIndex);
				if (token.getClusterList().contains(cluster)) {
					countFields++;
				} else {
					countFields = 0;
					/*System.out.println("\nCluster gehört nicht zu Token: " + tokenStartIndex + " t-size: "
							+ token.getSize() + " -- c-size: " + cluster.getRange() + "asc: " + ascending);*/
				}
			} else
				countFields++;
			tokenStartIndex += (ascending ? 1 : -1);
		}
		return tokenStartIndex - (ascending ? tokenLength : -tokenLength);
	}

	public List<Cluster> getClusterList() {
		return clusterList;
	}

	public void setClusterList(List<Cluster> clusterList) {
		this.clusterList = clusterList;
	}

	public boolean isEmpty() {
		for (Token token : rowTokenList) {
			if (!token.isSolved())
				return false;
		}
		return true;
	}

	public void findEmptyFields() {
		int first, last;
		first = -1;
		last = this.rowTokenList[0].getMin();
		this.setEmpty(first, last);
		for (int i = 0; i < this.rowTokenList.length - 1; i++) {
			first = this.rowTokenList[i].getMax();
			last = this.rowTokenList[i + 1].getMin();
			this.setEmpty(first, last);
		}
		first = this.rowTokenList[this.rowTokenList.length - 1].getMax();
		last = rowFieldList.length;
		this.setEmpty(first, last);
	}

	public void setEmpty(int first, int last) {
		for (int i = first + 1; i < last; i++) {
			this.rowFieldList[i].setStatus(FieldStatus.EMPTY);
		}
	}

	// ermittelt Felder, die von einem Token in jeder möglichen Position innerhalb
	// seiner Token-Range abgedeckt werden
	public void findCoreTokenCovers() {
		for (Token token : rowTokenList) {
			token.getClusterList().clear();
			if (token.getSize() > (double) (token.getRange() / 2)) {
				tempCluster = new Cluster(token.getMax() - token.getSize() + 1);
				tempCluster.setMax(token.getMin() + token.getSize() - 1);
				token.getClusterList().add(tempCluster);
				if (token.getStatus() == TokenStatus.PENDING)
					token.setStatus(TokenStatus.MATCHED);
				token.setCluster(tempCluster);
				if (token.getSize() == token.getRange()) {
					token.setSolved();
					if (tempCluster.getMin() > 0)
						rowFieldList[tempCluster.getMin() - 1].setStatus(FieldStatus.EMPTY);
					if (tempCluster.getMax() < Position.puzzleSize - 1)
						rowFieldList[tempCluster.getMax() + 1].setStatus(FieldStatus.EMPTY);
				}
				for (int i = tempCluster.getMin(); i <= tempCluster.getMax(); i++) {
					tempCluster.FieldList.add(this.rowFieldList[i]);
					rowFieldList[i].setStatus(FieldStatus.CHECKED);
				}
			}
			if (token.getStatus() == TokenStatus.MATCHED && token.getSize() == token.getCluster().getRange()) {
				tempCluster = token.getCluster();
				token.setSolved();
				if (tempCluster.getMin() > 0)
					rowFieldList[tempCluster.getMin() - 1].setStatus(FieldStatus.EMPTY);
				if (tempCluster.getMax() < Position.puzzleSize - 1)
					rowFieldList[tempCluster.getMax() + 1].setStatus(FieldStatus.EMPTY);
			}
		}

	}

	public Cluster getClusterByFieldId(int fieldId) {
		for (Cluster cluster : clusterList) {
			if (cluster.hasFieldIndex(fieldId)) {
				return cluster;
			}
		}
		return null;
	}

	public boolean fieldHasCluster(int fieldId) {
		for (Cluster cluster : clusterList) {
			if (cluster.hasFieldIndex(fieldId)) {
				return true;
			}
		}
		return false;
	}
}
