
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ParseUnknownXMLStructure {
	static Position[] positionList = new Position[2];// vertikal und horizontal
	static Row row;
	static int position_id;
	static int size;

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		// Get Docuemnt Builder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		// Build Document
		Document document = builder.parse(new File("data30x30.xml"));
		// Normalize the XML Structure; It's just too important !!
		document.getDocumentElement().normalize();
		NodeList nList = document.getElementsByTagName("position");
		// Daten einlesen
		readData(nList);
		//Scanner scanner = new Scanner(System.in);
		int i = 0;
		final long timeStart = System.currentTimeMillis();
		while (/*scanner.hasNext() || */ i < 28) {
			for (Position position : positionList) {
				position.checkRows(positionList);
			}
			ParseUnknownXMLStructure.drawField(positionList);
			System.out.println("\n-----------------------------");
			System.out.println("Durchlauf Nr: " + (i++ + 1));
			System.out.println("-----------------------------");
			
			//scanner = new Scanner(System.in);
		}
		
		final long timeEnd = System.currentTimeMillis();
        System.out.println("Verlaufszeit der Schleife: " + (timeEnd - timeStart) + " Millisek.");
	}

	// This function is called recursively
	private static void readData(NodeList nList) {
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node node = nList.item(temp);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				// Check all attributes
				if (node.hasAttributes()) {
					// get attributes names and values
					NamedNodeMap nodeMap = node.getAttributes();
					Node tempNode = nodeMap.item(0);
					int value = 0;
					String valueStr = node.getFirstChild().getNodeValue();
					if (valueStr != null && !valueStr.isBlank())
						value = Integer.parseInt(node.getFirstChild().getNodeValue());

					if (node.hasChildNodes()) {
						// We got more childs; Let's visit them as well
						int count = ((node.getChildNodes().getLength() - 1) / 2);
						int id = Integer.parseInt(tempNode.getNodeValue());
						switch (node.getNodeName().toString()) {
						case "position":
							size = count;
							position_id = id;
							positionList[position_id] = new Position(size);
							break;
						case "row":
							row = new Row(count, size);
							positionList[position_id].rowList[id] = row;
							// generate Fields
							for (int i = 0; i < size; i++)
								if (position_id == 0)
									positionList[0].rowList[id].setField(i, new Field());
								else
									positionList[1].rowList[id].setField(i, positionList[0].rowList[i].getField(id));
							break;
						case "token":
							row.setToken(id, value);
							break;
						}
						readData(node.getChildNodes());
					}
				}
			}
		}
	}

	public static void drawField(Position[] positions) {
		Row[] rows = positions[0].rowList;
		String line = "\n-";
		for (int i = 0; i < rows.length * 4; i++) {
			line += "-";
		}
		System.out.print("\n ");
		for (int i = 0; i < rows.length; i++) {
			System.out.print(" " + i + ((i > 9) ? " " : "  "));
		}
		System.out.print(line);
		for (Row row : rows) {
			Field[] fields = row.getFields();
			System.out.print("\n|");
			for (Field field : fields) {
				FieldStatus status = field.getStatus();
				switch (status) {
				case EMPTY:
					System.out.print(" O |");
					break;
				case CHECKED:
					System.out.print(" X |");
					break;
				default:
					System.out.print("   |");
				}

			}
			for (Token token : row.getTokens()) {
				if (!token.isSolved())
					System.out.print(" " + token.getSize());
				else
					System.out.print(" S" + token.getSize());
			}
			System.out.print(line);
		}
		rows = positions[1].rowList;
		int tokenId = 0;
		boolean tokensLeft = true;
		Token token;
		while (tokensLeft) {
			System.out.print("\n ");
			tokensLeft = false;
			for (int i = 0; i < rows.length; i++) {
				token = rows[i].getToken(tokenId);
				if (token == null)
					System.out.print("    ");
				else {
					System.out.print(
							(token.isSolved() ? "S" : " ") + token.getSize() + ((token.getSize() > 9) ? " " : "  "));
					tokensLeft = true;
				}
			}
			tokenId++;
		}

	}
}