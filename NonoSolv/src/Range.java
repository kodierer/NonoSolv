import java.util.ArrayList;
import java.util.List;

public class Range {
	int min;
	int max;
	List<Field> FieldList;
	public Range(int min) {
		super();
		this.min = min;
		FieldList = new ArrayList<>();
	}
	
	
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	
	public int getRange() {
		return this.max - this.min + 1;
	}
	
	public boolean isEnclosedBy(int first, int last) {
		return this.min >= first && this.max <= last;
	}
}
