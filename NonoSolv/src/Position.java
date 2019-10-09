
public class Position {
	Row[] rowList;
	Row row;
	static public int puzzleSize;

	public Position(int puzzleSize) {
		Position.puzzleSize = puzzleSize;
		this.rowList = new Row[puzzleSize];
	}

	public void checkRows(Position[] positionList) {
		int i = 0;
		for (Row row : rowList) {
			if (row.isSolved()) {
				row.finalize();
				continue;
			}
			row.checkRange();
			row.findCoreTokenCovers();
			row.findCluster();
			row.findClusterTokenRelation();
			row.finalizTokens();
			row.finalizCluster();
			row.findEmptyFields();
			this.row = row;
			//drawDebugFields(positionList, i++);
		}
	}

	private void drawDebugFields(Position[] positionList, int i) {	
		System.out.print("\n" + i++ + ".)");
		ParseUnknownXMLStructure.drawField(positionList);
		for (Token token : row.getTokens()) {
			System.out.print("\nTOKENSsize: " + token.getSize());
			System.out.print("  -- range: " + token.getRange());
			System.out.print("  -- min: " + token.getMin());
			System.out.print("  -- max: " + token.getMax());
			System.out.print("  -- status: " + token.getStatus());
			if (token.getCluster() != null)
				System.out.print("  -- tokenClusterRange: " + token.getCluster().getRange());
		}

		for (Cluster cluster : row.getClusterList()) {
			System.out.print("\nCLUSTER  -- range: " + cluster.getRange());
			System.out.print("  -- min: " + cluster.getMin());
			System.out.print("  -- max: " + cluster.getMax());
			System.out.print("  -- status: " + cluster.getStatus());
			for (Token token : cluster.getPossibleTokens()) {
				System.out.println("\ntokensize:  " + token.getSize());
			}
		}
	}
}
